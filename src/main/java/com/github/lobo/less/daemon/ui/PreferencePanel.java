package com.github.lobo.less.daemon.ui;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.github.lobo.less.daemon.resources.Icons;
import com.github.lobo.less.daemon.ui.dep.DependencyPanel;
import com.google.common.eventbus.EventBus;
import com.google.inject.Singleton;

@SuppressWarnings("serial")
@Singleton
public class PreferencePanel extends JPanel {

	private FolderManagerPanel folderManagerPanel;
	private DependencyPanel dependencyPanel;

	public PreferencePanel(EventBus eventBus) {
		eventBus.register(this);
		folderManagerPanel = new FolderManagerPanel(eventBus);
		dependencyPanel = new DependencyPanel(eventBus);
		initUi();
	}

	public PreferencePanel() {
		folderManagerPanel = new FolderManagerPanel();
		dependencyPanel = new DependencyPanel();
		initUi();
	}

	private void initUi() {
		setLayout(new BorderLayout(0, 0));

		JTabbedPane tabs = new JTabbedPane(JTabbedPane.TOP);
		add(tabs);

		tabs.addTab("Folder Manager", Icons.FOLDER_ICON, folderManagerPanel, null);
		tabs.addTab("Dependency Viewer", Icons.TREE_ICON, dependencyPanel, null);
	}

	public FolderManagerPanel getFolderManagerPanel() {
		return folderManagerPanel;
	}

}
