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

/**
 * Panel that displays mountpoints: MZDB and RESULT plus Fastas if sequence repository is activated
 * allows editing of these mountpoints
 *
 * @see ConfigWindow
 */

public class FolderPanel extends JPanel {


    private final static Color DEFAULT_LABEL_COLOR = new Color(40, 0, 230);

    private final static Color ERROR_COLOR = new Color(145, 147, 154);
    private final static Color WARNING_COLOR = new Color(145, 147, 154);
    private final static Color LABEL_ERROR = new Color(180, 0, 0);

    private final static Color JTEXT_COLOR = new Color(5, 5, 5);

    private final static String duplicateToolTipText ="Warning at least two labels point on this folder";
    private final static String duplicateLabelToolTipText="the label of this mount point is shared by another mount point";
    private final static String misssingPathToolTipText="Missing path for this mount point";

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
        folderPanelConstraints.gridy++;
        folderPanelConstraints.gridx=0;

        if (!ConfigManager.getInstance().getMountPointManager().verif()){
            JLabel errorIcon=new JLabel(IconManager.getIcon(IconManager.IconType.WARNING));

            add(errorIcon,folderPanelConstraints);

        }
        else {
            JLabel noErrorIcon=new JLabel(IconManager.getIcon(IconManager.IconType.TICK_CIRCLE));

            add(noErrorIcon,folderPanelConstraints);
        }

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
            valuesInsideDialog = addDialog.getValuesFromEditDialog();
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


