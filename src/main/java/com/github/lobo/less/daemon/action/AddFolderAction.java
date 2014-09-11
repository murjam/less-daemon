package com.github.lobo.less.daemon.action;

import java.io.File;

import com.github.lobo.less.daemon.FolderManager;
import com.github.lobo.less.daemon.ui.Dialogs;
import com.google.inject.Inject;

public class AddFolderAction {

	private FolderManager folderManager;

	@Inject
	public AddFolderAction(FolderManager folderManager) {
		this.folderManager = folderManager;
	}

	public void execute() {
		Dialogs.chooseDirectory(new Dialogs.FileDialogAdapter() {
			@Override
			public void selected(File file) {
				String path;
				if (file.isFile()) {
					path = file.getParentFile().getAbsolutePath();
					System.out.println("Siin! " + path);
				}
				else {
					path = file.getAbsolutePath();
				}
				folderManager.addFolder(path);
			}
		});
	}

}