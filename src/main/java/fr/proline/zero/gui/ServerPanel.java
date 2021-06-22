package fr.proline.zero.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ServerPanel extends JPanel {
	private JTextField dataStorePortField;
	private JButton advancedSettingsButton;
	private AdvancedConfigWindow paramAvancesWindow;
	private JTextArea aide;

	public ServerPanel() {
		initialize();
	}

	private void initialize() {
		// ajout du layout
		setLayout(new GridBagLayout());
		GridBagConstraints serverpanelConstraints = new GridBagConstraints();
		serverpanelConstraints.fill = GridBagConstraints.BOTH;
		serverpanelConstraints.anchor = GridBagConstraints.NORTH;
		serverpanelConstraints.weightx = 1;

		// creation des widgets
		paramAvancesWindow = new AdvancedConfigWindow();

		this.dataStorePortField = new JTextField();
		dataStorePortField.setPreferredSize(new Dimension(50, 20));

		this.advancedSettingsButton = new JButton("Advanced settings...");
		advancedSettingsButton.addActionListener(openAdvancedConfig());

		aide = new JTextArea();
		aide.setPreferredSize(new Dimension(300, 75));
		aide.setText("ici est l'aide concernant l'onglet server");
		aide.setEditable(false);

		// ajout des widgets au layout
		serverpanelConstraints.gridx = 0;
		serverpanelConstraints.gridy = 0;
		add(aide, serverpanelConstraints);

		serverpanelConstraints.gridy++;
		add(createDataStorePanel(), serverpanelConstraints);

		serverpanelConstraints.gridy++;
		serverpanelConstraints.fill = GridBagConstraints.NONE;
		add(advancedSettingsButton, serverpanelConstraints);

		serverpanelConstraints.gridy++;
		serverpanelConstraints.fill = GridBagConstraints.VERTICAL;
		serverpanelConstraints.anchor = GridBagConstraints.NORTH;
		serverpanelConstraints.weighty = 1;
		add(Box.createHorizontalGlue(), serverpanelConstraints);
	}

	private JPanel createDataStorePanel() {
		JPanel dataStorePortPanel = new JPanel();
		dataStorePortPanel.add(new JLabel("DataStore port : "));
		dataStorePortPanel.add(dataStorePortField);
		return dataStorePortPanel;
	}

	private ActionListener openAdvancedConfig() {
		ActionListener openAdvancedConfig = new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				paramAvancesWindow.setVisible(true);
			}
		};
		return openAdvancedConfig;
	}
}
