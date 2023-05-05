package fr.proline.zero.gui;

import fr.proline.studio.gui.DefaultDialog;
import fr.proline.studio.utils.IconManager;
import fr.proline.zero.util.ConfigManager;
import fr.proline.zero.util.MountPointUtils;
import fr.proline.zero.util.SettingsConstant;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.lang.String.valueOf;


public class FolderPanel extends JPanel {


    private final Color stringColor = new Color(50, 0, 230);
    private final Color errorColor = new Color(255, 0, 50);

    private final Color softErrorColor = new Color(243, 227, 227);


    private String labelEdited;

    // help component used to position Dialogs ont it for better visibility
    private HelpHeaderPanel help;


    private String pathBackup;


    public ArrayList<String> valuesInsideDialog;


    public FolderPanel() {
        super();
        initialize();
    }

    private void initialize() {
        // creation du layout

        setLayout(new GridBagLayout());
        GridBagConstraints folderPanelConstraints = new GridBagConstraints();
        folderPanelConstraints.fill = GridBagConstraints.BOTH;
        folderPanelConstraints.anchor = GridBagConstraints.NORTHWEST;
        folderPanelConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        folderPanelConstraints.gridx = 0;
        folderPanelConstraints.gridy = 0;
        folderPanelConstraints.weightx = 1;
        folderPanelConstraints.weighty = 0;
        folderPanelConstraints.gridwidth = 3;
        help = new HelpHeaderPanel("Folder", SettingsConstant.FOLDERS_HELP_PANE);
        add(help, folderPanelConstraints);

        // temp folder size
        folderPanelConstraints.gridwidth = 1;
        folderPanelConstraints.gridy++;
        folderPanelConstraints.weightx = 0;
        folderPanelConstraints.fill = GridBagConstraints.NONE;
        folderPanelConstraints.anchor = GridBagConstraints.EAST;
        add(new JLabel("Maximum size for temp folder :"), folderPanelConstraints);

        JTextField maximumTmpFolderSizeField = new JTextField();
        maximumTmpFolderSizeField.setText(valueOf(ConfigManager.getMaxTmpFolderSize()));
        maximumTmpFolderSizeField.setToolTipText(SettingsConstant.FOLDER_MAX_SIZE_TOOLTIP);
        maximumTmpFolderSizeField.setToolTipText(SettingsConstant.FOLDER_MAX_SIZE_TOOLTIP);
        folderPanelConstraints.gridx++;
        folderPanelConstraints.weightx = 0.5;
        folderPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
        folderPanelConstraints.anchor = GridBagConstraints.WEST;
        add(maximumTmpFolderSizeField, folderPanelConstraints);

        folderPanelConstraints.gridx++;
        folderPanelConstraints.weightx = 0;
        folderPanelConstraints.insets = new java.awt.Insets(5, 5, 5, 15);
        add(new JLabel("Mo"), folderPanelConstraints);

        // Add Mounting Point
        folderPanelConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        folderPanelConstraints.gridx = 0;
        folderPanelConstraints.gridy++;
        folderPanelConstraints.weightx = 0;
        folderPanelConstraints.weighty = 0;
        folderPanelConstraints.fill = GridBagConstraints.NONE;
        folderPanelConstraints.anchor = GridBagConstraints.EAST;
        //add(createTmpFolderPanel(), folderPanelConstraints);
        add(new JLabel("Add folder :"), folderPanelConstraints);

        JButton openAddDialogButton = new JButton(IconManager.getIcon(IconManager.IconType.PLUS_16X16));
        openAddDialogButton.addActionListener(e -> {
            openAddDialog();
        });
        folderPanelConstraints.gridx++;
        folderPanelConstraints.anchor = GridBagConstraints.WEST;
        folderPanelConstraints.gridwidth = 2;
        add(openAddDialogButton, folderPanelConstraints);

        folderPanelConstraints.gridx = 0;
        folderPanelConstraints.gridwidth = 3;
        folderPanelConstraints.fill = GridBagConstraints.BOTH;
        folderPanelConstraints.anchor = GridBagConstraints.NORTHWEST;
        folderPanelConstraints.gridy++;
        add(createFolderListPanel(), folderPanelConstraints);
        folderPanelConstraints.gridy++;
        folderPanelConstraints.weighty = 1;
        add(Box.createHorizontalGlue(), folderPanelConstraints);


    }

