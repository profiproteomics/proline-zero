package fr.proline.zero.gui;

import fr.proline.studio.gui.DefaultDialog;
import fr.proline.studio.utils.IconManager;
import fr.proline.zero.util.ConfigManager;
import fr.proline.zero.util.MountPointUtils;
import fr.proline.zero.util.SettingsConstant;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.lang.String.valueOf;

/**
 * Panel that displays mountpoints: MZDB and RESULT plus Fastas if sequence repository is activated
 * allows editing of these mountpoints
 *
 * @see ConfigWindow
 */

public class FolderPanel extends JPanel {


    private final static Color DEFAULT_LABEL_COLOR = new Color(50, 0, 230);

    private final static Color ERROR_COLOR = new Color(190, 0, 0);
    private final static Color WARNING_COLOR = new Color(230, 230, 230);
    private final static Color LABEL_ERROR = new Color(180, 0, 0);

    private final static Color JTEXT_COLOR = new Color(5, 5, 5);


    private String labelEdited;
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
        HelpHeaderPanel help = new HelpHeaderPanel("Folder", SettingsConstant.FOLDERS_HELP_PANE);
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

        add(new JLabel("Add folder :"), folderPanelConstraints);

        JButton openAddDialogButton = new JButton(IconManager.getIcon(IconManager.IconType.PLUS_16X16));
        openAddDialogButton.addActionListener(e -> openAddDialog());
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
        addDialog.setSize(450, 200);
        addDialog.centerToWindow(ConfigWindow.getInstance());
        addDialog.setVisible(true);

