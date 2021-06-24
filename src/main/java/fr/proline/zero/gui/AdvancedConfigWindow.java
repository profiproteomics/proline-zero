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
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.HORIZONTAL;

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
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 3;
		c.weightx = 1;
		add(aide, c);

		c.insets = new Insets(5, 5, 5, 5);
		c.gridy++;
		c.weighty = 0;
		add(createPortChoicePanel(), c);

		c.gridwidth = 1;
		c.gridy++;
		add(new JLabel("Server default timeout : ", SwingConstants.RIGHT), c);

		c.gridx++;
		add(serverDefaultTimeout, c);

		c.gridx++;
		add(new JLabel("ms"), c);

		c.gridx = 0;
		c.gridy++;
		add(new JLabel("Server thread pool size : ", SwingConstants.RIGHT), c);

		c.gridx++;
		add(threadPoolSize, c);

		c.gridx = 0;
		c.gridy++;
		add(new JLabel("JVM path : ", SwingConstants.RIGHT), c);

		c.gridx++;
		add(jvmpath, c);

		c.gridx++;
		add(foldersButton, c);

		c.gridx = 0;
		c.gridy++;
		add(createForceDatastorePanel(), c);

		c.gridy++;
		c.fill = GridBagConstraints.VERTICAL;
		c.anchor = GridBagConstraints.SOUTH;
		c.weighty = 1;
		add(Box.createVerticalGlue(), c);
	}

	private JPanel createPortChoicePanel() {
		// creation du panel et du layout
		JPanel portChoice = new JPanel(new GridBagLayout());
		portChoice.setBorder(BorderFactory.createTitledBorder("Ports choice"));
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(5, 5, 5, 5);

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
		c.gridx = 0;
		c.gridy = 0;
		portChoice.add(new JLabel("<HTML><U>Proline server : </HTML></U>"), c);

		c.gridx++;
		portChoice.add(portlabel, c);

		c.gridx++;
		portChoice.add(port, c);

		c.gridx++;
		portChoice.add(addButton, c);

		c.gridx++;
		portChoice.add(testButton, c);

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
