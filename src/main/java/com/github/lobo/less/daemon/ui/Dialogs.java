package com.github.lobo.less.daemon.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.io.File;
import java.nio.file.Paths;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.google.common.base.Preconditions;

public abstract class Dialogs {

	public interface FileCallback {
		void selected(File file);

		void canceled();
	}

	public static abstract class FileDialogAdapter implements FileCallback {
		@Override
		public void canceled() {

		}
	}

	public static void chooseDirectory(FileCallback callback) {
		chooseDirectory(callback, null);
	}
	
	public static void chooseDirectory(FileCallback callback, String selected) {
		Preconditions.checkNotNull(callback);

		FileDialog dialog = dialog(true, selected);
		if (dialog.getFile() != null) {
			File file = Paths.get(dialog.getDirectory(), dialog.getFile()).toFile();
			callback.selected(file);
		} else
			callback.canceled();
	}

	private static FileDialog dialog(boolean directory, String selected) {
		if(directory)
			System.setProperty("apple.awt.fileDialogForDirectories", "true");
		FileDialog dialog = new FileDialog((Frame) null);
		if(selected != null)
			dialog.setFile(selected);
		dialog.setVisible(true);
		if(directory)
			System.setProperty("apple.awt.fileDialogForDirectories", "false");
		return dialog;
	}

	public static void chooseFile(FileCallback callback) {
		chooseFile(callback, null);
	}

	public static void chooseFile(FileCallback callback, String selected) {
		Preconditions.checkNotNull(callback);

		FileDialog dialog = dialog(false, selected);
		if (dialog.getFile() != null) {
			File file = Paths.get(dialog.getDirectory(), dialog.getFile()).toFile();
			callback.selected(file);
		} else
			callback.canceled();
	}

	public static void showError(String error, String title) {
		try {
			JOptionPane.showMessageDialog(null, error, title, JOptionPane.ERROR_MESSAGE);
		} catch (HeadlessException e) {
		}		
	}
	
	public static JFrame wrap(JComponent component, String title) {
		JFrame frame = new JFrame(title);
		Container contentPane = frame.getContentPane();
		contentPane.setLayout(new BorderLayout());
		contentPane.add(component);

		Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
		int screenWidth = size.width;
		int width = screenWidth / 2;
		int screenHeight = size.height;
		int height = screenHeight / 2;
		
		frame.setSize(width, height);
		frame.setLocation((screenWidth-width)/2, (screenHeight-height)/2);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		return frame;
	}

}
