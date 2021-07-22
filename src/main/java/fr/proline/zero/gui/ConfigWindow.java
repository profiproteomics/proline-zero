package fr.proline.zero.gui;

import java.awt.Dialog;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import fr.proline.zero.util.ConfigManager;

public class ConfigWindow extends JDialog {

	private static final long G = 1024;

	static ConfigWindow instance;

	private JTabbedPane tabbedPane;
	private MemoryPanel memoryPanel;
	private FolderPanel folderPanel;
	private ServerPanel serverPanel;
	private ParsingRulesPanel parsePanel;
	private JCheckBox doNotShowAgainBox;
	private JCheckBox serverModuleBox;
	private JCheckBox studioModuleBox;
	private JCheckBox seqRepModuleBox;

	private boolean seqRepBoxChanging = false;

	private JButton continueButton;
	private JButton cancelButton;
	private JButton restoreButton;

	private ConfigManager configManager;

	private boolean firstClick = false;

	private ConfigWindow() {
		super(null, Dialog.ModalityType.APPLICATION_MODAL);
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			initialize();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static ConfigWindow getInstance() {
		if (instance == null) {
			instance = new ConfigWindow();

		}
		return instance;
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		configManager = ConfigManager.getInstance();

		// Action when the user press on the dialog cross
		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent we) {
				cancelButtonActionPerformed();
			}
		});

		setTitle("Proline zero config window");
		setBounds(100, 100, 400, 750);
		// setResizable(false);
		setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;

		doNotShowAgainBox = new JCheckBox("Do not show again");
		doNotShowAgainBox.setSelected(!configManager.showConfigDialog());
		ItemListener checkHide = new ItemListener() {
			public void itemStateChanged(ItemEvent e) {

				configManager.setShowConfigDialog(!doNotShowAgainBox.isSelected());
			}
		};
		doNotShowAgainBox.addItemListener(checkHide);

		// ajout des differents panels
		c.gridwidth = 3;
		c.gridx = 0;
		c.gridy = 0;
		add(createModulesPanel(), c);

		c.weightx = 1;
		c.weighty = 1;
		c.gridy++;
		add(createTabPanel(), c);

		c.gridwidth = 3;
		c.gridy++;
		c.weighty = 0;
		add(doNotShowAgainBox, c);

		c.gridy++;
		c.weightx = 5;
		add(createBottomButtonsPanel(), c);
	}

	// create the tabs for the many options
	private JTabbedPane createTabPanel() {

		tabbedPane = new JTabbedPane();
		memoryPanel = new MemoryPanel();
		folderPanel = new FolderPanel();
		serverPanel = new ServerPanel();
		parsePanel = new ParsingRulesPanel();
		memoryPanel.addPropertyChangeListener(MemoryPanel.SEQ_REPO_PROPERTY, evt -> {
			if (!seqRepBoxChanging)
				seqRepModuleBox.setSelected((boolean) evt.getNewValue());
		});
		memoryPanel.addPropertyChangeListener(MemoryPanel.STUDIO_PROPERTY, evt -> {
			studioModuleBox.setSelected((boolean) evt.getNewValue());
		});
		tabbedPane.add(memoryPanel, "Memory");
		tabbedPane.add(folderPanel, "Folders");
		tabbedPane.add(serverPanel, "Server");
		tabbedPane.add(parsePanel, "Parsing rules");
		if (!configManager.isSeqRepActive()) {
			tabbedPane.setEnabledAt(3, false);
		}
		tabbedPane.addChangeListener(resizeDynamique());

		return tabbedPane;
	}

	private JPanel createModulesPanel() {

		// mise en place du panel et layout
		JPanel modulePane = new JPanel(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;

		// mise en place des widgets
		serverModuleBox = new JCheckBox("Start Proline Server");
		serverModuleBox.setSelected(true);
		serverModuleBox.setEnabled(false);

		seqRepModuleBox = new JCheckBox("Start Sequence Repository");
		seqRepModuleBox.setSelected(configManager.isSeqRepActive());
		// Action listener for the sequence repository checkBox
		seqRepModuleBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				// changing the boolean to true so we don't loop when we update the values of
				// the GUI
				seqRepBoxChanging = true;

				// If we activate SeqRep :
				if (seqRepModuleBox.isSelected()) {
					// Test if it is possible regarding memory
					boolean canEnable = configManager.getMemoryManager().canEnableSeqRepo();

					if (canEnable) {
						// enable the tab
						tabbedPane.setEnabledAt(3, true);
						memoryPanel.seqRepBeingActive(true);

						// enable it in the util to recalculate the memory values
						configManager.setSeqRepActive(true);

						// then we update graphically the values from the util
						memoryPanel.updateMemoryValues();

					} else {
//
//						// else we basically do the same as if we deactivate it
						Popup.warning("there is not enough memory to use sequence repository");
						seqRepModuleBox.setSelected(false);
					}
				} else {
					// If we deactivate SeqRep :
					// disable it graphically
					if (tabbedPane.getSelectedIndex() == 3) {
						tabbedPane.setSelectedIndex(0);
					}
					tabbedPane.setEnabledAt(3, false);
					memoryPanel.seqRepBeingActive(false);

					// and deactivate it in the utils to recalculate the memory
					configManager.setSeqRepActive(false);

					// then we update graphically the values from the util
					// if seqRepBoxChanging is not set to true, it will loop because of the property
					// listener on the memory values
					memoryPanel.updateMemoryValues();
				}
				seqRepBoxChanging = false;
			}
		});

		// same for the studio module except we don't need to disable a tab
		studioModuleBox = new JCheckBox("Start Proline Studio");
		studioModuleBox.setSelected(configManager.isStudioActive());
		studioModuleBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {

				if (studioModuleBox.isSelected()) {
					configManager.setStudioActive(true);
					memoryPanel.studioBeingActive(true);
				} else {
					configManager.setStudioActive(false);
					memoryPanel.studioBeingActive(false);
				}
				memoryPanel.updateMemoryValues();
			}
		});

		// ajout des widgets
		c.gridy = 0;
		modulePane.add(serverModuleBox, c);

		c.gridy++;
		modulePane.add(seqRepModuleBox, c);

		c.gridy++;
		modulePane.add(studioModuleBox, c);

		c.gridy = 0;
		c.gridheight = 3;
		c.anchor = GridBagConstraints.EAST;
		c.weightx = 1;
		modulePane.add(Box.createHorizontalGlue(), c);
		modulePane.setBorder(BorderFactory.createTitledBorder("Modules"));

		return modulePane;
	}

	// panel des trois boutons (ok restore cancel) + checkbox
	private JPanel createBottomButtonsPanel() {
		// mise en place du panel et du layout
		JPanel buttonPanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(5, 5, 5, 5);

		try {
			Icon crossIcon = new ImageIcon(ImageIO.read(ClassLoader.getSystemResource("cross.png")));
			Icon tickIcon = new ImageIcon(ImageIO.read(ClassLoader.getSystemResource("tick.png")));
			Icon restoreIcon = new ImageIcon(ImageIO.read(ClassLoader.getSystemResource("arrow-circle.png")));
			continueButton = new JButton("Ok", tickIcon);
			continueButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					checkErrorAndExecute();
				}
			});

			cancelButton = new JButton("Cancel", crossIcon);
			cancelButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					cancelButtonActionPerformed();
				}
			});

			restoreButton = new JButton("Restore Settings", restoreIcon);
			restoreButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					boolean yesPressed = Popup.yesNo(
							"Are you sure you want to reset the properties values to those from the config file ?\n(those values may not be valid)");
					if (yesPressed) {
						configManager.restoreValues();
						updateValues();
						memoryPanel.updateValues();
						folderPanel.updateValues();
						serverPanel.updateValues();
						parsePanel.updateValues();
					}
				}
			});
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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

	// launching of proline zero
	private void checkErrorAndExecute() {
		boolean success = ConfigManager.getInstance().verif();
		if (success) {
			// no errors after verification : we launch proline with the current
			// configuration
			ConfigManager.getInstance().updateConfigFileZero();
			setVisible(false);

		} else {
			// At least one error (fatal or not !)
			if (ConfigManager.getInstance().isErrorFatal()) {
				// fatal error, can't launch proline Zero
				StringBuilder msgToDisplay = new StringBuilder("Proline Zero can't start with current errors :\n");
				msgToDisplay.append(ConfigManager.getInstance().getLastErrorMessage());
				msgToDisplay.append("\nWould you like to exit Proline Zero ?");
				msgToDisplay.append(
						"\nAn example proline_launcher.config file is provided in proline_launcher.config.origin file");

				boolean yesPressed = Popup.yesNo(msgToDisplay.toString());
				if (yesPressed) {
					System.exit(0);
				}

			} else {
				StringBuilder msgToDisplay = new StringBuilder(
						"There are still some minors errors in configuration :\n");
				msgToDisplay.append(ConfigManager.getInstance().getLastErrorMessage());
				msgToDisplay.append("\nContinue and launch Proline Zero ?");
				boolean yesPressed = Popup.yesNo(msgToDisplay.toString());
				if (yesPressed) {
					ConfigManager.getInstance().updateConfigFileZero();
					setVisible(false);
				}
			}
		}
	}

	// when the cross or the cancel button is pressed
	private void cancelButtonActionPerformed() {
		boolean yesPressed = Popup.yesNo("Continue with previous configuration (No = exit Proline Zero) ?");
		if (yesPressed) {
			ConfigManager.getInstance().restoreValues();
			checkErrorAndExecute();
		} else {
			System.exit(0);
		}
	}

	// TODO rework to dinamically size when you add elements
	private ChangeListener resizeDynamique() {
		ChangeListener resize = new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				switch (tabbedPane.getSelectedIndex()) {
				case 0:
					setSize(400, 750);
					break;
				case 1:
					setSize(400, 660);
					break;
				case 2:
					setSize(400, 430);
					break;
				case 3:
					setSize(400, 530);
					break;
				}
			}
		};
		return resize;
	}

	// repaint the window with new values
	private void updateValues() {
		doNotShowAgainBox.setSelected(!configManager.showConfigDialog());
		studioModuleBox.setSelected(configManager.isStudioActive());
		seqRepModuleBox.setSelected(configManager.isSeqRepActive());
	}

}
