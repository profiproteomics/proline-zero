package fr.proline.zero.gui;

import fr.proline.zero.util.ConfigManager;
import fr.proline.zero.util.SettingsConstant;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

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

    private boolean moduleBoxesChanging = false;

    private JButton continueButton;
    private JButton cancelButton;
    private JButton restoreButton;


    private ConfigManager configManager;


    private ConfigWindow() {
        super(null, Dialog.ModalityType.APPLICATION_MODAL);
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            setResizable(true);
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
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        // Action when the user press on the dialog cross
        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent we) {

                windowClosingActionPerformed();

            }
        });

        setTitle("Proline zero config window");
        try {
            setIconImage(ImageIO.read(ClassLoader.getSystemResource("logo32x32.png")));
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        setLayout(new BorderLayout());

        JPanel internalPanel = new JPanel();
        internalPanel.setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;

        doNotShowAgainBox = new JCheckBox("Do not show again");
        doNotShowAgainBox.setSelected(!configManager.showConfigDialog());
        ItemListener checkHide = new ItemListener() {
            public void itemStateChanged(ItemEvent e) {

                configManager.setShowConfigDialog(!doNotShowAgainBox.isSelected());
            }
        };
        doNotShowAgainBox.setToolTipText(SettingsConstant.HIDEDIALOG_TOOLTIP);
        doNotShowAgainBox.addItemListener(checkHide);

        // ajout des differents panels
        c.gridwidth = 3;
        c.gridx = 0;
        c.gridy = 0;
        internalPanel.add(createModulesPanel(), c);

        c.weightx = 1;
        c.weighty = 1;
        c.gridy++;
        internalPanel.add(createTabPanel(), c);

        c.gridwidth = 3;
        c.gridy++;
        c.weighty = 0;
        internalPanel.add(doNotShowAgainBox, c);
        JScrollPane sPane = new JScrollPane();

        c.gridy++;
        c.weightx = 5;
        internalPanel.add(createBottomButtonsPanel(), c);

        sPane.setViewportView(internalPanel);
        add(sPane, BorderLayout.CENTER);
        pack();
        this.setLocationRelativeTo(null);
    }


    // create the tabs for the many options
    private JTabbedPane createTabPanel() {

        tabbedPane = new JTabbedPane();
        memoryPanel = new MemoryPanel();
        folderPanel = new FolderPanel();
        serverPanel = new ServerPanel();
        parsePanel = new ParsingRulesPanel();
        memoryPanel.addPropertyChangeListener(MemoryPanel.SEQ_REPO_PROPERTY, evt -> {
            if (!moduleBoxesChanging)
                seqRepModuleBox.setSelected((boolean) evt.getNewValue());
        });
        memoryPanel.addPropertyChangeListener(MemoryPanel.STUDIO_PROPERTY, evt -> {
            if (!moduleBoxesChanging)
                studioModuleBox.setSelected((boolean) evt.getNewValue());
        });
        tabbedPane.add(memoryPanel, "Memory");
        tabbedPane.add(folderPanel, "Folders");
        tabbedPane.add(serverPanel, "Server");


        tabbedPane.add(parsePanel, "Parsing rules");
        tabbedPane.setEnabledAt(3, configManager.isSeqRepActive());


//		tabbedPane.addChangeListener(resizeDynamique());
        //VDS TODO : implement to allow !

        pack();
        return tabbedPane;
    }

    private JPanel createModulesPanel() {

        // mise en place du panel et layout
        JPanel modulePane = new JPanel(new GridBagLayout());
        modulePane.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
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
                moduleBoxesChanging = true;

                // If we activate SeqRep :
                if (seqRepModuleBox.isSelected()) {
                    // Test if it is possible regarding memory and if config file has been found
                    boolean canEnable = configManager.getMemoryManager().canEnableSeqRepo() && !configManager.noSeqRepoConfigFile();


                    if (canEnable) {
                        // in case parsing-rules.conf has not been verified
                        boolean verif=configManager.getParsingRulesManager().verif();


                        // enable the tab
                        //VDS NYI tabbedPane.setEnabledAt(3, true);
                        memoryPanel.seqRepBeingActive(true);

                        // enable it in the util to recalculate the memory values

                        configManager.setSeqRepActive(true);
                        // then we update graphically the values from the util
                        memoryPanel.updateMemoryValues();
                        // Bug seems to be fixed

                        parsePanel.updateValues();
                        tabbedPane.setEnabledAt(3, true);
                        folderPanel.updateValues();


                    } else {

                        // else we basically do the same as if we deactivate it
                        if (!configManager.getMemoryManager().canEnableSeqRepo() && !configManager.noSeqRepoConfigFile()) {
                            Popup.warning("there is not enough memory to use sequence repository");
                        }
                        if (configManager.getMemoryManager().canEnableSeqRepo() && configManager.noSeqRepoConfigFile()) {
                            Popup.warning("You cannot activate Sequence Repository: no config file found");
                        }
                        if (!configManager.getMemoryManager().canEnableSeqRepo() && configManager.noSeqRepoConfigFile()) {
                            Popup.warning("there is not enough memory and sequence repository config file could not be found");
                        }
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
                    folderPanel.updateValues();
                }
                moduleBoxesChanging = false;
            }
        });
        seqRepModuleBox.setToolTipText(SettingsConstant.SEQREP_MODULES_TOOLTIP);

        // same for the studio module except we don't need to disable a tab
        studioModuleBox = new JCheckBox("Start Proline Studio");
        studioModuleBox.setSelected(configManager.isStudioActive());
        studioModuleBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                moduleBoxesChanging = true;

                if (studioModuleBox.isSelected()) {
                    configManager.setStudioActive(true);
                    memoryPanel.studioBeingActive(true);
                } else {
                    configManager.setStudioActive(false);
                    memoryPanel.studioBeingActive(false);
                }
                memoryPanel.updateMemoryValues();
                moduleBoxesChanging = false;
            }
        });
        studioModuleBox.setToolTipText(SettingsConstant.STUDIO_MODULES_TOOLTIP);

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
        buttonPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
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
                    boolean yesPressed = Popup.yesNoCenterTOWindow(ConfigWindow.getInstance(),"Are you sure you want to reset the properties values to those from the config file ?\n(those values may not be valid)");
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
            ConfigManager.getInstance().updateConfigurationParams();

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

                boolean yesPressed = Popup.yesNoCenterTOWindow(ConfigWindow.getInstance(),msgToDisplay.toString());
                if (yesPressed) {
                    System.exit(0);
                }

            } else {
                StringBuilder msgToDisplay = new StringBuilder(
                        "There are still some minors errors in configuration :\n");
                msgToDisplay.append(ConfigManager.getInstance().getLastErrorMessage());
                msgToDisplay.append("\nContinue and launch Proline Zero ?");
                boolean yesPressed = Popup.yesNoCenterTOWindow(ConfigWindow.getInstance(),msgToDisplay.toString());
                if (yesPressed) {
                    ConfigManager.getInstance().updateConfigurationParams();
                    //ConfigManager.getInstance().updateCortexConfigFile();
                    setVisible(false);
                }
            }
        }
    }

    // when the cancel button is pressed
    private void cancelButtonActionPerformed() {


     //   int userChoice = Popup.yesNoClose("Continue with previous configuration (No = exit Proline Zero) ?");
        int userChoice = Popup.yesNoCloseCenterToWindonw(ConfigWindow.getInstance(),"Continue with previous configuration (No = exit Proline Zero) ?");

        if (userChoice == JOptionPane.OK_OPTION) {
            ConfigManager.getInstance().restoreValues();
            checkErrorAndExecute();
        }
        if (userChoice == JOptionPane.NO_OPTION) {
            System.exit(0);
        }
        // if cancel do nothing
    }

    // called when the cross of the window is clicked
    private void windowClosingActionPerformed() {
        boolean configHasChanged = ConfigManager.getInstance().configHasChanged();

        if (configHasChanged) {

            int userChoice = Popup.yesNoCloseCenterToWindonw(ConfigWindow.getInstance(),"Some modifications have been made do you want to save them? ");

            if (userChoice == JOptionPane.OK_OPTION) {
                configManager.updateConfigurationParams();

                int secondUserChoice = Popup.yesNoCloseCenterToWindonw(ConfigWindow.getInstance(),"Modifications have been saved ,do you want to launch proline?");

                if (secondUserChoice == JOptionPane.OK_OPTION) {
                    checkErrorAndExecute();
                }
                if (secondUserChoice == JOptionPane.NO_OPTION) {
                    System.exit(0);
                }
            }

            if (userChoice == JOptionPane.NO_OPTION) {

                int launchProline = Popup.yesNoCloseCenterToWindonw(ConfigWindow.getInstance(),"Do you want to launch Proline with previous Configuration?");

                if (launchProline == JOptionPane.OK_OPTION) {
                    configManager.restoreValues();
                    checkErrorAndExecute();
                }
                if (launchProline == JOptionPane.NO_OPTION) {
                    System.exit(0);
                }

            }

        } else {

            // User did not make any modification before clicking on the cross
            int userChoice = Popup.yesNoCloseCenterToWindonw(ConfigWindow.getInstance(),"Do you want to launch proline with previous configuration?");
            if (userChoice == JOptionPane.OK_OPTION) {
                checkErrorAndExecute();
            }
            if (userChoice == JOptionPane.NO_OPTION) {
                System.exit(0);

            }

        }


    }

    // TODO rework to dinamically size when you add elements
//	private ChangeListener resizeDynamique() {
//		ChangeListener resize = new ChangeListener() {
//			public void stateChanged(ChangeEvent e) {
//				switch (tabbedPane.getSelectedIndex()) {
//				case 0:
//					setMinimumSize(new Dimension(380, 790));
//					setSize(450, 790);
//					break;
//				case 1:
//					setMinimumSize(new Dimension(330, 640));
//					setSize(400, 660);
//					break;
//				case 2:
//					setMinimumSize(new Dimension(320, 430));
//					setSize(380, 430);
//					break;
//				case 3:
//					setMinimumSize(new Dimension(380, 570));
//					setSize(430, 570);
//					break;
//				}
//			}
//		};
//		return resize;
//	}


    // repaint the window with new values
    private void updateValues() {
        doNotShowAgainBox.setSelected(!configManager.showConfigDialog());
        studioModuleBox.setSelected(configManager.isStudioActive());
        seqRepModuleBox.setSelected(configManager.isSeqRepActive());
    }



}
