package fr.proline.zero.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class AdvancedConfigWindow extends JFrame {
	private JTextField portlabel;
	private JTextField port;
	private JTextField serverDefaultTimeout;
	private JTextField threadPoolSize;
	private JTextField jvmpath;
	private JTextArea aide;
	private JCheckBox forceDatastoreUpdate;

	public AdvancedConfigWindow() {
		setTitle("Proline zero advanced config window");
		setBounds(100, 100, 450, 300);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		portlabel = new JTextField();
		portlabel.setPreferredSize(new Dimension(60, 20));
		port = new JTextField();
		port.setPreferredSize(new Dimension(60, 20));
		serverDefaultTimeout = new JTextField();
		serverDefaultTimeout.setPreferredSize(new Dimension(60, 20));
		threadPoolSize = new JTextField();
		threadPoolSize.setPreferredSize(new Dimension(60, 20));
		jvmpath = new JTextField();
		jvmpath.setPreferredSize(new Dimension(60, 20));
		aide = new JTextArea();
		aide.setPreferredSize(new Dimension(300, 75));
		forceDatastoreUpdate = new JCheckBox();
		initialize();
	}

	private void initialize() {
		setLayout(new GridBagLayout());
		GridBagConstraints advancedSettingsConstraints = new GridBagConstraints();
		advancedSettingsConstraints.gridx = 0;
		advancedSettingsConstraints.gridy = 0;
		advancedSettingsConstraints.anchor = GridBagConstraints.NORTHWEST;
		advancedSettingsConstraints.fill = GridBagConstraints.BOTH;
		advancedSettingsConstraints.gridwidth = 3;
		advancedSettingsConstraints.weightx = 1;

		// bandeau d'aide
		// TODO : texte à changer et centrer avec une icone
		aide.setText("ici est l'aide concernant l'onglet advanced settings");
		aide.setEditable(false);
		add(aide, advancedSettingsConstraints);
		advancedSettingsConstraints.gridy++;
		advancedSettingsConstraints.weighty = 0;

		// panel choix et ajout des ports
		JPanel portChoice = new JPanel();
		portChoice.setBorder(BorderFactory.createTitledBorder("Ports choice"));
		portChoice.setLayout(new GridBagLayout());
		GridBagConstraints portChoiceConstraints = new GridBagConstraints();
		portChoiceConstraints.gridx = 0;
		portChoiceConstraints.gridy = 0;
		// test d'un texte souligné
		portChoice.add(new JLabel("<HTML><U>Proline server : </HTML></U>"), portChoiceConstraints);

		portChoiceConstraints.gridx++;
		portChoice.add(portlabel, portChoiceConstraints);
		portChoiceConstraints.gridx++;
		portChoice.add(port, portChoiceConstraints);
		portChoiceConstraints.gridx++;
		portChoice.add(port, portChoiceConstraints);
		portChoiceConstraints.gridx++;
		JButton addButton = new JButton("+");
		portChoice.add(addButton, portChoiceConstraints);
		portChoiceConstraints.gridx++;
		JButton testButton = new JButton("test");
		portChoice.add(testButton, portChoiceConstraints);
		add(portChoice, advancedSettingsConstraints);

		advancedSettingsConstraints.gridwidth = 1;
		advancedSettingsConstraints.gridy++;
		add(new JLabel("Server default timeout : ", SwingConstants.RIGHT), advancedSettingsConstraints);
		advancedSettingsConstraints.gridx++;
		add(serverDefaultTimeout, advancedSettingsConstraints);
		advancedSettingsConstraints.gridx++;
		add(new JLabel("ms"), advancedSettingsConstraints);

		advancedSettingsConstraints.gridx = 0;
		advancedSettingsConstraints.gridy++;
		add(new JLabel("Server thread pool size : ", SwingConstants.RIGHT), advancedSettingsConstraints);
		advancedSettingsConstraints.gridx++;
		add(threadPoolSize, advancedSettingsConstraints);

		advancedSettingsConstraints.gridx = 0;
		advancedSettingsConstraints.gridy++;
		add(new JLabel("JVM path : ", SwingConstants.RIGHT), advancedSettingsConstraints);
		advancedSettingsConstraints.gridx++;
		add(jvmpath, advancedSettingsConstraints);
		advancedSettingsConstraints.gridx++;
		JButton exploreFolder = new JButton("dossier");
		add(exploreFolder, advancedSettingsConstraints);

		advancedSettingsConstraints.gridx = 0;
		advancedSettingsConstraints.gridy++;
		JPanel forceDatastorePanel = new JPanel();
		forceDatastorePanel.add(forceDatastoreUpdate);
		forceDatastorePanel.add(new JLabel(" Force DataStore update"));
		add(forceDatastorePanel, advancedSettingsConstraints);
	}
}
