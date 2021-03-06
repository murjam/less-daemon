package com.github.lobo.less.daemon;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.DirectoryStream.Filter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.lobo.less.daemon.event.AddFolderEvent;
import com.github.lobo.less.daemon.event.RemoveFolderEvent;
import com.github.lobo.less.daemon.model.LessFile;
import com.github.lobo.less.daemon.model.LessFolder;
import com.google.common.collect.Maps;
import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

@Singleton
public class FolderManager {

	private static final Logger logger = LoggerFactory.getLogger(FolderManager.class);
	
	private Map<String, LessFolder> folders = Maps.newHashMap();

	private static final String LESS_EXTENSION = "less";

	private static final Filter<Path> LESS_FILE_FILTER = new Filter<Path>() {
		@Override
		public boolean accept(Path entry) throws IOException {
			if (Files.isDirectory(entry))
				return false;
			return entry.toString().endsWith("." + LESS_EXTENSION);
		}
	};

	private EventBus eventBus;
	
	@Inject Provider<LessFile> lessFileProvider;

	@Inject
	public FolderManager(EventBus eventBus) {
		this.eventBus = eventBus;
	}

	private void processFolder(LessFolder folder) {
		Path folderPath = Paths.get(folder.getFilename());
		try (
			DirectoryStream<Path> stream = Files.newDirectoryStream(folderPath, LESS_FILE_FILTER)) {
			for (Path filePath : stream) {
				if(logger.isTraceEnabled())
					logger.trace("Adding file '{}' to folder '{}'", filePath.getFileName().toString(), folderPath.toString());
				LessFile lessFile = lessFileProvider.get();
				lessFile.initialize(filePath.toString(), folder);
			}
		} catch (IOException e) {
			logger.error("Error reading folder {}", folder.getFilename());
		}
	}

	public int getFolderCount() {
		return folders.size();
	}

	public void addFolder(String filename) {
		addFolder(filename, true);
	}

	public void addFolder(String filename, boolean store) {
		addFolder(new LessFolder(filename), store);
	}

	public void addFolder(LessFolder folder) {
		addFolder(folder, true);
	}

	public void addFolder(LessFolder folder, boolean store) {
		if (logger.isTraceEnabled())
			logger.trace("Adding LessFolder({}) - store: {}", folder.getFilename(), store);
		folders.put(folder.getFilename(), folder);
		processFolder(folder);
		eventBus.post(new AddFolderEvent(folder, store));
	}

	public void removeFolder(String filename) {
		removeFolder(filename, true);
	}

	private void removeFolder(String filename, boolean store) {
		if (!folders.containsKey(filename))
			return;
		LessFolder removed = folders.get(filename);
		if (logger.isDebugEnabled())
			logger.debug("Removing LessFolder({}) - store: {}", filename, store);
		folders.remove(filename);
		eventBus.post(new RemoveFolderEvent(removed, store));
	}

	public void removeFolder(LessFolder folder) {
		removeFolder(folder.getFilename());
	}

	public void removeFolder(LessFolder folder, boolean store) {
		removeFolder(folder.getFilename(), store);
	}

	public void addFolders(Set<LessFolder> folders) {
		for (LessFolder folder : folders)
			addFolder(folder, false);
	}

	public Collection<LessFolder> getFolderList() {
		return folders.values(); 
	}

}
