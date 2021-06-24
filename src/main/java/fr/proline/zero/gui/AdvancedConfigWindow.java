package fr.proline.zero.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class AdvancedConfigWindow extends JDialog {
	private JTextField portlabel;
	private JTextField port;
	private JTextField serverDefaultTimeout;
	private JTextField threadPoolSize;
	private JTextField jvmpath;
	private JTextArea aide;
	private JCheckBox forceDatastoreUpdate;

	public AdvancedConfigWindow() {
		setModal(true);
		setTitle("Proline zero advanced config window");
		setBounds(100, 100, 450, 350);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		initialize();
	}

	private void initialize() {
		// creation du layout
		setLayout(new GridBagLayout());
		GridBagConstraints advancedSettingsConstraints = new GridBagConstraints();
		advancedSettingsConstraints.anchor = GridBagConstraints.NORTHWEST;
		advancedSettingsConstraints.fill = GridBagConstraints.HORIZONTAL;

		// creation des widgets
		serverDefaultTimeout = new JTextField();
		serverDefaultTimeout.setPreferredSize(new Dimension(60, 20));

		threadPoolSize = new JTextField();
		threadPoolSize.setPreferredSize(new Dimension(60, 20));

		jvmpath = new JTextField();
		jvmpath.setPreferredSize(new Dimension(60, 20));

		// TODO : texte à changer et centrer avec une icone
		aide = new JTextArea();
		aide.setPreferredSize(new Dimension(300, 75));
		aide.setMinimumSize(new Dimension(300, 75));
		aide.setText("ici est l'aide concernant la fenetre \nadvanced settings");
		aide.setEditable(false);

		JButton foldersButton = new JButton("dossier");
		foldersButton.setSize(getPreferredSize());
		try {
			Icon folderIcon = new ImageIcon(ImageIO.read(ClassLoader.getSystemResource("folder-open.png")));
			foldersButton.setText("");
			foldersButton.setIcon(folderIcon);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// ajout des elements au layout
		advancedSettingsConstraints.gridx = 0;
		advancedSettingsConstraints.gridy = 0;
		advancedSettingsConstraints.gridwidth = 3;
		advancedSettingsConstraints.weightx = 1;
		add(aide, advancedSettingsConstraints);

		advancedSettingsConstraints.insets = new Insets(5, 5, 5, 5);
		advancedSettingsConstraints.gridy++;
		advancedSettingsConstraints.weighty = 0;
		add(createPortChoicePanel(), advancedSettingsConstraints);

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
		add(foldersButton, advancedSettingsConstraints);

		advancedSettingsConstraints.gridx = 0;
		advancedSettingsConstraints.gridy++;
		add(createForceDatastorePanel(), advancedSettingsConstraints);

		advancedSettingsConstraints.gridy++;
		advancedSettingsConstraints.fill = GridBagConstraints.VERTICAL;
		advancedSettingsConstraints.anchor = GridBagConstraints.SOUTH;
		advancedSettingsConstraints.weighty = 1;
		add(Box.createVerticalGlue(), advancedSettingsConstraints);
	}

	private JPanel createPortChoicePanel() {
		// creation du panel et du layout
		JPanel portChoice = new JPanel(new GridBagLayout());
		portChoice.setBorder(BorderFactory.createTitledBorder("Ports choice"));
		GridBagConstraints portChoiceConstraints = new GridBagConstraints();
		portChoiceConstraints.anchor = GridBagConstraints.NORTHWEST;
		portChoiceConstraints.fill = GridBagConstraints.BOTH;
		portChoiceConstraints.insets = new Insets(5, 5, 5, 5);

		// creation des widgets
		portlabel = new JTextField();
		portlabel.setPreferredSize(new Dimension(60, 20));

		port = new JTextField();
		port.setPreferredSize(new Dimension(60, 20));

		JButton addButton = new JButton("+");
		try {
			Icon plusIcon = new ImageIcon(ImageIO.read(ClassLoader.getSystemResource("plus.png")));
			addButton.setText("");
			addButton.setIcon(plusIcon);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		JButton testButton = new JButton("test");

		// ajout des widgets au layout
		portChoiceConstraints.gridx = 0;
		portChoiceConstraints.gridy = 0;
		portChoice.add(new JLabel("<HTML><U>Proline server : </HTML></U>"), portChoiceConstraints);

		portChoiceConstraints.gridx++;
		portChoice.add(portlabel, portChoiceConstraints);

		portChoiceConstraints.gridx++;
		portChoice.add(port, portChoiceConstraints);

		portChoiceConstraints.gridx++;
		portChoice.add(addButton, portChoiceConstraints);

		portChoiceConstraints.gridx++;
		portChoice.add(testButton, portChoiceConstraints);

		return portChoice;
	}

	private JPanel createForceDatastorePanel() {
		JPanel forceDatastorePanel = new JPanel();

		forceDatastoreUpdate = new JCheckBox();

		forceDatastorePanel.add(forceDatastoreUpdate);
		forceDatastorePanel.add(new JLabel(" Force DataStore update"));
		return forceDatastorePanel;
	}
}
