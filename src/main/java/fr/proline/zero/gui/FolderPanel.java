package fr.proline.zero.gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.File;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.*;

import javax.swing.filechooser.FileSystemView;


import fr.proline.zero.util.*;


public class FolderPanel extends JPanel {
    private JTextField maximumTmpFolderSizeField;
    private JComboBox<String> dataTypeBox;
    private JTextField folderLabelField;
    private JTextField folderPathField;
    private JPanel resultListPanel;

    private JPanel mzdbListPanel;

    private JPanel fastaListPanel;
    private GridBagConstraints fastaListPanelConstraints;

    private final Color stringColor = new Color(50, 0, 230);
    private final Color errorColor = new Color(255, 0, 50);


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
        add(createAddFolderPanel(), folderPanelConstraints);
        folderPanelConstraints.gridy++;
        add(createFolderListPanel(), folderPanelConstraints);
        folderPanelConstraints.gridy++;
        folderPanelConstraints.weighty = 1;
        add(Box.createHorizontalGlue(), folderPanelConstraints);

    }


    //VDS TODO: test lighter update method...
    private void updateJpanel() {

        removeAll();

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
        add(createAddFolderPanel(), folderPanelConstraints);
        folderPanelConstraints.gridy++;
        add(createFolderListPanel(), folderPanelConstraints);
        folderPanelConstraints.gridy++;
        folderPanelConstraints.weighty = 1;
        add(Box.createHorizontalGlue(), folderPanelConstraints);

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

        maximumTmpFolderSizeField = new JTextField();
        maximumTmpFolderSizeField.setText(String.valueOf(ConfigManager.getInstance().getMaxTmpFolderSize()));
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

    private JPanel createAddFolderPanel() {
        // creation du panel et du layout
        JPanel addFolderPanel = new JPanel(new GridBagLayout());
        addFolderPanel.setBorder(BorderFactory.createTitledBorder("Add folder"));
        GridBagConstraints addFolderConstraint = new GridBagConstraints();
        addFolderConstraint.insets = new java.awt.Insets(5, 5, 5, 5);
        addFolderConstraint.anchor = GridBagConstraints.EAST;
        addFolderConstraint.fill = GridBagConstraints.HORIZONTAL;

        // creation des widgets
        dataTypeBox = new JComboBox<String>();
        dataTypeBox.addItem(MountPointUtils.MountPointType.RESULT.getDisplayString());
        dataTypeBox.addItem(MountPointUtils.MountPointType.MZDB.getDisplayString());
        dataTypeBox.addItem("Fasta folder");

        dataTypeBox.addActionListener(greyLabelforFasta());
        dataTypeBox.setEnabled(true);

        folderLabelField = new JTextField();


        folderPathField = new JTextField();


        JButton addButton = new JButton("add");
        JButton clearButton = new JButton("clear");
        JButton browseButton = new JButton("folder");

        try {

            Icon addIcon = new ImageIcon(ImageIO.read(ClassLoader.getSystemResource("plus.png")));
            addButton = new JButton(addIcon);
            addButton.addActionListener(addFolderAction());

            Icon eraserIcon = new ImageIcon(ImageIO.read(ClassLoader.getSystemResource("eraser.png")));
            clearButton = new JButton(eraserIcon);

            Icon folderIcon = new ImageIcon(ImageIO.read(ClassLoader.getSystemResource("folder-open.png")));
            browseButton = new JButton(folderIcon);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        clearButton.addActionListener(clearAction());
        browseButton.addActionListener(openFolderView());

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
        addFolderConstraint.weightx = 0;
        addFolderConstraint.gridx++;
        addFolderPanel.add(browseButton, addFolderConstraint);
        addFolderConstraint.anchor = GridBagConstraints.EAST;
        addFolderConstraint.gridy++;
        addFolderConstraint.gridx = 1;
        clearButton.setEnabled(true);
        addFolderPanel.add(clearButton, addFolderConstraint);

        addFolderConstraint.anchor = GridBagConstraints.CENTER;
        addFolderConstraint.gridx++;
        addButton.setEnabled(true);
        addFolderPanel.add(addButton, addFolderConstraint);
        addFolderConstraint.gridy++;


        return addFolderPanel;
    }


    private JPanel createFolderListPanel() {
        // creation du panel et layout
        JPanel folderListPanel = new JPanel(new GridBagLayout());
        folderListPanel.setBorder(BorderFactory.createTitledBorder("Folder list"));
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
        resultListPanel = new JPanel(new GridBagLayout());

        this.initAnyFolderPanel(MountPointUtils.MountPointType.RESULT, resultListPanel);
        folderListPanel.add(resultListPanel, c);

        mzdbListPanel = new JPanel(new GridBagLayout());
//
        this.initAnyFolderPanel(MountPointUtils.MountPointType.MZDB, mzdbListPanel);
        c.gridy++;
        folderListPanel.add(mzdbListPanel, c);
        // TODO : desactiver si seqrep décoché;
        c.gridy++;
        this.initFastaFolderPanel();
        folderListPanel.add(fastaListPanel, c);

        return folderListPanel;
    }


    private void initAnyFolderPanel(MountPointUtils.MountPointType mpt, JPanel anyPanel) {
        ArrayList<String> pathWrong = ConfigManager.getInstance().getMountPointManager().getInvalidPaths();
        GridBagConstraints anyPanelConstraints = new GridBagConstraints();
        anyPanel.setBorder(BorderFactory.createTitledBorder(mpt.getDisplayString()));
        anyPanelConstraints.insets = new Insets(5, 5, 5, 5);
        anyPanelConstraints.gridx = 0;
        anyPanelConstraints.gridy = 0;
        anyPanelConstraints.weightx = 0.1;
        anyPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
        anyPanelConstraints.anchor = GridBagConstraints.EAST;

        Icon deleteIcon = null;
        try {
            deleteIcon = new ImageIcon(ImageIO.read(ClassLoader.getSystemResource("cross.png")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Icon permanentIcon = null;
        try {
            permanentIcon = new ImageIcon(ImageIO.read(ClassLoader.getSystemResource("cube.png")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // get mounting points to be displayed
        Map<String, String> temp = ConfigManager.getInstance().getMountPointManager().getMountPointMap().get(mpt);
        //places none deletable mounting point at the top of the list
        if (temp != null) {
            JLabel fieldInitial = new JLabel(MountPointUtils.getMountPointDefaultPathLabel(mpt) + ": ");
            fieldInitial.setPreferredSize(new Dimension(60, 20));
            fieldInitial.setForeground(stringColor);
            anyPanel.add(fieldInitial, anyPanelConstraints);
            String path = temp.get(MountPointUtils.getMountPointDefaultPathLabel(mpt));
            JTextField pathInitial = new JTextField(temp.get(MountPointUtils.getMountPointDefaultPathLabel(mpt)));
            pathInitial.setPreferredSize(new Dimension(300, 20));
            pathInitial.setEnabled(false);
            anyPanelConstraints.gridx++;
            anyPanelConstraints.anchor = GridBagConstraints.EAST;
            anyPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
            anyPanelConstraints.weightx = 1.6;
            if (path != null) {
                if (pathWrong.contains(temp.get(MountPointUtils.getMountPointDefaultPathLabel(mpt)))) {
                    pathInitial.setEnabled(true);
                    pathInitial.setForeground(errorColor);
                    pathInitial.setToolTipText("This folder does not exist");
                    anyPanel.add(pathInitial, anyPanelConstraints);
                    anyPanelConstraints.fill = GridBagConstraints.NONE;
                    anyPanelConstraints.weightx = 0;
                    anyPanelConstraints.gridx++;
                    JButton clearButton = new JButton(deleteIcon);
                    clearButton.setHorizontalAlignment(SwingConstants.CENTER);
                    clearButton.setToolTipText("Click to delete mount point");
                    clearButton.addActionListener(delFolderPath(mpt, MountPointUtils.getMountPointDefaultPathLabel(mpt), true));
                    anyPanel.add(clearButton, anyPanelConstraints);
                } else {
                    pathInitial.setEnabled(false);
                    anyPanel.add(pathInitial, anyPanelConstraints);
                    anyPanelConstraints.gridx++;
                }
            }
            anyPanelConstraints.gridx++;
            anyPanelConstraints.fill = GridBagConstraints.NONE;
            anyPanelConstraints.weightx = 0;
            JLabel resultLabel = new JLabel(permanentIcon);
            resultLabel.setToolTipText("This mount point cannot be deleted");
            if (path != null) {
                anyPanel.add(resultLabel);
            }
            anyPanelConstraints.gridy++;
            //iterator on the map to display remaining key-values
            for (String key : temp.keySet()) {
                if (key.equals(MountPointUtils.getMountPointDefaultPathLabel(mpt)))
                    continue;
                anyPanelConstraints.gridx = 0;
                anyPanelConstraints.weightx = 0.1;
                anyPanelConstraints.anchor = GridBagConstraints.WEST;
                anyPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
                JLabel field = new JLabel(key + " : ");
                field.setPreferredSize(new Dimension(60, 20));
                anyPanel.add(field, anyPanelConstraints);
                JTextField resultPath = new JTextField(temp.get(key));
                resultPath.setPreferredSize(new Dimension(300, 20));
                resultPath.setEditable(false);
                resultPath.setEnabled(false);
                if (temp.get(key).length() > 87) {
                    resultPath.setEnabled(true);
                    resultPath.setEditable(true);
                }
                anyPanelConstraints.gridx++;
                anyPanelConstraints.anchor = GridBagConstraints.EAST;
                anyPanelConstraints.weightx = 1.6;
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
                JButton clearButton = new JButton(deleteIcon);
                clearButton.setHorizontalAlignment(SwingConstants.CENTER);
                clearButton.setToolTipText("Click to delete mount point");
                clearButton.addActionListener(delFolderPath(mpt, key, false));
                anyPanel.add(clearButton, anyPanelConstraints);
                anyPanelConstraints.gridy++;

            }
        }
    }

    private void initFastaFolderPanel() {
        fastaListPanel = new JPanel(new GridBagLayout());
        fastaListPanel.setBorder(BorderFactory.createTitledBorder("Fasta files folders"));
        fastaListPanelConstraints = new GridBagConstraints();
        fastaListPanelConstraints.insets = new Insets(5, 5, 5, 5);
        fastaListPanelConstraints.gridx = 0;
        fastaListPanelConstraints.gridy = 0;
        fastaListPanelConstraints.weightx = 1;
        fastaListPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
    }


    private ActionListener addFolderAction() {
        ActionListener addFolder = new ActionListener() {
            public void actionPerformed(ActionEvent event) {

                String folderField = folderLabelField.getText();
                String folderPath = folderPathField.getText();
                Path pathToTest = Paths.get(folderPath);
                Boolean pathExists = Files.exists(pathToTest);
                Boolean verifUserEntry = (!folderPath.isEmpty() && !folderField.isEmpty())
                        || (!folderPath.isEmpty() && dataTypeBox.getSelectedItem().equals("Fasta folder"));

                String type = dataTypeBox.getSelectedItem().toString();
                if (pathExists && verifUserEntry) {
                    switch (type) {
                        case "Result folder":
                            boolean addSuccesresult = ConfigManager.getInstance().getMountPointManager().addMountPointEntry(MountPointUtils.MountPointType.RESULT, folderField, folderPath);
                            if (addSuccesresult) {
                                folderPathField.setText("");
                                folderLabelField.setText("");
                                updateJpanel();
                            } else {
                                Popup.warning("The label and/or the path already exist please choose new values");
                                folderPathField.setText("");
                                folderLabelField.setText("");
                            }
                            break;

                        case "Mzdb folder":
                            boolean addSuccesmzdb = ConfigManager.getInstance().getMountPointManager().addMountPointEntry(MountPointUtils.MountPointType.MZDB, folderField, folderPath);
                            if (addSuccesmzdb) {
                                folderPathField.setText("");
                                folderLabelField.setText("");
                                updateJpanel();
                            } else {
                                Popup.warning("The label and/or the path already exist please choose new values");
                                folderPathField.setText("");
                                folderLabelField.setText("");
                            }
                            break;
                        case "Raw folders":
                            boolean addSuccesraw = ConfigManager.getInstance().getMountPointManager().addMountPointEntry(MountPointUtils.MountPointType.RAW, folderField, folderPath);
                            if (addSuccesraw) {
                                folderPathField.setText("");
                                folderLabelField.setText("");
                                updateJpanel();
                            } else {
                                Popup.warning("The label and/or the path already exist please choose new values");
                                folderPathField.setText("");
                                folderLabelField.setText("");
                            }
                            break;

                        // TODO not implemented
                        default: //(String) dataTypeBox.getSelectedItem() = FASTA File
                            fastaListPanelConstraints.weightx = 1;
                            //fastaListPanel.add(path, fastaListPanelConstraints);
                            fastaListPanelConstraints.gridx++;
                            fastaListPanelConstraints.weightx = 0;
                            //fastaListPanel.add(delete, fastaListPanelConstraints);
                            fastaListPanelConstraints.gridx = 0;
                            fastaListPanelConstraints.gridy++;
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
            }
        };
        return addFolder;
    }

    private ActionListener greyLabelforFasta() {
        ActionListener greyLabel = new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                if (dataTypeBox.getSelectedItem().equals("Fasta folder")) {
                    folderLabelField.setText("");
                    folderLabelField.setEnabled(false);
                } else {
                    folderLabelField.setEnabled(true);
                }
            }
        };
        return greyLabel;
    }

    private ActionListener openFolderView() {
        ActionListener openFolderView = new ActionListener() {

            public void actionPerformed(ActionEvent event) {
                JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
                jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int returnValue = jfc.showOpenDialog(null);

                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = jfc.getSelectedFile();
                    folderPathField.setToolTipText(selectedFile.getAbsolutePath());
                    folderPathField.setText(selectedFile.getAbsolutePath());
                }
            }
        };
        return openFolderView;
    }

    private ActionListener clearAction() {
        ActionListener clearFields = new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                folderPathField.setText("");
                folderLabelField.setText("");
            }
        };
        return clearFields;
    }

    private ActionListener delFolderPath(MountPointUtils.MountPointType mountPointType, String key, boolean forced) {
        ActionListener clearFields = new ActionListener() {
            public void actionPerformed(ActionEvent event) {

                boolean delSucces = ConfigManager.getInstance().getMountPointManager().delMountPointEntry(mountPointType, key, forced);
                if (delSucces) {
                    updateJpanel();
                } else {
                    Popup.warning("This Mount point cannot be deleted");
                }
            }
        };
        return clearFields;
    }


    public void updateValues() {
        updateJpanel();
//
        // TODO
    }


    public static void main(String[] args) {
        ConfigManager.getInstance().initialize();
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            JFrame f = new JFrame();
            f.add(new FolderPanel());
            f.pack();

            f.setVisible(true);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedLookAndFeelException e) {
            throw new RuntimeException(e);
        }


    }

}
