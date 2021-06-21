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
	private JTextField dataStorePort;
	private JButton advancedSettingsButton;
	private JTextArea aide;
	private AdvancedConfigWindow paramAvances;

	public ServerPanel() {
		paramAvances = new AdvancedConfigWindow();
		this.dataStorePort = new JTextField();
		dataStorePort.setPreferredSize(new Dimension(50, 20));
		this.advancedSettingsButton = new JButton("Advanced settings...");
		aide = new JTextArea();
		aide.setPreferredSize(new Dimension(300, 75));
		initialize();
	}

	private void initialize() {
		setLayout(new GridBagLayout());
		GridBagConstraints serverpanelConstraints = new GridBagConstraints();
		serverpanelConstraints.gridx = 0;
		serverpanelConstraints.gridy = 0;
		serverpanelConstraints.fill = GridBagConstraints.BOTH;
		serverpanelConstraints.anchor = GridBagConstraints.NORTH;
		serverpanelConstraints.weightx = 1;

		// bandeau d'aide
		// TODO : texte à changer et centrer avec une icone
		aide.setText("ici est l'aide concernant l'onglet server");
		aide.setEditable(false);
		add(aide, serverpanelConstraints);
		serverpanelConstraints.gridy++;

		// datastore port textfield
		JPanel dataStorePortPanel = new JPanel();
		dataStorePortPanel.add(new JLabel("DataStore port : "));
		dataStorePortPanel.add(dataStorePort);
		add(dataStorePortPanel, serverpanelConstraints);
		serverpanelConstraints.gridy++;

		// advanced settings button
		serverpanelConstraints.fill = GridBagConstraints.NONE;

		// action du bouton param avancés
		ActionListener openAdvancedConfig = new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				// TODO : enlever l'acces à la fenetre principale lorsque les settings avancés
				// sont ouverts
				paramAvances.setVisible(true);
			}
		};
		advancedSettingsButton.addActionListener(openAdvancedConfig);
		add(advancedSettingsButton, serverpanelConstraints);
		serverpanelConstraints.gridy++;

		serverpanelConstraints.fill = GridBagConstraints.VERTICAL;
		serverpanelConstraints.anchor = GridBagConstraints.NORTH;
		serverpanelConstraints.weighty = 1;
		add(Box.createHorizontalGlue(), serverpanelConstraints);
	}
}
