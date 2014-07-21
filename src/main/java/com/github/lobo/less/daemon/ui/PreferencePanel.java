package com.github.lobo.less.daemon.ui;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.github.lobo.less.daemon.resources.Icons;
import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@SuppressWarnings("serial")
@Singleton
public class PreferencePanel extends JPanel {

	@Inject
	public PreferencePanel(EventBus eventBus, FolderManagerPanel folderManagerPanel, PreferenceManagerPanel preferenceManagerPanel) {
		eventBus.register(this);
		setLayout(new BorderLayout(0, 0));
		JTabbedPane tabs = new JTabbedPane(JTabbedPane.TOP);
		add(tabs);

		tabs.addTab("Folder Manager", Icons.FOLDER_ICON, folderManagerPanel, null);
		tabs.addTab("Options", Icons.LESS_ICON, preferenceManagerPanel, null);
	}

}
