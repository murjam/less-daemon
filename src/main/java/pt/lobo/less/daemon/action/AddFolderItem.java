package pt.lobo.less.daemon.action;

import java.awt.MenuItem;
import java.awt.MenuShortcut;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.JFileChooser;

import pt.lobo.less.daemon.FolderManager;

import com.google.inject.Inject;

@SuppressWarnings("serial")
public class AddFolderItem extends MenuItem implements ActionListener {

	private FolderManager folderManager;

	@Inject
	public AddFolderItem(FolderManager folderManager) {
		super("Add Folder", new MenuShortcut(KeyEvent.VK_A));
		this.folderManager = folderManager;
		addActionListener(this);
	}

	public void actionPerformed(ActionEvent e) {
		JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			File selected = fc.getSelectedFile();
			if (selected != null)
				folderManager.addFolder(selected.getAbsolutePath());
		}
	}

}