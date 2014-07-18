package com.github.lobo.less.daemon.event;

import java.io.Serializable;

import com.google.common.base.Preconditions;

@SuppressWarnings("serial")
public class FileChangedEvent implements Serializable {

	private String filename;

	public FileChangedEvent(String filename) {
		Preconditions.checkNotNull(filename, "The filename cannot be null for file change event");
		this.filename = filename;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String folder) {
		this.filename = folder;
	}

	@Override
	public String toString() {
		return "FileChangedEvent [filename=" + filename + "]";
	}

}