    private void initAnyFolderPanel(MountPointUtils.MountPointType mountPointType, JPanel folderPanel) {

        List<String> pathWrong = ConfigManager.getInstance().getMountPointManager().getInvalidPaths();
        List<String> labelsDuplicate = ConfigManager.getInstance().getMountPointManager().getNonUniqueLabels(ConfigManager.getInstance().getMountPointManager().getMountPointMap());
        List<String> listOfDuplicates = ConfigManager.getInstance().getMountPointManager().getDuplicatePaths();
        Map<String, String> mountPointsToBeDisplayed = ConfigManager.getInstance().getMountPointManager().getMountPointMap().get(mountPointType);

        GridBagConstraints folderPanelConstraints = new GridBagConstraints();
        folderPanel.setBorder(BorderFactory.createTitledBorder("  " + mountPointType.getDisplayString() + "  "));
        folderPanelConstraints.insets = new Insets(5, 5, 5, 5);
        folderPanelConstraints.gridx = 0;
        folderPanelConstraints.gridy = 0;
        folderPanelConstraints.weightx = 0;
        folderPanelConstraints.fill = GridBagConstraints.HORIZONTAL;

        //places none deletable default mounting point at the top of the list

        JLabel jLabelDefault = new JLabel(MountPointUtils.getMountPointDefaultPathLabel(mountPointType) + ": ", SwingConstants.RIGHT);

        jLabelDefault.setForeground(DEFAULT_LABEL_COLOR);
        folderPanelConstraints.anchor = GridBagConstraints.EAST;
        jLabelDefault.setEnabled(true);
        jLabelDefault.setPreferredSize(new Dimension(getMaximumSizesOfJLabels()+8 , 20));
        folderPanel.add(jLabelDefault, folderPanelConstraints);
        String pathDisplayed = null;
        JTextField jTextPathInitial;

        boolean defaultMountPointHasAWrongPath = false;
        boolean defaultMountPointLabelIsDuplicate = false;
        boolean defaultMountPointPathIsDuplicate = false;

        if (mountPointsToBeDisplayed != null) {

            pathDisplayed = mountPointsToBeDisplayed.get(MountPointUtils.getMountPointDefaultPathLabel(mountPointType));
            jTextPathInitial = new JTextField(pathDisplayed);
            defaultMountPointHasAWrongPath = pathWrong.contains(pathDisplayed);
            defaultMountPointLabelIsDuplicate = labelsDuplicate.contains(MountPointUtils.getMountPointDefaultPathLabel(mountPointType));
            defaultMountPointPathIsDuplicate = listOfDuplicates.contains(pathDisplayed);

        } else {
            jTextPathInitial = new JTextField();
        }

        if (pathDisplayed != null) {
            if (pathDisplayed.equals("")) {
                jTextPathInitial.setText("Missing path please enter a path for this mount point");
                changeJTextFieldLook(jTextPathInitial, ERROR_COLOR, Color.WHITE, misssingPathToolTipText);
            } else if (defaultMountPointHasAWrongPath) {
                changeJTextFieldLook(jTextPathInitial, ERROR_COLOR, Color.WHITE, "This path is not valid");
            } else if (defaultMountPointLabelIsDuplicate) {
                changeJTextFieldLook(jTextPathInitial, WARNING_COLOR, Color.WHITE, duplicateLabelToolTipText);
                jLabelDefault.setForeground(LABEL_ERROR);
            } else if (defaultMountPointPathIsDuplicate) {

                changeJTextFieldLook(jTextPathInitial,WARNING_COLOR,Color.BLACK,duplicateToolTipText);
            } else {
                jTextPathInitial.setEnabled(false);
            }


        } else {
            // treats the case where default mounting point is not present (path==null)
            jTextPathInitial.setText("Please add a path for this mount point");
            changeJTextFieldLook(jTextPathInitial, ERROR_COLOR, Color.WHITE, misssingPathToolTipText);
        }

        folderPanelConstraints.gridx++;
        folderPanelConstraints.anchor = GridBagConstraints.WEST;
        folderPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
        folderPanelConstraints.weightx = 1;
        folderPanel.add(jTextPathInitial, folderPanelConstraints);
        int sizeOfJText = getSizeInPixels(jTextPathInitial);
        if (sizeOfJText > 350) {
            jTextPathInitial.setEnabled(true);
            jTextPathInitial.setEditable(true);
        }

        folderPanelConstraints.fill = GridBagConstraints.NONE;
        folderPanelConstraints.weightx = 0;
        folderPanelConstraints.gridx++;

        JButton editDefaultMountPoint = new JButton(IconManager.getIcon(IconManager.IconType.EDIT));
        editDefaultMountPoint.setHorizontalAlignment(SwingConstants.CENTER);
        editDefaultMountPoint.setToolTipText("Click to edit ");
        editDefaultMountPoint.setSize(20, 15);

        final String  finalPath = pathDisplayed;
        editDefaultMountPoint.addActionListener(e -> {

            String labelEdited = MountPointUtils.getMountPointDefaultPathLabel(mountPointType);
            FolderEditDialog editDialog = new FolderEditDialog(ConfigWindow.getInstance(), FolderEditDialog.TypeOfDialog.EditingDefaultMPts, finalPath, labelEdited, mountPointType);
            editDialog.setSize(500, 200);
            editDialog.centerToWindow(ConfigWindow.getInstance());
            editDialog.setVisible(true);

            if (editDialog.getButtonClicked() == DefaultDialog.BUTTON_OK) {
                valuesInsideDialog = editDialog.getValuesFromEditDialog();
                String newLabel = valuesInsideDialog.get(0);
                String newPath = valuesInsideDialog.get(1);
                boolean updateSuccess = ConfigManager.getInstance().getMountPointManager().updateMountPointEntry(mountPointType, newLabel, newPath, labelEdited, finalPath);

                if (updateSuccess) {
                    updateJPanel();
                    ConfigWindow.getInstance().pack();
                } else {
                    Popup.warning("Error while editing MountPoint");
                }
            }
        });
        folderPanelConstraints.insets = new Insets(5, 2, 5, 2);
        folderPanel.add(editDefaultMountPoint, folderPanelConstraints);
        folderPanelConstraints.gridx++;
        folderPanelConstraints.fill = GridBagConstraints.NONE;
        folderPanelConstraints.weightx = 0;
        JLabel resultLabel = new JLabel(IconManager.getIcon(IconManager.IconType.LOCK));
        resultLabel.setToolTipText("This mount point cannot be deleted");
        if (pathDisplayed != null) {
            if (mountPointsToBeDisplayed.size() == 1) {
                resultLabel.setPreferredSize(new Dimension(50, 20));
            }
        }
        folderPanel.add(resultLabel);

        folderPanelConstraints.gridy++;
        if (mountPointsToBeDisplayed != null) {
            //iterator on the map to display remaining key-values
            for (String key : mountPointsToBeDisplayed.keySet()) {
                if (key.equals(MountPointUtils.getMountPointDefaultPathLabel(mountPointType)))
                    continue;
                folderPanelConstraints.gridx = 0;
                folderPanelConstraints.weightx = 0;

                folderPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
                JLabel label = new JLabel(key + " :", SwingConstants.RIGHT);
                label.setPreferredSize(new Dimension(getMaximumSizesOfJLabels()+8 , 20));
                folderPanelConstraints.anchor = GridBagConstraints.EAST;

                folderPanel.add(label, folderPanelConstraints);

                JTextField mountPointPathTField = new JTextField(mountPointsToBeDisplayed.get(key));

                mountPointPathTField.setEditable(false);
                mountPointPathTField.setEnabled(false);

                if (mountPointsToBeDisplayed.get(key).equals("")) {
                    mountPointPathTField.setText("This path is empty please enter a path for this mount point");
                    changeJTextFieldLook(mountPointPathTField, ERROR_COLOR, Color.WHITE, misssingPathToolTipText);

                } else if (pathWrong.contains(mountPointsToBeDisplayed.get(key))) {
                    changeJTextFieldLook(mountPointPathTField, ERROR_COLOR, Color.WHITE, "This path is not valid");

                } else if (labelsDuplicate.contains(key)) {
                    changeJTextFieldLook(mountPointPathTField, WARNING_COLOR, Color.WHITE, duplicateLabelToolTipText);

                    label.setForeground(LABEL_ERROR);

                } else if (listOfDuplicates.contains(mountPointsToBeDisplayed.get(key))) {

                    changeJTextFieldLook(mountPointPathTField,WARNING_COLOR,Color.WHITE,duplicateToolTipText);

                }
                folderPanelConstraints.gridx++;
                folderPanelConstraints.anchor = GridBagConstraints.WEST;
                folderPanelConstraints.weightx = 1;
                folderPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
                int sizeOfJtext = getSizeInPixels(mountPointPathTField);

                if (sizeOfJtext > 350) {
                    mountPointPathTField.setEnabled(true);
                    mountPointPathTField.setEditable(true);
                }

                folderPanelConstraints.insets = new Insets(0, 5, 0, 5);
                folderPanel.add(mountPointPathTField, folderPanelConstraints);
                folderPanelConstraints.fill = GridBagConstraints.NONE;
                folderPanelConstraints.weightx = 0;
                folderPanelConstraints.gridx++;
                JButton editMPointButton = new JButton(IconManager.getIcon(IconManager.IconType.EDIT));
                editMPointButton.setSize(20, 30);
                editMPointButton.setHorizontalAlignment(SwingConstants.CENTER);
                editMPointButton.setToolTipText("Click to edit mounting point");
                editMPointButton.setEnabled(true);
                final String pathClone = mountPointsToBeDisplayed.get(key);
                final String labelClone = key;
                editMPointButton.addActionListener(e -> {

                    FolderEditDialog editDialog = new FolderEditDialog(ConfigWindow.getInstance(), FolderEditDialog.TypeOfDialog.EditingMpts, pathClone, labelClone, mountPointType);
                    editDialog.setSize(500, 200);
                    editDialog.centerToWindow(ConfigWindow.getInstance());
                    editDialog.setVisible(true);

                    if (editDialog.getButtonClicked() == DefaultDialog.BUTTON_OK) {

                        valuesInsideDialog = editDialog.getValuesFromEditDialog();
                        String newLabel = valuesInsideDialog.get(0);
                        String newPath = valuesInsideDialog.get(1);

                        boolean updateSuccess = ConfigManager.getInstance().getMountPointManager().updateMountPointEntry(mountPointType, newLabel, newPath, labelClone, pathClone);

                        if (updateSuccess) {
                            updateJPanel();
                            ConfigWindow.getInstance().pack();
                        } else {
                            Popup.warning("error while editing MountPoint");
                        }
                    }
                });
                folderPanelConstraints.insets = new Insets(5, 2, 5, 2);
                folderPanel.add(editMPointButton, folderPanelConstraints);
                JButton clearButton = new JButton(IconManager.getIcon(IconManager.IconType.TRASH));
                clearButton.setSize(10, 20);

                clearButton.setHorizontalAlignment(SwingConstants.CENTER);
                clearButton.setToolTipText("Click to delete mount point");

                clearButton.addActionListener(e -> deleteFolderPath(mountPointType, key));
                folderPanelConstraints.gridx++;
                folderPanel.add(clearButton, folderPanelConstraints);
                folderPanelConstraints.gridy++;

            }
        }
    }