    private void openAddDialog() {
        FolderEditDialog addDialog = new FolderEditDialog(ConfigWindow.getInstance(), FolderEditDialog.TypeOfDialog.AddingAMountPoint, null, null, null);
        // addDialog.centerToWindow(ConfigWindow.getInstance());
        addDialog.setLocationRelativeTo(help);
        addDialog.setSize(450, 200);
        addDialog.setVisible(true);

        // TODO execute if button ok is clicked inside dialog
        if (addDialog.getButtonClicked() == DefaultDialog.BUTTON_OK) {
            valuesInsideDialog = addDialog.getValuesEntered();
            addFolderAction();
        }
    }

    //VDS TODO: test lighter update method...
    private void updateJpanel() {
        removeAll();
        initialize();
        revalidate();
        repaint();
    }


    private JPanel createFolderListPanel() {
        // creation du panel et layout
        JPanel folderListPanel = new JPanel(new GridBagLayout());
        folderListPanel.setBorder(BorderFactory.createTitledBorder("List of folders"));
        GridBagConstraints c = new GridBagConstraints();

        c.weightx = 1;
        c.weighty = 1;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.fill = GridBagConstraints.BOTH;


        // creation des panels en attribut de classe pour pouvoir ajouter dynamiquement
        // des elements

        // ajout des panels au layout
        // TODO : rendre la liste des dossiers plus belle// result files folder
        c.gridx = 0;
        c.gridy = 0;
        JPanel resultListPanel = new JPanel(new GridBagLayout());

        this.initAnyFolderPanel(MountPointUtils.MountPointType.RESULT, resultListPanel);
        folderListPanel.add(resultListPanel, c);

        JPanel mzDBListPanel = new JPanel(new GridBagLayout());

        this.initAnyFolderPanel(MountPointUtils.MountPointType.MZDB, mzDBListPanel);

        c.gridy++;
        folderListPanel.add(mzDBListPanel, c);
        // TODO : desactiver si seqrep décoché;
        c.gridy++;

        JPanel fastaListPanel = new JPanel(new GridBagLayout());
        this.initFastaFolderPanel(fastaListPanel);
        folderListPanel.add(fastaListPanel, c);

        return folderListPanel;
    }


