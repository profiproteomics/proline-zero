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

import static java.lang.String.valueOf;

public class FolderEditDialog extends DefaultDialog {
    private JComboBox dataTypeBox;
    private JTextField folderLabelField;
    private JTextField folderPathField;
  //  private MountPointUtils.MountPointType mountPointTypeEdited;
    int index;
  //  private final String labelEdited;
   // private final String pathBackup;
    private TypeOfDialog context;
    private boolean firstCall = true;
    private String backuplabel = "";
    private String backupPath = "";
    private FolderEditDialog folderEditDialog;


    public enum TypeOfDialog {EditingDefaultMPts, EditingFastas, EditingMpts, AddingAMountPoint}



    public FolderEditDialog(Window parent, TypeOfDialog typeOfDialog, String pathBackup, String labelEdited, MountPointUtils.MountPointType mountPointType) {
        super(parent);
        this.setButtonVisible(BUTTON_HELP, false);


        this.setInternalComponent(createAddFolderPanel(typeOfDialog, mountPointType));

        this.context = typeOfDialog;
        this.setButtonName(6, "Clear");
        this.setButtonIcon(6, IconManager.getIcon(IconManager.IconType.ERASER));
        this.setButtonIcon(0, IconManager.getIcon(IconManager.IconType.PLUS_16X16));
        this.setButtonName(0, "Add");
        this.setButtonName(4, "Edit");
        if (typeOfDialog.equals(TypeOfDialog.AddingAMountPoint)) {

            this.setTitle("Add Mounting Point");
            this.setIconImage(IconManager.getImage(IconManager.IconType.PLUS_16X16));


        } else if (typeOfDialog.equals(TypeOfDialog.EditingFastas)) {
            this.setTitle("Edit Fasta Folder");
            this.setIconImage(IconManager.getImage(IconManager.IconType.EDIT));
            folderPathField.setText(pathBackup);
            folderLabelField.setEnabled(false);
            dataTypeBox.setSelectedItem("Fasta folder");
            dataTypeBox.setEnabled(false);

            this.setButtonVisible(BUTTON_OK, true);
            this.setButtonVisible(BUTTON_SAVE, false);


        } else if (typeOfDialog.equals(TypeOfDialog.EditingDefaultMPts)) {
            this.setTitle("Edit  Mounting Point By Default");
            this.setIconImage(IconManager.getImage(IconManager.IconType.EDIT));
            this.setButtonVisible(BUTTON_OK, true);
            this.setButtonVisible(BUTTON_SAVE, false);
            folderPathField.setText(pathBackup);
            folderLabelField.setText(MountPointUtils.getMountPointDefaultPathLabel(mountPointType));
            folderLabelField.setEnabled(false);
            folderLabelField.setEditable(false);
            dataTypeBox.setSelectedItem(mountPointType.getDisplayString());
            dataTypeBox.setEnabled(false);
        } else if (typeOfDialog.equals(TypeOfDialog.EditingMpts)) {
            this.setTitle("Edit Mounting Point");
            this.setIconImage(IconManager.getImage(IconManager.IconType.EDIT));
            this.setButtonVisible(BUTTON_OK, true);
            this.setButtonVisible(BUTTON_SAVE, false);
            folderLabelField.setText(labelEdited);
            folderPathField.setText(pathBackup);
            dataTypeBox.setSelectedItem(mountPointType.getDisplayString());
            dataTypeBox.setEnabled(false);

        }
        this.setButtonVisible(BUTTON_BACK, true);
        this.setResizable(true);
        super.pack();
    }

    private JPanel createAddFolderPanel(TypeOfDialog context, MountPointUtils.MountPointType mountPointTypeEdited) {
        // creation du panel et du layout
        JPanel addFolderPanel = new JPanel(new GridBagLayout());
        //
        GridBagConstraints addFolderConstraint = new GridBagConstraints();
        addFolderConstraint.insets = new java.awt.Insets(5, 5, 5, 5);
        addFolderConstraint.anchor = GridBagConstraints.EAST;
        addFolderConstraint.fill = GridBagConstraints.HORIZONTAL;

        // creation des widgets
        dataTypeBox = new JComboBox<String>();
        dataTypeBox.addItem(MountPointUtils.MountPointType.RESULT.getDisplayString());
        dataTypeBox.addItem(MountPointUtils.MountPointType.MZDB.getDisplayString());
        dataTypeBox.addItem("Fasta folder");


        dataTypeBox.addActionListener(e -> {
            greyLabelforFasta();
        });

        dataTypeBox.setEnabled(true);

        folderLabelField = new JTextField();

        folderPathField = new JTextField();

      /*  if (context.equals(TypeOfDialog.EditingDefaultMPts)) {


        }
        if (context.equals(TypeOfDialog.EditingFastas)) {




        }
        if (context.equals(TypeOfDialog.EditingMpts)) {

        }*/

        JButton browseButton = new JButton(IconManager.getIcon(IconManager.IconType.OPEN_FILE));

        browseButton.addActionListener(e -> {
            openFolderView();
        });


        // ajout des widgets au layout
        addFolderConstraint.gridx = 0;
        addFolderConstraint.gridy = 0;
        JLabel l = new JLabel("Data type : ", SwingConstants.RIGHT);
        l.setEnabled(true);
        addFolderPanel.add(l, addFolderConstraint);

        addFolderConstraint.gridx++;
        addFolderConstraint.anchor = GridBagConstraints.WEST;
        addFolderPanel.add(dataTypeBox, addFolderConstraint);

        addFolderConstraint.gridx = 0;
        addFolderConstraint.gridy++;
        addFolderConstraint.anchor = GridBagConstraints.EAST;
        l = new JLabel("Label : ", SwingConstants.RIGHT);
        l.setEnabled(true);

        addFolderPanel.add(l, addFolderConstraint);
        addFolderConstraint.gridx++;
        addFolderConstraint.anchor = GridBagConstraints.WEST;
        addFolderConstraint.weightx = 0.3;
        folderLabelField.setEnabled(true);
        addFolderPanel.add(folderLabelField, addFolderConstraint);

        addFolderConstraint.weightx = 0;
        addFolderConstraint.gridx = 0;
        addFolderConstraint.gridy++;
        addFolderConstraint.anchor = GridBagConstraints.EAST;
        l = new JLabel("Path : ", SwingConstants.RIGHT);
        l.setEnabled(true);
        folderPathField.setEnabled(true);
        browseButton.setEnabled(true);
        addFolderPanel.add(l, addFolderConstraint);
        addFolderConstraint.gridx++;
        addFolderConstraint.anchor = GridBagConstraints.WEST;
        addFolderConstraint.weightx = 0;
        addFolderPanel.add(folderPathField, addFolderConstraint);


        addFolderConstraint.fill = GridBagConstraints.NONE;
        addFolderConstraint.gridx++;
        addFolderPanel.add(browseButton, addFolderConstraint);

        add(Box.createHorizontalGlue());


        return addFolderPanel;
    }




