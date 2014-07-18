package com.github.lobo.less.daemon.event;

import java.io.Serializable;
import java.util.Set;

import com.beust.jcommander.internal.Sets;
import com.github.lobo.less.daemon.model.LessFile;

@SuppressWarnings("serial")
public class NeedsCompileEvent implements Serializable {

	private Set<LessFile> compileSet = Sets.newHashSet();

	public NeedsCompileEvent(Set<LessFile> compileSet) {
		this.compileSet = compileSet;
	}

	public Set<LessFile> getCompileSet() {
		return compileSet;
	}

	public void setCompileSet(Set<LessFile> compileSet) {
		this.compileSet = compileSet;
	}

	@Override
	public String toString() {
		return "NeedsCompileEvent [compileSet=" + compileSet + "]";
	}
	
	public boolean isEmpty() {
		return compileSet.isEmpty();
	}

}
