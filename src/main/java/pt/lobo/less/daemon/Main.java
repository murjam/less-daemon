package pt.lobo.less.daemon;

import java.awt.EventQueue;
import java.awt.PopupMenu;

import javax.swing.JFrame;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.lobo.less.daemon.event.AddFolderEvent;
import pt.lobo.less.daemon.event.ExitEvent;
import pt.lobo.less.daemon.event.TrayReadyEvent;
import pt.lobo.less.daemon.tray.Tray;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Guice;
import com.google.inject.Inject;

@SuppressWarnings("serial")
public class Main extends JFrame {
	
	private static final Logger logger = LoggerFactory.getLogger(Main.class);

	@Inject Tray tray;
	@Inject EventBus eventBus;
	
	public Main() {
		Guice.createInjector(new LessModule()).injectMembers(this);
		eventBus.register(this);

		EventQueue.invokeLater(tray);
	}
	
	public static void main(String[] args) throws Exception {
		new Main();
	}
	
	@Subscribe
	public void onAddFolder(AddFolderEvent event) {
		
	}
	
	@Subscribe
	public void onTrayReady(TrayReadyEvent event) {
		PopupMenu menu = event.getPopupMenu();
	}
	
	@Subscribe
	public void onExitEvent(ExitEvent event) {
		System.exit(event.getStatus());
	}
	
}
