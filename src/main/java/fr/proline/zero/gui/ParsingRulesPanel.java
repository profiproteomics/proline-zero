package fr.proline.zero.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ParsingRulesPanel extends JPanel {
	private JTextField label;
	private JTextField regEx;
	private JTextField fastaFile;
	private JTextArea aide;
	private Component filler;

	public ParsingRulesPanel() {
		label = new JTextField();
		label.setPreferredSize(new Dimension(60, 20));
		regEx = new JTextField();
		regEx.setPreferredSize(new Dimension(60, 20));
		fastaFile = new JTextField();
		fastaFile.setPreferredSize(new Dimension(60, 20));
		aide = new JTextArea();
		aide.setPreferredSize(new Dimension(300, 75));
		initialize();
	}

	private void initialize() {
		setLayout(new GridBagLayout());
		GridBagConstraints parsingPanelConstraints = new GridBagConstraints();
		parsingPanelConstraints.gridx = 0;
		parsingPanelConstraints.gridy = 0;
		parsingPanelConstraints.fill = GridBagConstraints.BOTH;
		parsingPanelConstraints.anchor = GridBagConstraints.NORTH;
		parsingPanelConstraints.weightx = 1;

		// bandeau d'aide
		// TODO : texte à changer et centrer avec une icone
		aide.setText("ici est l'aide concernant l'onglet parsing rules");
		aide.setEditable(false);
		add(aide, parsingPanelConstraints);
		parsingPanelConstraints.gridy++;

		// panel des parsing rules
		JPanel addParsingRules = new JPanel();
		addParsingRules.setBorder(BorderFactory.createTitledBorder("Parsing rules"));
		addParsingRules.setLayout(new GridBagLayout());
		GridBagConstraints addParsingRulesConstraints = new GridBagConstraints();
		addParsingRulesConstraints.gridx = 0;
		addParsingRulesConstraints.gridy = 0;
		addParsingRulesConstraints.fill = GridBagConstraints.BOTH;

		addParsingRules.add(new JLabel("Add a parsing rule : "), addParsingRulesConstraints);
		addParsingRulesConstraints.gridy++;

		// add parsing rule

		// add parsing rule label
		addParsingRules.add(new JLabel("Label : "), addParsingRulesConstraints);
		addParsingRulesConstraints.gridx++;

		addParsingRules.add(new JLabel("Regex : "), addParsingRulesConstraints);
		addParsingRulesConstraints.gridx++;

		addParsingRules.add(new JLabel("Fasta file : "), addParsingRulesConstraints);
		addParsingRulesConstraints.gridx++;

		addParsingRulesConstraints.gridy++;
		addParsingRulesConstraints.gridx = 0;

		// add parsing rule text fields
		label.setToolTipText("label");
		addParsingRules.add(label, addParsingRulesConstraints);
		addParsingRulesConstraints.gridx++;

		regEx.setToolTipText("regEx");
		addParsingRules.add(regEx, addParsingRulesConstraints);
		addParsingRulesConstraints.gridx++;

		fastaFile.setToolTipText("Fasta file");
		addParsingRules.add(fastaFile, addParsingRulesConstraints);
		addParsingRulesConstraints.gridx++;

		// add parsing rule button
		JButton plus = new JButton("+");
		ActionListener addParseRule = new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if (!label.getText().isEmpty() && !regEx.getText().isEmpty() && !fastaFile.getText().isEmpty()) {

					JLabel addedLabel = new JLabel(label.getText());
					JLabel addedRegex = new JLabel(regEx.getText());
					JLabel addedFasta = new JLabel(fastaFile.getText());

					addParsingRules.remove(filler);
					addParsingRulesConstraints.fill = GridBagConstraints.NONE;
					addParsingRulesConstraints.weighty = 0;
					addParsingRulesConstraints.gridx = 0;

					addParsingRules.add(addedLabel, addParsingRulesConstraints);
					addParsingRulesConstraints.gridx++;

					addParsingRules.add(addedRegex, addParsingRulesConstraints);
					addParsingRulesConstraints.gridx++;

					addParsingRules.add(addedFasta, addParsingRulesConstraints);
					addParsingRulesConstraints.gridx++;

					// remove button
					JButton delete = new JButton("x");
					addParsingRules.add(delete, addParsingRulesConstraints);

					ActionListener delParseRule = new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							addParsingRules.remove(addedLabel);
							addParsingRules.remove(addedFasta);
							addParsingRules.remove(addedRegex);
							addParsingRules.remove(delete);
							revalidate();
							repaint();
						}
					};
					delete.addActionListener(delParseRule);
					addParsingRulesConstraints.gridy++;

					label.setText("");
					regEx.setText("");
					fastaFile.setText("");

					addParsingRulesConstraints.fill = GridBagConstraints.VERTICAL;
					addParsingRulesConstraints.weighty = 1;
					addParsingRules.add(filler, addParsingRulesConstraints);
					revalidate();
					repaint();
				}
			}
		};
		plus.addActionListener(addParseRule);
		addParsingRules.add(plus, addParsingRulesConstraints);
		addParsingRulesConstraints.gridx++;

		addParsingRulesConstraints.gridy++;
		addParsingRulesConstraints.gridx = 0;

		addParsingRulesConstraints.fill = GridBagConstraints.VERTICAL;
		addParsingRulesConstraints.weighty = 1;
		filler = Box.createHorizontalGlue();
		addParsingRules.add(filler, addParsingRulesConstraints);

		add(addParsingRules, parsingPanelConstraints);

		parsingPanelConstraints.fill = GridBagConstraints.VERTICAL;
		parsingPanelConstraints.weighty = 1;
		add(Box.createHorizontalGlue(), parsingPanelConstraints);
	}
}
