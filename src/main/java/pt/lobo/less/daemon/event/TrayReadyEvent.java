package pt.lobo.less.daemon.event;

import java.awt.PopupMenu;
import java.io.Serializable;

import pt.lobo.less.daemon.tray.Tray;

@SuppressWarnings("serial")
public class TrayReadyEvent implements Serializable {

	private Tray tray;

	public TrayReadyEvent(Tray tray) {
		this.tray = tray;
	}
	
	public Tray getTray() {
		return tray;
	}

	public PopupMenu getPopupMenu() {
		return tray.getPopupMenu();
	}

}
