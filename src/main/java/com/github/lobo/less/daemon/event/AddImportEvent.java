package com.github.lobo.less.daemon.event;

import java.io.Serializable;

import com.github.lobo.less.daemon.model.LessFile;

@SuppressWarnings("serial")
public class AddImportEvent implements Serializable {

	private LessFile file;

	public AddImportEvent(LessFile file) {
		this.file = file;
	}
	
	public LessFile getFile() {
		return file;
	}

	public void setFile(LessFile file) {
		this.file = file;
	}

	public boolean isRoot() {
		return file.isRoot();
	}
	
	@Override
	public String toString() {
		return "AddImportEvent [file=" + file + "]";
	}

}
