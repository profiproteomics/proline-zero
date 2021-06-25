package fr.proline.zero.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileSystemView;

import fr.proline.zero.util.Config;

public class AdvancedConfigWindow extends JDialog {
	private JTextField jmsPortField;
	private JTextField jmsBatchPortField;
	private JTextField jnpPortField;
	private JTextField jnpRmiPortField;
	private JTextField serverDefaultTimeoutField;
	private JTextField threadPoolSizeField;
	private JTextField jvmPathField;
	private JTextArea aide;
	private JCheckBox forceDatastoreUpdate;
	private JButton continueButton;
	private JButton cancelButton;
	private JButton restoreButton;

	public AdvancedConfigWindow() {
		setModal(true);
		setResizable(false);
		setTitle("Proline zero advanced config window");
		setBounds(100, 100, 450, 550);
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
		serverDefaultTimeoutField = new JTextField();
		serverDefaultTimeoutField.setPreferredSize(new Dimension(60, 20));

		threadPoolSizeField = new JTextField();
		threadPoolSizeField.setPreferredSize(new Dimension(60, 20));

		jvmPathField = new JTextField();
		jvmPathField.setPreferredSize(new Dimension(60, 20));
		jvmPathField.setEditable(false);

		// TODO : texte à changer et centrer avec une icone
		aide = new JTextArea();
		aide.setPreferredSize(new Dimension(300, 75));
		aide.setMinimumSize(new Dimension(300, 75));
		aide.setText("ici est l'aide concernant la fenetre \nadvanced settings");
		aide.setEditable(false);

		JButton foldersButton = new JButton("dossier");
		// foldersButton.setPreferredSize(new Dimension(30, 30));
		try {
			Icon folderIcon = new ImageIcon(ImageIO.read(ClassLoader.getSystemResource("folder-open.png")));
			foldersButton.setText("");
			foldersButton.setIcon(folderIcon);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		foldersButton.addActionListener(openFolderView());

		// ajout des elements au layout
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 4;
		c.weightx = 1;
		add(aide, c);

		c.insets = new java.awt.Insets(20, 15, 0, 15);
		c.gridy++;
		c.weightx = 0;
		add(createPortChoicePanel(), c);

		c.fill = GridBagConstraints.NONE;
		c.gridwidth = 1;
		c.gridy++;
		c.anchor = GridBagConstraints.EAST;
		add(new JLabel("Server default timeout : ", SwingConstants.RIGHT), c);

		c.gridx++;
		c.anchor = GridBagConstraints.NORTHWEST;
		serverDefaultTimeoutField.setText((String.valueOf(Config.getDefaultTimeout())));
		add(serverDefaultTimeoutField, c);

		c.gridx++;
		c.weightx = 0;
		add(new JLabel("ms"), c);

		c.gridx = 0;
		c.gridy++;
		c.anchor = GridBagConstraints.EAST;
		add(new JLabel("Server thread pool size : ", SwingConstants.RIGHT), c);

		c.gridx++;
		c.anchor = GridBagConstraints.NORTHWEST;
		threadPoolSizeField.setText((String.valueOf(Config.getCortexNbParallelizableServiceRunners())));
		add(threadPoolSizeField, c);

		c.gridx++;
		c.weightx = 1;
		add(Box.createHorizontalGlue(), c);

		c.gridx = 0;
		c.gridy++;
		c.weightx = 0;
		c.anchor = GridBagConstraints.EAST;
		add(new JLabel("JVM path : ", SwingConstants.RIGHT), c);

		c.gridx++;
		c.weightx = 1;
		c.gridwidth = 2;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		jvmPathField.setToolTipText(Config.getJavaExePath());
		jvmPathField.setText(Config.getJavaExePath());
		add(jvmPathField, c);

		c.gridx++;
		c.gridx++;
		c.weightx = 0;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.NONE;
		add(foldersButton, c);

		c.gridwidth = 5;
		c.gridx = 0;
		c.gridy++;
		add(createForceDatastorePanel(), c);

		c.gridy++;
		c.weighty = 1;
		add(Box.createVerticalGlue(), c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridy++;
		c.weighty = 0;
		c.weightx = 1;
		add(createBottomButtonsPanel(), c);
	}

	private JPanel createPortChoicePanel() {
		// creation du panel et du layout
		JPanel portChoice = new JPanel(new GridBagLayout());
		portChoice.setBorder(BorderFactory.createTitledBorder("Ports choice"));
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(5, 5, 5, 5);

		// creation des widgets
		jmsPortField = new JTextField();
		jmsPortField.setText(String.valueOf(Config.getJmsPort()));
		jmsPortField.setHorizontalAlignment(SwingConstants.RIGHT);
		jmsPortField.setPreferredSize(new Dimension(50, 20));

		jmsBatchPortField = new JTextField();
		jmsBatchPortField.setText(String.valueOf(Config.getJmsBatchPort()));
		jmsBatchPortField.setHorizontalAlignment(SwingConstants.RIGHT);
		jmsBatchPortField.setPreferredSize(new Dimension(50, 20));

		jnpPortField = new JTextField();
		jnpPortField.setText(String.valueOf(Config.getJnpPort()));
		jnpPortField.setHorizontalAlignment(SwingConstants.RIGHT);
		jnpPortField.setPreferredSize(new Dimension(50, 20));

		jnpRmiPortField = new JTextField();
		jnpRmiPortField.setText(String.valueOf(Config.getJnpRmiPort()));
		jnpRmiPortField.setHorizontalAlignment(SwingConstants.RIGHT);
		jnpRmiPortField.setPreferredSize(new Dimension(50, 20));

		// ajout des widgets au layout
		c.gridx = 0;
		c.gridy = 0;
		portChoice.add(new JLabel("<HTML><U>JMS Server Port : </HTML></U>", SwingConstants.RIGHT), c);

		c.gridx++;
		portChoice.add(jmsPortField, c);

		c.gridx++;
		portChoice.add(createbuttonTest(jmsPortField), c);

		c.gridx = 0;
		c.gridy++;
		portChoice.add(new JLabel("<HTML><U>JMS Batch Server Port : </HTML></U>", SwingConstants.RIGHT), c);

		c.gridx++;
		portChoice.add(jmsBatchPortField, c);

		c.gridx++;
		portChoice.add(createbuttonTest(jmsBatchPortField), c);

		c.gridx = 0;
		c.gridy++;
		portChoice.add(new JLabel("<HTML><U>JNP Server Port : </HTML></U>", SwingConstants.RIGHT), c);

		c.gridx++;
		portChoice.add(jnpPortField, c);

		c.gridx++;
		portChoice.add(createbuttonTest(jnpPortField), c);

		c.gridx = 0;
		c.gridy++;
		portChoice.add(new JLabel("<HTML><U>JNP RMI Server Port : </HTML></U>", SwingConstants.RIGHT), c);

		c.gridx++;
		portChoice.add(jnpRmiPortField, c);

		c.gridx++;
		portChoice.add(createbuttonTest(jnpRmiPortField), c);

		return portChoice;
	}

	private JPanel createForceDatastorePanel() {
		JPanel forceDatastorePanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.NORTHWEST;
		c.insets = new Insets(5, 5, 5, 5);

		forceDatastoreUpdate = new JCheckBox();
		forceDatastoreUpdate.setSelected(Config.getForceUpdate());

		c.gridx = 0;
		forceDatastorePanel.add(forceDatastoreUpdate, c);

		c.gridx++;
		c.insets = new Insets(10, 0, 0, 0);
		forceDatastorePanel.add(new JLabel("Force DataStore update"), c);

		c.gridx++;
		c.weightx = 1;
		forceDatastorePanel.add(Box.createVerticalGlue(), c);

		return forceDatastorePanel;
	}

	// panel des trois boutons (ok restore cancel) + checkbox
	private JPanel createBottomButtonsPanel() {
		// mise en place du panel et du layout
		JPanel buttonPanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(5, 5, 5, 5);

		// mise en place des widgets
		try {
			Icon crossIcon = new ImageIcon(ImageIO.read(ClassLoader.getSystemResource("cross.png")));
			Icon tickIcon = new ImageIcon(ImageIO.read(ClassLoader.getSystemResource("tick.png")));
			Icon restoreIcon = new ImageIcon(ImageIO.read(ClassLoader.getSystemResource("arrow-circle.png")));
			continueButton = new JButton("Ok", tickIcon);
			cancelButton = new JButton("Cancel", crossIcon);
			restoreButton = new JButton("Restore", restoreIcon);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ActionListener actionListener = new ActionListener() {
			public void actionPerformed(ActionEvent event) {
			}
		};
		continueButton.addActionListener(actionListener);

		// ajout des widgets au layout
		c.gridx = 0;
		c.weightx = 0;
		buttonPanel.add(restoreButton, c);

		c.gridx++;
		c.weightx = 1;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		buttonPanel.add(Box.createHorizontalGlue(), c);

		c.fill = GridBagConstraints.NONE;
		c.gridx++;
		c.weightx = 0;
		buttonPanel.add(continueButton, c);

		c.gridx++;
		c.weightx = 0;
		buttonPanel.add(cancelButton, c);

		return buttonPanel;
	}

	JButton createbuttonTest(JTextField port) {
		JButton testButton = new JButton("test");
		JPanel panel = (JPanel) port.getParent();

		// TODO faire le actionlistener

		return testButton;
	}

	private ActionListener openFolderView() {
		ActionListener openFolderView = new ActionListener() {

			public void actionPerformed(ActionEvent event) {
				JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
				jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int returnValue = jfc.showOpenDialog(null);

				if (returnValue == JFileChooser.APPROVE_OPTION) {
					File selectedFile = jfc.getSelectedFile();
					jvmPathField.setToolTipText(selectedFile.getAbsolutePath());
					jvmPathField.setText(selectedFile.getAbsolutePath());
				}
			}
		};
		return openFolderView;
	}
}
