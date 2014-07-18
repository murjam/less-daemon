package com.github.lobo.less.daemon.action;

import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.MenuItem;
import java.awt.MenuShortcut;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.nio.file.Paths;

import com.github.lobo.less.daemon.FolderManager;
import com.google.inject.Inject;

@SuppressWarnings("serial")
public class AddFolderItem extends MenuItem implements ActionListener {

	private FolderManager folderManager;
	
	@Inject
	public AddFolderItem(FolderManager folderManager) {
		super("Add Folder", new MenuShortcut(KeyEvent.VK_A));
		this.folderManager = folderManager;
		addActionListener(this);
	}

	public void actionPerformed(ActionEvent e) {
		System.setProperty("apple.awt.fileDialogForDirectories", "true");
		FileDialog dialog = new FileDialog((Frame) null);
		dialog.setVisible(true);
		System.setProperty("apple.awt.fileDialogForDirectories", "false");
		if (dialog.getFile() != null) {
			String filename = Paths.get(dialog.getDirectory(), dialog.getFile()).toAbsolutePath().toString();
			folderManager.addFolder(filename);
		}
	}

}