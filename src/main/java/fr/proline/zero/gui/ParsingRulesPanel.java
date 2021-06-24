package fr.proline.zero.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ParsingRulesPanel extends JPanel {
	private JTextField labelField;
	private JTextField accessionParseRuleField;
	private JTextField fastaPatternField;
	private JTextField fastaVersionField;
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
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.NORTH;
		c.weightx = 1;

		// creation des widgets
		// TODO : texte à changer et centrer avec une icone
		aide = new JTextArea();
		aide.setMinimumSize(new Dimension(300, 75));
		aide.setPreferredSize(new Dimension(300, 75));
		aide.setText("ici est l'aide concernant l'onglet \nparsing rules");
		aide.setEditable(false);

		// ajout des widgets au layout
		c.gridx = 0;
		c.gridy = 0;
		add(aide, c);

		c.insets = new java.awt.Insets(5, 0, 0, 0);
		c.gridy++;
		add(createParsingRulesPanel(), c);

		c.fill = GridBagConstraints.VERTICAL;
		c.weighty = 1;
		add(Box.createHorizontalGlue(), c);
	}

	private JPanel createParsingRulesPanel() {
		// creation du panel et du layout
		addParsingRules = new JPanel(new GridBagLayout());
		addParsingRules.setBorder(BorderFactory.createTitledBorder("Parsing rules"));
		addParsingRulesConstraints = new GridBagConstraints();
		addParsingRulesConstraints.fill = GridBagConstraints.BOTH;
		addParsingRulesConstraints.insets = new java.awt.Insets(5, 5, 5, 5);

		// creation des widgets
		labelField = new JTextField();
		labelField.setPreferredSize(new Dimension(60, 20));

		accessionParseRuleField = new JTextField();
		accessionParseRuleField.setPreferredSize(new Dimension(60, 20));

		fastaPatternField = new JTextField();
		fastaPatternField.setPreferredSize(new Dimension(200, 20));

		fastaVersionField = new JTextField();
		fastaVersionField.setPreferredSize(new Dimension(200, 20));

		JButton plus = new JButton("+");
		try {
			Icon plusIcon = new ImageIcon(ImageIO.read(ClassLoader.getSystemResource("plus.png")));
			plus.setText("");
			plus.setIcon(plusIcon);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// plus.addActionListener(addParsingRule());

		filler = Box.createHorizontalGlue();

		// ajout des widgets au layout
		addParsingRulesConstraints.gridx = 0;
		addParsingRulesConstraints.gridy = 0;
		addParsingRules.add(new JLabel("Add a parsing rule : "), addParsingRulesConstraints);

		addParsingRulesConstraints.gridy++;
		addParsingRules.add(new JLabel("Label : "), addParsingRulesConstraints);

		addParsingRulesConstraints.gridx++;
		addParsingRules.add(new JLabel("Accession Parse Rule : "), addParsingRulesConstraints);

		addParsingRulesConstraints.gridy++;
		addParsingRulesConstraints.gridx = 0;
		addParsingRules.add(labelField, addParsingRulesConstraints);

		addParsingRulesConstraints.gridx++;
		addParsingRules.add(accessionParseRuleField, addParsingRulesConstraints);

		addParsingRulesConstraints.gridy++;
		addParsingRulesConstraints.gridx = 0;
		addParsingRules.add(new JLabel("Fasta Pattern : "), addParsingRulesConstraints);

		addParsingRulesConstraints.gridx++;
		addParsingRules.add(new JLabel("Fasta file : "), addParsingRulesConstraints);

		addParsingRulesConstraints.gridy++;
		addParsingRulesConstraints.gridx = 0;
		addParsingRulesConstraints.weightx = 1;
		addParsingRules.add(fastaPatternField, addParsingRulesConstraints);

		addParsingRulesConstraints.gridx++;
		addParsingRules.add(fastaVersionField, addParsingRulesConstraints);

		addParsingRulesConstraints.gridy = 2;
		addParsingRulesConstraints.gridheight = 3;
		addParsingRulesConstraints.gridx++;
		addParsingRulesConstraints.weightx = 0;
		addParsingRulesConstraints.fill = GridBagConstraints.NONE;
		addParsingRules.add(plus, addParsingRulesConstraints);

		addParsingRulesConstraints.gridy++;
		addParsingRulesConstraints.gridx = 0;
		addParsingRulesConstraints.weighty = 1;
		addParsingRules.add(filler, addParsingRulesConstraints);

		return addParsingRules;
	}

	// TODO : à refaire avec un nouveau panel
//	private ActionListener addParsingRule() {
//		ActionListener addParseRule = new ActionListener() {
//			public void actionPerformed(ActionEvent event) {
//				if (!labelField.getText().isEmpty() && !accessionParseRuleField.getText().isEmpty()
//						&& !fastaPatternField.getText().isEmpty()) {
//
//					JLabel addedLabel = new JLabel(labelField.getText());
//					JLabel addedRegex = new JLabel(accessionParseRuleField.getText());
//					JLabel addedFasta = new JLabel(fastaPatternField.getText());
//
//					addParsingRules.remove(filler);
//					addParsingRulesConstraints.fill = GridBagConstraints.NONE;
//					addParsingRulesConstraints.weighty = 0;
//					addParsingRulesConstraints.gridx = 0;
//
//					addParsingRules.add(addedLabel, addParsingRulesConstraints);
//					addParsingRulesConstraints.gridx++;
//
//					addParsingRules.add(addedRegex, addParsingRulesConstraints);
//					addParsingRulesConstraints.gridx++;
//
//					addParsingRules.add(addedFasta, addParsingRulesConstraints);
//					addParsingRulesConstraints.gridx++;
//
//					// remove button
//					JButton delete = new JButton("x");
//					try {
//						Icon crossIcon = new ImageIcon(ImageIO.read(ClassLoader.getSystemResource("cross.png")));
//						delete.setText("");
//						delete.setIcon(crossIcon);
//					} catch (IOException e1) {
//						// TODO Auto-generated catch block
//						e1.printStackTrace();
//					}
//					addParsingRules.add(delete, addParsingRulesConstraints);
//
//					ActionListener delParseRule = new ActionListener() {
//						public void actionPerformed(ActionEvent e) {
//							addParsingRules.remove(addedLabel);
//							addParsingRules.remove(addedFasta);
//							addParsingRules.remove(addedRegex);
//							addParsingRules.remove(delete);
//							revalidate();
//							repaint();
//						}
//					};
//					delete.addActionListener(delParseRule);
//					addParsingRulesConstraints.gridy++;
//
//					labelField.setText("");
//					accessionParseRuleField.setText("");
//					fastaPatternField.setText("");
//
//					addParsingRulesConstraints.fill = GridBagConstraints.VERTICAL;
//					addParsingRulesConstraints.weighty = 1;
//					addParsingRules.add(filler, addParsingRulesConstraints);
//					revalidate();
//					repaint();
//				}
//			}
//		};
//		return addParseRule;
//	}
}
