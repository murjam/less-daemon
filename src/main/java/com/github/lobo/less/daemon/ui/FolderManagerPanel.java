package com.github.lobo.less.daemon.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ListCellRenderer;

import com.github.lobo.less.daemon.FolderManager;
import com.github.lobo.less.daemon.action.AddFolderAction;
import com.github.lobo.less.daemon.action.RemoveFolderAction;
import com.github.lobo.less.daemon.event.AddFolderEvent;
import com.github.lobo.less.daemon.event.RemoveFolderEvent;
import com.github.lobo.less.daemon.model.LessFolder;
import com.github.lobo.less.daemon.resources.Icons;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;

@SuppressWarnings("serial")
public class FolderManagerPanel extends JPanel {

	private JList<LessFolder> folderList;
	private JButton buttonAdd;
	private JButton buttonRemove;
	private DefaultListModel<LessFolder> listModel;

	class FolderListRenderer extends JLabel implements ListCellRenderer<LessFolder> {

		public FolderListRenderer() {
			setOpaque(true);
			setIcon(Icons.FOLDER_ICON);
			setHorizontalAlignment(LEFT);
			setVerticalAlignment(CENTER);
		}

		@Override
		public Component getListCellRendererComponent(JList<? extends LessFolder> list, LessFolder value, int index, boolean isSelected, boolean cellHasFocus) {
			setText(value.getFilename());

			setComponentOrientation(list.getComponentOrientation());

			if (isSelected) {
				setBackground(Color.lightGray);
			} else {
				setBackground(list.getBackground());
			}

			setEnabled(list.isEnabled());
			setFont(list.getFont());

			return this;
		}

	}

	@Inject
	public FolderManagerPanel(EventBus eventBus, final FolderManager folderManager, final AddFolderAction addFolderAction, final RemoveFolderAction removeFolderAction) {
		eventBus.register(this);

		buttonAdd = new JButton("Add");
		buttonAdd.setIcon(Icons.ADD_FOLDER_ICON);

		buttonRemove = new JButton("Remove");
		buttonRemove.setIcon(Icons.REMOVE_FOLDER_ICON);

		JSeparator separator = new JSeparator();

		JScrollPane scrollPane = new JScrollPane();

		// @formatter:off
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addComponent(scrollPane, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 438, Short.MAX_VALUE)
						.addComponent(separator, GroupLayout.DEFAULT_SIZE, 438, Short.MAX_VALUE)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(buttonAdd)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(buttonRemove)))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(buttonRemove)
						.addComponent(buttonAdd))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(separator, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 236, Short.MAX_VALUE)
					.addContainerGap())
		);
		// @formatter:on

		listModel = new DefaultListModel<LessFolder>();
		folderList = new JList<LessFolder>(listModel);
		scrollPane.setViewportView(folderList);
		folderList.setModel(listModel);
		folderList.setCellRenderer(new FolderListRenderer());
		setLayout(groupLayout);

		buttonAdd.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if(addFolderAction == null)
					return;
				
				addFolderAction.execute();
			}
		});

		buttonRemove.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if(removeFolderAction == null)
					return;
				
				for (LessFolder folder : folderList.getSelectedValuesList())
					removeFolderAction.execute(folder);
			}
		});
		
		if(folderManager != null)
			for(LessFolder folder : folderManager.getFolderList())
				addFolder(folder);
	}

	public void addFolder(LessFolder folder) {
		listModel.addElement(folder);
	}

	public void removeFolder(LessFolder folder) {
		listModel.removeElement(folder);
	}

	@Subscribe
	public void onAddFolder(AddFolderEvent event) {
		addFolder(event.getFolder());
	}

	@Subscribe
	public void onRemoveFolder(RemoveFolderEvent event) {
		removeFolder(event.getFolder());
	}
}