        if (addDialog.getButtonClicked() == DefaultDialog.BUTTON_OK) {
            valuesInsideDialog = addDialog.getValuesEntered();
            addFolderAction();
        }
    }

    //VDS TODO: test lighter update method...
    private void updateJPanel() {
        removeAll();
        initialize();
        revalidate();
        repaint();
    }


    private JPanel createFolderListPanel() {
        // creation du panel et layout
        JPanel folderListPanel = new JPanel(new GridBagLayout());
        folderListPanel.setBorder(BorderFactory.createTitledBorder(" List of folders "));
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

        initAnyFolderPanel(MountPointUtils.MountPointType.RESULT, resultListPanel);
        folderListPanel.add(resultListPanel, c);

        JPanel mzDBListPanel = new JPanel(new GridBagLayout());

        initAnyFolderPanel(MountPointUtils.MountPointType.MZDB, mzDBListPanel);

        c.gridy++;
        folderListPanel.add(mzDBListPanel, c);

        c.gridy++;

        JPanel fastaListPanel = new JPanel(new GridBagLayout());
        initFastaFolderPanel(fastaListPanel);


        folderListPanel.add(fastaListPanel, c);


        return folderListPanel;
    }


    private void initAnyFolderPanel(MountPointUtils.MountPointType mountPointType, JPanel anyPanel) {
        List<String> pathWrong = ConfigManager.getInstance().getMountPointManager().getInvalidPaths();

        List<String> labelsDuplicate = ConfigManager.getInstance().getMountPointManager().getNonUniqueLabels(ConfigManager.getInstance().getMountPointManager().getMountPointMap());
        List<String> listOfDuplicates = ConfigManager.getInstance().getMountPointManager().getDuplicatePaths();
        Map<String, String> mountPointsToBeDisplayed = ConfigManager.getInstance().getMountPointManager().getMountPointMap().get(mountPointType);

        GridBagConstraints anyPanelConstraints = new GridBagConstraints();
        anyPanel.setBorder(BorderFactory.createTitledBorder("  " + mountPointType.getDisplayString() + "  "));
        anyPanelConstraints.insets = new Insets(5, 5, 5, 5);
        anyPanelConstraints.gridx = 0;
        anyPanelConstraints.gridy = 0;
        anyPanelConstraints.weightx = 0;
        anyPanelConstraints.fill = GridBagConstraints.HORIZONTAL;

        //places none deletable default mounting point at the top of the list

        JLabel fieldInitial = new JLabel(MountPointUtils.getMountPointDefaultPathLabel(mountPointType) + ": ", SwingConstants.RIGHT);

        fieldInitial.setForeground(DEFAULT_LABEL_COLOR);
        anyPanelConstraints.anchor = GridBagConstraints.EAST;
        fieldInitial.setEnabled(true);
        fieldInitial.setPreferredSize(new Dimension(getMaximumSizesOfJLabels() + 8, 20));
        anyPanel.add(fieldInitial, anyPanelConstraints);
        String path = null;
        JTextField jTextPathInitial;
        boolean defaultMountPointHasAWrongPath = false;

        boolean defaultMountPointLabelIsDuplicate = false;
        boolean defaultMountPointIsDuplicate = false;
        if (mountPointsToBeDisplayed != null) {

            path = mountPointsToBeDisplayed.get(MountPointUtils.getMountPointDefaultPathLabel(mountPointType));
            jTextPathInitial = new JTextField(path);

            defaultMountPointHasAWrongPath = pathWrong.contains(path);
            // check if the label is duplicate
            defaultMountPointLabelIsDuplicate = labelsDuplicate.contains(MountPointUtils.getMountPointDefaultPathLabel(mountPointType));
            defaultMountPointIsDuplicate = listOfDuplicates.contains(path);

          ;

        } else {
            jTextPathInitial = new JTextField();
        }


        anyPanelConstraints.gridx++;
        anyPanelConstraints.anchor = GridBagConstraints.WEST;
        anyPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
        anyPanelConstraints.weightx = 1;


        if (path != null) {
            if (path.equals("")) {

                jTextPathInitial.setText("Missing path please enter a path for this mount point");
                changeJTextFieldLook(jTextPathInitial, ERROR_COLOR, Color.WHITE, "empty path ");
            } else if (defaultMountPointHasAWrongPath) {
                changeJTextFieldLook(jTextPathInitial, ERROR_COLOR, Color.WHITE, "This path is not valid");
            } else if (defaultMountPointLabelIsDuplicate) {

                changeJTextFieldLook(jTextPathInitial, LABEL_ERROR, Color.WHITE, "This mount point has not unique label");
                fieldInitial.setForeground(LABEL_ERROR);
            } else if (defaultMountPointIsDuplicate) {

                changeJTextFieldLook(jTextPathInitial, WARNING_COLOR, JTEXT_COLOR, "This path is a duplicate");

            } else {
                jTextPathInitial.setEnabled(false);

            }


        } else {
            // treats the case where default mounting point is not present (path==null)
            jTextPathInitial.setText("This mount point doesn't exist ");
            changeJTextFieldLook(jTextPathInitial, ERROR_COLOR, Color.WHITE, "This default mounting point is missing");
        }
        anyPanel.add(jTextPathInitial, anyPanelConstraints);
        int sizeOfJText = getSizeInPixels(jTextPathInitial);
        if (sizeOfJText > 350) {
            jTextPathInitial.setEnabled(true);
            jTextPathInitial.setEditable(true);
        }

        anyPanelConstraints.fill = GridBagConstraints.NONE;
        anyPanelConstraints.weightx = 0;
        anyPanelConstraints.gridx++;

        JButton editDefaultMountPoint = new JButton(IconManager.getIcon(IconManager.IconType.EDIT));
        editDefaultMountPoint.setHorizontalAlignment(SwingConstants.CENTER);
        editDefaultMountPoint.setToolTipText("Click to edit ");
        editDefaultMountPoint.setSize(20, 15);

        String finalPath = path;
        editDefaultMountPoint.addActionListener(e -> {


            pathBackup = finalPath;

            ConfigManager.getInstance().getMountPointManager().deleteMountPointEntry(mountPointType,MountPointUtils.getMountPointDefaultPathLabel(mountPointType),true);


            FolderEditDialog editDialog = new FolderEditDialog(ConfigWindow.getInstance(), FolderEditDialog.TypeOfDialog.EditingDefaultMPts, pathBackup, null, mountPointType);
            editDialog.setSize(500, 200);
            editDialog.centerToWindow(ConfigWindow.getInstance());

            editDialog.setVisible(true);
            if (editDialog.getButtonClicked() == DefaultDialog.BUTTON_OK) {
                valuesInsideDialog = editDialog.getValuesEntered();
                addFolderAction();
            }
            if (editDialog.getButtonClicked() == DefaultDialog.BUTTON_CANCEL) {

                ConfigManager.getInstance().getMountPointManager().cancelDeleteMountPointEntry(mountPointType, MountPointUtils.getMountPointDefaultPathLabel(mountPointType), pathBackup);
            }


        });
        anyPanelConstraints.insets = new Insets(5, 2, 5, 2);
        anyPanel.add(editDefaultMountPoint, anyPanelConstraints);
        anyPanelConstraints.gridx++;
        anyPanelConstraints.fill = GridBagConstraints.NONE;
        anyPanelConstraints.weightx = 0;
        JLabel resultLabel = new JLabel(IconManager.getIcon(IconManager.IconType.LOCK));
        resultLabel.setToolTipText("This mount point cannot be deleted");

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
                if (key.equals(MountPointUtils.getMountPointDefaultPathLabel(mountPointType)))
                    continue;
                anyPanelConstraints.gridx = 0;
                anyPanelConstraints.weightx = 0;

                anyPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
                JLabel field = new JLabel(key + " :", SwingConstants.RIGHT);
                field.setPreferredSize(new Dimension(getMaximumSizesOfJLabels() + 8, 20));
                anyPanelConstraints.anchor = GridBagConstraints.EAST;

                anyPanel.add(field, anyPanelConstraints);

                JTextField jTextMountPointPath = new JTextField(mountPointsToBeDisplayed.get(key));

                jTextMountPointPath.setEditable(false);
                jTextMountPointPath.setEnabled(false);

                if (mountPointsToBeDisplayed.get(key).equals("")) {
                    jTextMountPointPath.setText("This path is empty please enter a path for this mount point");
                    changeJTextFieldLook(jTextMountPointPath, ERROR_COLOR, Color.WHITE, "This path is empty please enter a path for this mount point");
                } else if (pathWrong.contains(mountPointsToBeDisplayed.get(key))) {

                    changeJTextFieldLook(jTextMountPointPath, ERROR_COLOR, Color.WHITE, "This path is not valid");
                } else if (labelsDuplicate.contains(key)) {

                    changeJTextFieldLook(jTextMountPointPath, LABEL_ERROR, Color.WHITE, "this mount point has not unique label");
                    field.setForeground(LABEL_ERROR);
                } else if (listOfDuplicates.contains(mountPointsToBeDisplayed.get(key))) {

                    changeJTextFieldLook(jTextMountPointPath, WARNING_COLOR, JTEXT_COLOR, "This path is a duplicate ");

                }
                anyPanelConstraints.gridx++;
                anyPanelConstraints.anchor = GridBagConstraints.WEST;
                anyPanelConstraints.weightx = 1;
                anyPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
                int sizeOfJtext = getSizeInPixels(jTextMountPointPath);

                if (sizeOfJtext > 350) {
                    jTextMountPointPath.setEnabled(true);
                    jTextMountPointPath.setEditable(true);
                }

                anyPanelConstraints.insets = new Insets(0, 5, 0, 5);
                anyPanel.add(jTextMountPointPath, anyPanelConstraints);
                anyPanelConstraints.fill = GridBagConstraints.NONE;
                anyPanelConstraints.weightx = 0;
                anyPanelConstraints.gridx++;
                JButton editMPointButton = new JButton(IconManager.getIcon(IconManager.IconType.EDIT));
                editMPointButton.setSize(20, 30);
                editMPointButton.setHorizontalAlignment(SwingConstants.CENTER);
                editMPointButton.setToolTipText("Click to edit mounting point");

                editMPointButton.setEnabled(true);
                final String pathClone = mountPointsToBeDisplayed.get(key);
                final String labelClone = key;

                editMPointButton.addActionListener(e -> {

                    pathBackup = pathClone;
                    labelEdited = labelClone;
                    ConfigManager.getInstance().getMountPointManager().deleteMountPointEntry(mountPointType, key, false);
                    FolderEditDialog editDialog = new FolderEditDialog(ConfigWindow.getInstance(), FolderEditDialog.TypeOfDialog.EditingMpts, pathBackup, labelEdited, mountPointType);
                    editDialog.setSize(500, 200);
                    editDialog.centerToWindow(ConfigWindow.getInstance());


                    editDialog.setVisible(true);
                    if (editDialog.getButtonClicked() == DefaultDialog.BUTTON_OK) {
                        valuesInsideDialog = editDialog.getValuesEntered();
                        addFolderAction();
                    }
                    if (editDialog.getButtonClicked() == DefaultDialog.BUTTON_CANCEL) {


                        ConfigManager.getInstance().getMountPointManager().cancelDeleteMountPointEntry(mountPointType, labelEdited, pathBackup);

                    }
                    if (editDialog.getButtonClicked() == DefaultDialog.BUTTON_BACK) {

                        pathBackup = pathClone;
                        labelEdited = labelClone;

                    }

                });
                anyPanelConstraints.insets = new Insets(5, 2, 5, 2);
                anyPanel.add(editMPointButton, anyPanelConstraints);
                JButton clearButton = new JButton(IconManager.getIcon(IconManager.IconType.TRASH));
                clearButton.setSize(10, 20);

                clearButton.setHorizontalAlignment(SwingConstants.CENTER);
                clearButton.setToolTipText("Click to delete mount point");

                clearButton.addActionListener(e -> {
                    deleteFolderPath(mountPointType, key, false);
                });
                anyPanelConstraints.gridx++;
                anyPanel.add(clearButton, anyPanelConstraints);
                anyPanelConstraints.gridy++;

            }
        }
    }

    private int getMaximumSizesOfJLabels() {
        int maximum;
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
        fastaListPanel.setBorder(BorderFactory.createTitledBorder("  Fasta files folders  "));
        GridBagConstraints fastaListPanelConstraints = new GridBagConstraints();

        fastaListPanelConstraints.insets = new Insets(5, 5, 5, 5);
        fastaListPanelConstraints.gridx = 0;
        fastaListPanelConstraints.gridy = 0;


        List<String> fastaToBeDisplayed = ConfigManager.getInstance().getParsingRulesManager().getFastaPaths();
        List<String> wrongFastaDirectories = ConfigManager.getInstance().getParsingRulesManager().getInvalidFastaPaths();
        // TODO
        List<String> duplicatePaths = ConfigManager.getInstance().getMountPointManager().getDuplicatePaths();

        if (ConfigManager.getInstance().isSeqRepActive()) {
            if (fastaToBeDisplayed != null) {
                for (int k = 0; k < fastaToBeDisplayed.size(); k++) {
                    boolean errorInThePath = wrongFastaDirectories != null && wrongFastaDirectories.contains(fastaToBeDisplayed.get(k));

                    boolean pathBelongsToDuplicate = duplicatePaths.contains(fastaToBeDisplayed.get(k));
                    fastaListPanelConstraints.gridx = 0;
                    fastaListPanelConstraints.weightx = 0;
                    fastaListPanelConstraints.anchor = GridBagConstraints.EAST;
                    fastaListPanelConstraints.fill = GridBagConstraints.NONE;
                    JLabel fastaLabel = new JLabel("Folder    " + valueOf(k + 1) + ":", SwingConstants.RIGHT);
                    fastaLabel.setPreferredSize(new Dimension(getMaximumSizesOfJLabels() + 8, 20));

                    fastaListPanel.add(fastaLabel, fastaListPanelConstraints);
                    JTextField jTextFieldPathFasta = new JTextField(fastaToBeDisplayed.get(k));
                    jTextFieldPathFasta.setEnabled(false);
                    if (errorInThePath) {
                        changeJTextFieldLook(jTextFieldPathFasta, ERROR_COLOR, Color.WHITE, "This path is not valid");
                    }
                    if (fastaToBeDisplayed.get(k).equals("")) {
                        jTextFieldPathFasta.setText("Path does not exist");
                        changeJTextFieldLook(jTextFieldPathFasta, ERROR_COLOR, Color.WHITE, "Missing path please enter a path for this mount point");
                    }

                    if (pathBelongsToDuplicate) {
                        changeJTextFieldLook(jTextFieldPathFasta, WARNING_COLOR, JTEXT_COLOR, "This path is duplicate");
                    }

                    int sizeOfJTextField = getSizeInPixels(jTextFieldPathFasta);
                    if (sizeOfJTextField > 350) {
                        jTextFieldPathFasta.setEditable(true);
                        jTextFieldPathFasta.setEnabled(true);
                    }


                    fastaListPanelConstraints.gridx++;
                    fastaListPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
                    fastaListPanelConstraints.anchor = GridBagConstraints.WEST;
                    fastaListPanelConstraints.weightx = 1;

                    fastaListPanel.add(jTextFieldPathFasta, fastaListPanelConstraints);
                    fastaListPanelConstraints.gridx++;


                    JButton editButton = new JButton(IconManager.getIcon(IconManager.IconType.EDIT));

                    editButton.setToolTipText("Click to repair mounting point");
                    final int kFinal = k;
                    final String pathEdited = fastaToBeDisplayed.get(k);

                    editButton.setEnabled(true);
                    editButton.addActionListener(e -> {

                        pathBackup = pathEdited;
                        boolean deleteSuccess = ConfigManager.getInstance().getParsingRulesManager().deleteFastaFolder(pathBackup);

                        FolderEditDialog editFastaDialog = new FolderEditDialog(ConfigWindow.getInstance(), FolderEditDialog.TypeOfDialog.EditingFastas, pathBackup, null, null);
                        editFastaDialog.setSize(500, 200);
                        editFastaDialog.centerToWindow(ConfigWindow.getInstance());


                        editFastaDialog.setVisible(true);

                        if (editFastaDialog.getButtonClicked() == DefaultDialog.BUTTON_OK) {
                            valuesInsideDialog = editFastaDialog.getValuesEntered();
                            addFolderAction();
                        }
                        if (editFastaDialog.getButtonClicked() == DefaultDialog.BUTTON_CANCEL) {

                            ConfigManager.getInstance().getParsingRulesManager().addFastaFolder(pathBackup, true);

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
        } else {
            fastaListPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
            JLabel SeqRepoDeactivated = new JLabel("Sequence repository deactivated");
            SeqRepoDeactivated.setIcon(IconManager.getIcon(IconManager.IconType.WARNING));
            SeqRepoDeactivated.setToolTipText("Activate Sequence repository to view fasta directories \n (at the top of the window)");
            fastaListPanel.add(SeqRepoDeactivated, fastaListPanelConstraints);

        }
    }

    private void deleteFolderPath(MountPointUtils.MountPointType mountPointType, String key, boolean forced) {

        boolean deleteConfirmation = Popup.yesNoCenterToComponent(ConfigWindow.getInstance(), "Are you sure you want to delete this Folder?");
        if (deleteConfirmation) {
            boolean deleteSucces = ConfigManager.getInstance().getMountPointManager().deleteMountPointEntry(mountPointType, key, forced);
            if (deleteSucces) {
                updateJPanel();
            } else {
                Popup.warning("The mounting point has not been deleted");

            }
        }

    }


    private void deleteFastaFolder(String path) {

        boolean deleteConfirmation = Popup.yesNoCenterToComponent(ConfigWindow.getInstance(), "Are you sure you want to delete this Folder?");

        if (deleteConfirmation) {
            boolean deleteSucess = ConfigManager.getInstance().getParsingRulesManager().deleteFastaFolder(path);
            if (deleteSucess) {
                updateJPanel();

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

        switch (type) {
            case "Result folder":
                boolean addSuccessResult = ConfigManager.getInstance().getMountPointManager().addMountPointEntry(MountPointUtils.MountPointType.RESULT, folderField, folderPath);
                if (addSuccessResult) {

                    updateJPanel();
                    ConfigWindow.getInstance().pack();
                    overallSuccess = true;

                } else {
                    Popup.warning("Error while adding the mounting point");


                }
                break;

            case "mzDB folder":
                boolean addSuccessMzdb = ConfigManager.getInstance().getMountPointManager().addMountPointEntry(MountPointUtils.MountPointType.MZDB, folderField, folderPath);
                if (addSuccessMzdb) {

                    updateJPanel();
                    ConfigWindow.getInstance().pack();
                    overallSuccess = true;
                } else {
                    Popup.warning("Error while adding the mounting point");

                }
                break;
            case "Raw folders":
                boolean addSuccessRaw = ConfigManager.getInstance().getMountPointManager().addMountPointEntry(MountPointUtils.MountPointType.RAW, folderField, folderPath);
                if (addSuccessRaw) {

                    updateJPanel();
                    ConfigWindow.getInstance().pack();
                    overallSuccess = true;
                } else {
                    Popup.warning("Error while adding the mounting point");


                }
                break;
            case "Fasta folder":
                boolean addSuccesFasta = ConfigManager.getInstance().getParsingRulesManager().addFastaFolder(folderPath, false);
                if (addSuccesFasta) {

                    updateJPanel();
                    ConfigWindow.getInstance().pack();
                    overallSuccess = true;


                } else {
                    Popup.warning("Error while adding Fasta folder  ");


                }

                break;


            default:
                Popup.error("no such type of Folder");


                break;
        }
        revalidate();
        repaint();

        return overallSuccess;
    }


    public void updateValues() {
        updateJPanel();
        ConfigWindow.getInstance().pack();
        //// TODO
    }

    // calculate the size in Pixels of the text inside a JLabel
    private int getSizeInPixels(JLabel jLabel) {
        Font fontUsed = jLabel.getFont();
        FontMetrics fm = jLabel.getFontMetrics(fontUsed);
        String stringInTextField = jLabel.getText();
        return fm.stringWidth(stringInTextField);

    }


    private int getSizesOfString(String string) {
        //TODO get the right font and size in proline zero
        Font font = new Font("Verdana", Font.PLAIN, 10);
        FontMetrics metrics = new FontMetrics(font) {
        };
        Rectangle2D bounds = metrics.getStringBounds(string, null);
        int widthInPixels = (int) bounds.getWidth();
        return widthInPixels;
    }

    private int getSizeInPixels(JTextField jTextField) {
        Font fontUsed = jTextField.getFont();
        FontMetrics fontMetrics = jTextField.getFontMetrics(fontUsed);
        String stringInTextField = jTextField.getText();
        return fontMetrics.stringWidth(stringInTextField);

    }

    private void changeJTextFieldLook(JTextField jTextField, Color backGroundColor, Color textColor, String toolTipMessage) {
        jTextField.setBackground(backGroundColor);
        if (!jTextField.isEnabled()) {
            jTextField.setDisabledTextColor(textColor);
        } else {
            jTextField.setForeground(textColor);
        }
        jTextField.setToolTipText(toolTipMessage);
    }


}
