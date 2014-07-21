package com.github.lobo.less.daemon.ui;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.github.lobo.less.daemon.preferences.PreferenceManager;
import com.github.lobo.less.daemon.resources.Icons;
import com.github.lobo.less.daemon.ui.dep.DependencyPanel;
import com.google.common.eventbus.EventBus;
import com.google.inject.Singleton;

@SuppressWarnings("serial")
@Singleton
public class PreferencePanel extends JPanel {

	private FolderManagerPanel folderManagerPanel;
	private DependencyPanel dependencyPanel;
	private PreferenceManagerPanel preferenceManagerPanel;

	public PreferencePanel(PreferenceManager preferenceManager, EventBus eventBus) {
		eventBus.register(this);
		folderManagerPanel = new FolderManagerPanel(eventBus);
		dependencyPanel = new DependencyPanel(eventBus);
		preferenceManagerPanel = new PreferenceManagerPanel(preferenceManager, eventBus);
		initUi();
	}

	public PreferencePanel() {
		folderManagerPanel = new FolderManagerPanel();
		dependencyPanel = new DependencyPanel();
		preferenceManagerPanel = new PreferenceManagerPanel();
		preferenceManagerPanel = new PreferenceManagerPanel();
		initUi();
	}

	private void initUi() {
		setLayout(new BorderLayout(0, 0));

		JTabbedPane tabs = new JTabbedPane(JTabbedPane.TOP);
		add(tabs);

		tabs.addTab("Folder Manager", Icons.FOLDER_ICON, folderManagerPanel, null);
		tabs.addTab("Dependency Viewer", Icons.TREE_ICON, dependencyPanel, null);
		tabs.addTab("Options", Icons.LESS_ICON, preferenceManagerPanel, null);
	}

	public FolderManagerPanel getFolderManagerPanel() {
		return folderManagerPanel;
	}

}
