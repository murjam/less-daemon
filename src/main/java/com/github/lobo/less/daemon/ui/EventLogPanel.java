package com.github.lobo.less.daemon.ui;

import javax.swing.JPanel;
import javax.swing.JTable;

import com.google.common.eventbus.EventBus;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;

@SuppressWarnings("serial")
public class EventLogPanel extends JPanel {
	private JTable table;

	public EventLogPanel(EventBus eventBus) {
		eventBus.register(this);
		initUi();
	}
	
	public EventLogPanel() {
		initUi();
	}

	private void initUi() {
		
		table = new JTable();
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(table, GroupLayout.DEFAULT_SIZE, 438, Short.MAX_VALUE)
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(Alignment.LEADING, groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(table, GroupLayout.DEFAULT_SIZE, 288, Short.MAX_VALUE)
					.addContainerGap())
		);
		setLayout(groupLayout);
	}
}
