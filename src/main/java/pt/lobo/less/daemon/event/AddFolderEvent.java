package pt.lobo.less.daemon.event;

import java.io.Serializable;

import pt.lobo.less.daemon.types.LessFolder;

@SuppressWarnings("serial")
public class AddFolderEvent implements Serializable {

	private LessFolder folder;

	public AddFolderEvent(LessFolder folder) {
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
