package com.github.lobo.less.daemon.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.MenuItem;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;

import com.github.lobo.less.daemon.action.AddFolderAction;
import com.github.lobo.less.daemon.action.RemoveFolderAction;
import com.github.lobo.less.daemon.event.AddFolderEvent;
import com.github.lobo.less.daemon.model.LessFolder;
import com.github.lobo.less.daemon.preferences.PreferenceManager;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;

@SuppressWarnings("serial")
public class PreferencesProxy extends MenuItem implements ActionListener{

	private JFrame frame;
	
	PreferencePanel preferencePanel; 
	
	@Inject
	AddFolderAction addFolderAction;
	@Inject
	RemoveFolderAction removeFolderAction;

	private JButton buttonAdd;

	private JButton buttonRemove;

	private DefaultListModel<LessFolder> folderListModel;

	private JList<LessFolder> folderList;

	private EventBus eventBus;

	private PreferenceManager preferenceManager;
	
	@Inject
	public PreferencesProxy(EventBus eventBus, PreferenceManager preferenceManager) {
		super("Preferences...");
		this.eventBus = eventBus;
		this.preferenceManager = preferenceManager;
		eventBus.register(this);
		addActionListener(this);
	}

	private void init() {
		if(frame != null)
			return;
		
		frame = new JFrame("Less Compiler Daemon - Dependency Tree");
		Container contentPane = frame.getContentPane();
		contentPane.setLayout(new BorderLayout());
		
		preferencePanel = new PreferencePanel(preferenceManager, eventBus);
		contentPane.add(preferencePanel);

		Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
		int screenWidth = size.width;
		int width = screenWidth / 2;
		int screenHeight = size.height;
		int height = screenHeight / 2;
		
		frame.setSize(width, height);
		frame.setLocation((screenWidth-width)/2, (screenHeight-height)/2);
		
		buttonAdd = preferencePanel.getFolderManagerPanel().getButtonAdd();
		buttonRemove = preferencePanel.getFolderManagerPanel().getButtonRemove();
		folderListModel = preferencePanel.getFolderManagerPanel().getListModel();
		folderList = preferencePanel.getFolderManagerPanel().getList();

		setupUi();
	}

	private void setupUi() {
		buttonAdd.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				addFolderAction.execute();
			}
		});
		
		buttonRemove.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				for(LessFolder folder : folderList.getSelectedValuesList())
					removeFolderAction.execute(folder);
			}
		});
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		init();
		frame.setVisible(true);
	}
	
	@Subscribe
	public void onAddFolder(AddFolderEvent event) {
		
	}

}
