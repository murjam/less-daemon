package com.github.lobo.less.daemon.ui;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import com.github.lobo.less.daemon.less.Less;
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
		if(preferenceManager != null) {
			lesscPath.setText(preferenceManager.getLesscPath());
			lesscOptions.setText(preferenceManager.getLesscOptions());
			String outputPath = preferenceManager.getOutputOption();
		}
	}

	private void initUi() {
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
				FormFactory.DEFAULT_ROWSPEC,}));
		
		JLabel lblLessPath = new JLabel("Less Path");
		add(lblLessPath, "2, 2, right, default");
		
		lesscPath = new JTextField();
		add(lesscPath, "4, 2, fill, default");
		lesscPath.setColumns(10);
		
		JButton btnBrowse = new JButton("Browse...");
		add(btnBrowse, "4, 4, right, default");
		
		JLabel lblCommandLineOptions = new JLabel("Command line options");
		add(lblCommandLineOptions, "2, 6, right, default");
		
		lesscOptions = new JTextField();
		add(lesscOptions, "4, 6, fill, default");
		lesscOptions.setColumns(10);
		
		JLabel lblOutputLocation = new JLabel("Output Location");
		add(lblOutputLocation, "2, 8, right, default");
		
		outputOptionSame = new JRadioButton("Same Directory");
		add(outputOptionSame, "4, 8");
		
		outputOptionParent = new JRadioButton("CSS folder in parent directory (../css)");
		outputOptionParent.setSelected(true);
		add(outputOptionParent, "4, 10");
	}
	
}
