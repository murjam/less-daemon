package com.github.lobo.less.daemon.ui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JTextField;

import com.github.lobo.less.daemon.event.PreferenceChangeEvent;
import com.github.lobo.less.daemon.less.Less;
import com.github.lobo.less.daemon.less.Less.OutputOption;
import com.github.lobo.less.daemon.preferences.PreferenceManager;
import com.google.common.base.Preconditions;
import com.google.common.eventbus.EventBus;
import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

@SuppressWarnings("serial")
public class PreferenceManagerPanel extends JPanel {
	private JTextField lesscPath;
	private JTextField lesscOptions;
	private EventBus eventBus;
	private PreferenceManager preferenceManager;
	private JRadioButton outputOptionSame;
	private JRadioButton outputOptionParent;
	private final ButtonGroup buttonGroup = new ButtonGroup();
	private JRadioButton outputOptionCustom;
	private JTextField outputOptionCustomPath;
	private JSeparator separator;
	private final JButton buttonSave = new JButton("Save");
	private JButton buttonOutputPath;
	private MouseListener outputOptionListener = new MouseAdapter() {
		@Override
		public void mouseReleased(MouseEvent e) {
			boolean enable = outputOptionCustom.isSelected();
			outputOptionCustomPath.setEnabled(enable);
			buttonOutputPath.setEnabled(enable);
		}
	};

	public PreferenceManagerPanel() {
		initUi();
	}

	public PreferenceManagerPanel(PreferenceManager preferenceManager, EventBus eventBus) {
		Preconditions.checkNotNull(preferenceManager, "Preference manager cannot be nul");
		this.preferenceManager = preferenceManager;
		this.eventBus = eventBus;
		initUi();
		updateValues();
	}

	private void updateValues() {
		if (preferenceManager != null) {
			lesscPath.setText(preferenceManager.getLesscPath());
			lesscOptions.setText(preferenceManager.getLesscOptions());
			OutputOption outputOption = OutputOption.valueOf(preferenceManager.getOutputOption());
			outputOptionSame.setSelected(false);
			outputOptionParent.setSelected(false);
			outputOptionCustom.setSelected(true);
			switch (outputOption) {
				default:
				case PARENT_CSS:
					outputOptionParent.setSelected(true);
				break;
				case SAME:
					outputOptionSame.setSelected(true);
				break;
				case CUSTOM:
					outputOptionCustom.setSelected(true);
					outputOptionCustomPath.setText(preferenceManager.getOutputPath());
				break;
			}
		}
	}

	private void initUi() {
		// @formatter:off
		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,}));
		// @formatter:on

		JLabel lblLessPath = new JLabel("Less Path");
		add(lblLessPath, "2, 2, right, default");

		lesscPath = new JTextField();
		add(lesscPath, "4, 2, fill, default");
		lesscPath.setColumns(10);

		JButton buttonLesscPath = new JButton("Browse...");
		buttonLesscPath.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				Dialogs.chooseFile(new Dialogs.FileDialogAdapter() {
					@Override
					public void selected(File file) {
						lesscPath.setText(file.getAbsolutePath());
					}
				}, lesscPath.getText());
			}
		});
		add(buttonLesscPath, "4, 4, right, default");

		JLabel lblCommandLineOptions = new JLabel("Command line options");
		add(lblCommandLineOptions, "2, 6, right, default");

		lesscOptions = new JTextField();
		add(lesscOptions, "4, 6, fill, default");
		lesscOptions.setColumns(10);

		JLabel lblOutputLocation = new JLabel("Output Location");
		add(lblOutputLocation, "2, 8, right, default");

		outputOptionSame = new JRadioButton("Same Directory");
		outputOptionSame.addMouseListener(outputOptionListener);
		buttonGroup.add(outputOptionSame);
		add(outputOptionSame, "4, 8");

		outputOptionParent = new JRadioButton("CSS folder in parent directory (../css)");
		outputOptionParent.addMouseListener(outputOptionListener);
		buttonGroup.add(outputOptionParent);
		outputOptionParent.setSelected(true);
		add(outputOptionParent, "4, 10");

		outputOptionCustom = new JRadioButton("Custom Location");
		outputOptionCustom.addMouseListener(outputOptionListener);
		buttonGroup.add(outputOptionCustom);
		add(outputOptionCustom, "4, 12");

		outputOptionCustomPath = new JTextField();
		outputOptionCustomPath.setEnabled(false);
		add(outputOptionCustomPath, "4, 14, fill, default");
		outputOptionCustomPath.setColumns(10);
		
		buttonOutputPath = new JButton("Browse...");
		buttonOutputPath.setEnabled(false);
		buttonOutputPath.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				Dialogs.chooseDirectory(new Dialogs.FileDialogAdapter() {
					@Override
					public void selected(File file) {
						outputOptionCustomPath.setText(file.getAbsolutePath());
					}
				}, outputOptionCustomPath.getText());
			}
		});
		add(buttonOutputPath, "4, 16, right, default");
		
		separator = new JSeparator();
		add(separator, "2, 18, 3, 1");
		buttonSave.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				save();
			}
		});
		add(buttonSave, "4, 20, right, default");
	}

	public void save() {
		if(eventBus == null)
			return;
		
		boolean isCustom = false;
		String outputOption = Less.DEFAULT_OUTPUT_OPTION;
		if(outputOptionSame.isSelected())
			outputOption = OutputOption.SAME.name();
		if(outputOptionParent.isSelected())
			outputOption = OutputOption.PARENT_CSS.name();
		if(outputOptionCustom.isSelected()) {
			outputOption = OutputOption.CUSTOM.name();
			isCustom = true;
		}
		
		eventBus.post(new PreferenceChangeEvent(PreferenceManager.KEY_OUTPUT_OPTION, outputOption));
		if(isCustom)
			eventBus.post(new PreferenceChangeEvent(PreferenceManager.KEY_OUTPUT_PATH, outputOptionCustomPath.getText()));
		
		eventBus.post(new PreferenceChangeEvent(PreferenceManager.KEY_LESSC_PATH, lesscPath.getText()));
		eventBus.post(new PreferenceChangeEvent(PreferenceManager.KEY_LESSC_OPTIONS, lesscOptions.getText()));
	}

}
