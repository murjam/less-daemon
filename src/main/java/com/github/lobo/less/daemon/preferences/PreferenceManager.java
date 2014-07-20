package com.github.lobo.less.daemon.preferences;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.lobo.less.daemon.FolderManager;
import com.github.lobo.less.daemon.event.AddFolderEvent;
import com.github.lobo.less.daemon.event.RemoveFolderEvent;
import com.github.lobo.less.daemon.less.Less;
import com.github.lobo.less.daemon.model.LessFolder;
import com.google.common.collect.Sets;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;

public class PreferenceManager {

	private Preferences preferences;
	
	public static final String KEY_FOLDER_LIST = "folder.list";

	public static final String KEY_LESSC_PATH = "lessc.path";

	public static final String KEY_LESSC_OPTIONS = "lessc.options";
	
	public static final String KEY_OUTPUT_PATH = "output.path";

	private static final Logger logger = LoggerFactory.getLogger(PreferenceManager.class);
	
	private Set<String> folderSet = Sets.newHashSet();

	@Inject
	public PreferenceManager(EventBus eventBus) {
		eventBus.register(this);
		preferences = Preferences.userNodeForPackage(FolderManager.class);
	}
	
	public String getLesscPath() {
		return preferences.get(KEY_LESSC_PATH, Less.DEFAULT_LESSC_PATH);
	}

	public String getLesscOptions() {
		return preferences.get(KEY_LESSC_OPTIONS, Less.DEFAULT_LESSC_OPTIONS);
	}

	public String getOutputOption() {
		return preferences.get(KEY_OUTPUT_PATH, Less.OutputOption.PARENT_CSS.name());
	}
	
	public Set<LessFolder> readFolderSet() throws IOException {
		String json = preferences.get(KEY_FOLDER_LIST, "[]");
		List<String> folderPahts = PreferenceUtil.fromJson(json);
		
		folderSet.clear();
		Set<LessFolder> lessFolderSet = Sets.newHashSet();
		for(String filename : folderPahts) {
			lessFolderSet.add(new LessFolder(filename));
			folderSet.add(filename);
		}
		
		return lessFolderSet;
	}

	@Subscribe
	public void onAddFolder(AddFolderEvent event) {
		if(!event.isStore())
			return;
		
		folderSet.add(event.getFolder().getFilename());
		store();
	}

	@Subscribe
	public void onRemoveFolder(RemoveFolderEvent event) {
		if(!event.isStore())
			return;
		
		folderSet.remove(event.getFolder().getFilename());
		store();
	}

	private void store() {
		try {
			String json = PreferenceUtil.toJson(folderSet);
			preferences.put(KEY_FOLDER_LIST, json);
			preferences.sync();
		} catch (BackingStoreException e) {
			logger.error(e.getMessage());
		}
	}


}
