package pt.lobo.less.daemon.action;

import java.awt.MenuItem;
import java.awt.MenuShortcut;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import pt.lobo.less.daemon.event.ExitEvent;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;

@SuppressWarnings("serial")
public class ExitItem extends MenuItem implements ActionListener {

	private EventBus eventBus;

	@Inject
	public ExitItem(EventBus eventBus) {
		super("Quit", new MenuShortcut(KeyEvent.VK_Q));
		this.eventBus = eventBus;
		addActionListener(this);
	}

	public void actionPerformed(ActionEvent e) {
		eventBus.post(new ExitEvent(0));
	}

}