    private void initAnyFolderPanel(MountPointUtils.MountPointType mpt, JPanel anyPanel) {
        List<String> pathWrong = ConfigManager.getInstance().getMountPointManager().getInvalidPaths();
        List<String> missingMps = ConfigManager.getInstance().getMountPointManager().getMissingMPs();
        Map<String, String> mountPointsToBeDisplayed = ConfigManager.getInstance().getMountPointManager().getMountPointMap().get(mpt);
        boolean missingMountPoint = missingMps.contains(mpt.getDisplayString());

        GridBagConstraints anyPanelConstraints = new GridBagConstraints();
        anyPanel.setBorder(BorderFactory.createTitledBorder(mpt.getDisplayString()));
        anyPanelConstraints.insets = new Insets(5, 5, 5, 5);
        anyPanelConstraints.gridx = 0;
        anyPanelConstraints.gridy = 0;
        anyPanelConstraints.weightx = 0;
        anyPanelConstraints.fill = GridBagConstraints.HORIZONTAL;

        //places none deletable mounting point at the top of the list


        JLabel fieldInitial = new JLabel(MountPointUtils.getMountPointDefaultPathLabel(mpt) + ": ", SwingConstants.RIGHT);

        fieldInitial.setForeground(stringColor);
        anyPanelConstraints.anchor = GridBagConstraints.EAST;
        fieldInitial.setEnabled(true);
        fieldInitial.setPreferredSize(new Dimension(getMaximumSizesOfJLabels() + 8, 20));
        anyPanel.add(fieldInitial, anyPanelConstraints);
        String path = null;
        JTextField pathInitial;
        boolean defaultMountPointHasAWrongPath = false;
        if (mountPointsToBeDisplayed != null) {
            path = mountPointsToBeDisplayed.get(MountPointUtils.getMountPointDefaultPathLabel(mpt));
            pathInitial = new JTextField(mountPointsToBeDisplayed.get(MountPointUtils.getMountPointDefaultPathLabel(mpt)));
            defaultMountPointHasAWrongPath = pathWrong.contains(mountPointsToBeDisplayed.get(MountPointUtils.getMountPointDefaultPathLabel(mpt)));
        } else {
            pathInitial = new JTextField();
        }

        pathInitial.setEnabled(false);
        anyPanelConstraints.gridx++;
        anyPanelConstraints.anchor = GridBagConstraints.WEST;
        anyPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
        anyPanelConstraints.weightx = 1;

        // refaire
        if (path != null) {
            if (defaultMountPointHasAWrongPath) {
                pathInitial.setEnabled(true);
                pathInitial.setForeground(errorColor);
                // pathInitial.setToolTipText("Click to fix the error");
                anyPanel.add(pathInitial, anyPanelConstraints);


            } else if (path.equals("")) {
                pathInitial.setText("This path is not valid please enter a path for this mount point");
                pathInitial.setBackground(softErrorColor);
            } else {
                pathInitial.setEnabled(false);
                anyPanel.add(pathInitial, anyPanelConstraints);

            }

        } else {
            // treats the case where default mounting point is not present (path==null)
            pathInitial.setText("This mount point doesn't exist please click on the button to repair that issue");
            pathInitial.setEnabled(true);
            pathInitial.setEditable(false);
            pathInitial.setForeground(Color.RED);
            anyPanel.add(pathInitial, anyPanelConstraints);


        }
        // indicates the path of the folder is an empty string


        anyPanelConstraints.fill = GridBagConstraints.NONE;
        anyPanelConstraints.weightx = 0;
        anyPanelConstraints.gridx++;

        JButton editButton = new JButton(IconManager.getIcon(IconManager.IconType.EDIT));
        editButton.setHorizontalAlignment(SwingConstants.CENTER);
        editButton.setToolTipText("Click to edit ");
        editButton.setSize(20, 15);

        String finalPath = path;
        editButton.addActionListener(e -> {


            pathBackup = finalPath;
            if (finalPath != null) {
                ConfigManager.getInstance().getMountPointManager().deleteMountPointEntry(mpt, MountPointUtils.getMountPointDefaultPathLabel(mpt), true);
            }


            FolderEditDialog editDialog = new FolderEditDialog(ConfigWindow.getInstance(), FolderEditDialog.TypeOfDialog.EditingDefaultMPts, pathBackup, null, mpt);
            editDialog.setLocationRelativeTo(help);
            // editDialog.centerToWindow(ConfigWindow.getInstance());
            editDialog.setSize(500, 200);
            editDialog.setVisible(true);
            if (editDialog.getButtonClicked() == DefaultDialog.BUTTON_OK) {
                valuesInsideDialog = editDialog.getValuesEntered();
                addFolderAction();
            }
            if (editDialog.getButtonClicked() == DefaultDialog.BUTTON_CANCEL) {
                ConfigManager.getInstance().getMountPointManager().addMountPointEntry(mpt, MountPointUtils.getMountPointDefaultPathLabel(mpt), pathBackup);
            }


        });
        anyPanelConstraints.insets = new Insets(5, 2, 5, 2);
        anyPanel.add(editButton, anyPanelConstraints);
        anyPanelConstraints.gridx++;
        anyPanelConstraints.fill = GridBagConstraints.NONE;
        anyPanelConstraints.weightx = 0;
        JLabel resultLabel = new JLabel(IconManager.getIcon(IconManager.IconType.LOCK));
        resultLabel.setToolTipText("This mount point cannot be deleted");
        // patch used to harmonize elements
        if (path != null) {
            if (mountPointsToBeDisplayed.size() == 1) {
                resultLabel.setPreferredSize(new Dimension(50, 20));
            }
        }
        anyPanel.add(resultLabel);

        anyPanelConstraints.gridy++;
        if (mountPointsToBeDisplayed != null) {
            //iterator on the map to display remaining key-values
            for (String key : mountPointsToBeDisplayed.keySet()) {
                if (key.equals(MountPointUtils.getMountPointDefaultPathLabel(mpt)))
                    continue;
                anyPanelConstraints.gridx = 0;
                anyPanelConstraints.weightx = 0;

                anyPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
                JLabel field = new JLabel(key + " :", SwingConstants.RIGHT);
                field.setPreferredSize(new Dimension(getMaximumSizesOfJLabels() + 8, 20));
                anyPanelConstraints.anchor = GridBagConstraints.EAST;

                anyPanel.add(field, anyPanelConstraints);

                JTextField resultPath = new JTextField(mountPointsToBeDisplayed.get(key));
                // resultPath.setPreferredSize(new Dimension(300, 20));
                resultPath.setEditable(false);
                resultPath.setEnabled(false);
                if (mountPointsToBeDisplayed.get(key).length() > 87) {
                    resultPath.setEnabled(true);
                    resultPath.setEditable(true);
                }
                if (mountPointsToBeDisplayed.get(key).equals("")) {
                    resultPath.setBackground(softErrorColor);
                    resultPath.setText("This path is not valid please enter a path for this mount point");

                }
                anyPanelConstraints.gridx++;
                anyPanelConstraints.anchor = GridBagConstraints.WEST;
                anyPanelConstraints.weightx = 1;
                anyPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
                if (pathWrong.contains(mountPointsToBeDisplayed.get(key))) {
                    resultPath.setEnabled(true);
                    resultPath.setEditable(true);
                    resultPath.setForeground(errorColor);
                    resultPath.setToolTipText("This path does not exist you should delete it by clicking on the delete button ");

                }
                anyPanelConstraints.insets = new Insets(0, 5, 0, 5);
                anyPanel.add(resultPath, anyPanelConstraints);
                anyPanelConstraints.fill = GridBagConstraints.NONE;
                anyPanelConstraints.weightx = 0;
                anyPanelConstraints.gridx++;
                JButton editButton2 = new JButton(IconManager.getIcon(IconManager.IconType.EDIT));
                editButton2.setSize(20, 30);
                editButton2.setHorizontalAlignment(SwingConstants.CENTER);
                editButton2.setToolTipText("Click to edit mounting point");
                editButton2.addActionListener(e -> {

                    pathBackup = mountPointsToBeDisplayed.get(key);
                    labelEdited = key;
                    ConfigManager.getInstance().getMountPointManager().deleteMountPointEntry(mpt, key, false);
                    FolderEditDialog editDialog = new FolderEditDialog(ConfigWindow.getInstance(), FolderEditDialog.TypeOfDialog.EditingMpts, pathBackup, labelEdited, mpt);
                    //  editDialog.centerToWindow(ConfigWindow.getInstance());
                    editDialog.setLocationRelativeTo(help);
                    editDialog.setSize(500, 200);
                    editDialog.setVisible(true);
                    if (editDialog.getButtonClicked() == DefaultDialog.BUTTON_OK) {
                        valuesInsideDialog = editDialog.getValuesEntered();
                        addFolderAction();
                    }
                    if (editDialog.getButtonClicked() == DefaultDialog.BUTTON_CANCEL) {
                        ConfigManager.getInstance().getMountPointManager().addMountPointEntry(mpt, labelEdited, pathBackup);
                    }

                });
                anyPanelConstraints.insets = new Insets(5, 2, 5, 2);
                anyPanel.add(editButton2, anyPanelConstraints);
                JButton clearButton = new JButton(IconManager.getIcon(IconManager.IconType.TRASH));
                clearButton.setSize(10, 20);

                clearButton.setHorizontalAlignment(SwingConstants.CENTER);
                clearButton.setToolTipText("Click to delete mount point");
                //clearButton.setText("Delete");

                clearButton.addActionListener(e -> {
                    deleteFolderPath(mpt, key, false);
                });
                anyPanelConstraints.gridx++;
                anyPanel.add(clearButton, anyPanelConstraints);
                anyPanelConstraints.gridy++;

            }
        }
    }


