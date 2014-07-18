package com.github.lobo.less.daemon.model;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.lobo.less.daemon.event.AddFileEvent;
import com.github.lobo.less.daemon.event.RemoveImportEvent;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;
import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.google.inject.Provider;

@SuppressWarnings("serial")
public class LessFile implements Serializable, LessContainer {

	public static final Pattern IMPORT_PATTERN = Pattern.compile("@import[ ]+'([^']*)'[ ]*;?$");
	
	private static final Logger logger = LoggerFactory.getLogger(LessFile.class);

	private String filename;

	private List<LessFile> files = Lists.newCopyOnWriteArrayList();
	
	private Map<String, LessFile> paths = Maps.newConcurrentMap();

	private LessContainer parent;

	private LessFile root;

	private FileAlterationMonitor monitor;

	private FileAlterationObserver observer;
	
	FileAlterationListenerAdaptor changeListener = new FileAlterationListenerAdaptor() {
		public void onFileChange(File file) {
			if(filename.equals(file.getAbsolutePath())) {
				Path path = Paths.get(filename);
				if(!Files.exists(path))
					return;
				
				Set<String> newImports = parseImports(path);

				// Process removes
				SetView<String> removes = Sets.difference(importDeclarations, newImports);
				for(String removedImport : removes)
					removeFile(removedImport);

				// Process add
				SetView<String> adds = Sets.difference(newImports, importDeclarations);
				for(String addedImport : adds)
					addFile(addedImport);
				
				importDeclarations = newImports;
			}
		}
	};

	private EventBus eventBus;
	
	@Inject Provider<LessFile> lessFileProvider;

	private Set<String> importDeclarations = Sets.newHashSet();

	@Inject
	public LessFile(EventBus eventBus) {
		this.eventBus = eventBus;
		root = this;
		parent = this;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T extends LessContainer> T as() {
		return (T) this;
	}
	
	@Override
	public boolean isFile() {
		return true;
	}
	
	@Override
	public boolean isFolder() {
		return false;
	}
	
	public void initialize(String filename, LessContainer parent) {
		this.parent = parent;
		setRoot();
		this.filename = filename;
		parent.addFile(this);
		processImports();
		eventBus.post(new AddFileEvent(this));
	}
	
	private void setRoot() {
		if(parent.isFolder()) {
			root = this;
		} else {
			LessFile parentFile = parent.as();
			root = parentFile.getRoot();
		}
	}

	@Override
	public LessContainer getParent() {
		return parent;
	}

	public void setParent(LessContainer parent) {
		this.parent = parent;
	}

	public LessFile getRoot() {
		return root;
	}

	public void setRoot(LessFile root) {
		this.root = root;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public List<LessFile> getFiles() {
		return files;
	}

	public void setFiles(List<LessFile> imported) {
		this.files = imported;
	}

	@Override
	public String toString() {
		return "LessFile [filename=" + filename + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((filename == null) ? 0 : filename.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LessFile other = (LessFile) obj;
		if (filename == null) {
			if (other.filename != null)
				return false;
		} else if (!filename.equals(other.filename))
			return false;
		return true;
	}

	public void addFile(String filename) {
		LessFile file = lessFileProvider.get();
		file.initialize(filename, this);
	}
	
	@Override
	public void addFile(LessFile file) {
		files.add(file);
		paths.put(file.getFilename(), file);
		if(logger.isDebugEnabled())
			logger.debug("ADDED import {} to file {} (root={})", file, this, root);
	}

	public void removeFile(String removedFile) {
		LessFile toRemove = paths.get(removedFile);
		removeFile(toRemove);
	}
	
	@Override
	public void removeFile(LessFile file) {
		for(LessFile child : file.getFiles())
			file.removeFile(child.getFilename());
		if(logger.isDebugEnabled())
			logger.debug("REMOVED import {} from file {} (root={})", file, this, root);
		files.remove(file);
		paths.remove(file.getFilename());
		eventBus.post(new RemoveImportEvent(file));
	}
	
	public List<LessFile> getRecursiveImportList() {
		List<LessFile> list = Lists.newArrayList();
		list.addAll(files);
		for(LessFile importedFile : files)
			list.addAll(importedFile.getRecursiveImportList());
		return list;
	}

	public boolean isRoot() {
		return equals(root);
	}

	public FileAlterationListener getChangeListener() {
		return changeListener;
	}
	
	private void processImports() {
		Path path = Paths.get(filename);
		if(!Files.exists(path))
			return;
		
		importDeclarations = parseImports(path);
		
		for (String importFilename : importDeclarations) {
			LessFile toAdd = lessFileProvider.get();
			toAdd.initialize(importFilename, this);
			if (logger.isDebugEnabled())
				logger.debug("Adding file '{}'", importFilename);
		}
	}
	
	private boolean isLoop(String importFilename) {
		if(filename.equals(importFilename))
			return true;

		if(isRoot())
			return false;

		LessFile parentFile = getParentFile();
		if(parentFile == null)
			return false;
		
		if(parentFile.hasImportDeclarations(importFilename))
			return true;

		// keep searching
		return parentFile.isLoop(importFilename);
	}

	private LessFile getParentFile() {
		if(!parent.isFile())
			throw new ClassCastException("Not a file");
		return parent.as();
	}

	public boolean hasImportDeclarations(String importFilename) {
		return importDeclarations.contains(importFilename);
	}

	public Set<String> parseImports(Path path) {
		Set<String> imports = Sets.newHashSet();

		try {
			List<String> lines = Files.readAllLines(path, Charset.defaultCharset());
			for (String line : lines) {
				Matcher m = IMPORT_PATTERN.matcher(line);
				if (m.matches()) {
					String importFilename = Paths.get(filename).getParent().resolve(m.group(1)).toString();
					if (isLoop(importFilename)) {
						logger.warn("Refusing to @import '{}' in file '{}' because it was already imported in hierarchy", importFilename, filename);
						continue;
					}
					if (logger.isTraceEnabled())
						logger.trace("Found @import '{}';", importFilename);
					imports.add(importFilename);
				}
			}
		} catch (IOException e) {
			logger.error("Error processing less file {}", path);
		}
		
		return imports;
	}
	
}
