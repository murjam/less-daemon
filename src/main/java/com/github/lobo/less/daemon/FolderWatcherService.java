package com.github.lobo.less.daemon;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.internal.Maps;
import com.beust.jcommander.internal.Sets;
import com.github.lobo.less.daemon.event.AddFileEvent;
import com.github.lobo.less.daemon.event.NeedsCompileEvent;
import com.github.lobo.less.daemon.event.RemoveImportEvent;
import com.github.lobo.less.daemon.model.LessFile;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;

public class FolderWatcherService {

	private static final Logger logger = LoggerFactory.getLogger(FolderWatcherService.class);
	
	Multimap<String, LessFile> filesByPath = ArrayListMultimap.create();
	Map<Path, FileAlterationObserver> folderObservers = Maps.newHashMap();

	private FileAlterationMonitor fam;

	private EventBus eventBus;
	
	private final FileAlterationListenerAdaptor localListener = new FileAlterationListenerAdaptor() {
		public void onFileChange(File file) {
			Collection<LessFile> fileList = filesByPath.get(file.getAbsolutePath());
			Set<LessFile> compileList = Sets.newHashSet();
			for(LessFile lessFile : fileList)
				compileList.add(lessFile.getRoot());
			eventBus.post(new NeedsCompileEvent(compileList));
		}
	};
	
	@Inject
	public FolderWatcherService(EventBus eventBus, FileAlterationMonitor fam) {
		eventBus.register(this);
		this.eventBus = eventBus;
		this.fam = fam;
	}
	
	public void start() throws Exception {
		fam.start();
	}
	
	@Subscribe
	public void onAddFile(AddFileEvent event) {
		if(logger.isTraceEnabled())
			logger.trace("onAddFile({}) - root: {}", event, event.isRoot());
		LessFile file = event.getFile();
		filesByPath.get(file.getFilename()).add(file);
		
		Path folder = Paths.get(file.getFilename()).getParent();
		FileAlterationObserver observer = folderObservers.get(folder);
		if(observer == null) {
			observer = new FileAlterationObserver(folder.toFile());
			folderObservers.put(folder, observer);
			observer.addListener(localListener);
			fam.addObserver(observer);
		}
		observer.addListener(event.getFile().getChangeListener());
	}
	
	@Subscribe
	public void onRemoveImport(RemoveImportEvent event) {
		if(logger.isTraceEnabled())
			logger.trace("onRemoveImport({})", event);
		LessFile file = event.getFile();
		Path folder = Paths.get(file.getFilename()).getParent();
		FileAlterationObserver observer = folderObservers.get(folder);
		if(observer != null) {
			observer.removeListener(file.getChangeListener());
			cleanup(observer);
		}
	}

	private void cleanup(FileAlterationObserver observer) {
		if(!observer.getListeners().iterator().hasNext())
			fam.removeObserver(observer);
	}

}
