package com.github.lobo.less.daemon.action;

import com.github.lobo.less.daemon.FolderManager;
import com.github.lobo.less.daemon.model.LessFolder;
import com.google.inject.Inject;

public class RemoveFolderAction {

	private FolderManager folderManager;

	@Inject
	public RemoveFolderAction(FolderManager folderManager) {
		this.folderManager = folderManager;
	}

	public void execute(LessFolder folder) {
		folderManager.removeFolder(folder);
	}

}