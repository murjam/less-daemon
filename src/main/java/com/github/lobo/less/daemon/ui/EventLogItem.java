package com.github.lobo.less.daemon.ui;

import static java.text.MessageFormat.format;

import java.awt.Color;
import java.awt.Font;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;

import com.github.lobo.less.daemon.resources.Icons;

@SuppressWarnings("serial")
public class EventLogItem extends JPanel {

	private JLabel iconLabel;
	private JLabel messageLabel;
	private JLabel textLabel;

	public EventLogItem() {
		
		textLabel = new JLabel("Text");
		Font defaultFont = textLabel.getFont();
		Font bold = new Font(defaultFont.getName(), Font.BOLD, defaultFont.getSize());
		textLabel.setFont(bold);
		
		iconLabel = new JLabel(Icons.OK_ICON);
		
		messageLabel = new JLabel("Message");
		messageLabel.setForeground(Color.DARK_GRAY);
		messageLabel.setVerticalAlignment(SwingConstants.TOP);
		messageLabel.setFont(new Font("Monaco", Font.PLAIN, 11));
		
		JSeparator separator = new JSeparator();
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addGroup(groupLayout.createSequentialGroup()
							.addContainerGap()
							.addComponent(iconLabel)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(textLabel, GroupLayout.DEFAULT_SIZE, 0, Short.MAX_VALUE))
						.addGroup(Alignment.LEADING, groupLayout.createSequentialGroup()
							.addGap(28)
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(messageLabel, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 0, Short.MAX_VALUE)
								.addComponent(separator, GroupLayout.DEFAULT_SIZE, 0, Short.MAX_VALUE))))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(iconLabel)
						.addComponent(textLabel))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(separator, GroupLayout.PREFERRED_SIZE, 0, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(messageLabel, GroupLayout.DEFAULT_SIZE, 0, Short.MAX_VALUE)
					.addContainerGap())
		);
		setLayout(groupLayout);
	}
	
	public void setIcon(Icon icon) {
		iconLabel.setIcon(icon);
	}
	
	public void setText(String text) {
		textLabel.setText(text);
	}
	
	public void setMessage(String text) {
		messageLabel.setText(format("<html><pre>{0}</pre></html>", text));
	}
	
}
