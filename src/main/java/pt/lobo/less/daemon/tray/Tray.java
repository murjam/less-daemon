package pt.lobo.less.daemon.tray;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.lobo.less.daemon.FolderManager;
import pt.lobo.less.daemon.action.AddFolderItem;
import pt.lobo.less.daemon.action.ExitItem;
import pt.lobo.less.daemon.event.AddFolderEvent;
import pt.lobo.less.daemon.event.ExitEvent;
import pt.lobo.less.daemon.event.RemoveFolderEvent;
import pt.lobo.less.daemon.event.TrayReadyEvent;
import pt.lobo.less.daemon.types.LessFolder;

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
	
	private MenuItem noFolderItem = new MenuItem("No folders...") {
		{
			setEnabled(false);
		}
	};
	
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
			mainMenu.add(noFolderItem);
			mainMenu.addSeparator();
			mainMenu.add(exitItem);
			
			eventBus.post(new TrayReadyEvent(this));
		} catch (AWTException e) {
			logger.error("Cannot add tray icon: " + e.getMessage(), e);
			eventBus.post(new ExitEvent(1));
		}			
		
	}

	@Subscribe
	public void onAddFolder(AddFolderEvent event) {
		addFolder(event.getFolder());
	}
	
	public void addFolder(LessFolder folder) {
		if(folderManager.getFolderCount() != 0)
			mainMenu.remove(noFolderItem);
		else
			mainMenu.add(noFolderItem);
		createFolderEntry(folder);
	}
	
	@Subscribe
	public void onFolderRemove(RemoveFolderEvent event) {
		PopupMenu menu = folderMenus.get(event.getFolder());
		mainMenu.remove(menu);
		folderMenus.remove(event.getFolder());
	}
	
	private void createFolderEntry(final LessFolder folder) {
		final PopupMenu menu = new PopupMenu(folder.getFilename());
		MenuItem removeItem = new MenuItem("Remove Folder");
		removeItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				folderManager.removeFolder(folder);
			}
		});
		menu.add(removeItem);
		mainMenu.insert(menu, folderMenus.size() + 2);
		
		folderMenus.put(folder, menu);
	}

	public PopupMenu getPopupMenu() {
		return mainMenu;
	}

	public SystemTray getTray() {
		return tray;
	}

}