    private void greyLabelforFasta() {

        if (dataTypeBox.getSelectedItem().equals("Fasta folder")) {
            folderLabelField.setText("");
            folderLabelField.setEnabled(false);
        } else {
            folderLabelField.setEnabled(true);
        }

    }


    private void openFolderView() {
        JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnValue = jfc.showOpenDialog(null);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = jfc.getSelectedFile();
            folderPathField.setToolTipText(selectedFile.getAbsolutePath());
            folderPathField.setText(selectedFile.getAbsolutePath());
        }
    }

    public ArrayList<String> getValuesEntered() {
        ArrayList<String> values = new ArrayList<>(3);
        values.add(0, folderLabelField.getText().trim());
        values.add(1, folderPathField.getText());
        values.add(2, dataTypeBox.getSelectedItem().toString());
        return values;
    }


    // used to add a mounting point
    @Override
    protected boolean okCalled() {
        System.out.println("Ok pressed");
        boolean entriesAreValid = true;

        ArrayList<String> values = getValuesEntered();

        String folderField = values.get(0);
        String folderPath = values.get(1);
        String datatypeboxselected = values.get(2);

        Path pathToTest = Paths.get(folderPath);
        boolean pathExists = Files.exists(pathToTest);
        boolean verifUserEntry = (!folderPath.isEmpty() && !folderField.isEmpty())
                || (!folderPath.isEmpty() && datatypeboxselected.equals("Fasta folder"));
        boolean labelAlreadyExists = ConfigManager.getInstance().getMountPointManager().getIfLabelExists(folderField);
        boolean pathAlreadyExists = ConfigManager.getInstance().getMountPointManager().getIfPathExist(folderPath);


        if (!verifUserEntry) {
            if (folderPath.isEmpty()) {
                highlight(folderPathField);
                setStatus(true, "Please add a folder");
                entriesAreValid = false;
            }
            if (folderField.isEmpty() && !datatypeboxselected.equals("Fasta folder")) {
                highlight(folderLabelField);
                setStatus(true, "Please add a label");
                entriesAreValid = false;
            }
        } else if (!pathExists) {
            highlight(folderPathField);
            setStatus(true, "The path is not valid");
            entriesAreValid = false;

        } else if (labelAlreadyExists) {
            highlight(folderLabelField);
            setStatus(true, "label already exists please choose another value");
            entriesAreValid = false;


        } else if (pathAlreadyExists) {
            highlight(folderPathField);
            setStatus(true, "path already exists please choose another value");
            entriesAreValid = false;

        } else if (pathAlreadyExists && labelAlreadyExists) {
            // does not highlight both only one
            highlight(folderPathField);
            highlight(folderLabelField);
            setStatus(true, "path and label already exist");
            entriesAreValid = false;
        }
        return entriesAreValid;

    }


    @Override
    protected boolean cancelCalled() {

        return true;
    }

    @Override
    protected boolean backCalled() {

        if (firstCall) {

            backuplabel = folderLabelField.getText();
            backupPath = folderPathField.getText();
            if (this.context.equals(TypeOfDialog.EditingFastas)||this.context.equals(TypeOfDialog.EditingDefaultMPts)) {
                folderPathField.setText("");
                firstCall = false;
                setButtonName(BUTTON_BACK, "Undo");
                setButtonIcon(BUTTON_BACK, IconManager.getIcon(IconManager.IconType.BACK));
            }
            else {
                folderLabelField.setText("");
                folderPathField.setText("");
                setButtonName(BUTTON_BACK, "Undo");
                setButtonIcon(BUTTON_BACK, IconManager.getIcon(IconManager.IconType.BACK));
                firstCall = false;
            }
        } else {

            folderLabelField.setText(backuplabel);
            folderPathField.setText(backupPath);
            setButtonName(BUTTON_BACK, "Clear");
            setButtonIcon(BUTTON_BACK, IconManager.getIcon(IconManager.IconType.ERASER));

            firstCall = true;

        }
        return true;
    }

    @Override
    protected boolean saveCalled() {
        System.out.println("Save called");

        return true;
    }
}
