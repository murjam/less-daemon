package com.github.lobo.less.daemon.tray;

import java.awt.AWTException;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;

import javax.swing.UIManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.lobo.less.daemon.action.ExitItem;
import com.github.lobo.less.daemon.event.ExitEvent;
import com.github.lobo.less.daemon.event.TrayReadyEvent;
import com.github.lobo.less.daemon.resources.Icons;
import com.github.lobo.less.daemon.ui.PreferencesProxy;
import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;

public class Tray implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger(Tray.class);
	
	private EventBus eventBus;

	private PopupMenu mainMenu;

	private TrayIcon trayIcon;

	private SystemTray tray;
	
	@Inject ExitItem exitItem;
	@Inject PreferencesProxy preferencesItem;
	
	@Inject
	public Tray(EventBus eventBus) {
		setLaf();
		this.eventBus = eventBus;
		eventBus.register(this);
		
		tray = SystemTray.getSystemTray();
		mainMenu = new PopupMenu();
		trayIcon = new TrayIcon(Icons.LESS_IMAGE, "Less Compiler Daemon", mainMenu);
		
	}
	
	private void setLaf() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			// not that important
		}
	}
	
	public void run() {
		try {
			tray.add(trayIcon);
			
			mainMenu.add(preferencesItem);
			mainMenu.addSeparator();
			mainMenu.add(exitItem);
			
			eventBus.post(new TrayReadyEvent(this));
		} catch (AWTException e) {
			logger.error("Cannot add tray icon: " + e.getMessage(), e);
			eventBus.post(new ExitEvent(1));
		}			
		
	}
	
	public PopupMenu getPopupMenu() {
		return mainMenu;
	}

	public SystemTray getTray() {
		return tray;
	}

}
