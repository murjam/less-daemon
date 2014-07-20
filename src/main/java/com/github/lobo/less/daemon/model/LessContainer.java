package com.github.lobo.less.daemon.model;

import java.util.List;

public interface LessContainer {

	List<LessFile> getFiles();
	
	String getFilename();
	
	LessContainer getParent();
	
	boolean isFolder();
	
	boolean isFile();
	
	<T extends LessContainer> T as();

	void removeFile(LessFile ile);

	void addFile(LessFile file);
	
	List<LessContainer> getPath();
	
}
