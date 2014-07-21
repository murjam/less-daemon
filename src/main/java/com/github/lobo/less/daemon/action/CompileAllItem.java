package com.github.lobo.less.daemon.action;

import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.github.lobo.less.daemon.FolderManager;
import com.github.lobo.less.daemon.event.NeedsCompileEvent;
import com.github.lobo.less.daemon.model.LessFolder;
import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;

@SuppressWarnings("serial")
public class CompileAllItem extends MenuItem implements ActionListener {

	private EventBus eventBus;
	private FolderManager folderManager;

	@Inject
	public CompileAllItem(EventBus eventBus, FolderManager folderManager) {
		super("Compile All...");
		this.eventBus = eventBus;
		this.folderManager = folderManager;
		addActionListener(this);
	}

	public void actionPerformed(ActionEvent e) {
		for(LessFolder folder : folderManager.getFolderList())
			eventBus.post(new NeedsCompileEvent(folder.getFiles()));
	}

}