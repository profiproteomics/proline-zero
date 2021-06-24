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
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.weightx = 1;

		// creation des widgets
		paramAvancesWindow = new AdvancedConfigWindow();

		this.dataStorePortField = new JTextField();
		dataStorePortField.setPreferredSize(new Dimension(50, 20));

		this.advancedSettingsButton = new JButton("Advanced settings...");
		advancedSettingsButton.addActionListener(openAdvancedConfig());

		aide = new JTextArea();
		aide.setPreferredSize(new Dimension(300, 75));
		aide.setText("ici est l'aide concernant l'onglet \nserver");
		aide.setEditable(false);

		// ajout des widgets au layout
		c.gridx = 0;
		c.gridy = 0;
		add(aide, c);

		c.insets = new java.awt.Insets(5, 5, 0, 0);
		c.fill = GridBagConstraints.NONE;
		c.gridy++;
		c.weightx = 0;
		add(createDataStorePanel(), c);

		c.gridy++;
		add(advancedSettingsButton, c);

		c.gridy++;
		c.fill = GridBagConstraints.VERTICAL;
		c.anchor = GridBagConstraints.SOUTH;
		c.weighty = 1;
		add(Box.createVerticalGlue(), c);
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