    /**
     * calculate sizes in pixels of labels and returns the maximum over
     * all labels displayed. will return a maximum value of 70 pixels
     *
     */
    private int getMaximumSizesOfJLabels() {

        int maximumSizesOfLabels=0;
        for (MountPointUtils.MountPointType mountPointType : MountPointUtils.MountPointType.values()) {
            Map<String, String> mountPointsToBeDisplayed = ConfigManager.getInstance().getMountPointManager().getMountPointMap().get(mountPointType);
            if (mountPointsToBeDisplayed!=null) {
                JLabel mounPointDefaultlabel=new JLabel(MountPointUtils.getMountPointDefaultPathLabel(mountPointType));
                maximumSizesOfLabels=Math.max(maximumSizesOfLabels,getSizeInPixels(mounPointDefaultlabel));
                for (String key : mountPointsToBeDisplayed.keySet()) {
                    if (key.equals(MountPointUtils.getMountPointDefaultPathLabel(mountPointType)))
                        continue;
                    JLabel mountPointLabel = new JLabel(key + ": ");
                    maximumSizesOfLabels = Math.max(maximumSizesOfLabels, getSizeInPixels(mountPointLabel));
                }
            }
        }
        List<String> fastaToBeDisplayed = ConfigManager.getInstance().getParsingRulesManager().getFastaPaths();
        for (int k=0;k< fastaToBeDisplayed.size();k++){
            JLabel fastaLabel=new JLabel("Folder: "+k);
            int size =getSizeInPixels(fastaLabel);
            maximumSizesOfLabels=Math.max(maximumSizesOfLabels,size);
        }

        maximumSizesOfLabels=Math.min(maximumSizesOfLabels,70);
        return maximumSizesOfLabels;
    }


