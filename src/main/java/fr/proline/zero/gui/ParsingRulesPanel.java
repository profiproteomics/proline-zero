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
	private JTextField labelField;
	private JTextField regExField;
	private JTextField fastaFileFIeld;
	private Component filler;
	private JTextArea aide;
	private JPanel addParsingRules;
	private GridBagConstraints addParsingRulesConstraints;

	public ParsingRulesPanel() {
		initialize();
	}

	private void initialize() {
		// creation du layout
		setLayout(new GridBagLayout());
		GridBagConstraints parsingPanelConstraints = new GridBagConstraints();
		parsingPanelConstraints.fill = GridBagConstraints.BOTH;
		parsingPanelConstraints.anchor = GridBagConstraints.NORTH;
		parsingPanelConstraints.weightx = 1;

		// creation des widgets
		// TODO : texte à changer et centrer avec une icone
		aide = new JTextArea();
		aide.setPreferredSize(new Dimension(300, 75));
		aide.setText("ici est l'aide concernant l'onglet parsing rules");
		aide.setEditable(false);

		// ajout des widgets au layout
		parsingPanelConstraints.gridx = 0;
		parsingPanelConstraints.gridy = 0;
		add(aide, parsingPanelConstraints);

		parsingPanelConstraints.gridy++;
		add(createParsingRulesPanel(), parsingPanelConstraints);

		parsingPanelConstraints.fill = GridBagConstraints.VERTICAL;
		parsingPanelConstraints.weighty = 1;
		add(Box.createHorizontalGlue(), parsingPanelConstraints);
	}

	private JPanel createParsingRulesPanel() {
		// creation du panel et du layout
		addParsingRules = new JPanel(new GridBagLayout());
		addParsingRules.setBorder(BorderFactory.createTitledBorder("Parsing rules"));
		addParsingRulesConstraints = new GridBagConstraints();
		addParsingRulesConstraints.fill = GridBagConstraints.BOTH;

		// creation des widgets
		labelField = new JTextField();
		labelField.setPreferredSize(new Dimension(60, 20));

		regExField = new JTextField();
		regExField.setPreferredSize(new Dimension(60, 20));

		fastaFileFIeld = new JTextField();
		fastaFileFIeld.setPreferredSize(new Dimension(60, 20));

		JButton plus = new JButton("+");
		plus.addActionListener(addParsingRule());

		filler = Box.createHorizontalGlue();

		// ajout des widgets au layout
		addParsingRulesConstraints.gridx = 0;
		addParsingRulesConstraints.gridy = 0;
		addParsingRules.add(new JLabel("Add a parsing rule : "), addParsingRulesConstraints);

		addParsingRulesConstraints.gridy++;
		addParsingRules.add(new JLabel("Label : "), addParsingRulesConstraints);

		addParsingRulesConstraints.gridx++;
		addParsingRules.add(new JLabel("Regex : "), addParsingRulesConstraints);

		addParsingRulesConstraints.gridx++;
		addParsingRules.add(new JLabel("Fasta file : "), addParsingRulesConstraints);

		addParsingRulesConstraints.gridx++;
		addParsingRulesConstraints.gridy++;
		addParsingRulesConstraints.gridx = 0;
		addParsingRules.add(labelField, addParsingRulesConstraints);

		addParsingRulesConstraints.gridx++;
		addParsingRules.add(regExField, addParsingRulesConstraints);

		addParsingRulesConstraints.gridx++;
		addParsingRules.add(fastaFileFIeld, addParsingRulesConstraints);

		addParsingRulesConstraints.gridx++;
		addParsingRules.add(plus, addParsingRulesConstraints);

		addParsingRulesConstraints.gridx++;
		addParsingRulesConstraints.gridy++;
		addParsingRulesConstraints.gridx = 0;
		addParsingRulesConstraints.fill = GridBagConstraints.VERTICAL;
		addParsingRulesConstraints.weighty = 1;
		addParsingRules.add(filler, addParsingRulesConstraints);

		return addParsingRules;
	}

	private ActionListener addParsingRule() {
		ActionListener addParseRule = new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if (!labelField.getText().isEmpty() && !regExField.getText().isEmpty()
						&& !fastaFileFIeld.getText().isEmpty()) {

					JLabel addedLabel = new JLabel(labelField.getText());
					JLabel addedRegex = new JLabel(regExField.getText());
					JLabel addedFasta = new JLabel(fastaFileFIeld.getText());

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

					labelField.setText("");
					regExField.setText("");
					fastaFileFIeld.setText("");

					addParsingRulesConstraints.fill = GridBagConstraints.VERTICAL;
					addParsingRulesConstraints.weighty = 1;
					addParsingRules.add(filler, addParsingRulesConstraints);
					revalidate();
					repaint();
				}
			}
		};
		return addParseRule;
	}
}
