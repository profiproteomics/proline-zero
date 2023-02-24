package fr.proline.zero.gui;

import java.awt.*;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.List;

import javax.swing.*;

import javax.swing.filechooser.FileSystemView;


import fr.proline.studio.utils.IconManager;
import fr.proline.zero.util.*;

import static java.lang.String.valueOf;


public class FolderPanel extends JPanel {
    private JComboBox<String> dataTypeBox;
    private JTextField folderLabelField;
    private JTextField folderPathField;
    private JDialog repairJDialog;
    private int index;


    private final Color stringColor = new Color(50, 0, 230);
    private final Color errorColor = new Color(255, 0, 50);

    // private JButton browseButton;

    private MountPointUtils.MountPointType mountPointTypeEdited;

    private enum Context {editingDefaultMPts, editingFastas, editingMpts, addingAMountPoint}

    private String labelEdited;
    private String pathBackup;




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

        folderPanelConstraints.gridx = 0;
        folderPanelConstraints.gridy = 0;
        folderPanelConstraints.weightx = 1;
        folderPanelConstraints.weighty = 0.5;
        HelpHeaderPanel help = new HelpHeaderPanel("Folder", SettingsConstant.FOLDERS_HELP_PANE);
        add(help, folderPanelConstraints);

        folderPanelConstraints.insets = new java.awt.Insets(20, 15, 5, 15);
        folderPanelConstraints.gridy++;
        folderPanelConstraints.weightx = 0;
        folderPanelConstraints.weighty = 0;
        add(createTmpFolderPanel(), folderPanelConstraints);

        folderPanelConstraints.gridy++;
        folderPanelConstraints.fill = GridBagConstraints.NONE;
        folderPanelConstraints.anchor = GridBagConstraints.WEST;

