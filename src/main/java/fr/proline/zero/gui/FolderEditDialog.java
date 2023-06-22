package fr.proline.zero.gui;

import fr.proline.studio.gui.DefaultDialog;
import fr.proline.studio.utils.IconManager;
import fr.proline.zero.util.ConfigManager;
import fr.proline.zero.util.MountPointUtils;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;


/**
 * used to add or update a mounting point (MZDB plus RESULT) as well as Fastas
 *
 * @author Christophe Delapierre
 * @see FolderPanel
 * @see ConfigManager
 * @see DefaultDialog
 */

public class FolderEditDialog extends DefaultDialog {
    private JComboBox<String> dataTypeBox;
    private JTextField folderLabelTField;
    private JTextField folderPathTField;

    private final TypeOfDialog typeOfDialog;

    private final String initialLabel;
    private final String initialPath;


    /**
     * Four different type of dialogs: add or edit mounting points or fastas
     */
    public enum TypeOfDialog {EditingDefaultMPts, EditingFastas, EditingMpts, AddingAMountPoint}


    public FolderEditDialog(Window parent, TypeOfDialog typeOfDialog, String pathBackup, String labelEdited, MountPointUtils.MountPointType mountPointType) {
        super(parent);
        this.setInternalComponent(createAddFolderPanel());
        this.typeOfDialog = typeOfDialog;
        this.setButtonName(BUTTON_BACK, "Clear");
        this.setButtonVisible(BUTTON_BACK, true);
        this.setButtonIcon(BUTTON_BACK, IconManager.getIcon(IconManager.IconType.ERASER));
        this.setButtonIcon(BUTTON_OK, IconManager.getIcon(IconManager.IconType.PLUS_16X16));
        this.setButtonVisible(BUTTON_OK, true);
        this.setButtonName(BUTTON_OK, "Update");
        this.setButtonVisible(BUTTON_HELP, false);
        this.initialLabel = labelEdited;
        this.initialPath = pathBackup;

        if (typeOfDialog.equals(TypeOfDialog.AddingAMountPoint)) {
            this.setTitle("Add folder");
            this.setIconImage(IconManager.getImage(IconManager.IconType.PLUS_16X16));
            this.setButtonName(BUTTON_OK, "Add");

        } else if (typeOfDialog.equals(TypeOfDialog.EditingFastas)) {
            this.setTitle("Edit Fasta Folder");
            this.setIconImage(IconManager.getImage(IconManager.IconType.EDIT));
            folderPathTField.setText(pathBackup);
            folderLabelTField.setEnabled(false);
            dataTypeBox.setSelectedItem("Fasta folder");
            dataTypeBox.setEnabled(false);


        } else if (typeOfDialog.equals(TypeOfDialog.EditingDefaultMPts)) {
            this.setTitle("Edit  Mounting Point By Default");
            this.setIconImage(IconManager.getImage(IconManager.IconType.EDIT));
            folderPathTField.setText(pathBackup);
            folderLabelTField.setText(MountPointUtils.getMountPointDefaultPathLabel(mountPointType));
            folderLabelTField.setEnabled(false);
            folderLabelTField.setEditable(false);
            dataTypeBox.setSelectedItem(mountPointType.getDisplayString());
            dataTypeBox.setEnabled(false);

        } else if (typeOfDialog.equals(TypeOfDialog.EditingMpts)) {

            this.setTitle("Edit Mounting Point");
            this.setIconImage(IconManager.getImage(IconManager.IconType.EDIT));
            folderLabelTField.setText(labelEdited);
            folderPathTField.setText(pathBackup);
            dataTypeBox.setSelectedItem(mountPointType.getDisplayString());
            dataTypeBox.setEnabled(false);

        }

        this.setResizable(true);
        super.pack();
    }

