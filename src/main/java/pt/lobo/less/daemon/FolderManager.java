package pt.lobo.less.daemon;

import java.util.Set;
import java.util.prefs.Preferences;

import pt.lobo.less.daemon.event.AddFolderEvent;
import pt.lobo.less.daemon.event.RemoveFolderEvent;
import pt.lobo.less.daemon.types.LessFolder;

import com.google.common.collect.Sets;
import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class FolderManager {

	private Set<LessFolder> folders = Sets.newHashSet();
	
	private EventBus eventBus;
	
	private Preferences preferences;
	
	private static final String KEY_FOLDER_LIST = "folders";
	
	@Inject
	public FolderManager(EventBus eventBus) {
		this.eventBus = eventBus;
		preferences = Preferences.userNodeForPackage(FolderManager.class);
		loadPreferences();
	}
	
	private void loadPreferences() {
		
	}

	public int getFolderCount() {
		return folders.size();
	}
	
	public void addFolder(String filename) {
		LessFolder folder = new LessFolder(filename);
		processFolder(folder);
		if(folders.add(folder))
			eventBus.post(new AddFolderEvent(folder));
	}
	
	private void processFolder(LessFolder folder) {
		
	}

	public void removeFolder(LessFolder folder) {
		if(folders.remove(folder))
			eventBus.post(new RemoveFolderEvent(folder));
	}
	
}
