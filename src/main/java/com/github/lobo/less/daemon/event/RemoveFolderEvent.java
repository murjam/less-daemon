package com.github.lobo.less.daemon.event;

import java.io.Serializable;

import com.github.lobo.less.daemon.model.LessFolder;
import com.google.common.base.Preconditions;

@SuppressWarnings("serial")
public class RemoveFolderEvent implements Serializable {

	private LessFolder folder;

	private boolean store;

	public RemoveFolderEvent(LessFolder folder, boolean store) {
		this.store = store;
		Preconditions.checkNotNull(folder, "The folder cannot be null for remove event");
		this.folder = folder;
	}

	public boolean isStore() {
		return store;
	}

	public void setStore(boolean store) {
		this.store = store;
	}

	public LessFolder getFolder() {
		return folder;
	}

	public void setFolder(LessFolder folder) {
		this.folder = folder;
	}

	@Override
	public String toString() {
		return "RemoveFolderEvent [folder=" + folder + ", store=" + store + "]";
	}

}
