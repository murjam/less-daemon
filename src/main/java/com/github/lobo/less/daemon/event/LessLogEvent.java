package com.github.lobo.less.daemon.event;

import java.io.Serializable;

import javax.swing.Icon;

import com.github.lobo.less.daemon.resources.Icons;

@SuppressWarnings("serial")
public class LessLogEvent implements Serializable {

	private String text;

	private String message;

	private Type type;

	public enum Type {
		OK, ERROR
	}

	public LessLogEvent(String text, Type type, String message) {
		this.text = text;
		this.type = type;
		this.message = message;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "LessLogEvent [text=" + text + ", message=" + message + ", type=" + type + "]";
	}

	public Icon getIcon() {
		switch (type) {
			case ERROR:
				return Icons.ERROR_ICON;
			default:
			case OK:
				return Icons.OK_ICON;
		}
	}

	public boolean isError() {
		return type == Type.ERROR;
	}
	
}
