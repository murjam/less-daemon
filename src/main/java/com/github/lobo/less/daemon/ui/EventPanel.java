package com.github.lobo.less.daemon.ui;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.LayoutStyle.ComponentPlacement;

import com.github.lobo.less.daemon.FolderManager;
import com.github.lobo.less.daemon.resources.Icons;
import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.google.inject.Provider;

@SuppressWarnings("serial")
public class EventPanel extends JPanel {

	@Inject
	public EventPanel(EventBus eventBus, FolderManager folderManager, Provider<DepencencyTree> dependencyTreeProvider, Provider<EventLogPanel> eventLogPanelProvider) {
		eventBus.register(this);

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);

		// @formatter:off
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(Alignment.LEADING, groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(tabbedPane, GroupLayout.DEFAULT_SIZE, 669, Short.MAX_VALUE)
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(tabbedPane, GroupLayout.DEFAULT_SIZE, 451, Short.MAX_VALUE)
					.addContainerGap())
		);
		
		JScrollPane treeScrollPane = new JScrollPane();
		tabbedPane.addTab("Dependency Viewer", Icons.TREE_ICON, treeScrollPane, null);
		// @formatter:on

		DepencencyTree dependencyTree = dependencyTreeProvider.get();
		treeScrollPane.setViewportView(dependencyTree);

		JPanel eventPanel = new JPanel();
		tabbedPane.addTab("Event Log", Icons.LESS_ICON, eventPanel, null);

		JButton btnClear = new JButton("Clear");

		JScrollPane eventScrollPane = new JScrollPane();

		JLabel lblEventLog_1 = new JLabel("Event Log");
		GroupLayout gl_eventPanel = new GroupLayout(eventPanel);
		gl_eventPanel.setHorizontalGroup(gl_eventPanel.createParallelGroup(Alignment.LEADING).addGroup(
			gl_eventPanel
				.createSequentialGroup()
				.addContainerGap()
				.addGroup(
					gl_eventPanel
						.createParallelGroup(Alignment.LEADING)
						.addGroup(Alignment.TRAILING, gl_eventPanel.createSequentialGroup().addComponent(btnClear).addContainerGap())
						.addGroup(
							Alignment.TRAILING,
							gl_eventPanel
								.createSequentialGroup()
								.addGroup(
									gl_eventPanel.createParallelGroup(Alignment.TRAILING)
										.addComponent(lblEventLog_1, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 636, Short.MAX_VALUE)
										.addComponent(eventScrollPane, GroupLayout.DEFAULT_SIZE, 144, Short.MAX_VALUE)).addContainerGap()))));
		gl_eventPanel.setVerticalGroup(gl_eventPanel.createParallelGroup(Alignment.LEADING).addGroup(
			gl_eventPanel.createSequentialGroup().addContainerGap().addComponent(lblEventLog_1, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE)
				.addPreferredGap(ComponentPlacement.RELATED).addComponent(eventScrollPane, GroupLayout.DEFAULT_SIZE, 94, Short.MAX_VALUE)
				.addPreferredGap(ComponentPlacement.RELATED).addComponent(btnClear).addContainerGap()));

		EventLogPanel eventLogPanel = eventLogPanelProvider.get();
		eventScrollPane.setViewportView(eventLogPanel);
		eventPanel.setLayout(gl_eventPanel);
		setLayout(groupLayout);

	}

}
