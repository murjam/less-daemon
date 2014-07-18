package com.github.lobo.less.daemon.tray;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuContainer;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.lobo.less.daemon.FolderManager;
import com.github.lobo.less.daemon.action.AddFolderItem;
import com.github.lobo.less.daemon.action.ExitItem;
import com.github.lobo.less.daemon.event.AddFileEvent;
import com.github.lobo.less.daemon.event.AddFolderEvent;
import com.github.lobo.less.daemon.event.ExitEvent;
import com.github.lobo.less.daemon.event.RemoveFolderEvent;
import com.github.lobo.less.daemon.event.TrayReadyEvent;
import com.github.lobo.less.daemon.model.LessContainer;
import com.github.lobo.less.daemon.model.LessFile;
import com.github.lobo.less.daemon.model.LessFolder;
import com.google.common.collect.Maps;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;

@SuppressWarnings("serial")
public class Tray implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger(Tray.class);
	
	private static final String ICON = "less_icon.png";
	
	private EventBus eventBus;

	private PopupMenu mainMenu;

	private TrayIcon trayIcon;

	private SystemTray tray;
	
	@Inject ExitItem exitItem;
	@Inject AddFolderItem addFolderItem;
	@Inject FolderManager folderManager;
	
	Map<LessFolder, PopupMenu> folderMenus = Maps.newHashMap();
	Map<Path, PopupMenu> pathMenus = Maps.newHashMap();
	
	private MenuItem noFolderItem = new MenuItem("No folders...") {
		{
			setEnabled(false);
		}
	};
	
	PopupMenu folderListMenu = new PopupMenu("Folders");
	
	@Inject
	public Tray(EventBus eventBus) {
		this.eventBus = eventBus;
		eventBus.register(this);
		
		Image image = Toolkit.getDefaultToolkit().getImage(Tray.class.getResource(ICON));

		tray = SystemTray.getSystemTray();
		mainMenu = new PopupMenu();
		trayIcon = new TrayIcon(image, "Less Compiler Daemon", mainMenu);
	}
	
	public void run() {
		try {
			tray.add(trayIcon);
			
			mainMenu.add(addFolderItem);
			mainMenu.addSeparator();
			
			mainMenu.add(folderListMenu);
			if(folderManager.getFolderCount() == 0)
				addNoFolderItem();
			
			mainMenu.addSeparator();
			mainMenu.add(exitItem);
			
			eventBus.post(new TrayReadyEvent(this));
		} catch (AWTException e) {
			logger.error("Cannot add tray icon: " + e.getMessage(), e);
			eventBus.post(new ExitEvent(1));
		}			
		
	}
	
	@Subscribe
	public void onAddFile(AddFileEvent event) {
		LessFile file = event.getFile();
		addFile(file);
	}

	private void addFile(LessFile file) {
		PopupMenu menu = getParent(file);
		if(menu != null) {
			String filename = file.getFilename();
			PopupMenu fileMenu = new PopupMenu(filename);
			menu.add(fileMenu);
			pathMenus.put(Paths.get(filename), fileMenu);
		}
	}

	private PopupMenu getParent(LessContainer container) {
		Path path = Paths.get(container.getParent().getFilename());
		return pathMenus.get(path);
	}

	@Subscribe
	public void onAddFolder(AddFolderEvent event) {
		createFolderEntry(event.getFolder());
	}

	@Subscribe
	public void onFolderRemove(RemoveFolderEvent event) {
		removeFolderEntry(event.getFolder());
	}

	private void removeFolderEntry(LessFolder folder) {
		PopupMenu menu = folderMenus.get(folder);
		MenuContainer parent = menu.getParent();
		if(parent != null)
			parent.remove(menu);
		
		folderMenus.remove(folder);
	
		if(folderManager.getFolderCount() == 0)
			addNoFolderItem();
	}

	private void removeNoFolderItem() {
		MenuContainer parent = noFolderItem.getParent();
		if(parent != null)
			parent.remove(noFolderItem);
	}
	
	private void addNoFolderItem() {
		folderListMenu.add(noFolderItem);
	}
	
	public void createFolderEntry(final LessFolder folder) {
		removeNoFolderItem();
		
		String foldername = folder.getFilename();
		final PopupMenu menu = new PopupMenu(foldername);
		MenuItem removeItem = new MenuItem("Remove Folder");
		removeItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				folderManager.removeFolder(folder);
			}
		});
		menu.add(removeItem);
		folderListMenu.add(menu);
		
		folderMenus.put(folder, menu);
		pathMenus.put(Paths.get(foldername), menu);
		
		walkFiles(folder);
	}

	private void walkFiles(LessContainer container) {
		for(LessFile file : container.getFiles()) {
			addFile(file);
			if(!file.getFiles().isEmpty())
				walkFiles(file);
		}
	}

	public PopupMenu getPopupMenu() {
		return mainMenu;
	}

	public SystemTray getTray() {
		return tray;
	}

}