    private void initFastaFolderPanel(JPanel fastaListPanel) {
        fastaListPanel.setBorder(BorderFactory.createTitledBorder("  Fasta files folders  "));
        GridBagConstraints fastaListPanelConstraints = new GridBagConstraints();
        fastaListPanelConstraints.insets = new Insets(5, 5, 5, 5);
        fastaListPanelConstraints.gridx = 0;
        fastaListPanelConstraints.gridy = 0;

        List<String> fastaToBeDisplayed = ConfigManager.getInstance().getParsingRulesManager().getFastaPaths();
        List<String> wrongFastaDirectories = ConfigManager.getInstance().getParsingRulesManager().getInvalidFastaPaths();
        List<String> duplicatePaths = ConfigManager.getInstance().getMountPointManager().getDuplicatePaths();

        if (ConfigManager.getInstance().isSeqRepActive()) {
            if (fastaToBeDisplayed != null) {
                for (int k = 0; k < fastaToBeDisplayed.size(); k++) {

                    boolean pathToBeDisplayedIsNotValid = wrongFastaDirectories != null && wrongFastaDirectories.contains(fastaToBeDisplayed.get(k));
                    boolean pathBelongsToDuplicate = duplicatePaths.contains(fastaToBeDisplayed.get(k));

                    fastaListPanelConstraints.gridx = 0;
                    fastaListPanelConstraints.weightx = 0;
                    fastaListPanelConstraints.anchor = GridBagConstraints.EAST;
                    fastaListPanelConstraints.fill = GridBagConstraints.NONE;

                    JLabel fastaLabel = new JLabel("Folder    " + (k + 1) + ":", SwingConstants.RIGHT);
                    fastaLabel.setPreferredSize(new Dimension(getMaximumSizesOfJLabels()+8 , 20));
                    fastaListPanel.add(fastaLabel, fastaListPanelConstraints);

                    JTextField jTextFieldPathFasta = new JTextField(fastaToBeDisplayed.get(k));
                    jTextFieldPathFasta.setEnabled(false);
                    if (pathToBeDisplayedIsNotValid) {
                        changeJTextFieldLook(jTextFieldPathFasta, ERROR_COLOR, Color.WHITE, "This path is not valid");
                    }
                    if (fastaToBeDisplayed.get(k).equals("")) {
                        // TODO delete the entry?
                        jTextFieldPathFasta.setText("Path does not exist");
                        changeJTextFieldLook(jTextFieldPathFasta, ERROR_COLOR, Color.WHITE, misssingPathToolTipText);
                    }
                    if (pathBelongsToDuplicate) {

                        jTextFieldPathFasta.setToolTipText(duplicateToolTipText);
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



                    JButton editButton = new JButton(IconManager.getIcon(IconManager.IconType.EDIT));
                    editButton.setToolTipText("Click to repair mounting point");
                    final int kFinal = k;
                    final String pathEdited = fastaToBeDisplayed.get(k);

                    editButton.setEnabled(true);
                    editButton.addActionListener(e -> {

                        FolderEditDialog editFastaDialog = new FolderEditDialog(ConfigWindow.getInstance(), FolderEditDialog.TypeOfDialog.EditingFastas, pathEdited, null, null);
                        editFastaDialog.setSize(500, 200);
                        editFastaDialog.centerToWindow(ConfigWindow.getInstance());
                        editFastaDialog.setVisible(true);

                        if (editFastaDialog.getButtonClicked() == DefaultDialog.BUTTON_OK) {

                            valuesInsideDialog = editFastaDialog.getValuesFromEditDialog();
                            String newFastaPath = valuesInsideDialog.get(1);

                            boolean updateSuccess = ConfigManager.getInstance().getParsingRulesManager().updateFastaFolder(pathEdited, newFastaPath);
                            if (updateSuccess) {
                                updateJPanel();
                                ConfigWindow.getInstance().pack();

                            } else {
                                Popup.warning("Error while editing fasta folder");
                            }
                        }
                    });



                    editButton.setHorizontalAlignment(SwingConstants.CENTER);
                    fastaListPanelConstraints.gridx++;
                    fastaListPanelConstraints.weightx = 0;
                    fastaListPanelConstraints.anchor = GridBagConstraints.WEST;
                    fastaListPanelConstraints.fill = GridBagConstraints.NONE;
                    fastaListPanelConstraints.insets = new Insets(5, 2, 5, 2);
                    fastaListPanel.add(editButton, fastaListPanelConstraints);

                    JButton clearButton = new JButton(IconManager.getIcon(IconManager.IconType.TRASH));

                    clearButton.addActionListener(e -> {
                        deleteFastaFolder(fastaToBeDisplayed.get(kFinal));
                    });
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

    private void deleteFolderPath(MountPointUtils.MountPointType mountPointType, String key) {

        boolean deleteConfirmation = Popup.yesNoCenterToComponent(ConfigWindow.getInstance(), "Are you sure you want to delete this Folder?");
        if (deleteConfirmation) {
            boolean deleteSucces = ConfigManager.getInstance().getMountPointManager().deleteMountPointEntry(mountPointType, key);
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
                boolean addSuccesFasta = ConfigManager.getInstance().getParsingRulesManager().addFastaFolder(folderPath);
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
        String stringInLabel = jLabel.getText();
        return fm.stringWidth(stringInLabel);

    }

    private int getSizeInPixels(JTextField jTextField) {
        Font fontUsed = jTextField.getFont();
        FontMetrics fontMetrics = jTextField.getFontMetrics(fontUsed);
        String stringInTextField = jTextField.getText();
        return fontMetrics.stringWidth(stringInTextField);

    }

    private static void changeJTextFieldLook(JTextField jTextField, Color backGroundColor, Color textColor, String toolTipMessage) {
        jTextField.setBackground(backGroundColor);
        if (!jTextField.isEnabled()) {
            jTextField.setDisabledTextColor(textColor);
        } else {
            jTextField.setForeground(textColor);
        }
        jTextField.setToolTipText(toolTipMessage);
    }


}
