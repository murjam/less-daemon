package com.github.lobo.less.daemon.model;

import java.io.Serializable;
import java.util.List;

import com.beust.jcommander.internal.Lists;
import com.fasterxml.jackson.annotation.JsonIgnore;

@SuppressWarnings("serial")
public class LessFolder implements Serializable, LessContainer {

	private String filename;

	@JsonIgnore
	private List<LessFile> files = Lists.newArrayList();

	public LessFolder() {}

	public LessFolder(String filename) {
		this.filename = filename;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T extends LessContainer> T as() {
		return (T) this;
	}

	@Override
	public void addFile(LessFile file) {
		getFiles().add(file);
	}
	
	@Override
	public void removeFile(LessFile file) {
		getFiles().remove(file);
	}

	@Override
	public boolean isFile() {
		return false;
	}
	
	@Override
	public boolean isFolder() {
		return true;
	}

	public List<LessFile> getFiles() {
		return files;
	}

	public void setFiles(List<LessFile> files) {
		this.files = files;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}
	
	@Override
	public LessContainer getParent() {
		return null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((filename == null) ? 0 : filename.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LessFolder other = (LessFolder) obj;
		if (filename == null) {
			if (other.filename != null)
				return false;
		} else if (!filename.equals(other.filename))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "LessFolder [filename=" + filename + "]";
	}
	
	@Override
	public List<LessContainer> getPath() {
		List<LessContainer> path = Lists.newArrayList();
		path.add(this);
		return path;
	}

}
