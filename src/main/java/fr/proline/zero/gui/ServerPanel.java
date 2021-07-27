package fr.proline.zero.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.text.ParseException;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.text.MaskFormatter;

import fr.proline.zero.util.AdvancedAndServerUtils;
import fr.proline.zero.util.ConfigManager;
import fr.proline.zero.util.SettingsConstant;
import fr.proline.zero.util.SystemUtils;

public class ServerPanel extends JPanel {
	private JFormattedTextField dataStorePortField;
	private JButton advancedSettingsButton;
	private AdvancedConfigWindow paramAvancesWindow;
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
		c.weightx = 1;

		// creation des widgets
		paramAvancesWindow = new AdvancedConfigWindow();

		this.advancedSettingsButton = new JButton("Advanced settings...");
		advancedSettingsButton.addActionListener(openAdvancedConfig());

		// ajout des widgets au layout

		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 2;
		add(HelpPannel.createPanel(SettingsConstant.SERVER_HELP_PANE), c);

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
		try {
			MaskFormatter portMask = new MaskFormatter("####");
			portMask.setPlaceholderCharacter('_');
			this.dataStorePortField = new JFormattedTextField(portMask);
			dataStorePortField.setValue(String.valueOf(advancedManager.getDataStorePort()));
			dataStorePortField.setPreferredSize(new Dimension(40, 20));
			dataStorePortField.setMinimumSize(new Dimension(40, 20));

			dataStoreLabel = new JLabel("");
			setImgTestDataStore();

			dataStorePortField.addPropertyChangeListener("value", new DatastorePortListener());
			c.weightx = 1;
			c.gridx = 0;
			dataStorePortPanel.add(new JLabel("DataStore port : "), c);
			c.gridx++;
			dataStorePortPanel.add(dataStorePortField, c);
			c.gridx++;
			dataStorePortPanel.add(dataStoreLabel, c);

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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

	public void updateValues() {
		dataStorePortField.setText(String.valueOf(advancedManager.getDataStorePort()));
		paramAvancesWindow.setrestoreValues(true);
		paramAvancesWindow.updateValues();
		paramAvancesWindow.setrestoreValues(false);
	}

	public class DatastorePortListener implements PropertyChangeListener {
		public void propertyChange(PropertyChangeEvent e) {
			advancedManager.setDataStorePort(Integer.parseInt((String) dataStorePortField.getValue()));
			advancedManager.setHasBeenChanged(true);
			setImgTestDataStore();
		}
	}

	public void setImgTestDataStore() {
		try {
			if (SystemUtils.isPortAvailable(Integer.parseInt((String) dataStorePortField.getValue()))) {
				ImageIcon iconToTest = new ImageIcon(ImageIO.read(ClassLoader.getSystemResource("tick.png")));
				dataStoreLabel.setIcon(iconToTest);
			} else {
				ImageIcon iconToTest = new ImageIcon(ImageIO.read(ClassLoader.getSystemResource("cross.png")));
				dataStoreLabel.setIcon(iconToTest);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
