package com.github.lobo.less.daemon.event;

import java.io.Serializable;

@SuppressWarnings("serial")
public class PreferenceChangeEvent implements Serializable {

	private String key;
	private String newValue;

	public PreferenceChangeEvent(String key, String newValue) {
		this.key = key;
		this.newValue = newValue;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getNewValue() {
		return newValue;
	}

	public void setNewValue(String newValue) {
		this.newValue = newValue;
	}

	@Override
	public String toString() {
		return "PreferenceChangeEvent [key=" + key + ", newValue=" + newValue + "]";
	}

}
