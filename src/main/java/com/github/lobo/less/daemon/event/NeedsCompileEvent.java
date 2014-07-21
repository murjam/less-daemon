package com.github.lobo.less.daemon.event;

import java.io.Serializable;
import java.util.Collection;

import com.beust.jcommander.internal.Sets;
import com.github.lobo.less.daemon.model.LessFile;

@SuppressWarnings("serial")
public class NeedsCompileEvent implements Serializable {

	private Collection<LessFile> compileSet = Sets.newHashSet();

	public NeedsCompileEvent(Collection<LessFile> compileSet) {
		this.compileSet = compileSet;
	}

	public Collection<LessFile> getCompileSet() {
		return compileSet;
	}

	public void setCompileSet(Collection<LessFile> compileSet) {
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
