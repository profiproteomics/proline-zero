package fr.proline.zero.gui;

import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;

import javax.imageio.ImageIO;
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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import fr.proline.zero.util.ConfigManager;
import fr.proline.zero.util.MemoryUtils;

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

	private ConfigManager configManager;

	public ConfigWindow() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					initialize();
					// frame.setMinimumSize(frame.getSize());
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
		configManager = ConfigManager.getInstance();

		// TODO gerer le resizing
		frame = new JFrame();
		frame.setTitle("Proline zero config window");
		frame.setBounds(100, 100, 400, 750);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;

		// ajout des differents panels
		c.gridwidth = 3;
		c.gridx = 0;
		c.gridy = 0;
		panel.add(createModulesPanel(), c);

		c.weightx = 1;
		c.weighty = 1;
		c.gridy++;
		panel.add(createTabPanel(), c);

		c.gridwidth = 3;
		c.gridy++;
		c.weighty = 0;
		doNotShowAgainBox = new JCheckBox("Do not show again");
		doNotShowAgainBox.setSelected(configManager.getHideConfigDialog());
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
		if (!configManager.getSeqRepActive()) {
			tabbedPane.setEnabledAt(3, false);
		}
		tabbedPane.addChangeListener(resizeDynamique());

		return tabbedPane;
	}

	private JPanel createModulesPanel() {

		// mise en place du panel et layout
		JPanel modulePane = new JPanel(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;

		// mise en place des widgets
		serverModuleBox = new JCheckBox("Start Proline Server");
		serverModuleBox.setSelected(true);
		serverModuleBox.setEnabled(false);

		seqRepModuleBox = new JCheckBox("Start Sequence Repository");
		seqRepModuleBox.setSelected(configManager.getSeqRepActive());
		ItemListener checkSeqRep = new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				MemoryUtils memoryManager = ConfigManager.getInstance().getMemoryManager();

				if (seqRepModuleBox.isSelected()) {
					tabbedPane.setEnabledAt(3, true);
					memoryManager.setSeqRepActive(true);
					memoryPanel.seqRepBeingActive(true);
				} else {
					if (tabbedPane.getSelectedIndex() == 3) {
						tabbedPane.setSelectedIndex(0);
					}
					tabbedPane.setEnabledAt(3, false);
					memoryManager.setSeqRepActive(false);
					memoryPanel.seqRepBeingActive(false);
				}
				memoryPanel.updateValues();
			}
		};
		seqRepModuleBox.addItemListener(checkSeqRep);

		studioModuleBox = new JCheckBox("Start Proline Studio");
		studioModuleBox.setSelected(configManager.getStudioActive());
		ItemListener checkStudio = new ItemListener() {
			public void itemStateChanged(ItemEvent e) {

				if (studioModuleBox.isSelected()) {
					configManager.setStudioActive(true);
					memoryPanel.studioBeingActive(true);
				} else {
					configManager.setStudioActive(false);
					memoryPanel.studioBeingActive(false);
				}
				memoryPanel.updateValues();
			}
		};
		studioModuleBox.addItemListener(checkStudio);

		// ajout des widgets
		c.gridy = 0;
		modulePane.add(serverModuleBox, c);

		c.gridy++;
		modulePane.add(seqRepModuleBox, c);

		c.gridy++;
		modulePane.add(studioModuleBox, c);

		c.gridy = 0;
		c.gridheight = 3;
		c.anchor = GridBagConstraints.EAST;
		c.weightx = 1;
		modulePane.add(Box.createHorizontalGlue(), c);
		modulePane.setBorder(BorderFactory.createTitledBorder("Modules"));

		return modulePane;
	}

	// panel des trois boutons (ok restore cancel) + checkbox
	private JPanel createBottomButtonsPanel() {
		// mise en place du panel et du layout
		JPanel buttonPanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(5, 5, 5, 5);

		// mise en place des widgets
		try {
			Icon crossIcon = new ImageIcon(ImageIO.read(ClassLoader.getSystemResource("cross.png")));
			Icon tickIcon = new ImageIcon(ImageIO.read(ClassLoader.getSystemResource("tick.png")));
			Icon restoreIcon = new ImageIcon(ImageIO.read(ClassLoader.getSystemResource("arrow-circle.png")));
			continueButton = new JButton("Ok", tickIcon);
			continueButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					configContinue();
					ConfigManager.getInstance().updateFileZero();
				}
			});
			cancelButton = new JButton("Cancel", crossIcon);
			cancelButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					System.exit(0);
				}
			});
			restoreButton = new JButton("Restore", restoreIcon);
			restoreButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					restoreValues();
				}
			});
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// ajout des widgets au layout
		c.gridx = 0;
		c.weightx = 0;
		buttonPanel.add(restoreButton, c);

		c.gridx++;
		c.weightx = 1;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		buttonPanel.add(Box.createHorizontalGlue(), c);

		c.fill = GridBagConstraints.NONE;
		c.gridx++;
		c.weightx = 0;
		buttonPanel.add(continueButton, c);

		c.gridx++;
		c.weightx = 0;
		buttonPanel.add(cancelButton, c);

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
		} else {
			System.out.println("unchecked");
		}
	}

	private ChangeListener resizeDynamique() {
		ChangeListener resize = new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				switch (tabbedPane.getSelectedIndex()) {
				case 0:
					frame.setBounds(100, 100, 400, 750);
					break;
				case 1:
					frame.setBounds(100, 100, 400, 660);
					break;
				case 2:
					frame.setBounds(100, 100, 400, 430);
					break;
				case 3:
					frame.setBounds(100, 100, 400, 530);
					break;
				}
			}
		};
		return resize;
	}

	private void restoreValues() {
		configManager.restoreValues();
		updateValues();
		memoryPanel.updateValues();
		folderPanel.updateValues();
		serverPanel.updateValues();
		parsePanel.updateValues();
	}

	private void updateValues() {
		doNotShowAgainBox.setSelected(configManager.getHideConfigDialog());
		studioModuleBox.setSelected(configManager.getStudioActive());
		seqRepModuleBox.setSelected(configManager.getSeqRepActive());
	}

}