    private JPanel createAddFolderPanel() {
        // creation du panel et du layout
        JPanel addFolderPanel = new JPanel(new GridBagLayout());
        GridBagConstraints addFolderConstraint = new GridBagConstraints();
        addFolderConstraint.insets = new java.awt.Insets(5, 5, 5, 5);
        addFolderConstraint.anchor = GridBagConstraints.NORTHEAST;
        addFolderConstraint.fill = GridBagConstraints.HORIZONTAL;
        addFolderConstraint.gridx = 0;
        addFolderConstraint.gridy = 0;

        // creation des widgets
        dataTypeBox = new JComboBox<>();
        dataTypeBox.addItem(MountPointUtils.MountPointType.RESULT.getDisplayString());
        dataTypeBox.addItem(MountPointUtils.MountPointType.MZDB.getDisplayString());

        boolean SeqRepoIsActive = ConfigManager.getInstance().isSeqRepActive();
        if (SeqRepoIsActive) {
            dataTypeBox.addItem("Fasta folder");
        }

        dataTypeBox.addActionListener(e -> greyLabelForFasta());
        dataTypeBox.setEnabled(true);

        folderLabelTField = new JTextField();
        folderLabelTField.setEnabled(true);
        folderPathTField = new JTextField();
        folderPathTField.setEnabled(true);

        JButton browseButton = new JButton(IconManager.getIcon(IconManager.IconType.OPEN_FILE));
        browseButton.addActionListener(e -> openFolderView());


        JLabel jLabelData = new JLabel("Data type : ", SwingConstants.RIGHT);
        jLabelData.setEnabled(true);
        addFolderPanel.add(jLabelData, addFolderConstraint);

        addFolderConstraint.gridx++;
        addFolderConstraint.anchor = GridBagConstraints.NORTHWEST;
        addFolderPanel.add(dataTypeBox, addFolderConstraint);

        addFolderConstraint.gridx = 0;
        addFolderConstraint.gridy++;
        addFolderConstraint.anchor = GridBagConstraints.NORTHEAST;
        JLabel jLabelLabel = new JLabel("Label : ", SwingConstants.RIGHT);
        jLabelLabel.setEnabled(true);
        addFolderPanel.add(jLabelLabel, addFolderConstraint);

        addFolderConstraint.gridx++;
        addFolderConstraint.anchor = GridBagConstraints.NORTHWEST;
        addFolderConstraint.weightx = 0.3;
        addFolderPanel.add(folderLabelTField, addFolderConstraint);

        addFolderConstraint.weightx = 0;
        addFolderConstraint.gridx = 0;
        addFolderConstraint.gridy++;
        addFolderConstraint.anchor = GridBagConstraints.NORTHEAST;
        JLabel jLabelPath = new JLabel("Path : ", SwingConstants.RIGHT);
        jLabelPath.setEnabled(true);
        browseButton.setEnabled(true);
        addFolderPanel.add(jLabelPath, addFolderConstraint);

        addFolderConstraint.gridx++;
        addFolderConstraint.anchor = GridBagConstraints.NORTHWEST;
        addFolderConstraint.weightx = 0.5;
        addFolderPanel.add(folderPathTField, addFolderConstraint);


        addFolderConstraint.fill = GridBagConstraints.NONE;
        addFolderConstraint.weightx = 0;
        addFolderConstraint.gridx++;
        addFolderPanel.add(browseButton, addFolderConstraint);

        addFolderConstraint.gridy++;
        addFolderConstraint.gridx = 0;
        addFolderConstraint.weighty = 1;
        add(Box.createVerticalGlue(), addFolderConstraint);

        return addFolderPanel;
    }


    private void greyLabelForFasta() {

        if (dataTypeBox.getSelectedItem().equals("Fasta folder")) {
            folderLabelTField.setText("");
            folderLabelTField.setEnabled(false);
        } else {
            folderLabelTField.setEnabled(true);
        }

    }


