package pt.lobo.less.daemon.types;

import java.io.Serializable;

@SuppressWarnings("serial")
public class LessFolder implements Serializable {

	private String filename;
	
	public LessFolder(String filename) {
		this.filename = filename;
	}
	
	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
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
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		LessFolder other = (LessFolder) obj;
		if (filename == null) {
			if (other.filename != null) return false;
		} else if (!filename.equals(other.filename)) return false;
		return true;
	}

	@Override
	public String toString() {
		return "LessFolder [filename=" + filename + "]";
	}

}
