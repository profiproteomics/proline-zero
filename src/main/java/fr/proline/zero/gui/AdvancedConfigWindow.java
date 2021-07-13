package fr.proline.zero.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.text.MaskFormatter;

import fr.proline.zero.util.AdvancedAndServerUtils;
import fr.proline.zero.util.ConfigManager;

public class AdvancedConfigWindow extends JDialog {
	private JFormattedTextField jmsPortField;
	private JFormattedTextField jmsBatchPortField;
	private JFormattedTextField jnpPortField;
	private JFormattedTextField jnpRmiPortField;
	private JTextField serverDefaultTimeoutField;
	private JTextField threadPoolSizeField;
	private JTextField jvmPathField;
	private JTextArea aide;
	private JCheckBox forceDatastoreUpdate;
	private JButton continueButton;
	private JButton cancelButton;

	AdvancedAndServerUtils advancedManager;

	public AdvancedConfigWindow() {

		ConfigManager configManager = ConfigManager.getInstance();
		advancedManager = configManager.getAdvancedManager();

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
		serverDefaultTimeoutField.setHorizontalAlignment(SwingConstants.RIGHT);
		serverDefaultTimeoutField.setText((String.valueOf(advancedManager.getServerDefaultTimeout()/1000)));
		serverDefaultTimeoutField.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				int timeout = Integer.parseInt(serverDefaultTimeoutField.getText());
				advancedManager.setServerDefaultTimeout(timeout*1000); // in ms
				advancedManager.setHasBeenChanged(true);
			}

