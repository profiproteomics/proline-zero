package fr.proline.zero.gui;

import java.awt.*;

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


import fr.proline.studio.utils.IconManager;
import fr.proline.zero.util.*;

import static java.lang.String.valueOf;


public class FolderPanel extends JPanel {
    private JComboBox<String> dataTypeBox;
    private JTextField folderLabelField;
    private JTextField folderPathField;


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
        maximumTmpFolderSizeField.setText(valueOf(ConfigManager.getInstance().getMaxTmpFolderSize()));
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


        dataTypeBox.addActionListener(e -> {
            greyLabelforFasta();
        });

        dataTypeBox.setEnabled(true);

        folderLabelField = new JTextField();


        folderPathField = new JTextField();


        JButton addButton = new JButton(IconManager.getIcon(IconManager.IconType.PLUS_16X16));
        JButton clearButton = new JButton(IconManager.getIcon(IconManager.IconType.CLEAR_ALL));
        JButton browseButton = new JButton(IconManager.getIcon(IconManager.IconType.OPEN_FILE));

        addButton.addActionListener(e -> {
            addFolderAction();

        });

        clearButton.addActionListener(e -> {
            clearAction();
        });
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
        ArrayList<String> pathWrong = ConfigManager.getInstance().getMountPointManager().getInvalidPaths();
        GridBagConstraints anyPanelConstraints = new GridBagConstraints();
        anyPanel.setBorder(BorderFactory.createTitledBorder(mpt.getDisplayString()));
        anyPanelConstraints.insets = new Insets(5, 5, 5, 5);
        anyPanelConstraints.gridx = 0;
        anyPanelConstraints.gridy = 0;
        anyPanelConstraints.weightx = 0;
        anyPanelConstraints.fill = GridBagConstraints.HORIZONTAL;


        // get mounting points to be displayed
        Map<String, String> temp = ConfigManager.getInstance().getMountPointManager().getMountPointMap().get(mpt);


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
                    pathInitial.setToolTipText("This folder does not exist");
                    anyPanel.add(pathInitial, anyPanelConstraints);
                    anyPanelConstraints.fill = GridBagConstraints.NONE;
                    anyPanelConstraints.weightx = 0;
                    anyPanelConstraints.gridx++;
                    JButton clearButton = new JButton(IconManager.getIcon(IconManager.IconType.TRASH));
                    clearButton.setHorizontalAlignment(SwingConstants.CENTER);
                    clearButton.setToolTipText("Click to delete mount point");
                    clearButton.setSize(20, 15);
                    clearButton.addActionListener(e -> {
                        deleteFolderPath(mpt, MountPointUtils.getMountPointDefaultPathLabel(mpt), true);
                    });
                    anyPanel.add(clearButton, anyPanelConstraints);

                } else {
                    pathInitial.setEnabled(false);
                    anyPanel.add(pathInitial, anyPanelConstraints);

                }
            }
            anyPanelConstraints.gridx++;
            anyPanelConstraints.fill = GridBagConstraints.NONE;
            anyPanelConstraints.weightx = 0;
            JLabel resultLabel = new JLabel(IconManager.getIcon(IconManager.IconType.LOCK));
            resultLabel.setToolTipText("This mount point cannot be deleted");
            if (path != null && !pathWrong.contains(temp.get(MountPointUtils.getMountPointDefaultPathLabel(mpt)))) {
                anyPanel.add(resultLabel);
            }
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
                JButton clearButton = new JButton(IconManager.getIcon(IconManager.IconType.TRASH));
                clearButton.setSize(20, 30);
                clearButton.setHorizontalAlignment(SwingConstants.CENTER);
                clearButton.setToolTipText("Click to delete mount point");

                clearButton.addActionListener(e -> {
                    deleteFolderPath(mpt, key, false);
                });
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


        System.out.println("maximum:   " + maximum);
        return maximum;


    }


    private void initFastaFolderPanel(JPanel fastaListPanel) {
        //fastaListPanel = new JPanel(new GridBagLayout());
        fastaListPanel.setBorder(BorderFactory.createTitledBorder("Fasta files folders"));
        GridBagConstraints fastaListPanelConstraints = new GridBagConstraints();

        fastaListPanelConstraints.insets = new Insets(5, 5, 5, 5);
        fastaListPanelConstraints.gridx = 0;
        fastaListPanelConstraints.gridy = 0;


        ArrayList<String> fastaToBeDisplayed = ConfigManager.getInstance().getParsingRulesManager().getFastaPaths();
        for (int k = 0; k < fastaToBeDisplayed.size(); k++) {
            fastaListPanelConstraints.gridx = 0;
            fastaListPanelConstraints.weightx = 0;
            fastaListPanelConstraints.anchor = GridBagConstraints.EAST;
            fastaListPanelConstraints.fill = GridBagConstraints.NONE;
            JLabel fastaLabel = new JLabel("Folder    " + valueOf(k + 1) + ":", SwingConstants.RIGHT);
            fastaLabel.setPreferredSize(new Dimension(getMaximumSizesOfJLabels() + 8, 20));
            fastaListPanel.add(fastaLabel, fastaListPanelConstraints);
            JTextField pathFasta = new JTextField(fastaToBeDisplayed.get(k));
            pathFasta.setEnabled(false);
            fastaListPanelConstraints.gridx++;
            fastaListPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
            fastaListPanelConstraints.anchor = GridBagConstraints.WEST;
            fastaListPanelConstraints.weightx = 1;

            fastaListPanel.add(pathFasta, fastaListPanelConstraints);
            fastaListPanelConstraints.gridx++;

            JButton clearButton = new JButton(IconManager.getIcon(IconManager.IconType.TRASH));

            final int kFinal = k;
            clearButton.addActionListener(e -> {
                deleteFastaFolder(fastaToBeDisplayed.get(kFinal));
            });

            clearButton.setHorizontalAlignment(SwingConstants.CENTER);
            fastaListPanelConstraints.weightx = 0;
            fastaListPanel.add(clearButton, fastaListPanelConstraints);
            fastaListPanelConstraints.gridy++;

        }
    }

    private void addFolderAction() {
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
                    boolean addSuccessResult = ConfigManager.getInstance().getMountPointManager().addMountPointEntry(MountPointUtils.MountPointType.RESULT, folderField, folderPath);
                    if (addSuccessResult) {
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
                    boolean addSuccessMzdb = ConfigManager.getInstance().getMountPointManager().addMountPointEntry(MountPointUtils.MountPointType.MZDB, folderField, folderPath);
                    if (addSuccessMzdb) {
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
                    boolean addSuccesRaw = ConfigManager.getInstance().getMountPointManager().addMountPointEntry(MountPointUtils.MountPointType.RAW, folderField, folderPath);
                    if (addSuccesRaw) {
                        folderPathField.setText("");
                        folderLabelField.setText("");
                        updateJpanel();
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
        boolean delSucces = ConfigManager.getInstance().getMountPointManager().delMountPointEntry(mountPointType, key, forced);
        if (delSucces) {
            updateJpanel();
        } else {
            Popup.warning("This Mount point cannot be deleted");
        }

    }


    private void deleteFastaFolder(String path) {
        boolean dellSucess = ConfigManager.getInstance().getParsingRulesManager().deleteFastaFolder(path);
        if (dellSucess) {
            updateJpanel();
        } else {
            Popup.warning("Error while deleting the directory");
        }

    }


    public void updateValues() {
        updateJpanel();
//
        // TODO
    }

    // calculate the size in Pixels of the text of a JLabel
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
