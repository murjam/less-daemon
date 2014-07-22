package com.github.lobo.less.daemon.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import com.github.lobo.less.daemon.event.LessLogEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@SuppressWarnings("serial")
@Singleton
public class EventLogList extends JList<LessLogEvent> {
	
	@Inject Actions actions;

	class CellRenderer extends EventLogItem implements ListCellRenderer<LessLogEvent> {

		@Override
		public Component getListCellRendererComponent(JList<? extends LessLogEvent> list, LessLogEvent value, int index, boolean isSelected, boolean cellHasFocus) {
			setText(value.getText());
			setIcon(value.getIcon());
			setMessage(value.getMessage());
			setEnabled(list.isEnabled());
			setFont(list.getFont());
			
			if(isSelected)
				setBackground(Color.lightGray);
			else
				setBackground(list.getBackground());
			
			return this;
		}
	};

	private DefaultListModel<LessLogEvent> listModel = new DefaultListModel<LessLogEvent>();

	@Inject
	public EventLogList(EventBus eventBus) {
		eventBus.register(this);
		setModel(listModel);
		setCellRenderer(new CellRenderer());
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				setFixedCellHeight(10);
				setFixedCellHeight(-1);
			}
		});
	}

	@Subscribe
	public void onLessLogEvent(LessLogEvent event) {
		listModel.addElement(event);
	}

	public void clear() {
		listModel.clear();
	}

	public String getMessage() {
		LessLogEvent value = getSelectedValue();
		if(value == null)
			return null;
		return value.getMessage();
	}

}