			@Override
			public void changedUpdate(DocumentEvent e) {

			}
		});

		threadPoolSizeField = new JTextField();
		threadPoolSizeField.setPreferredSize(new Dimension(60, 20));
		threadPoolSizeField.setHorizontalAlignment(SwingConstants.RIGHT);
		threadPoolSizeField.setText((String.valueOf(advancedManager.getCortexNbParallelizableServiceRunners())));
		threadPoolSizeField.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				int size = Integer.parseInt(threadPoolSizeField.getText());
				advancedManager.setCortexNbParallelizableServiceRunners(size);
				advancedManager.setHasBeenChanged(true);
			}

			@Override
			public void changedUpdate(DocumentEvent e) {

			}
		});

		jvmPathField = new JTextField();
		jvmPathField.setPreferredSize(new Dimension(60, 20));
		jvmPathField.setEditable(false);
		jvmPathField.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				advancedManager.setHasBeenChanged(true);
			}

			@Override
			public void changedUpdate(DocumentEvent e) {

			}
		});

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
		add(serverDefaultTimeoutField, c);

		c.gridx++;
		c.weightx = 0;
		add(new JLabel("s"), c);

		c.gridx = 0;
		c.gridy++;
		c.anchor = GridBagConstraints.EAST;
		add(new JLabel("Server thread pool size : ", SwingConstants.RIGHT), c);

		c.gridx++;
		c.anchor = GridBagConstraints.NORTHWEST;
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
		jvmPathField.setToolTipText(advancedManager.getJvmPath());
		jvmPathField.setText(advancedManager.getJvmPath());
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
		MaskFormatter portMask;
		try {
			portMask = new MaskFormatter("####");
			portMask.setPlaceholderCharacter('_');

			jmsPortField = new JFormattedTextField(portMask);
			jmsPortField.setValue(String.valueOf(advancedManager.getJmsServerPort()));
			jmsPortField.setHorizontalAlignment(SwingConstants.RIGHT);
			jmsPortField.setPreferredSize(new Dimension(50, 20));
			jmsPortField.addPropertyChangeListener("value", new JMSPortListener());

			jmsBatchPortField = new JFormattedTextField(portMask);
			jmsBatchPortField.setValue(String.valueOf(advancedManager.getJmsBatchServerPort()));
			jmsBatchPortField.setHorizontalAlignment(SwingConstants.RIGHT);
			jmsBatchPortField.setPreferredSize(new Dimension(50, 20));
			jmsBatchPortField.addPropertyChangeListener("value", new JmsBatchPortListener());

			jnpPortField = new JFormattedTextField(portMask);
			jnpPortField.setValue(String.valueOf(advancedManager.getJnpServerPort()));
			jnpPortField.setHorizontalAlignment(SwingConstants.RIGHT);
			jnpPortField.setPreferredSize(new Dimension(50, 20));
			jnpPortField.addPropertyChangeListener("value", new JNPPortListener());

			jnpRmiPortField = new JFormattedTextField(portMask);
			jnpRmiPortField.setValue(String.valueOf(advancedManager.getJnpRmiServerPort()));
			jnpRmiPortField.setHorizontalAlignment(SwingConstants.RIGHT);
			jnpRmiPortField.setPreferredSize(new Dimension(50, 20));
			jnpRmiPortField.addPropertyChangeListener("value", new JnpRmiPortListener());

		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

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
		forceDatastoreUpdate.setSelected(advancedManager.getForceDataStoreUpdate());
		forceDatastoreUpdate.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				advancedManager.setHasBeenChanged(true);
			}
		});

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
			continueButton = new JButton("Ok", tickIcon);
			cancelButton = new JButton("Cancel", crossIcon);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ActionListener actionContinue = new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				advancedManager.setJmsServerPort(Integer.parseInt((String) jmsPortField.getValue()));
				advancedManager.setJmsBatchServerPort(Integer.parseInt((String) jmsBatchPortField.getValue()));
				advancedManager.setJnpRmiServerPort(Integer.parseInt((String) jnpRmiPortField.getValue()));
				advancedManager.setJnpServerPort(Integer.parseInt((String) jnpPortField.getValue()));
				advancedManager.setForceDataStoreUpdate(forceDatastoreUpdate.isSelected());
				int size = Integer.parseInt(threadPoolSizeField.getText());
				advancedManager.setJvmPath(jvmPathField.getText());
				advancedManager.setCortexNbParallelizableServiceRunners(size);
				dispose();
			}
		};
		continueButton.addActionListener(actionContinue);

		ActionListener actionCancel = new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				advancedManager.setHasBeenChanged(true);
				updateValues();
				dispose();
			}
		};
		cancelButton.addActionListener(actionCancel);

		// ajout des widgets au layout
		c.gridx = 0;
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

	public void updateValues() {
		serverDefaultTimeoutField.setText((String.valueOf(advancedManager.getServerDefaultTimeout()/1000)));
		threadPoolSizeField.setText((String.valueOf(advancedManager.getCortexNbParallelizableServiceRunners())));
		jvmPathField.setText(advancedManager.getJvmPath());
		jmsPortField.setValue(String.valueOf(advancedManager.getJmsServerPort()));
		jmsBatchPortField.setValue(String.valueOf(advancedManager.getJmsBatchServerPort()));
		jnpPortField.setValue(String.valueOf(advancedManager.getJnpServerPort()));
		jnpRmiPortField.setValue(String.valueOf(advancedManager.getJnpRmiServerPort()));
	}

	public class JMSPortListener implements PropertyChangeListener {
		public void propertyChange(PropertyChangeEvent e) {
			advancedManager.setHasBeenChanged(true);
		}
	}

	public class JmsBatchPortListener implements PropertyChangeListener {
		public void propertyChange(PropertyChangeEvent e) {
			advancedManager.setHasBeenChanged(true);
		}
	}

	public class JNPPortListener implements PropertyChangeListener {
		public void propertyChange(PropertyChangeEvent e) {
			advancedManager.setHasBeenChanged(true);
		}
	}

	public class JnpRmiPortListener implements PropertyChangeListener {
		public void propertyChange(PropertyChangeEvent e) {
			advancedManager.setHasBeenChanged(true);
		}
	}
}
