package fr.proline.zero.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.MaskFormatter;

import fr.proline.zero.util.AdvancedAndServerUtils;
import fr.proline.zero.util.ConfigManager;
import fr.proline.zero.util.SettingsConstant;
import fr.proline.zero.util.SystemUtils;

public class AdvancedConfigWindow extends JDialog {
	private JFormattedTextField jmsPortField;
	private JLabel testJmsIcon;
	private JFormattedTextField jmsBatchPortField;
	private JLabel testJmsBatchIcon;
	private JFormattedTextField jnpPortField;
	private JLabel testJnpIcon;
	private JFormattedTextField jnpRmiPortField;
	private JLabel testJnpRmiIcon;
	private JTextField serverDefaultTimeoutField;
	private JTextField threadPoolSizeField;
	private JTextField jvmPathField;
	private JCheckBox forceDatastoreUpdate;
	private JButton continueButton;
	private JButton cancelButton;

	private boolean restoreValues = false;
	private boolean deuxiemeAvertissement = false;

	private final String portChangingSTring = "Warning ! changing the ports after the first execution of Proline Zero may lead to dysfunctionnements in the programm.";

	AdvancedAndServerUtils advancedManager;

	public AdvancedConfigWindow() {

		ConfigManager configManager = ConfigManager.getInstance();
		advancedManager = configManager.getAdvancedManager();

		setModal(true);
		setTitle("Proline zero advanced config window");
		try {
			setIconImage(ImageIO.read(ClassLoader.getSystemResource("logo32x32.png")));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		setSize(430, 575);
		setMinimumSize(new Dimension(380, 575));

		// Action when the user press on the dialog cross
		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent we) {
				cancelButtonActionPerformed();
			}
		});

		initialize();
	}

	private void initialize() {
		// creation du layout
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.HORIZONTAL;

		// creation des widgets
		JLabel serverDefaultTimeoutLabel = new JLabel("Server default timeout (s) : ", SwingConstants.RIGHT);
		serverDefaultTimeoutLabel.setToolTipText(SettingsConstant.SERVER_TIMEOUT_TOOLTIP);

		serverDefaultTimeoutField = new JTextField();
		serverDefaultTimeoutField.setToolTipText(SettingsConstant.SERVER_TIMEOUT_TOOLTIP);
		serverDefaultTimeoutField.setPreferredSize(new Dimension(60, 20));
		serverDefaultTimeoutField.setMinimumSize(new Dimension(60, 20));
		serverDefaultTimeoutField.setHorizontalAlignment(SwingConstants.RIGHT);
		serverDefaultTimeoutField.setText((String.valueOf(advancedManager.getServerDefaultTimeout() / 1000)));
		serverDefaultTimeoutField.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				int timeout = Integer.parseInt(serverDefaultTimeoutField.getText());
				advancedManager.setServerDefaultTimeout(timeout * 1000); // in ms
				advancedManager.setHasBeenChanged(true);
			}

			@Override
			public void changedUpdate(DocumentEvent e) {

			}
		});

		threadPoolSizeField = new JTextField();
		threadPoolSizeField.setPreferredSize(new Dimension(60, 20));
		threadPoolSizeField.setMinimumSize(new Dimension(60, 20));
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

		JLabel jvmPathLabel = new JLabel("JVM path : ", SwingConstants.RIGHT);
		jvmPathLabel.setToolTipText(SettingsConstant.JVM_PATH_TOOLTIP);

		jvmPathField = new JTextField(SettingsConstant.JVM_PATH_TOOLTIP);
		jvmPathField.setToolTipText(portChangingSTring);
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

		JButton foldersButton = new JButton("dossier");
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
		add(HelpPannel.createPanel(SettingsConstant.ADVANCED_HELP_PANE), c);

		c.insets = new java.awt.Insets(20, 15, 0, 15);
		c.gridy++;
		add(createPortChoicePanel(), c);

		c.fill = GridBagConstraints.NONE;
		c.gridwidth = 1;
		c.gridy++;
		c.anchor = GridBagConstraints.EAST;
		add(serverDefaultTimeoutLabel, c);

		c.gridx++;
		c.anchor = GridBagConstraints.NORTHWEST;
		add(serverDefaultTimeoutField, c);

		c.weightx = 0;
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
		add(jvmPathLabel, c);

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
			jmsPortField.addPropertyChangeListener("value", new JmsPortListener());

			testJmsIcon = new JLabel();
			setImgTestJms();

			jmsBatchPortField = new JFormattedTextField(portMask);
			jmsBatchPortField.setValue(String.valueOf(advancedManager.getJmsBatchServerPort()));
			jmsBatchPortField.setHorizontalAlignment(SwingConstants.RIGHT);
			jmsBatchPortField.setPreferredSize(new Dimension(50, 20));
			jmsBatchPortField.addPropertyChangeListener("value", new JmsBatchPortListener());

			testJmsBatchIcon = new JLabel();
			setImgTestJmsBatch();

			jnpPortField = new JFormattedTextField(portMask);
			jnpPortField.setValue(String.valueOf(advancedManager.getJnpServerPort()));
			jnpPortField.setHorizontalAlignment(SwingConstants.RIGHT);
			jnpPortField.setPreferredSize(new Dimension(50, 20));
			jnpPortField.addPropertyChangeListener("value", new JnpPortListener());

			testJnpIcon = new JLabel();
			setImgTestJnp();

			jnpRmiPortField = new JFormattedTextField(portMask);
			jnpRmiPortField.setValue(String.valueOf(advancedManager.getJnpRmiServerPort()));
			jnpRmiPortField.setHorizontalAlignment(SwingConstants.RIGHT);
			jnpRmiPortField.setPreferredSize(new Dimension(50, 20));
			jnpRmiPortField.addPropertyChangeListener("value", new JnpRmiBatchPortListener());

			testJnpRmiIcon = new JLabel();
			setImgTestJnpRmi();

			// ajout des widgets au layout
			c.gridx = 0;
			c.gridy = 0;
			portChoice.add(new JLabel("<HTML><U>JMS Server Port : </HTML></U>", SwingConstants.RIGHT), c);

			c.gridx++;
			portChoice.add(jmsPortField, c);

			c.gridx++;
			portChoice.add(testJmsIcon, c);

			c.gridx = 0;
			c.gridy++;
			portChoice.add(new JLabel("<HTML><U>JMS Batch Server Port : </HTML></U>", SwingConstants.RIGHT), c);

			c.gridx++;
			portChoice.add(jmsBatchPortField, c);

			c.gridx++;
			portChoice.add(testJmsBatchIcon, c);

			c.gridx = 0;
			c.gridy++;
			portChoice.add(new JLabel("<HTML><U>JNP Server Port : </HTML></U>", SwingConstants.RIGHT), c);

			c.gridx++;
			portChoice.add(jnpPortField, c);

			c.gridx++;
			portChoice.add(testJnpIcon, c);

			c.gridx = 0;
			c.gridy++;
			portChoice.add(new JLabel("<HTML><U>JNP RMI Server Port : </HTML></U>", SwingConstants.RIGHT), c);

			c.gridx++;
			portChoice.add(jnpRmiPortField, c);

			c.gridx++;
			portChoice.add(testJnpRmiIcon, c);

		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return portChoice;
	}

	private JPanel createForceDatastorePanel() {
		JPanel forceDatastorePanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.NORTHWEST;
		c.insets = new Insets(5, 5, 5, 5);

		forceDatastoreUpdate = new JCheckBox("	Force DataStore update");
		forceDatastoreUpdate.setToolTipText(SettingsConstant.FORCE_DATASTORE_UPDATE_TOOLTIP);
		forceDatastoreUpdate.setSelected(advancedManager.getForceDataStoreUpdate());
		forceDatastoreUpdate.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				advancedManager.setHasBeenChanged(true);
				System.out.println(getBounds());
			}
		});

		c.gridx = 0;
		forceDatastorePanel.add(forceDatastoreUpdate, c);

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
				deuxiemeAvertissement = false;
			}
		};
		continueButton.addActionListener(actionContinue);

		ActionListener actionCancel = new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				cancelButtonActionPerformed();
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

	private ActionListener openFolderView() {
		ActionListener openFolderView = new ActionListener() {

			public void actionPerformed(ActionEvent event) {
				JFileChooser jfc = new JFileChooser(new File(advancedManager.getJvmPath()));
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
		serverDefaultTimeoutField.setText((String.valueOf(advancedManager.getServerDefaultTimeout() / 1000)));
		threadPoolSizeField.setText((String.valueOf(advancedManager.getCortexNbParallelizableServiceRunners())));
		jvmPathField.setText(advancedManager.getJvmPath());
		jmsPortField.setValue(String.valueOf(advancedManager.getJmsServerPort()));
		jmsBatchPortField.setValue(String.valueOf(advancedManager.getJmsBatchServerPort()));
		jnpPortField.setValue(String.valueOf(advancedManager.getJnpServerPort()));
		jnpRmiPortField.setValue(String.valueOf(advancedManager.getJnpRmiServerPort()));
	}

	public class JmsPortListener implements PropertyChangeListener {
		public void propertyChange(PropertyChangeEvent e) {
			if (!restoreValues) {
				if (!deuxiemeAvertissement) {
					Popup.warning(portChangingSTring);
					deuxiemeAvertissement = true;
				}
				setImgTestJms();
				advancedManager.setHasBeenChanged(true);
			} else {
				setImgTestJms();
				advancedManager.setHasBeenChanged(false);
			}
		}
	}

	public class JmsBatchPortListener implements PropertyChangeListener {
		public void propertyChange(PropertyChangeEvent e) {
			if (!restoreValues) {
				if (!deuxiemeAvertissement) {
					Popup.warning(portChangingSTring);
					deuxiemeAvertissement = true;
				}
				setImgTestJmsBatch();
				advancedManager.setHasBeenChanged(true);
			} else {
				setImgTestJmsBatch();
				advancedManager.setHasBeenChanged(false);
			}
		}
	}

	public class JnpPortListener implements PropertyChangeListener {
		public void propertyChange(PropertyChangeEvent e) {
			if (!restoreValues) {
				if (!deuxiemeAvertissement) {
					Popup.warning(portChangingSTring);
					deuxiemeAvertissement = true;
				}
				setImgTestJnp();
				advancedManager.setHasBeenChanged(true);
			} else {
				setImgTestJnp();
				advancedManager.setHasBeenChanged(false);
			}
		}
	}

	public class JnpRmiBatchPortListener implements PropertyChangeListener {
		public void propertyChange(PropertyChangeEvent e) {
			if (!restoreValues) {
				if (!deuxiemeAvertissement) {
					Popup.warning(portChangingSTring);
					deuxiemeAvertissement = true;
				}
				setImgTestJnpRmi();
				advancedManager.setHasBeenChanged(true);
			} else {
				setImgTestJnpRmi();
				advancedManager.setHasBeenChanged(false);
			}
		}
	}

	public void setrestoreValues(Boolean b) {
		this.restoreValues = b;
	}

	public void setImgTestJms() {
		try {
			if (SystemUtils.isPortAvailable(Integer.parseInt((String) jmsPortField.getValue()))) {
				ImageIcon iconToTest = new ImageIcon(ImageIO.read(ClassLoader.getSystemResource("tick.png")));
				testJmsIcon.setIcon(iconToTest);
			} else {
				ImageIcon iconToTest = new ImageIcon(ImageIO.read(ClassLoader.getSystemResource("cross.png")));
				testJmsIcon.setIcon(iconToTest);
			}
			repaint();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setImgTestJmsBatch() {
		try {
			if (SystemUtils.isPortAvailable(Integer.parseInt((String) jmsBatchPortField.getValue()))) {
				ImageIcon iconToTest = new ImageIcon(ImageIO.read(ClassLoader.getSystemResource("tick.png")));
				testJmsBatchIcon.setIcon(iconToTest);
			} else {
				ImageIcon iconToTest = new ImageIcon(ImageIO.read(ClassLoader.getSystemResource("cross.png")));
				testJmsBatchIcon.setIcon(iconToTest);
			}
			repaint();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setImgTestJnp() {
		try {
			if (SystemUtils.isPortAvailable(Integer.parseInt((String) jnpPortField.getValue()))) {
				ImageIcon iconToTest = new ImageIcon(ImageIO.read(ClassLoader.getSystemResource("tick.png")));
				testJnpIcon.setIcon(iconToTest);
			} else {
				ImageIcon iconToTest = new ImageIcon(ImageIO.read(ClassLoader.getSystemResource("cross.png")));
				testJnpIcon.setIcon(iconToTest);
			}
			repaint();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setImgTestJnpRmi() {
		try {
			if (SystemUtils.isPortAvailable(Integer.parseInt((String) jnpRmiPortField.getValue()))) {
				ImageIcon iconToTest = new ImageIcon(ImageIO.read(ClassLoader.getSystemResource("tick.png")));
				testJnpRmiIcon.setIcon(iconToTest);
			} else {
				ImageIcon iconToTest = new ImageIcon(ImageIO.read(ClassLoader.getSystemResource("cross.png")));
				testJnpRmiIcon.setIcon(iconToTest);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void cancelButtonActionPerformed() {
		boolean yesPressed = Popup
				.yesNo("Warning !\nAll changes will be lost, are you sure you want to exit this window ?");
		if (yesPressed) {
			restoreValues = true;
			advancedManager.setHasBeenChanged(false);
			updateValues();
			dispose();
			restoreValues = false;
			deuxiemeAvertissement = false;
		}

	}
}
