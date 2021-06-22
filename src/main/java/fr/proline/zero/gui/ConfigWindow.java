package fr.proline.zero.gui;

import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;

import fr.proline.zero.util.ProlineFiles;

public class ConfigWindow {

	private JFrame frame;
	private JTabbedPane tabbedPane;
	private MemoryPanel memoryPanel;
	private FolderPanel folderPanel;
	private ServerPanel serverPanel;
	private ParsingRulesPanel parsePanel;
	private JCheckBox doNotShowAgainBox;
	private JCheckBox serverModuleBox;
	private JCheckBox studioModuleBox;
	private JCheckBox seqRepModuleBox;
	private JButton continueButton;
	private JButton cancelButton;
	private JButton restoreButton;

	public ConfigWindow() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					initialize();
					frame.setMinimumSize(frame.getSize());
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		// TODO gerer le resizing
		frame = new JFrame();
		frame.setTitle("Proline zero config window");
		frame.setBounds(100, 100, 400, 570);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.gridwidth = 3;
		c.gridx = 0;
		c.gridy = 0;

		// ajout des differents panels
		panel.add(createModulesPanel(), c);
		c.weightx = 1;
		c.weighty = 1;
		c.gridy++;
		panel.add(createTabPanel(), c);
		c.gridwidth = 3;
		c.gridy++;
		c.weighty = 0;
		doNotShowAgainBox = new JCheckBox("Do not show again");
		panel.add(doNotShowAgainBox, c);
		c.gridy++;
		c.weightx = 5;
		panel.add(createBottomButtonsPanel(), c);

		frame.setContentPane(panel);
	}

	private JTabbedPane createTabPanel() {
		tabbedPane = new JTabbedPane();
		memoryPanel = new MemoryPanel();
		folderPanel = new FolderPanel();
		serverPanel = new ServerPanel();
		parsePanel = new ParsingRulesPanel();
		tabbedPane.add(memoryPanel, "Memory");
		tabbedPane.add(folderPanel, "Folders");
		tabbedPane.add(serverPanel, "Server");
		tabbedPane.add(parsePanel, "Parsing rules");

		return tabbedPane;
	}

	private JPanel createModulesPanel() {

		// mise en place du panel et layout
		JPanel modulePane = new JPanel(new GridBagLayout());

		GridBagConstraints moduleConstraint = new GridBagConstraints();
		moduleConstraint.fill = GridBagConstraints.NONE;
		moduleConstraint.anchor = GridBagConstraints.WEST;

		// mise en place des widgets
		serverModuleBox = new JCheckBox("Start proline server");
		serverModuleBox.setSelected(true);
		serverModuleBox.setEnabled(false);

		studioModuleBox = new JCheckBox("Start proline studio");
		studioModuleBox.setSelected(true);

		seqRepModuleBox = new JCheckBox("Start sequence repository");
		seqRepModuleBox.setSelected(true);
		ActionListener checkSeqRep = new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if (seqRepModuleBox.isSelected()) {
					tabbedPane.setEnabledAt(3, true);
				} else {
					if (tabbedPane.getSelectedIndex() == 3) {
						tabbedPane.setSelectedIndex(0);
					}
					tabbedPane.setEnabledAt(3, false);
				}
			}
		};
		seqRepModuleBox.addActionListener(checkSeqRep);

		// ajout des widgets
		moduleConstraint.gridy = 0;
		modulePane.add(serverModuleBox, moduleConstraint);

		moduleConstraint.gridy++;
		modulePane.add(studioModuleBox, moduleConstraint);

		moduleConstraint.gridy++;
		modulePane.add(seqRepModuleBox, moduleConstraint);

		moduleConstraint.gridy = 0;
		moduleConstraint.gridheight = 3;
		moduleConstraint.anchor = GridBagConstraints.EAST;
		moduleConstraint.weightx = 1;
		modulePane.add(Box.createHorizontalGlue(), moduleConstraint);
		modulePane.setBorder(BorderFactory.createTitledBorder("Modules"));

		return modulePane;
	}

	// panel des trois boutons (ok restore cancel) + checkbox
	private JPanel createBottomButtonsPanel() {
		// mise en place du panel et du layout
		JPanel buttonPanel = new JPanel(new GridBagLayout());

		GridBagConstraints buttonconstraint = new GridBagConstraints();
		buttonconstraint.gridx = 0;
		buttonconstraint.weightx = 1;

		// mise en place des widgets
		Icon crossIcon = new ImageIcon("./src/main/resources/cross.png");
		cancelButton = new JButton("Cancel", crossIcon);
		restoreButton = new JButton("restore");
		Icon tickIcon = new ImageIcon("./src/main/resources/tick.png");
		continueButton = new JButton("Ok", tickIcon);
		ActionListener actionListener = new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				configContinue();
			}
		};
		continueButton.addActionListener(actionListener);

		// ajout des widgets au layout
		buttonPanel.add(Box.createHorizontalGlue(), buttonconstraint);
		buttonconstraint.gridx++;
		buttonconstraint.weightx = 0;
		buttonPanel.add(cancelButton, buttonconstraint);
		buttonconstraint.gridx++;
		buttonPanel.add(Box.createHorizontalStrut(20), buttonconstraint);

		buttonconstraint.fill = GridBagConstraints.NONE;
		buttonconstraint.gridx++;
		buttonconstraint.weightx = 0;
		buttonPanel.add(restoreButton, buttonconstraint);

		buttonconstraint.gridx++;
		buttonPanel.add(Box.createHorizontalStrut(20), buttonconstraint);

		buttonconstraint.gridx++;
		buttonconstraint.weightx = 0;
		buttonPanel.add(continueButton, buttonconstraint);

		return buttonPanel;
	}

	// tests pour l'instant, ecriture dans le fichier plus tard et lancement de la
	// suite de proline
	private void configContinue() {
		Rectangle r = frame.getBounds();
		int h = r.height;
		int w = r.width;
		System.out.println("h : " + h + " et r : " + w);
		if (doNotShowAgainBox.isSelected()) {
			System.out.println("checked");
			replaceLines();
		} else {
			System.out.println("unchecked");
		}
	}

	// methode test d'ecriture dans un fichier
	public static void replaceLines() {
		try {
			// input the (modified) file content to the StringBuffer "input"
			BufferedReader file = new BufferedReader(new FileReader(ProlineFiles.PROLINE_ZERO_CONFIG_FILE));
			StringBuffer inputBuffer = new StringBuffer();
			String line;

			while ((line = file.readLine()) != null) {
				line = line + "yo";
				inputBuffer.append(line);
				inputBuffer.append('\n');
			}
			file.close();

			// write the new string with the replaced line OVER the same file
			FileOutputStream fileOut = new FileOutputStream(ProlineFiles.PROLINE_ZERO_CONFIG_FILE);
			fileOut.write(inputBuffer.toString().getBytes());
			fileOut.close();

		} catch (Exception e) {
			System.out.println("Problem reading file.");
		}
	}

}