package fr.proline.zero.gui;

import fr.proline.zero.util.AdvancedAndServerUtils;
import fr.proline.zero.util.ConfigManager;
import fr.proline.zero.util.SettingsConstant;
import fr.proline.zero.util.SystemUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.io.IOException;
import java.text.ParseException;

public class ServerPanel extends JPanel {
	private JFormattedTextField dataStorePortField;
	private JButton advancedSettingsButton;
	private AdvancedConfigWindow advancedParamDialog;
	private JLabel dataStoreLabel;

	AdvancedAndServerUtils advancedManager;

	public ServerPanel() {
		ConfigManager configManager = ConfigManager.getInstance();
		advancedManager = configManager.getAdvancedManager();
		try {
			initialize();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		advancedManager.setHasBeenChanged(false);
	}

	private void initialize() throws ParseException {
		// ajout du layout
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.NORTHWEST;

		// creation des widgets
		advancedParamDialog = new AdvancedConfigWindow();

		this.advancedSettingsButton = new JButton("Advanced settings...");
		advancedSettingsButton.addActionListener(e -> {
			advancedParamDialog.setVisible(true);
		});

		HelpHeaderPanel help = new HelpHeaderPanel("Folder" , SettingsConstant.SERVER_HELP_PANE);
		c.weightx = 1;
		c.weighty = 0;
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 2;
		add(help, c);

		c.fill = GridBagConstraints.NONE;
		c.gridwidth = 1;
		c.weighty = 0;
		c.insets = new java.awt.Insets(20, 15, 0, 15);
		c.gridy++;
		c.weightx = 0;
		add(createDataStorePanel(), c);

		c.gridy++;
		add(advancedSettingsButton, c);

		c.weightx = 1;
		c.gridy = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx++;
		c.gridheight = 2;
		add(Box.createHorizontalGlue(), c);

		c.weighty = 1;
		c.gridy = 2;
		c.gridheight = 1;
		c.fill = GridBagConstraints.VERTICAL;
		c.anchor = GridBagConstraints.SOUTH;
		add(Box.createVerticalGlue(), c);
	}

	private JPanel createDataStorePanel() {

		JPanel dataStorePortPanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new java.awt.Insets(5, 5, 5, 5);
		try {
			MaskFormatter portMask = new MaskFormatter("####");
			portMask.setPlaceholderCharacter('_');
			this.dataStorePortField = new JFormattedTextField(portMask);
			dataStorePortField.setValue(String.valueOf(advancedManager.getDataStorePort()));
			dataStorePortField.setPreferredSize(new Dimension(40, 20));
			dataStorePortField.setMinimumSize(new Dimension(40, 20));

			JButton testPortStatus = new JButton("Test available");
			testPortStatus.addActionListener(e -> {
				updateDatastorePortStatus();
			});

			dataStoreLabel = new JLabel("");
			updateDatastorePortStatus();

			dataStorePortField.addPropertyChangeListener("value", evt -> {
				advancedManager.setDataStorePort(Integer.parseInt((String) dataStorePortField.getValue()));
				advancedManager.setHasBeenChanged(true);
			});
			c.weightx = 1;
			c.gridx = 0;
			dataStorePortPanel.add(new JLabel("DataStore port : "), c);
			c.gridx++;
			dataStorePortPanel.add(dataStorePortField, c);
			c.gridx++;
			dataStorePortPanel.add(testPortStatus, c);
			c.gridx++;
			dataStorePortPanel.add(dataStoreLabel, c);

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dataStorePortPanel;
	}


	public void updateValues() {
		dataStorePortField.setText(String.valueOf(advancedManager.getDataStorePort()));
		advancedParamDialog.setRestoreValues(true);
		advancedParamDialog.updateValues();
		advancedParamDialog.setRestoreValues(false);
	}

//	public class DatastorePortListener implements PropertyChangeListener {
//		public void propertyChange(PropertyChangeEvent e) {
//			advancedManager.setDataStorePort(Integer.parseInt((String) dataStorePortField.getValue()));
//			advancedManager.setHasBeenChanged(true);
//			updateDatastorePortStatus();
//		}
//	}


	private void updateDatastorePortStatus() {
		try {
			if(dataStorePortField.getValue() == null || dataStorePortField.getValue().toString().isEmpty()){
				ImageIcon iconToTest = new ImageIcon(ImageIO.read(ClassLoader.getSystemResource("question.png")));
				dataStoreLabel.setIcon(iconToTest);
			}
			if (SystemUtils.isPortAvailable(Integer.parseInt((String) dataStorePortField.getValue()))) {
				ImageIcon iconToTest = new ImageIcon(ImageIO.read(ClassLoader.getSystemResource("tick.png")));
				dataStoreLabel.setIcon(iconToTest);
			} else {
				ImageIcon iconToTest = new ImageIcon(ImageIO.read(ClassLoader.getSystemResource("cross.png")));
				dataStoreLabel.setIcon(iconToTest);
			}
		} catch (IOException e) {
			ImageIcon iconToTest = null;
			try {
				iconToTest = new ImageIcon(ImageIO.read(ClassLoader.getSystemResource("question.png")));
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			dataStoreLabel.setIcon(iconToTest);
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
