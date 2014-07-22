package com.github.lobo.less.daemon.preferences;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.lobo.less.daemon.FolderManager;
import com.github.lobo.less.daemon.Main;
import com.github.lobo.less.daemon.event.AddFolderEvent;
import com.github.lobo.less.daemon.event.PreferenceChangeEvent;
import com.github.lobo.less.daemon.event.RemoveFolderEvent;
import com.github.lobo.less.daemon.less.Less;
import com.github.lobo.less.daemon.model.LessFolder;
import com.github.lobo.less.daemon.ui.Dialogs;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class PreferenceManager {

	private Preferences preferences;
	
	public static final String KEY_FOLDER_LIST = "folder.list";

	public static final String KEY_LESSC_PATH = "lessc.path";

	public static final String KEY_LESSC_OPTIONS = "lessc.options";
	
	public static final String KEY_OUTPUT_OPTION = "output.option";

	public static final String KEY_OUTPUT_PATH = "output.path";

	private static final Logger logger = LoggerFactory.getLogger(PreferenceManager.class);
	
	private Set<String> folderSet = Sets.newHashSet();
	
	private boolean wasUserWarned = false;

	@Inject
	public PreferenceManager(EventBus eventBus) {
		eventBus.register(this);
		preferences = Preferences.userNodeForPackage(FolderManager.class);
	}
	
	public String getLesscPath() {
		String path = preferences.get(KEY_LESSC_PATH, null);
		if(Strings.isNullOrEmpty(path)) {
			path = checkLesscPath();
			store();
		}
		return path;
	}

	public String checkLesscPath() {
		String path = Main.findInPath("lessc");
		if(path == null && !wasUserWarned) {
			String nodePath = Main.findInPath("node");
			String nodeWarning = "";
			if(nodePath == null)
				nodeWarning = "Also could not find 'node' executable - This can cause problems.\n";
			Dialogs.showError(
				"Could not find 'lessc' executable.\n" +
				nodeWarning +
				"Searched in:\n" +
				Joiner.on('\n').join(Main.paths()) + "\n" +
				"Please set 'lessc' path in Preferences",
				"WARNING: Cannot find lessc"
			);
			wasUserWarned = true;
		}

		return path;
	}

	public String getLesscOptions() {
		return preferences.get(KEY_LESSC_OPTIONS, Less.DEFAULT_LESSC_OPTIONS);
	}

	public String getOutputOption() {
		return preferences.get(KEY_OUTPUT_OPTION, Less.DEFAULT_OUTPUT_OPTION);
	}
	
	public String getOutputPath() {
		return preferences.get(KEY_OUTPUT_PATH, ".");
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
		storeFolderList();
	}

	@Subscribe
	public void onRemoveFolder(RemoveFolderEvent event) {
		if(!event.isStore())
			return;
		
		folderSet.remove(event.getFolder().getFilename());
		storeFolderList();
	}
	
	@Subscribe
	public void onPreferenceChange(PreferenceChangeEvent event) {
		if(logger.isDebugEnabled())
			logger.debug("Preference '{}' changed to '{}'", event.getKey(), event.getNewValue());
		preferences.put(event.getKey(), event.getNewValue());
		store();
	}
	
	private void storeFolderList() {
		String json = PreferenceUtil.toJson(folderSet);
		preferences.put(KEY_FOLDER_LIST, json);
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