    private int getMaximumSizesOfJLabels() {
        int maximum = 0;
        JLabel masotLabel = new JLabel(MountPointUtils.getMountPointDefaultPathLabel(MountPointUtils.MountPointType.RESULT));
        int size1 = getSizeInPixels(masotLabel);
        JLabel mzdbPanel = new JLabel((MountPointUtils.getMountPointDefaultPathLabel(MountPointUtils.MountPointType.MZDB)));
        int size2 = getSizeInPixels(mzdbPanel);
        JLabel fastaLabel = new JLabel("Folder: " + valueOf(1));
        int size3 = getSizeInPixels(fastaLabel);
        maximum = Math.max(size1, size2);
        maximum = Math.max(maximum, size3);

        return maximum;


    }


    private void initFastaFolderPanel(JPanel fastaListPanel) {

        fastaListPanel.setBorder(BorderFactory.createTitledBorder("Fasta files folders"));
        GridBagConstraints fastaListPanelConstraints = new GridBagConstraints();

        fastaListPanelConstraints.insets = new Insets(5, 5, 5, 5);
        fastaListPanelConstraints.gridx = 0;
        fastaListPanelConstraints.gridy = 0;


        List<String> fastaToBeDisplayed = ConfigManager.getInstance().getParsingRulesManager().getFastaPaths();
        List<String> wrongFastaDirectories = ConfigManager.getInstance().getParsingRulesManager().getInvalidFastaPaths();
        if (fastaToBeDisplayed != null) {
            for (int k = 0; k < fastaToBeDisplayed.size(); k++) {
                boolean errorInThePath = wrongFastaDirectories != null && wrongFastaDirectories.contains(fastaToBeDisplayed.get(k));
                fastaListPanelConstraints.gridx = 0;
                fastaListPanelConstraints.weightx = 0;
                fastaListPanelConstraints.anchor = GridBagConstraints.EAST;
                fastaListPanelConstraints.fill = GridBagConstraints.NONE;
                JLabel fastaLabel = new JLabel("Folder    " + valueOf(k + 1) + ":", SwingConstants.RIGHT);
                fastaLabel.setPreferredSize(new Dimension(getMaximumSizesOfJLabels() + 8, 20));

                fastaListPanel.add(fastaLabel, fastaListPanelConstraints);
                JTextField pathFasta = new JTextField(fastaToBeDisplayed.get(k));
                pathFasta.setEnabled(false);
                if (errorInThePath) {
                    pathFasta.setEnabled(true);
                    pathFasta.setEditable(true);
                    pathFasta.setForeground(errorColor);
                    pathFasta.setToolTipText("This path is not valid");
                }
                if (fastaToBeDisplayed.get(k).equals("")) {
                    pathFasta.setBackground(softErrorColor);
                    pathFasta.setForeground(Color.WHITE);
                    pathFasta.setText("This path is not valid please enter a path for this mount point");
                }

                fastaListPanelConstraints.gridx++;
                fastaListPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
                fastaListPanelConstraints.anchor = GridBagConstraints.WEST;
                fastaListPanelConstraints.weightx = 1;

                fastaListPanel.add(pathFasta, fastaListPanelConstraints);
                fastaListPanelConstraints.gridx++;


                JButton editButton = new JButton(IconManager.getIcon(IconManager.IconType.EDIT));
                editButton.setToolTipText("Click to repair mounting point");
                final int kFinal = k;
                final String pathedited = fastaToBeDisplayed.get(k);

                editButton.addActionListener(e -> {

                    pathBackup = pathedited;
                    boolean deleteSuccess = ConfigManager.getInstance().getParsingRulesManager().deleteFastaFolder(pathBackup);

                    FolderEditDialog editFastaDialog = new FolderEditDialog(ConfigWindow.getInstance(), FolderEditDialog.TypeOfDialog.EditingFastas, pathBackup, null, null);
                    // editFastaDialog.centerToWindow(ConfigWindow.getInstance());
                    editFastaDialog.setLocationRelativeTo(help);
                    editFastaDialog.setSize(500, 200);
                    editFastaDialog.setVisible(true);

                    if (editFastaDialog.getButtonClicked() == DefaultDialog.BUTTON_OK) {

                        valuesInsideDialog = editFastaDialog.getValuesEntered();

                        addFolderAction();
                    }
                    if (editFastaDialog.getButtonClicked() == DefaultDialog.BUTTON_CANCEL) {

                        ConfigManager.getInstance().getParsingRulesManager().addFastaFolder(pathBackup);

                    }


                });

                JButton clearButton = new JButton(IconManager.getIcon(IconManager.IconType.TRASH));

                clearButton.addActionListener(e -> {
                    deleteFastaFolder(fastaToBeDisplayed.get(kFinal));
                });

                editButton.setHorizontalAlignment(SwingConstants.CENTER);
                fastaListPanelConstraints.weightx = 0;
                fastaListPanelConstraints.anchor = GridBagConstraints.WEST;
                fastaListPanelConstraints.fill = GridBagConstraints.NONE;
                fastaListPanelConstraints.insets = new Insets(5, 2, 5, 2);
                fastaListPanel.add(editButton, fastaListPanelConstraints);


                clearButton.setHorizontalAlignment(SwingConstants.CENTER);
                fastaListPanelConstraints.weightx = 0;
                fastaListPanelConstraints.gridx++;
                fastaListPanelConstraints.anchor = GridBagConstraints.EAST;
                fastaListPanel.add(clearButton, fastaListPanelConstraints);
                fastaListPanelConstraints.gridy++;

            }
        }
    }

