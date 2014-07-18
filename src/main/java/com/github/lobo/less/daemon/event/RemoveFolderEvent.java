package com.github.lobo.less.daemon.event;

import java.io.Serializable;

import com.github.lobo.less.daemon.model.LessFolder;
import com.google.common.base.Preconditions;

@SuppressWarnings("serial")
public class RemoveFolderEvent implements Serializable {

	private LessFolder folder;

	public RemoveFolderEvent(LessFolder folder) {
		Preconditions.checkNotNull(folder, "The folder cannot be null for remove event");
		this.folder = folder;
	}

	public LessFolder getFolder() {
		return folder;
	}

	public void setFolder(LessFolder folder) {
		this.folder = folder;
	}

	@Override
	public String toString() {
		return "AddFolderEvent [folder=" + folder + "]";
	}

}
