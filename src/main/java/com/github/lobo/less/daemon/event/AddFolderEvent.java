package com.github.lobo.less.daemon.event;

import java.io.Serializable;

import com.github.lobo.less.daemon.model.LessFolder;
import com.google.common.base.Preconditions;

@SuppressWarnings("serial")
public class AddFolderEvent implements Serializable {

	private LessFolder folder;
	private boolean store;

	public AddFolderEvent(LessFolder folder, boolean store) {
		Preconditions.checkNotNull(folder, "The folder cannot be null for add event");
		this.folder = folder;
		this.store = store;
	}

	public LessFolder getFolder() {
		return folder;
	}

	public void setFolder(LessFolder folder) {
		this.folder = folder;
	}

	public boolean isStore() {
		return store;
	}

	public void setStore(boolean store) {
		this.store = store;
	}

	@Override
	public String toString() {
		return "AddFolderEvent [folder=" + folder + ", store=" + store + "]";
	}

}