    private void deleteFolderPath(MountPointUtils.MountPointType mountPointType, String key, boolean forced) {
        boolean deleteConfirmation = Popup.yesNo("Are you sure you want to delete this Folder?");
        if (deleteConfirmation) {
            boolean deleteSucces = ConfigManager.getInstance().getMountPointManager().deleteMountPointEntry(mountPointType, key, forced);
            if (deleteSucces) {
                updateJpanel();
            } else {
                Popup.warning("The mounting point has not been deleted");
            }
        }

    }


    private void deleteFastaFolder(String path) {

        boolean deleteConfirmation = Popup.yesNo("Are you sure you want to delete this Folder?");
        if (deleteConfirmation) {
            boolean deleteSucess = ConfigManager.getInstance().getParsingRulesManager().deleteFastaFolder(path);
            if (deleteSucess) {
                updateJpanel();

            } else {
                Popup.warning("Error while deleting the directory");
            }
        }

    }

    public boolean addFolderAction() {

        // verifications are done inside FolderEditDialog

        String folderField = valuesInsideDialog.get(0);
        String folderPath = valuesInsideDialog.get(1);
        String type = valuesInsideDialog.get(2);


        boolean overallSuccess = false;
        if (true) {
            switch (type) {
                case "Result folder":
                    boolean addSuccessResult = ConfigManager.getInstance().getMountPointManager().addMountPointEntry(MountPointUtils.MountPointType.RESULT, folderField, folderPath);
                    if (addSuccessResult) {

                        updateJpanel();
                        ConfigWindow.getInstance().pack();
                        overallSuccess = true;

                    } else {
                        Popup.warning("Error while adding the mounting point");


                    }
                    break;

                case "mzDB folder":
                    boolean addSuccessMzdb = ConfigManager.getInstance().getMountPointManager().addMountPointEntry(MountPointUtils.MountPointType.MZDB, folderField, folderPath);
                    if (addSuccessMzdb) {

                        updateJpanel();
                        ConfigWindow.getInstance().pack();
                        overallSuccess = true;
                    } else {
                        Popup.warning("Error while adding the mounting point");

                    }
                    break;
                case "Raw folders":
                    boolean addSuccesRaw = ConfigManager.getInstance().getMountPointManager().addMountPointEntry(MountPointUtils.MountPointType.RAW, folderField, folderPath);
                    if (addSuccesRaw) {

                        updateJpanel();
                        ConfigWindow.getInstance().pack();
                        overallSuccess = true;
                    } else {
                        Popup.warning("Error while adding the mounting point");


                    }
                    break;
                case "Fasta folder":
                    boolean addSuccesFasta = ConfigManager.getInstance().getParsingRulesManager().addFastaFolder(folderPath);
                    if (addSuccesFasta) {

                        updateJpanel();
                        ConfigWindow.getInstance().pack();
                        overallSuccess = true;


                    } else {
                        Popup.warning("Errror while adding Fasta folder");


                    }

                    break;

                // TODO not implemented
                default:
                    Popup.error("no such type of Folder");

                    break;
            }
            revalidate();
            repaint();
        }
        return overallSuccess;
    }


    public void updateValues() {
        updateJpanel();
        ConfigWindow.getInstance().pack();
//
        // TODO
    }

    // calculate the size in Pixels of the text inside a JLabel
    private int getSizeInPixels(JLabel jLabel) {
        Font fontused = jLabel.getFont();
        FontMetrics fm = jLabel.getFontMetrics(fontused);
        String stringInTextField = jLabel.getText();
        return fm.stringWidth(stringInTextField);

    }


    public static void main(String[] args) {
        // ConfigWindow.getInstance();
        ConfigManager.getInstance().initialize();
        // ConfigManager.getInstance().getMountPointManager().getMountPointMap();
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            JFrame f = new JFrame();
            f.add(new FolderPanel());
            f.setVisible(true);
            f.setSize(800, 800);
            f.setAlwaysOnTop(true);
            f.pack();

            f.setVisible(true);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                 UnsupportedLookAndFeelException e) {
            throw new RuntimeException(e);
        }


    }

}
