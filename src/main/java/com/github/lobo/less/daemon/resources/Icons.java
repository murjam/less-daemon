package com.github.lobo.less.daemon.resources;

import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.ImageIcon;

public abstract class Icons {

	public static final String ADD_FOLDER = "add-folder.gif";
	
	public static final ImageIcon ADD_FOLDER_ICON = new ImageIcon(Icons.class.getResource(ADD_FOLDER));
	
	public static final String REMOVE_FOLDER = "remove-folder.gif";
	
	public static final ImageIcon REMOVE_FOLDER_ICON = new ImageIcon(Icons.class.getResource(REMOVE_FOLDER));
	
	public static final String REFRESH = "refresh.gif";
	
	public static final ImageIcon REFRESH_ICON = new ImageIcon(Icons.class.getResource(REFRESH));

	private static final String FOLDER = "folder.gif";

	public static final ImageIcon FOLDER_ICON = new ImageIcon(Icons.class.getResource(FOLDER));

	private static final String TREE = "tree.gif";

	public static final ImageIcon TREE_ICON = new ImageIcon(Icons.class.getResource(TREE));

	public static final String LESS = "less_icon.png";

	public static final ImageIcon LESS_ICON = new ImageIcon(Icons.class.getResource(LESS));
	
	public static final Image LESS_IMAGE = Toolkit.getDefaultToolkit().createImage(Icons.class.getResource(LESS));

	public static final String IMPORT  = "import_icon.png";

	public static final ImageIcon IMPORT_ICON = new ImageIcon(Icons.class.getResource(IMPORT));

	
}