        add(createAddFolderPanel(), folderPanelConstraints);
        folderPanelConstraints.fill = GridBagConstraints.BOTH;
        folderPanelConstraints.anchor = GridBagConstraints.NORTHWEST;
        folderPanelConstraints.gridy++;
        add(createFolderListPanel(), folderPanelConstraints);
        folderPanelConstraints.gridy++;
        folderPanelConstraints.weighty = 1;
        add(Box.createHorizontalGlue(), folderPanelConstraints);


    }


    //VDS TODO: test lighter update method...
    private void updateJpanel() {
        removeAll();
        initialize();
        revalidate();
        repaint();
    }


    private JPanel createTmpFolderPanel() {
        // creation du panel et du layout
        JPanel tmpFolderPanel = new JPanel(new GridBagLayout());
        tmpFolderPanel.setToolTipText(SettingsConstant.FOLDER_MAX_SIZE_TOOLTIP);
        GridBagConstraints tmpFolderConstraint = new GridBagConstraints();
        tmpFolderConstraint.insets = new java.awt.Insets(5, 5, 5, 5);
        tmpFolderConstraint.anchor = GridBagConstraints.NORTHWEST;

        // creation des widgets

        JTextField maximumTmpFolderSizeField = new JTextField();
        // maximumTmpFolderSizeField.setText(valueOf(ConfigManager.getInstance().getMaxTmpFolderSize()));
        maximumTmpFolderSizeField.setText(valueOf(ConfigManager.getMaxTmpFolderSize()));
        maximumTmpFolderSizeField.setToolTipText(SettingsConstant.FOLDER_MAX_SIZE_TOOLTIP);

        // ajout des widgets au layout
        tmpFolderConstraint.gridx = 0;
        tmpFolderConstraint.gridy = 0;
        tmpFolderConstraint.fill = GridBagConstraints.HORIZONTAL;
        tmpFolderPanel.add(new JLabel("Maximum size for temp folder : ", SwingConstants.RIGHT), tmpFolderConstraint);

        tmpFolderConstraint.gridx++;
        tmpFolderConstraint.weightx = 0.5;
        tmpFolderConstraint.fill = GridBagConstraints.BOTH;
        tmpFolderPanel.add(maximumTmpFolderSizeField, tmpFolderConstraint);

        tmpFolderConstraint.gridx++;
        tmpFolderConstraint.weightx = 0;
        tmpFolderPanel.add(new JLabel("Mo"), tmpFolderConstraint);

        return tmpFolderPanel;
    }


    private JPanel createAddFolderPanel(Context context) {
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

        if (context.equals(Context.editingDefaultMPts)) {
            folderLabelField.setText(MountPointUtils.getMountPointDefaultPathLabel(mountPointTypeEdited));
            dataTypeBox.setSelectedItem(mountPointTypeEdited.getDisplayString());
            folderLabelField.setEnabled(false);
            folderLabelField.setEditable(false);
            dataTypeBox.setEnabled(false);

        }
        if (context.equals(Context.editingFastas)) {

            dataTypeBox.setSelectedItem("Fasta folder");
          //  folderLabelField.setText("Folder    " + valueOf(index + 1) + ":");
            folderLabelField.setText("");
            dataTypeBox.setEnabled(false);
            folderLabelField.setEnabled(false);
            folderLabelField.setEditable(false);

        }
        if (context.equals(Context.editingMpts)) {
            dataTypeBox.setSelectedItem(mountPointTypeEdited.getDisplayString());
            folderLabelField.setText(labelEdited);
            dataTypeBox.setEnabled(false);
            folderLabelField.setEnabled(false);
            folderLabelField.setEditable(false);

        }
        if (context.equals(Context.addingAMountPoint)) {
            addFolderPanel.setBorder(BorderFactory.createTitledBorder("Add folder"));

        } else {
            addFolderPanel.setBorder(BorderFactory.createTitledBorder("Edit Folder"));
        }


        JButton addButton = new JButton(IconManager.getIcon(IconManager.IconType.PLUS_16X16));
        JButton clearButton = new JButton(IconManager.getIcon(IconManager.IconType.CLEAR_ALL));
        JButton browseButton = new JButton(IconManager.getIcon(IconManager.IconType.OPEN_FILE));
        JButton cancelButton = new JButton(IconManager.getIcon(IconManager.IconType.UNDO));

        addButton.addActionListener(e -> {
            boolean mountPointAdded = addFolderAction();

            if (mountPointAdded) {
                repairJDialog.dispose();
            }


        });

        clearButton.addActionListener(e -> {
            if (context.equals(Context.addingAMountPoint)) {
                clearAction();
            } else {
                folderPathField.setText("");
            }
        });
        browseButton.addActionListener(e -> {
            openFolderView();
        });
        cancelButton.addActionListener(e -> {
            //  restore mounting point
            if (pathBackup != null && context.equals(Context.editingDefaultMPts)) {
                ConfigManager.getInstance().getMountPointManager().addMountPointEntry(mountPointTypeEdited, MountPointUtils.getMountPointDefaultPathLabel(mountPointTypeEdited), pathBackup);
            }
            if (pathBackup != null && context.equals(Context.editingMpts)) {
                ConfigManager.getInstance().getMountPointManager().addMountPointEntry(mountPointTypeEdited, labelEdited, pathBackup);
            }
            if (pathBackup != null && context.equals(Context.editingFastas)) {
                ConfigManager.getInstance().getParsingRulesManager().addFastaFolder(pathBackup);

            }
            // to do restore fastas and mount points
            repairJDialog.dispose();
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
        addFolderConstraint.weightx = 1;
        addFolderPanel.add(folderPathField, addFolderConstraint);


        addFolderConstraint.fill = GridBagConstraints.NONE;
        addFolderConstraint.gridx++;
        addFolderPanel.add(browseButton, addFolderConstraint);

        addFolderConstraint.gridy++;
        addFolderConstraint.gridx = 1;
        addFolderConstraint.anchor = GridBagConstraints.EAST;
        addFolderPanel.add(cancelButton, addFolderConstraint);

        addFolderConstraint.fill = GridBagConstraints.NONE;
        addFolderConstraint.weightx = 0;



        addFolderConstraint.anchor = GridBagConstraints.CENTER;

        addFolderConstraint.gridx = 1;
        clearButton.setEnabled(true);
        addFolderPanel.add(clearButton, addFolderConstraint);


        addButton.setEnabled(true);
        addFolderConstraint.gridy=3;
        addFolderConstraint.gridx=2;
        addFolderConstraint.anchor=GridBagConstraints.WEST;
        addFolderPanel.add(addButton, addFolderConstraint);
        add(Box.createHorizontalGlue());

       // addFolderConstraint.gridy++;


        return addFolderPanel;
    }

    private JPanel createAddFolderPanel() {
        JPanel addFolderPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel addFolderLabel = new JLabel("Add Mounting Point: ");
        addFolderPanel.add(addFolderLabel, gbc);
        gbc.gridx++;
        JButton openAddJDialog = new JButton(IconManager.getIcon(IconManager.IconType.PLUS_16X16));
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        openAddJDialog.setSize(30, 30);
        openAddJDialog.addActionListener(e -> {
            openRepairJDialog(Context.addingAMountPoint);

        });
        gbc.insets=new Insets(5,30,5,10);
        addFolderPanel.add(openAddJDialog, gbc);


        return addFolderPanel;
    }

    private JPanel createFolderListPanel() {
        // creation du panel et layout
        JPanel folderListPanel = new JPanel(new GridBagLayout());
        // folderListPanel.setBorder(BorderFactory.createTitledBorder("Folder list"));
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

        JPanel mzdbListPanel = new JPanel(new GridBagLayout());

        this.initAnyFolderPanel(MountPointUtils.MountPointType.MZDB, mzdbListPanel);

        c.gridy++;
        folderListPanel.add(mzdbListPanel, c);
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
        Map<String, String> temp = ConfigManager.getInstance().getMountPointManager().getMountPointMap().get(mpt);
        boolean missingMountPoint = missingMps.contains(mpt.getDisplayString());

        GridBagConstraints anyPanelConstraints = new GridBagConstraints();
        anyPanel.setBorder(BorderFactory.createTitledBorder(mpt.getDisplayString()));
        anyPanelConstraints.insets = new Insets(5, 5, 5, 5);
        anyPanelConstraints.gridx = 0;
        anyPanelConstraints.gridy = 0;
        anyPanelConstraints.weightx = 0;
        anyPanelConstraints.fill = GridBagConstraints.HORIZONTAL;


        // get mounting points to be displayed


        //places none deletable mounting point at the top of the list
        if (temp != null) {

            JLabel fieldInitial = new JLabel(MountPointUtils.getMountPointDefaultPathLabel(mpt) + ": ", SwingConstants.RIGHT);

            fieldInitial.setForeground(stringColor);
            anyPanelConstraints.anchor = GridBagConstraints.EAST;
            fieldInitial.setEnabled(true);
            fieldInitial.setPreferredSize(new Dimension(getMaximumSizesOfJLabels() + 8, 20));
            anyPanel.add(fieldInitial, anyPanelConstraints);


            String path = temp.get(MountPointUtils.getMountPointDefaultPathLabel(mpt));
            JTextField pathInitial = new JTextField(temp.get(MountPointUtils.getMountPointDefaultPathLabel(mpt)));

            pathInitial.setEnabled(false);
            anyPanelConstraints.gridx++;
            anyPanelConstraints.anchor = GridBagConstraints.WEST;
            anyPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
            anyPanelConstraints.weightx = 1;
            boolean defaultMountPointHasAWrongPath = pathWrong.contains(temp.get(MountPointUtils.getMountPointDefaultPathLabel(mpt)));

            if (path != null) {
                if (defaultMountPointHasAWrongPath) {
                    pathInitial.setEnabled(true);
                    pathInitial.setForeground(errorColor);
                    // pathInitial.setToolTipText("Click to fix the error");
                    anyPanel.add(pathInitial, anyPanelConstraints);


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

            anyPanelConstraints.fill = GridBagConstraints.NONE;
            anyPanelConstraints.weightx = 0;
            anyPanelConstraints.gridx++;

            JButton editButton = new JButton(IconManager.getIcon(IconManager.IconType.EDIT));
            editButton.setHorizontalAlignment(SwingConstants.CENTER);
            editButton.setToolTipText("Click to edit ");
            editButton.setSize(20, 15);
            editButton.addActionListener(e -> {
                // delete the wrong path
                int index = 0;

                if (path != null) {
                    pathBackup = path;
                    ConfigManager.getInstance().getMountPointManager().deleteMountPointEntry(mpt, MountPointUtils.getMountPointDefaultPathLabel(mpt), true);
                }
                mountPointTypeEdited = mpt;
                openRepairJDialog(Context.editingDefaultMPts);


            });
            anyPanelConstraints.insets = new Insets(5, 2, 5, 2);
            anyPanel.add(editButton, anyPanelConstraints);
            anyPanelConstraints.gridx++;
            anyPanelConstraints.fill = GridBagConstraints.NONE;
            anyPanelConstraints.weightx = 0;
            JLabel resultLabel = new JLabel(IconManager.getIcon(IconManager.IconType.LOCK));
            resultLabel.setToolTipText("This mount point cannot be deleted");
            anyPanel.add(resultLabel);

            anyPanelConstraints.gridy++;
            //iterator on the map to display remaining key-values
            for (String key : temp.keySet()) {
                if (key.equals(MountPointUtils.getMountPointDefaultPathLabel(mpt)))
                    continue;
                anyPanelConstraints.gridx = 0;
                anyPanelConstraints.weightx = 0;

                anyPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
                JLabel field = new JLabel(key + " :", SwingConstants.RIGHT);
                field.setPreferredSize(new Dimension(getMaximumSizesOfJLabels() + 8, 20));
                anyPanelConstraints.anchor = GridBagConstraints.EAST;
                anyPanel.add(field, anyPanelConstraints);

                JTextField resultPath = new JTextField(temp.get(key));
                // resultPath.setPreferredSize(new Dimension(300, 20));
                resultPath.setEditable(false);
                resultPath.setEnabled(false);
                if (temp.get(key).length() > 87) {
                    resultPath.setEnabled(true);
                    resultPath.setEditable(true);
                }
                anyPanelConstraints.gridx++;
                anyPanelConstraints.anchor = GridBagConstraints.WEST;
                anyPanelConstraints.weightx = 1;
                anyPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
                if (pathWrong.contains(temp.get(key))) {
                    resultPath.setEnabled(true);
                    resultPath.setEditable(false);
                    resultPath.setForeground(errorColor);
                    resultPath.setToolTipText("This path does not exist you should delete it by clicking on the delete button ");

                }
                anyPanel.add(resultPath, anyPanelConstraints);
                anyPanelConstraints.fill = GridBagConstraints.NONE;
                anyPanelConstraints.weightx = 0;
                anyPanelConstraints.gridx++;
                JButton editButton2 = new JButton(IconManager.getIcon(IconManager.IconType.EDIT));
                editButton2.setSize(20, 30);
                editButton2.setHorizontalAlignment(SwingConstants.CENTER);
                editButton2.setToolTipText("Click to edit mounting point");
                editButton2.addActionListener(e -> {
                    mountPointTypeEdited = mpt;
                    pathBackup = temp.get(key);
                    ConfigManager.getInstance().getMountPointManager().deleteMountPointEntry(mpt, key, false);
                    labelEdited = key;
                    openRepairJDialog(Context.editingMpts);

                });
                anyPanelConstraints.insets = new Insets(5, 2, 5, 2);
                anyPanel.add(editButton2, anyPanelConstraints);
                JButton clearButton = new JButton(IconManager.getIcon(IconManager.IconType.TRASH));
                clearButton.setSize(20, 30);
                clearButton.setHorizontalAlignment(SwingConstants.CENTER);
                clearButton.setToolTipText("Click to delete mount point");

                clearButton.addActionListener(e -> {
                    deleteFolderPath(mpt, key, false);
                });
                anyPanelConstraints.gridx++;
                anyPanel.add(clearButton, anyPanelConstraints);
                anyPanelConstraints.gridy++;

            }
        }
    }

    private void openRepairJDialog(Context context) {
        if (context.equals(Context.addingAMountPoint)){
            repairJDialog=new JDialog(ConfigWindow.getInstance(),"Add a Mounting Point");
        }
        else {
        repairJDialog = new JDialog(ConfigWindow.getInstance(), "Edit mounting point");}

        repairJDialog.setSize(new Dimension(600, 300));
        // repairJDialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        repairJDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // restore mounting point edited if user close the JDialog
                if (pathBackup != null && context.equals(Context.editingDefaultMPts)) {
                    ConfigManager.getInstance().getMountPointManager().addMountPointEntry(mountPointTypeEdited, MountPointUtils.getMountPointDefaultPathLabel(mountPointTypeEdited), pathBackup);
                }
                if (pathBackup != null && context.equals(Context.editingMpts)) {
                    ConfigManager.getInstance().getMountPointManager().addMountPointEntry(mountPointTypeEdited, labelEdited, pathBackup);
                }
                if (pathBackup != null && context.equals(Context.editingFastas)) {
                    ConfigManager.getInstance().getParsingRulesManager().addFastaFolder(pathBackup);

                }
                //TODO treats the case adding a mount point

                repairJDialog.dispose();

            }
        });

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        JPanel repairMountingPoint = createAddFolderPanel(context);
        repairJDialog.add(repairMountingPoint);
        repairJDialog.setLocationRelativeTo(null);
        repairJDialog.setVisible(true);

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
                pathFasta.setEditable(false);
                pathFasta.setForeground(errorColor);
                pathFasta.setToolTipText("This path is not valid");
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
                // save path and position inside the list before delete
                index = kFinal;
                pathBackup = pathedited;
                editFastaDirectory(pathedited);
            });

            JButton clearButton = new JButton(IconManager.getIcon(IconManager.IconType.TRASH));
            // final int kFinal = k;
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

    private boolean addFolderAction() {
        String folderField = folderLabelField.getText();
        String folderPath = folderPathField.getText();
        Path pathToTest = Paths.get(folderPath);
        Boolean pathExists = Files.exists(pathToTest);
        Boolean verifUserEntry = (!folderPath.isEmpty() && !folderField.isEmpty())
                || (!folderPath.isEmpty() && dataTypeBox.getSelectedItem().equals("Fasta folder"));

        String type = dataTypeBox.getSelectedItem().toString();
        boolean overallSuccess = false;
        if (pathExists && verifUserEntry) {
            switch (type) {
                case "Result folder":
                    boolean addSuccessResult = ConfigManager.getInstance().getMountPointManager().addMountPointEntry(MountPointUtils.MountPointType.RESULT, folderField, folderPath);
                    if (addSuccessResult) {
                        folderPathField.setText("");
                        folderLabelField.setText("");
                        updateJpanel();
                        ConfigWindow.getInstance().pack();
                        overallSuccess = true;

                    } else {
                        Popup.warning("The label and/or the path already exist please choose new values");
                        folderPathField.setText("");
                        // folderLabelField.setText("");

                    }
                    break;

                case "Mzdb folder":
                    boolean addSuccessMzdb = ConfigManager.getInstance().getMountPointManager().addMountPointEntry(MountPointUtils.MountPointType.MZDB, folderField, folderPath);
                    if (addSuccessMzdb) {
                        folderPathField.setText("");
                        folderLabelField.setText("");
                        updateJpanel();
                        ConfigWindow.getInstance().pack();
                        overallSuccess = true;
                    } else {
                        Popup.warning("The label and/or the path already exist please choose new values");
                        folderPathField.setText("");
                        // folderLabelField.setText("");

                    }
                    break;
                case "Raw folders":
                    boolean addSuccesRaw = ConfigManager.getInstance().getMountPointManager().addMountPointEntry(MountPointUtils.MountPointType.RAW, folderField, folderPath);
                    if (addSuccesRaw) {
                        folderPathField.setText("");
                        //  folderLabelField.setText("");
                        updateJpanel();
                        ConfigWindow.getInstance().pack();
                        overallSuccess = true;
                    } else {
                        Popup.warning("The label and/or the path already exist please choose new values");
                        folderPathField.setText("");
                        folderLabelField.setText("");

                    }
                    break;
                case "Fasta folder":
                    boolean addSuccesFasta = ConfigManager.getInstance().getParsingRulesManager().addFastaFolder(folderPath);
                    if (addSuccesFasta) {
                        folderPathField.setText("");

                        updateJpanel();
                        ConfigWindow.getInstance().pack();
                        overallSuccess = true;


                    } else {
                        Popup.warning("Path already exists please choose another value");
                        folderPathField.setText("");

                    }

                    break;

                // TODO not implemented
                default:
                    Popup.error("no such type of Folder");

                    break;
            }
            revalidate();
            repaint();
        } else {
            if (!pathExists) {
                Popup.warning("The path you specified doesn't exist");

            }
            if (pathExists) {
                Popup.warning("Please fill path and label");

            }
        }
        return overallSuccess;
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


    private void clearAction() {
        folderPathField.setText("");
        folderLabelField.setText("");

    }

    private void deleteFolderPath(MountPointUtils.MountPointType mountPointType, String key, boolean forced) {
        boolean deleteSucces = ConfigManager.getInstance().getMountPointManager().deleteMountPointEntry(mountPointType, key, forced);
        if (deleteSucces) {

            updateJpanel();


        } else {
            Popup.warning("The mounting point has not been deleted");
        }

    }


    private void deleteFastaFolder(String path) {
        boolean deleteSucess = ConfigManager.getInstance().getParsingRulesManager().deleteFastaFolder(path);
        if (deleteSucess) {
            updateJpanel();
        } else {
            Popup.warning("Error while deleting the directory");
        }

    }

    private void editFastaDirectory(String path) {


        boolean deleteSuccess = ConfigManager.getInstance().getParsingRulesManager().deleteFastaFolder(path);


        openRepairJDialog(Context.editingFastas);


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
