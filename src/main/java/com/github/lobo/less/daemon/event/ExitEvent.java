package com.github.lobo.less.daemon.event;

import java.io.Serializable;

@SuppressWarnings("serial")
public class ExitEvent implements Serializable {

	private int status;

	public ExitEvent(int status) {
		this.status = status;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "ShutdownEvent [status=" + status + "]";
	}

}
