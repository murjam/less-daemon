package com.github.lobo.less.daemon.ui;

import java.awt.MenuItem;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import com.github.lobo.less.daemon.Constants;
import com.github.lobo.less.daemon.FolderManager;
import com.github.lobo.less.daemon.event.ExitEvent;
import com.github.lobo.less.daemon.event.NeedsCompileEvent;
import com.github.lobo.less.daemon.model.LessFolder;
import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

@Singleton
@SuppressWarnings("serial")
public class Actions {

	@Inject Provider<PreferencePanel> preferencePanel;
	@Inject Provider<EventPanel> eventPanel;

	@Inject FolderManager folderManager;
	@Inject EventBus eventBus;

	private AbstractAction openPreferencesAction;
	private AbstractAction compileAllAction;
	private AbstractAction quitAction;
	private AbstractAction eventAction;

	public Actions() {
	}

	public Action openPreferencesAction() {
		if (openPreferencesAction == null) {
			openPreferencesAction = new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Dialogs.wrap(preferencePanel.get(), Constants.APP_NAME + " - Preferences").setVisible(true);
				}
			};
		}
		return openPreferencesAction;
	}

	public MenuItem openPreferencesMenuItem() {
		MenuItem item = new MenuItem("Preferences...");
		item.addActionListener(openPreferencesAction());
		return item;
	}

	public Action compileAllAction() {
		if (compileAllAction == null) {
			compileAllAction = new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent e) {
					for (LessFolder folder : folderManager.getFolderList())
						eventBus.post(new NeedsCompileEvent(folder.getFiles()));
				}
			};
		}
		return compileAllAction;
	}

	public MenuItem compileAllMenuItem() {
		MenuItem item = new MenuItem("Compile all");
		item.addActionListener(compileAllAction());
		return item;
	}

	private Action quitAction() {
		if (quitAction == null) {
			quitAction = new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent e) {
					eventBus.post(new ExitEvent(0));
				}
			};
		}
		return quitAction;
	}

	public MenuItem quitMenuItem() {
		MenuItem item = new MenuItem("Quit");
		item.addActionListener(quitAction());
		return item;
	}

	public Action eventAction() {
		if (eventAction == null) {
			eventAction = new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Dialogs.wrap(eventPanel.get(), Constants.APP_NAME + " - Dependency Panel").setVisible(true);
				}
			};
		}
		return eventAction;
	}
	
	public MenuItem eventMenuItem() {
		MenuItem item = new MenuItem("Depencencies/Events");
		item.addActionListener(eventAction());
		return item;
	}

}
