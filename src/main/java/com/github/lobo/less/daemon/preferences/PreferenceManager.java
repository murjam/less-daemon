package com.github.lobo.less.daemon.preferences;

import static com.github.lobo.less.daemon.preferences.PreferenceUtil.toFolder;
import static com.github.lobo.less.daemon.preferences.PreferenceUtil.toJson;
import static com.github.lobo.less.daemon.preferences.PreferenceUtil.toKey;

import java.io.IOException;
import java.util.Set;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.lobo.less.daemon.FolderManager;
import com.github.lobo.less.daemon.event.AddFolderEvent;
import com.github.lobo.less.daemon.event.RemoveFolderEvent;
import com.github.lobo.less.daemon.model.LessFolder;
import com.google.common.collect.Sets;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;

public class PreferenceManager {

	private Preferences preferences;

	private static final Logger logger = LoggerFactory.getLogger(PreferenceManager.class);

	private FolderManager folderManager;

	@Inject
	public PreferenceManager(EventBus eventBus, FolderManager folderManager) {
		this.folderManager = folderManager;
		// eventBus.register(this);
		preferences = Preferences.userNodeForPackage(FolderManager.class);
	}

	public Set<LessFolder> readPreferences() {
		Set<LessFolder> folders = Sets.newHashSet();
		try {
			boolean performSync = false;
			for (String key : preferences.keys()) {
				if (logger.isDebugEnabled())
					logger.debug("Found preferences key: {}", key);
				String json = preferences.get(key, null);
				if (json == null)
					preferences.remove(key);
				try {
					LessFolder folder = toFolder(json);
					folders.add(folder);
				} catch (IOException e) {
					if (logger.isWarnEnabled())
						logger.warn("Error parsing JSON for key {} (removing...): {}", key, e.getMessage());
					preferences.remove(key);
					performSync = true;
				}
			}

			if (performSync)
				preferences.sync();
		} catch (BackingStoreException e) {
			logger.error(e.getMessage());
		}
		
		return folders;
	}

	@Subscribe
	public void onAddFolder(AddFolderEvent event) {
		LessFolder folder = event.getFolder();
		String json = toJson(folder);
		preferences.put(toKey(folder), json);
		store();
	}

	@Subscribe
	public void onRemoveFolder(RemoveFolderEvent event) {
		LessFolder folder = event.getFolder();
		preferences.remove(toKey(folder));
		store();
	}

	private void store() {
		try {
			preferences.sync();
		} catch (BackingStoreException e) {
			logger.error(e.getMessage());
		}
	}

}
