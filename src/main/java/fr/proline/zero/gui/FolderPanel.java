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
	private JTextField maximumTmpFolderSize;
	private JComboBox<String> dataType;
	private JTextField folderLabel;
	private JTextField folderPath;
	private JTextArea aide;

	public FolderPanel() {
		super();
		aide = new JTextArea();
		aide.setPreferredSize(new Dimension(300, 75));
		maximumTmpFolderSize = new JTextField();
		maximumTmpFolderSize.setPreferredSize(new Dimension(50, 20));
		dataType = new JComboBox<String>();
		dataType.setPreferredSize(new Dimension(100, 20));
		folderLabel = new JTextField();
		folderLabel.setPreferredSize(new Dimension(60, 20));
		folderPath = new JTextField();
		folderPath.setPreferredSize(new Dimension(160, 20));
		initialize();
	}

	private void initialize() {
		setLayout(new GridBagLayout());
		GridBagConstraints folderPanelConstraints = new GridBagConstraints();
		folderPanelConstraints.gridx = 0;
		folderPanelConstraints.gridy = 0;
		folderPanelConstraints.fill = GridBagConstraints.BOTH;
		folderPanelConstraints.anchor = GridBagConstraints.NORTH;
		folderPanelConstraints.weightx = 1;

		// bandeau d'aide
		// TODO : texte à changer et centrer avec une icone
		aide.setText("ici est l'aide concernant l'onglet folder");
		aide.setEditable(false);
		add(aide, folderPanelConstraints);
		folderPanelConstraints.gridy++;

		// maximum tmp folder size setting
		JPanel tmpFolderPanel = new JPanel();
		tmpFolderPanel.setLayout(new GridBagLayout());
		GridBagConstraints tmpFolderConstraint = new GridBagConstraints();
		tmpFolderConstraint.gridx = 0;
		tmpFolderConstraint.gridy = 0;
		tmpFolderPanel.add(new JLabel("Maximum size for temp folder : ", SwingConstants.RIGHT), tmpFolderConstraint);
		tmpFolderConstraint.gridx++;
		tmpFolderPanel.add(maximumTmpFolderSize, tmpFolderConstraint);
		tmpFolderConstraint.gridx++;
		tmpFolderPanel.add(new JLabel("Mo"), tmpFolderConstraint);
		folderPanelConstraints.weightx = 0;
		add(tmpFolderPanel, folderPanelConstraints);

		// addfolder panel
		JPanel addFolderPanel = new JPanel();
		addFolderPanel.setBorder(BorderFactory.createTitledBorder("Add folder"));
		addFolderPanel.setLayout(new GridBagLayout());
		GridBagConstraints addFolderConstraint = new GridBagConstraints();
		addFolderConstraint.gridx = 0;
		addFolderConstraint.gridy = 0;
		addFolderConstraint.anchor = GridBagConstraints.EAST;

		// datatype comboBox
		addFolderPanel.add(new JLabel("Data type : ", SwingConstants.RIGHT), addFolderConstraint);
		addFolderConstraint.gridx++;
		dataType.addItem("Result folder");
		dataType.addItem("Mzdb folder");
		dataType.addItem("Fasta folder");
		addFolderConstraint.anchor = GridBagConstraints.WEST;
		addFolderPanel.add(dataType, addFolderConstraint);

		// folder label
		addFolderConstraint.gridx = 0;
		addFolderConstraint.gridy++;
		addFolderConstraint.anchor = GridBagConstraints.EAST;
		addFolderPanel.add(new JLabel("Label : ", SwingConstants.RIGHT), addFolderConstraint);
		addFolderConstraint.gridx++;
		addFolderConstraint.anchor = GridBagConstraints.WEST;
		addFolderPanel.add(folderLabel, addFolderConstraint);

		// folder path
		addFolderConstraint.gridx = 0;
		addFolderConstraint.gridy++;
		addFolderConstraint.anchor = GridBagConstraints.EAST;
		addFolderPanel.add(new JLabel("Path : ", SwingConstants.RIGHT), addFolderConstraint);
		addFolderConstraint.gridx++;
		addFolderConstraint.anchor = GridBagConstraints.WEST;
		addFolderPanel.add(folderPath, addFolderConstraint);
		addFolderConstraint.gridx++;

		// browse button
		// TODO : mettre une icone
		JButton browseButton = new JButton("dossier");
		addFolderPanel.add(browseButton, addFolderConstraint);

		// clear button
		JButton clearButton = new JButton("clear");
		addFolderConstraint.anchor = GridBagConstraints.EAST;
		addFolderConstraint.gridy++;
		addFolderConstraint.gridx = 1;
		addFolderPanel.add(clearButton, addFolderConstraint);

		// add button
		JButton addButton = new JButton("add");
		addFolderConstraint.anchor = GridBagConstraints.CENTER;
		addFolderConstraint.gridx++;

		addFolderPanel.add(addButton, addFolderConstraint);

		// folder list panel
		JPanel folderListPanel = new JPanel();
		folderListPanel.setBorder(BorderFactory.createTitledBorder("Folder list"));
		folderListPanel.setLayout(new GridBagLayout());
		GridBagConstraints folderListPanelConstraints = new GridBagConstraints();
		folderListPanelConstraints.gridx = 0;
		folderListPanelConstraints.gridy = 0;
		folderListPanelConstraints.weightx = 0;
		folderListPanelConstraints.weighty = 0;
		folderListPanelConstraints.fill = GridBagConstraints.BOTH;

		// TODO : rendre la liste des dossiers plus belle

		// result files folder
		JPanel resultListPanel = new JPanel();
		resultListPanel.setBorder(BorderFactory.createTitledBorder("Results folders"));
		resultListPanel.setLayout(new GridBagLayout());
		GridBagConstraints resultListPanelConstraints = new GridBagConstraints();
		resultListPanelConstraints.gridx = 0;
		resultListPanelConstraints.gridy = 0;
		resultListPanelConstraints.weightx = 1;
		resultListPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
		folderListPanel.add(resultListPanel, folderListPanelConstraints);

		// mzdb files folder panel
		JPanel mzdbListPanel = new JPanel();
		mzdbListPanel.setBorder(BorderFactory.createTitledBorder("Mzdb files folders"));
		mzdbListPanel.setLayout(new GridBagLayout());
		GridBagConstraints mzdbListPanelConstraints = new GridBagConstraints();
		mzdbListPanelConstraints.gridx = 0;
		mzdbListPanelConstraints.gridy = 0;
		mzdbListPanelConstraints.weightx = 1;
		mzdbListPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
		folderListPanelConstraints.gridy++;
		folderListPanel.add(mzdbListPanel, folderListPanelConstraints);

		// fasta files folder panel
		// TODO : desactiver si seqrep décoché;
		JPanel fastaListPanel = new JPanel();
		fastaListPanel.setBorder(BorderFactory.createTitledBorder("Fasta files folders"));
		fastaListPanel.setLayout(new GridBagLayout());
		GridBagConstraints fastaListPanelConstraints = new GridBagConstraints();
		fastaListPanelConstraints.gridx = 0;
		fastaListPanelConstraints.gridy = 0;
		fastaListPanelConstraints.weightx = 1;
		fastaListPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
		folderListPanelConstraints.gridy++;
		folderListPanel.add(fastaListPanel, folderListPanelConstraints);

		// action du add button
		ActionListener addFolder = new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if (!folderPath.getText().isEmpty() && !folderLabel.getText().isEmpty()) {
					JLabel label = new JLabel(folderLabel.getText());
					JLabel path = new JLabel(folderPath.getText());
					JButton delete = new JButton("x");

					switch ((String) dataType.getSelectedItem()) {
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
					folderPath.setText("");
					folderLabel.setText("");
					revalidate();
					repaint();
				}
			}
		};
		addButton.addActionListener(addFolder);

		folderPanelConstraints.gridy++;
		add(addFolderPanel, folderPanelConstraints);

		folderPanelConstraints.gridy++;
		add(folderListPanel, folderPanelConstraints);

		folderPanelConstraints.gridy++;
		folderPanelConstraints.weighty = 1;
		add(Box.createHorizontalGlue(), folderPanelConstraints);

	}
}
