package com.github.lobo.less.daemon.action;

import java.awt.FileDialog;
import java.awt.Frame;
import java.nio.file.Paths;

import com.github.lobo.less.daemon.FolderManager;
import com.google.inject.Inject;

public class AddFolderAction {

	private FolderManager folderManager;

	@Inject
	public AddFolderAction(FolderManager folderManager) {
		this.folderManager = folderManager;
	}

	public void execute() {
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