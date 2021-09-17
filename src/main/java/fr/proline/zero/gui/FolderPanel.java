package fr.proline.zero.gui;

import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileSystemView;

import fr.proline.zero.util.ConfigManager;
import fr.proline.zero.util.SettingsConstant;

public class FolderPanel extends JPanel {
	private JTextField maximumTmpFolderSizeField;
	private JComboBox<String> dataTypeBox;
	private JTextField folderLabelField;
	private JTextField folderPathField;
	private JPanel resultListPanel;
	private GridBagConstraints resultListPanelConstraints;
	private JPanel mzdbListPanel;
	private GridBagConstraints mzdbListPanelConstraints;
	private JPanel fastaListPanel;
	private GridBagConstraints fastaListPanelConstraints;

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
		HelpHeaderPanel help = new HelpHeaderPanel("Folder" , SettingsConstant.FOLDERS_HELP_PANE);
		add(help, folderPanelConstraints);

		folderPanelConstraints.insets = new java.awt.Insets(20, 15, 0, 15);
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

	private JPanel createTmpFolderPanel() {
		// creation du panel et du layout
		JPanel tmpFolderPanel = new JPanel(new GridBagLayout());
		tmpFolderPanel.setToolTipText(SettingsConstant.FOLDER_MAX_SIZE_TOOLTIP);
		GridBagConstraints tmpFolderConstraint = new GridBagConstraints();
		tmpFolderConstraint.insets = new java.awt.Insets(5, 5, 5, 5);
		tmpFolderConstraint.anchor = GridBagConstraints.NORTHWEST;

		// creation des widgets

		maximumTmpFolderSizeField = new JTextField();
		maximumTmpFolderSizeField.setPreferredSize(new Dimension(50, 20));
		maximumTmpFolderSizeField.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
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
		dataTypeBox.addItem("Result folder");
		dataTypeBox.addItem("Mzdb folder");
		dataTypeBox.addItem("Fasta folder");
		dataTypeBox.setPreferredSize(new Dimension(100, 20));
		dataTypeBox.setMinimumSize(new Dimension(100, 20));
		dataTypeBox.addActionListener(greyLabelforFasta());

		folderLabelField = new JTextField();
		folderLabelField.setPreferredSize(new Dimension(60, 20));
		folderLabelField.setMinimumSize(new Dimension(60, 20));

		folderPathField = new JTextField();
		folderPathField.setPreferredSize(new Dimension(160, 20));
		folderPathField.setEditable(false);

		JButton addButton = new JButton("add");
		JButton clearButton = new JButton("clear");
		JButton browseButton = new JButton("folder");
		try {

			Icon addIcon = new ImageIcon(ImageIO.read(ClassLoader.getSystemResource("plus.png")));
			addButton = new JButton("add", addIcon);
			addButton.addActionListener(addFolderAction());

			Icon eraserIcon = new ImageIcon(ImageIO.read(ClassLoader.getSystemResource("eraser.png")));
			clearButton = new JButton("clear", eraserIcon);

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
		l.setEnabled(false);
		dataTypeBox.setEnabled(false);
		addFolderPanel.add(l, addFolderConstraint);
		addFolderConstraint.gridx++;
		addFolderConstraint.anchor = GridBagConstraints.WEST;
		addFolderPanel.add(dataTypeBox, addFolderConstraint);

		addFolderConstraint.gridx = 0;
		addFolderConstraint.gridy++;
		addFolderConstraint.anchor = GridBagConstraints.EAST;
		l = new JLabel("Label : ", SwingConstants.RIGHT);
		l.setEnabled(false);
		folderLabelField.setEnabled(false);
		addFolderPanel.add(l, addFolderConstraint);
		addFolderConstraint.gridx++;
		addFolderConstraint.anchor = GridBagConstraints.WEST;
		addFolderConstraint.weightx = 0.3;
		addFolderPanel.add(folderLabelField, addFolderConstraint);

		addFolderConstraint.weightx = 0;
		addFolderConstraint.gridx = 0;
		addFolderConstraint.gridy++;
		addFolderConstraint.anchor = GridBagConstraints.EAST;
		l = new JLabel("Path : ", SwingConstants.RIGHT);
		l.setEnabled(false);
		folderPathField.setEnabled(false);
		browseButton.setEnabled(false);
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
		clearButton.setEnabled(false);
		addFolderPanel.add(clearButton, addFolderConstraint);

		addFolderConstraint.anchor = GridBagConstraints.CENTER;
		addFolderConstraint.gridx++;
		addButton.setEnabled(false);
		addFolderPanel.add(addButton, addFolderConstraint);

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
		this.initResultFolderPanel();
		this.initMzdbFolderPanel();
		this.initFastaFolderPanel();

		// ajout des panels au layout
		// TODO : rendre la liste des dossiers plus belle// result files folder
		c.gridx = 0;
		c.gridy = 0;
		folderListPanel.add(resultListPanel, c);

		c.gridy++;
		folderListPanel.add(mzdbListPanel, c);

		// TODO : desactiver si seqrep décoché;
		c.gridy++;
		folderListPanel.add(fastaListPanel, c);

		return folderListPanel;
	}

	private void initResultFolderPanel() {
		resultListPanel = new JPanel(new GridBagLayout());
		resultListPanel.setBorder(BorderFactory.createTitledBorder("Results folders"));
		resultListPanelConstraints = new GridBagConstraints();
		resultListPanelConstraints.insets = new Insets(5, 5, 5, 5);
		resultListPanelConstraints.gridx = 0;
		resultListPanelConstraints.gridy = 0;
		resultListPanelConstraints.weightx = 1;
		resultListPanelConstraints.fill = GridBagConstraints.HORIZONTAL;

	}

	private void initMzdbFolderPanel() {
		mzdbListPanel = new JPanel(new GridBagLayout());
		mzdbListPanel.setBorder(BorderFactory.createTitledBorder("Mzdb files folders"));
		mzdbListPanelConstraints = new GridBagConstraints();
		mzdbListPanelConstraints.insets = new Insets(5, 5, 5, 5);
		mzdbListPanelConstraints.gridx = 0;
		mzdbListPanelConstraints.gridy = 0;
		mzdbListPanelConstraints.weightx = 1;
		mzdbListPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
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
		//VDS TODO : Enable save info in corresponding config files
		ActionListener addFolder = new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if ((!folderPathField.getText().isEmpty() && !folderLabelField.getText().isEmpty())
						|| (!folderPathField.getText().isEmpty()
								&& dataTypeBox.getSelectedItem().equals("Fasta folder"))) {
					JLabel label = new JLabel(folderLabelField.getText());
					JLabel path = new JLabel(folderPathField.getText());

//					JButton delete = new JButton("x");
//					try {
//						Icon crossIcon = new ImageIcon(ImageIO.read(ClassLoader.getSystemResource("cross.png")));
//						delete.setText("");
//						delete.setIcon(crossIcon);
//					} catch (IOException e1) {
//						// TODO Auto-generated catch block
//						e1.printStackTrace();
//					}

					switch ((String) dataTypeBox.getSelectedItem()) {
					case "Result folder":
						resultListPanelConstraints.weightx = 1;
						resultListPanel.add(label, resultListPanelConstraints);
						resultListPanelConstraints.gridx++;
						resultListPanel.add(path, resultListPanelConstraints);
						resultListPanelConstraints.gridx++;
						resultListPanelConstraints.weightx = 0;
//						resultListPanel.add(delete, resultListPanelConstraints);
						resultListPanelConstraints.gridx = 0;
						resultListPanelConstraints.gridy++;

//						ActionListener delFolderRes = new ActionListener() {
//							public void actionPerformed(ActionEvent e) {
//								resultListPanel.remove(label);
//								resultListPanel.remove(path);
//								resultListPanel.remove(delete);
//								revalidate();
//								repaint();
//							}
//						};
//						delete.addActionListener(delFolderRes);

						break;
					case "Mzdb folder":
						mzdbListPanelConstraints.weightx = 1;
						mzdbListPanel.add(label, mzdbListPanelConstraints);
						mzdbListPanelConstraints.gridx++;
						mzdbListPanel.add(path, mzdbListPanelConstraints);
						mzdbListPanelConstraints.gridx++;
						mzdbListPanelConstraints.weightx = 0;
//						mzdbListPanel.add(delete, mzdbListPanelConstraints);
						mzdbListPanelConstraints.gridx = 0;
						mzdbListPanelConstraints.gridy++;

//						ActionListener delFolderMZ = new ActionListener() {
//							public void actionPerformed(ActionEvent e) {
//								mzdbListPanel.remove(label);
//								mzdbListPanel.remove(path);
//								mzdbListPanel.remove(delete);
//								revalidate();
//								repaint();
//							}
//						};
//						delete.addActionListener(delFolderMZ);

						break;
					case "Fasta folder":
						fastaListPanelConstraints.weightx = 1;
						fastaListPanel.add(path, fastaListPanelConstraints);
						fastaListPanelConstraints.gridx++;
						fastaListPanelConstraints.weightx = 0;
//						fastaListPanel.add(delete, fastaListPanelConstraints);
						fastaListPanelConstraints.gridx = 0;
						fastaListPanelConstraints.gridy++;

//						ActionListener delFolderFasta = new ActionListener() {
//							public void actionPerformed(ActionEvent e) {
//								fastaListPanel.remove(label);
//								fastaListPanel.remove(path);
//								fastaListPanel.remove(delete);
//								revalidate();
//								repaint();
//							}
//						};
//						delete.addActionListener(delFolderFasta);

						break;

					}
					folderPathField.setText("");
					folderLabelField.setText("");
					revalidate();
					repaint();
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

	public void updateValues() {
		// TODO
	}
}