    private void openFolderView() {
        JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnValue = jfc.showOpenDialog(null);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = jfc.getSelectedFile();
            folderPathTField.setToolTipText(selectedFile.getAbsolutePath());
            folderPathTField.setText(selectedFile.getAbsolutePath());
        }
    }

    /**
     * @return the three values inside the folder edit dialog
     */
    public ArrayList<String> getValuesFromEditDialog() {
        ArrayList<String> valuesEntered = new ArrayList<>(3);
        valuesEntered.add(0, folderLabelTField.getText().trim());
        valuesEntered.add(1, folderPathTField.getText());
        valuesEntered.add(2, dataTypeBox.getSelectedItem().toString());
        return valuesEntered;
    }

    private static MountPointUtils.MountPointType getMountPointTypeSelected(String dataTypeBoxSelected) {
        MountPointUtils.MountPointType mountPointSelected;
        if (dataTypeBoxSelected.equals(MountPointUtils.MountPointType.RESULT.getDisplayString())) {
            mountPointSelected = MountPointUtils.MountPointType.RESULT;

        } else {
            mountPointSelected = MountPointUtils.MountPointType.MZDB;
        }
        return mountPointSelected;
    }

    /**
     * Method called when user clicks on the update button
     * Check if entries are valid
     * @return
     */
    @Override
    protected boolean okCalled() {

        boolean entriesAreValid = true;

        ArrayList<String> values = getValuesFromEditDialog();
        String folderField = values.get(0);
        String folderPath = values.get(1);
        String dataTypeBoxSelected = values.get(2);

        Path pathToTest = Paths.get(folderPath);
        boolean pathExists = Files.exists(pathToTest);
        boolean entriesAreFullyFilled = (!folderPath.isEmpty() && !folderField.isEmpty())
                || (!folderPath.isEmpty() && dataTypeBoxSelected.equals("Fasta folder"));

        boolean labelAlreadyExists=false;
        boolean pathAlreadyExists=false;
        boolean pathInFastaExist = false;

        if (!dataTypeBoxSelected.equals("Fasta folder")) {
            boolean labelDidChange = !folderField.equals(initialLabel);
            boolean pathDidChange = !folderPath.equals(initialPath);
            if (labelDidChange) {
                labelAlreadyExists = ConfigManager.getInstance().getMountPointManager().getIfLabelExists(folderField);
            }
            if (pathDidChange) {
                pathAlreadyExists = ConfigManager.getInstance().getMountPointManager().getIfPathExist(folderPath);
            }
        }

        if (dataTypeBoxSelected.equals("Fasta folder")) {
            boolean pathDidChange = !folderPath.equals(initialPath);
            if (pathDidChange) {
                pathInFastaExist = ConfigManager.getInstance().getMountPointManager().getIfPathExist(folderPath);
            }
        }

        if (!entriesAreFullyFilled) {
            if (folderPath.isEmpty()) {
                highlight(folderPathTField);
                setStatus(true, "Please add a folder");
                entriesAreValid = false;
            }
           else  {
                highlight(folderLabelTField);
                setStatus(true, "Please add a label");
                entriesAreValid = false;
            }
        } else if (!pathExists) {
            highlight(folderPathTField);
            setStatus(true, "The path is not valid");
            entriesAreValid = false;

        } else if (labelAlreadyExists) {
            highlight(folderLabelTField);
            setStatus(true, "label already exists please choose another label");
            entriesAreValid = false;

        } else if (pathAlreadyExists || pathInFastaExist) {
            highlight(folderPathTField);
            boolean addConfirm=Popup.yesNoCenterToComponent(ConfigWindow.getInstance(),"The path you entered already exists, do " +
                    "you want to add it anyway?");
            if (addConfirm){
                entriesAreValid=true;
            }
            else {
                entriesAreValid=false;
            }
        }

        return entriesAreValid;

    }


    @Override
    protected boolean cancelCalled() {
        return true;
    }

    @Override
    protected boolean backCalled() {

        if (this.typeOfDialog.equals(TypeOfDialog.EditingFastas) || this.typeOfDialog.equals(TypeOfDialog.EditingDefaultMPts)) {
            folderPathTField.setText("");

        } else {
            folderLabelTField.setText("");
            folderPathTField.setText("");
        }
        return true;
    }

    @Override
    protected boolean saveCalled() {
        return true;
    }
}
