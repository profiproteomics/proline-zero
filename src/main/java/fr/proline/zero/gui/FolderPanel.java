package fr.proline.zero.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

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
	private JTextArea aide;

	public FolderPanel() {
		super();
		initialize();
	}

	private void initialize() {
		// creation du layout
		setLayout(new GridBagLayout());
		GridBagConstraints folderPanelConstraints = new GridBagConstraints();
		folderPanelConstraints.fill = GridBagConstraints.BOTH;
		folderPanelConstraints.anchor = GridBagConstraints.NORTH;
		folderPanelConstraints.weightx = 1;

		// creation des widgets
		// TODO : texte à changer et centrer avec une icone
		aide = new JTextArea();
		aide.setPreferredSize(new Dimension(300, 75));
		aide.setText("ici est l'aide concernant l'onglet folder");
		aide.setEditable(false);

		// ajout des widgets au layout
		folderPanelConstraints.gridx = 0;
		folderPanelConstraints.gridy = 0;
		add(aide, folderPanelConstraints);
		folderPanelConstraints.gridy++;

		folderPanelConstraints.weightx = 0;
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
		GridBagConstraints tmpFolderConstraint = new GridBagConstraints();

		// creation des widgets
		maximumTmpFolderSizeField = new JTextField();
		maximumTmpFolderSizeField.setPreferredSize(new Dimension(50, 20));

		// ajout des widgets au layout
		tmpFolderConstraint.gridx = 0;
		tmpFolderConstraint.gridy = 0;
		tmpFolderPanel.add(new JLabel("Maximum size for temp folder : ", SwingConstants.RIGHT), tmpFolderConstraint);

		tmpFolderConstraint.gridx++;
		tmpFolderPanel.add(maximumTmpFolderSizeField, tmpFolderConstraint);

		tmpFolderConstraint.gridx++;
		tmpFolderPanel.add(new JLabel("Mo"), tmpFolderConstraint);

		return tmpFolderPanel;
	}

	private JPanel createAddFolderPanel() {
		// creation du panel et du layout
		JPanel addFolderPanel = new JPanel(new GridBagLayout());
		addFolderPanel.setBorder(BorderFactory.createTitledBorder("Add folder"));
		GridBagConstraints addFolderConstraint = new GridBagConstraints();
		addFolderConstraint.anchor = GridBagConstraints.EAST;

		// creation des widgets
		dataTypeBox = new JComboBox<String>();
		dataTypeBox.addItem("Result folder");
		dataTypeBox.addItem("Mzdb folder");
		dataTypeBox.addItem("Fasta folder");
		dataTypeBox.setPreferredSize(new Dimension(100, 20));

		folderLabelField = new JTextField();
		folderLabelField.setPreferredSize(new Dimension(60, 20));

		folderPathField = new JTextField();
		folderPathField.setPreferredSize(new Dimension(160, 20));

		JButton addButton = new JButton("add");
		addButton.addActionListener(addFolderAction());

		JButton clearButton = new JButton("clear");

		JButton browseButton = new JButton("dossier");

		// ajout des widgets au layout
		addFolderConstraint.gridx = 0;
		addFolderConstraint.gridy = 0;
		addFolderPanel.add(new JLabel("Data type : ", SwingConstants.RIGHT), addFolderConstraint);
		addFolderConstraint.gridx++;
		addFolderConstraint.anchor = GridBagConstraints.WEST;
		addFolderPanel.add(dataTypeBox, addFolderConstraint);

		addFolderConstraint.gridx = 0;
		addFolderConstraint.gridy++;
		addFolderConstraint.anchor = GridBagConstraints.EAST;
		addFolderPanel.add(new JLabel("Label : ", SwingConstants.RIGHT), addFolderConstraint);
		addFolderConstraint.gridx++;
		addFolderConstraint.anchor = GridBagConstraints.WEST;
		addFolderPanel.add(folderLabelField, addFolderConstraint);

		addFolderConstraint.gridx = 0;
		addFolderConstraint.gridy++;
		addFolderConstraint.anchor = GridBagConstraints.EAST;
		addFolderPanel.add(new JLabel("Path : ", SwingConstants.RIGHT), addFolderConstraint);
		addFolderConstraint.gridx++;
		addFolderConstraint.anchor = GridBagConstraints.WEST;
		addFolderPanel.add(folderPathField, addFolderConstraint);
		addFolderConstraint.gridx++;
		addFolderPanel.add(browseButton, addFolderConstraint);

		addFolderConstraint.anchor = GridBagConstraints.EAST;
		addFolderConstraint.gridy++;
		addFolderConstraint.gridx = 1;
		addFolderPanel.add(clearButton, addFolderConstraint);

		addFolderConstraint.anchor = GridBagConstraints.CENTER;
		addFolderConstraint.gridx++;
		addFolderPanel.add(addButton, addFolderConstraint);

		return addFolderPanel;
	}

	private JPanel createFolderListPanel() {
		// creation du panel et layout
		JPanel folderListPanel = new JPanel(new GridBagLayout());
		folderListPanel.setBorder(BorderFactory.createTitledBorder("Folder list"));
		GridBagConstraints folderListPanelConstraints = new GridBagConstraints();
		folderListPanelConstraints.weightx = 0;
		folderListPanelConstraints.weighty = 0;
		folderListPanelConstraints.fill = GridBagConstraints.BOTH;

		// creation des panels en attribut de classe pour pouvoir ajouter dynamiquement
		// des elements
		this.initResultFolderPanel();
		this.initMzdbFolderPanel();
		this.initFastaFolderPanel();

		// ajout des panels au layout
		// TODO : rendre la liste des dossiers plus belle// result files folder
		folderListPanelConstraints.gridx = 0;
		folderListPanelConstraints.gridy = 0;
		folderListPanel.add(resultListPanel, folderListPanelConstraints);

		folderListPanelConstraints.gridy++;
		folderListPanel.add(mzdbListPanel, folderListPanelConstraints);

		// TODO : desactiver si seqrep décoché;
		folderListPanelConstraints.gridy++;
		folderListPanel.add(fastaListPanel, folderListPanelConstraints);

		return folderListPanel;
	}

	private void initResultFolderPanel() {
		resultListPanel = new JPanel(new GridBagLayout());
		resultListPanel.setBorder(BorderFactory.createTitledBorder("Results folders"));
		resultListPanelConstraints = new GridBagConstraints();
		resultListPanelConstraints.gridx = 0;
		resultListPanelConstraints.gridy = 0;
		resultListPanelConstraints.weightx = 1;
		resultListPanelConstraints.fill = GridBagConstraints.HORIZONTAL;

	}

	private void initMzdbFolderPanel() {
		mzdbListPanel = new JPanel(new GridBagLayout());
		mzdbListPanel.setBorder(BorderFactory.createTitledBorder("Mzdb files folders"));
		mzdbListPanelConstraints = new GridBagConstraints();
		mzdbListPanelConstraints.gridx = 0;
		mzdbListPanelConstraints.gridy = 0;
		mzdbListPanelConstraints.weightx = 1;
		mzdbListPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
	}

	private void initFastaFolderPanel() {
		fastaListPanel = new JPanel(new GridBagLayout());
		fastaListPanel.setBorder(BorderFactory.createTitledBorder("Fasta files folders"));
		fastaListPanelConstraints = new GridBagConstraints();
		fastaListPanelConstraints.gridx = 0;
		fastaListPanelConstraints.gridy = 0;
		fastaListPanelConstraints.weightx = 1;
		fastaListPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
	}

	private ActionListener addFolderAction() {
		ActionListener addFolder = new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if (!folderPathField.getText().isEmpty() && !folderLabelField.getText().isEmpty()) {
					JLabel label = new JLabel(folderLabelField.getText());
					JLabel path = new JLabel(folderPathField.getText());
					JButton delete = new JButton("x");

					switch ((String) dataTypeBox.getSelectedItem()) {
					case "Result folder":
						resultListPanel.add(label, resultListPanelConstraints);
						resultListPanelConstraints.gridx++;
						resultListPanel.add(path, resultListPanelConstraints);
						resultListPanelConstraints.gridx++;
						resultListPanel.add(delete, resultListPanelConstraints);
						resultListPanelConstraints.gridx = 0;
						resultListPanelConstraints.gridy++;

						ActionListener delFolderRes = new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								resultListPanel.remove(label);
								resultListPanel.remove(path);
								resultListPanel.remove(delete);
								revalidate();
								repaint();
							}
						};
						delete.addActionListener(delFolderRes);

						break;
					case "Mzdb folder":
						mzdbListPanel.add(label, mzdbListPanelConstraints);
						mzdbListPanelConstraints.gridx++;
						mzdbListPanel.add(path, mzdbListPanelConstraints);
						mzdbListPanelConstraints.gridx++;
						mzdbListPanel.add(delete, mzdbListPanelConstraints);
						mzdbListPanelConstraints.gridx = 0;
						mzdbListPanelConstraints.gridy++;

						ActionListener delFolderMZ = new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								mzdbListPanel.remove(label);
								mzdbListPanel.remove(path);
								mzdbListPanel.remove(delete);
								revalidate();
								repaint();
							}
						};
						delete.addActionListener(delFolderMZ);

						break;
					case "Fasta folder":
						fastaListPanel.add(label, fastaListPanelConstraints);
						fastaListPanelConstraints.gridx++;
						fastaListPanel.add(path, fastaListPanelConstraints);
						fastaListPanelConstraints.gridx++;
						fastaListPanel.add(delete, fastaListPanelConstraints);
						fastaListPanelConstraints.gridx = 0;
						fastaListPanelConstraints.gridy++;

						ActionListener delFolderFasta = new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								fastaListPanel.remove(label);
								fastaListPanel.remove(path);
								fastaListPanel.remove(delete);
								revalidate();
								repaint();
							}
						};
						delete.addActionListener(delFolderFasta);

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

}
