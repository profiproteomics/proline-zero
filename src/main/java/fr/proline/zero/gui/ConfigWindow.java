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
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import fr.proline.zero.util.ConfigManager;
import fr.proline.zero.util.MemoryUtils;

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
	private JLabel seqRepModuleLabel;
	private JCheckBox seqRepModuleBox;
	private JButton continueButton;
	private JButton cancelButton;
	private JButton restoreButton;

	private ConfigManager configManager;

	private ConfigWindow() {
		super(null, Dialog.ModalityType.APPLICATION_MODAL);
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			initialize();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// singleton to be able to deactivate its elements (modules checkboxes) from any
	// other class
	public static ConfigWindow getInstance() {
		if (instance == null) {
			instance = new ConfigWindow();

			// we check here if we need to check or not the seqrep checkbox because
			// otherwise the ConfigWindow is not yet fully instanciated and the method won't
			// do its job (check how the method works)
			setSeqRep(ConfigManager.getInstance().getMemoryManager().getTotalMemory() >= 4 * G);
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

		// TODO gerer le resizing
		setTitle("Proline zero config window");
		setBounds(100, 100, 400, 750);
		setResizable(false);
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

		tabbedPane.add(memoryPanel, "Memory");
		tabbedPane.add(folderPanel, "Folders");
		tabbedPane.add(serverPanel, "Server");
		tabbedPane.add(parsePanel, "Parsing rules");
		if (!configManager.isSeqReppActive()) {
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
		seqRepModuleBox.setSelected(configManager.isSeqReppActive());
		seqRepModuleBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				MemoryUtils memoryManager = ConfigManager.getInstance().getMemoryManager();

				// If we activate SeqRep :
				if (seqRepModuleBox.isSelected()) {
					// enable the tab
					tabbedPane.setEnabledAt(3, true);
					memoryPanel.seqRepBeingActive(true);

					// enable it in the util to recalculate the memory values
					memoryManager.setSeqRepoActive(true);

					// then we verify that there is enough memory to activate it
					if (!ConfigManager.getInstance().getMemoryManager().verif()) {

						// else we basically do the same as if we deactivate it
						Popup.warning("there is not enough memory to use sequence repository");
						seqRepModuleBox.setSelected(false);
						if (tabbedPane.getSelectedIndex() == 3) {
							tabbedPane.setSelectedIndex(0);
						}
						tabbedPane.setEnabledAt(3, false);
						memoryManager.setSeqRepoActive(false);
						memoryPanel.seqRepBeingActive(false);
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
					memoryManager.setSeqRepoActive(false);
				}

				// then we update graphically the values from the util
				memoryPanel.updateValues();
			}
		});

		// same for the studio module except we don't need to disable a tab
		studioModuleBox = new JCheckBox("Start Proline Studio");
		studioModuleBox.setSelected(configManager.isStudioActive());
		studioModuleBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {

				if (studioModuleBox.isSelected()) {
					configManager.setStudioActive(true);
					ConfigManager.getInstance().getMemoryManager().setStudioActive(true);
					memoryPanel.studioBeingActive(true);
				} else {
					configManager.setStudioActive(false);
					ConfigManager.getInstance().getMemoryManager().setStudioActive(false);
					memoryPanel.studioBeingActive(false);
				}
				memoryPanel.updateValues();
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
					boolean success = ConfigManager.getInstance().verif();
					if (success) {
						// no errors after verification : we launch proline with the current
						// configuration
						ConfigManager.getInstance().updateConfigFileZero();
						setVisible(false);
					} else {
						// At least one error fatal or not !
						if (ConfigManager.getInstance().isErrorFatal()) {
							// fatal error, can't launch proline Zero
							Popup.error("Proline Zero can't start with current errors \nExiting...");
							System.exit(0);
						} else {
							// Minor errors
							boolean yesPressed = Popup.yesNo(
									"Proline Zero will start with errors \nWould you still like to launch Proline Zero ?");
							if (yesPressed) {
								ConfigManager.getInstance().updateConfigFileZero();
								setVisible(false);
							}
						}
					}
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
					configManager.restoreValues();
					updateValues();
					memoryPanel.updateValues();
					folderPanel.updateValues();
					serverPanel.updateValues();
					parsePanel.updateValues();
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

	// when the cross or the cancel button is pressed
	private void cancelButtonActionPerformed() {
		boolean yesPressed = Popup.yesNo("Continue with previous configuration (No = exit) ?");
		if (yesPressed) {
			ConfigManager.getInstance().restoreValues();

			boolean success = ConfigManager.getInstance().verif();
			if (success) {
				ConfigManager.getInstance().updateConfigFileZero();
				setVisible(false);
			} else { // At least one error fatal or not !
				if (ConfigManager.getInstance().isErrorFatal()) {
					Popup.error(
							"Proline Zero can't start with current configuration\nChange configuration in the proline_launcher.config file");
					System.exit(0);
				} else {
					boolean yesPressed2 = Popup.yesNo(
							"Proline Zero will start with errors \nWould you still like to launch Proline Zero ?");
					if (!yesPressed2) {
						System.exit(0);
					}
				}
			}
			ConfigManager.getInstance().updateConfigFileZero();
			setVisible(false);
		} else {
			System.exit(0);
		}
	}

	private ChangeListener resizeDynamique() {
		ChangeListener resize = new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				switch (tabbedPane.getSelectedIndex()) {
				case 0:
					setBounds(100, 100, 400, 750);
					break;
				case 1:
					setBounds(100, 100, 400, 660);
					break;
				case 2:
					setBounds(100, 100, 400, 430);
					break;
				case 3:
					setBounds(100, 100, 400, 530);
					break;
				}
			}
		};
		return resize;
	}

	// static method to deactivate seqrep from the MemoryUtil when the total memory
	// is too low
	public static void setSeqRep(boolean b) {
		if (instance != null) {
			if (b == false) {
				if (instance.seqRepModuleBox.isSelected()) {
					Popup.warning("there is not enough allocated Memory to activate SeqRepo");
				}
				instance.seqRepModuleBox.setSelected(b);
				instance.seqRepModuleBox.setEnabled(b);
			} else {
				instance.seqRepModuleBox.setEnabled(b);
			}
		}
	}

	// repaint the window with new values
	private void updateValues() {
		doNotShowAgainBox.setSelected(!configManager.showConfigDialog());
		studioModuleBox.setSelected(configManager.isStudioActive());
		seqRepModuleBox.setSelected(configManager.isSeqReppActive());
	}

}
