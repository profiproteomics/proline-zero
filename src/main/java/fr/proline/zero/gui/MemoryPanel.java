package fr.proline.zero.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import fr.proline.zero.util.ConfigManager;
import fr.proline.zero.util.MemoryUtils;
import fr.proline.zero.util.MemoryUtils.AttributionMode;
import fr.proline.zero.util.SettingsConstant;

public class MemoryPanel extends JPanel {
	private JComboBox<String> allocModeBox;

	private MemorySpinner totalMemorySpinner;

	// jlabels attributes to disable it when studio is disabled
	private JLabel studioLabel;
	private MemorySpinner studioMemorySpinner;

	private MemorySpinner totalServerMemorySpinner;

	// jlabels attributes to disable it when seqrep is disabled
	private JLabel seqrepLabel;
	public MemorySpinner seqrepMemorySpinner;

	private MemorySpinner dataStoreMemorySpinner;

	private MemorySpinner cortexMemorySpinner;

	private MemorySpinner jmsMemorySpinner;

	private MemoryUtils memoryManager;


	/**
	 * Memory Properties to propagate change for
	 */
	public static String SEQ_REPO_PROPERTY = "SeqRepoMemoryProperty";
	public static String STUDIO_PROPERTY = "StudioMemoryProperty";

	// boolean to prevent the values of changing in an infinite loop
	protected boolean firstClick;

	public MemoryPanel() {
		super();
		firstClick = true;
		initialize();
		memoryManager.setHasBeenChanged(false);
	}

	private void initialize() {

		// creation du layout
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new java.awt.Insets(5, 5, 5, 5);
		c.gridx = 0;
		c.gridy = 0;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1;
		c.weighty = 0;
		c.anchor = GridBagConstraints.NORTHWEST;

		HelpHeaderPanel help = new HelpHeaderPanel("Memory" , SettingsConstant.MEMORY_HELP_PANE);
		add(help, c);

		c.insets = new java.awt.Insets(5, 5, 5, 5);
		c.gridy++;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1;
		c.weighty = 0;
		add(createAllocationTypePanel(), c);

		c.gridy++;
		c.weighty =1;
		add(createAllocationPanel(), c);

		ConfigManager configManager = ConfigManager.getInstance();
		memoryManager = configManager.getMemoryManager();

		AttributionMode mode = memoryManager.getAttributionMode();
		firstClick = false;
		if (mode.equals(AttributionMode.AUTO)) {
			allocModeBox.setSelectedIndex(0);
		} else if (mode.equals(AttributionMode.SEMIAUTO)) {
			allocModeBox.setSelectedIndex(1);
		} else {
			allocModeBox.setSelectedIndex(2);
		}
		updateMemoryValues();
		firstClick = true;
		repaint();
	}

	private JPanel createAllocationTypePanel() {
		// creation du panel et du layout
		JPanel allocTypePanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new java.awt.Insets(2, 5, 2, 5);

		c.gridx = 0;
		allocTypePanel.add(new JLabel("Allocation mode :", SwingConstants.RIGHT), c);

		this.allocModeBox = new JComboBox<String>();
		allocModeBox.addItem("Automatic");
		allocModeBox.addItem("Semi-automatic");
		allocModeBox.addItem("Manual");
		allocModeBox.addActionListener(allocationModeAction());

		c.gridx++;
		c.weightx = 1;
		allocTypePanel.add(allocModeBox, c);

		return allocTypePanel;
	}

	private JPanel createAllocationPanel() {
		// creation du panel et du layout
		JPanel allocationPanel = new JPanel(new GridBagLayout());
		allocationPanel.setBorder(BorderFactory.createTitledBorder("Allocation"));
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.insets = new java.awt.Insets(20, 0, 0, 0);
		c.anchor = GridBagConstraints.NORTHWEST;

		// ajout des widgets au layout
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0.5;
		c.weighty = 0.5;
		allocationPanel.add(createTotalMemoryPanel(), c);

		c.gridy++;
		c.weighty = 0.5;
		allocationPanel.add(createStudioMemoryPanel(), c);

		c.gridy++;
		c.weighty = 1;
		allocationPanel.add(createServerTotalMemoryPanel(), c);

		return allocationPanel;
	}

	private JPanel createTotalMemoryPanel() {
		// creation du panel et du layout
		JPanel totalPanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(5, 5, 5, 5);

		c.gridx = 0;
//		c.gridy = 0;
//		c.weightx = 0;
//		c.weighty = 0;
		totalPanel.add(new JLabel("Total memory :"), c);

		this.totalMemorySpinner = new MemorySpinner(false, 3.2, "totalMemoryField");
		totalMemorySpinner.setPreferredSize(new Dimension(50, 20));
		totalMemorySpinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (!firstClick) {
					return;
				} else {
					firstClick = false;
					memoryManager.setTotalMemory(totalMemorySpinner.getMoLongValue());
					memoryManager.update();
					updateMemoryValues();
					firstClick = true;
				}
			}
		});

		c.gridx++;
		c.weightx =1;
		c.insets = new Insets(5, 5, 5, 5);
		totalPanel.add(totalMemorySpinner, c);

		c.gridx++;
		c.weighty = 0;
		totalPanel.add(totalMemorySpinner.unit, c);

		return totalPanel;
	}

	private JPanel createStudioMemoryPanel() {
		// creation du panel et du layout
		JPanel studioPanel = new JPanel(new GridBagLayout());
		studioPanel.setBorder(BorderFactory.createTitledBorder("Client"));

		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(5, 5, 5, 5);

		// creation des widgets
		this.studioMemorySpinner = new MemorySpinner(false, 2.0, "studioMemoryField");
		studioMemorySpinner.setPreferredSize(new Dimension(50, 20));
		studioMemorySpinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (!firstClick) {
					return;
				} else {
					firstClick = false;
					memoryManager.setStudioMemory(studioMemorySpinner.getMoLongValue());
					memoryManager.setStudioBeingChanged(true);
					memoryManager.update();
					memoryManager.setStudioBeingChanged(false);
					updateMemoryValues();
					firstClick = true;
				}
			}
		});

		// ajout des widgets au layout
		c.gridx = 0;
		studioLabel = new JLabel("Proline Studio :", SwingConstants.RIGHT);
		studioPanel.add(studioLabel, c);

		c.gridx++;
		c.weightx = 1;
		c.insets = new java.awt.Insets(5, 5, 5, 5);
		studioPanel.add(studioMemorySpinner, c);

		c.gridx++;
		c.weighty = 0;
		studioPanel.add(studioMemorySpinner.unit, c);

		return studioPanel;
	}

	private JPanel createServerTotalMemoryPanel() {
		// creation du panel et du layout
		JPanel serverTotal = new JPanel(new GridBagLayout());
		serverTotal.setBorder(BorderFactory.createTitledBorder("Server"));

		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new java.awt.Insets(5, 5, 5, 5);

		// creation des widgets
		this.totalServerMemorySpinner = new MemorySpinner(false, 1.5, "totalServerMemoryField");
		totalServerMemorySpinner.setPreferredSize(new Dimension(50, 20));
		totalServerMemorySpinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (!firstClick) {
					return;
				} else {
					firstClick = false;
					memoryManager.setServerTotalMemory(totalServerMemorySpinner.getMoLongValue());
					memoryManager.update();
					updateMemoryValues();
					firstClick = true;
				}
			}
		});

		// ajout des widgets au layout
		c.gridx = 0;
		serverTotal.add(new JLabel("Server memory :", SwingConstants.RIGHT), c);

		c.gridx++;
		c.weightx = 1;
		c.insets = new java.awt.Insets(5, 5, 5, 5);
		serverTotal.add(totalServerMemorySpinner, c);

		c.gridx++;
		serverTotal.add(totalServerMemorySpinner.unit, c);

		c.gridx=0;
		c.gridy++;
		c.gridy++;
		c.gridwidth = 3;
		c.insets = new java.awt.Insets(5, 70, 5, 0);
		serverTotal.add(createServerDetailPanel(), c);
		return serverTotal;
	}

	private JPanel createServerDetailPanel() {
		// creation du panel et du layout
		JPanel serverAllocation = new JPanel(new GridBagLayout());
		serverAllocation.setBorder(BorderFactory.createTitledBorder(""));

		GridBagConstraints serverConstraint = new GridBagConstraints();
		serverConstraint.fill = GridBagConstraints.HORIZONTAL;
		serverConstraint.anchor = GridBagConstraints.NORTHWEST;
		serverConstraint.insets = new java.awt.Insets(5, 5, 5, 5);

		// creation des widgets
		this.seqrepMemorySpinner = new MemorySpinner(true, 500, "seqrepMemoryField");
		seqrepMemorySpinner.setPreferredSize(new Dimension(50, 20));
		seqrepMemorySpinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (!firstClick) {
					return;
				} else {
					firstClick = false;
					memoryManager.setSeqrepMemory(seqrepMemorySpinner.getMoLongValue());
					memoryManager.update();
					updateMemoryValues();
					firstClick = true;
				}
			}
		});

		this.dataStoreMemorySpinner = new MemorySpinner(true, 500, "dataStoreMemoryField");
		dataStoreMemorySpinner.setPreferredSize(new Dimension(50, 20));
		dataStoreMemorySpinner.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if (!firstClick) {
					return;
				} else {
					firstClick = false;
					memoryManager.setDatastoreMemory(dataStoreMemorySpinner.getMoLongValue());
					memoryManager.update();
					updateMemoryValues();
					firstClick = true;
				}
			}
		});

		this.cortexMemorySpinner = new MemorySpinner(true, 500, "cortexMemoryField");
		cortexMemorySpinner.setPreferredSize(new Dimension(50, 20));
		cortexMemorySpinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (!firstClick) {
					return;
				} else {
					firstClick = false;
					memoryManager.setProlineServerMemory(cortexMemorySpinner.getMoLongValue());
					memoryManager.update();
					updateMemoryValues();
					firstClick = true;
				}
			}
		});

		this.jmsMemorySpinner = new MemorySpinner(true, 500, "jmsMemoryField");
		jmsMemorySpinner.setPreferredSize(new Dimension(50, 20));
		jmsMemorySpinner.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if (!firstClick) {
					return;
				} else {
					firstClick = false;
					memoryManager.setJmsMemory(jmsMemorySpinner.getMoLongValue());
					memoryManager.update();
					updateMemoryValues();
					firstClick = true;
				}
			}
		});

		// TODO : griser si seqrep non coché
		// ajout des widgets au layout
		serverConstraint.gridy = 0;
		serverConstraint.gridx = 0;
		seqrepLabel = new JLabel("Sequence repository : ", SwingConstants.RIGHT);
		serverAllocation.add(seqrepLabel, serverConstraint);

		serverConstraint.weightx = 1;
		serverConstraint.gridx++;
		serverAllocation.add(seqrepMemorySpinner, serverConstraint);

		serverConstraint.gridx++;
		serverAllocation.add(seqrepMemorySpinner.unit, serverConstraint);

		serverConstraint.gridx = 0;
		serverConstraint.gridy++;
		serverConstraint.weightx = 0;
		serverAllocation.add(new JLabel("DataStore : ", SwingConstants.RIGHT), serverConstraint);

		serverConstraint.gridx++;
		serverConstraint.weightx = 1;
		serverAllocation.add(dataStoreMemorySpinner, serverConstraint);

		serverConstraint.gridx++;
		serverAllocation.add(dataStoreMemorySpinner.unit, serverConstraint);

		serverConstraint.gridx = 0;
		serverConstraint.gridy++;
		serverConstraint.weightx = 0;
		serverAllocation.add(new JLabel("Proline server : ", SwingConstants.RIGHT), serverConstraint);

		serverConstraint.gridx++;
		serverConstraint.weightx = 1;
		serverAllocation.add(cortexMemorySpinner, serverConstraint);

		serverConstraint.gridx++;
		serverAllocation.add(cortexMemorySpinner.unit, serverConstraint);

		serverConstraint.gridx = 0;
		serverConstraint.gridy++;
		serverConstraint.weightx = 0;
		serverAllocation.add(new JLabel("JMS : ", SwingConstants.RIGHT), serverConstraint);

		serverConstraint.gridx++;
		serverConstraint.weightx = 1;
		serverAllocation.add(jmsMemorySpinner, serverConstraint);

		serverConstraint.gridx++;
		serverAllocation.add(jmsMemorySpinner.unit, serverConstraint);

		return serverAllocation;
	}

	// method called when the value of the allocation mode is changed
	private ActionListener allocationModeAction() {
		ActionListener allocationModeAction = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (allocModeBox.getSelectedItem().equals("Automatic")) {
					memoryManager.setAttributionMode(AttributionMode.AUTO);

					totalMemorySpinner.setEnabled(true);
					studioMemorySpinner.setEnabled(false);
					totalServerMemorySpinner.setEnabled(false);
					seqrepMemorySpinner.setEnabled(false);
					dataStoreMemorySpinner.setEnabled(false);
					cortexMemorySpinner.setEnabled(false);
					jmsMemorySpinner.setEnabled(false);
				} else if (allocModeBox.getSelectedItem().equals("Semi-automatic")) {

					memoryManager.setAttributionMode(AttributionMode.SEMIAUTO);

					totalMemorySpinner.setEnabled(false);
					if (ConfigManager.getInstance().isStudioActive()) {
						studioMemorySpinner.setEnabled(true);
					}
					totalServerMemorySpinner.setEnabled(true);
					seqrepMemorySpinner.setEnabled(false);
					dataStoreMemorySpinner.setEnabled(false);
					cortexMemorySpinner.setEnabled(false);
					jmsMemorySpinner.setEnabled(false);
				} else {

					memoryManager.setAttributionMode(AttributionMode.MANUAL);

					totalMemorySpinner.setEnabled(false);
					if (ConfigManager.getInstance().isStudioActive()) {
						studioMemorySpinner.setEnabled(true);
					}
					totalServerMemorySpinner.setEnabled(false);
					if (ConfigManager.getInstance().isSeqRepActive()) {
						seqrepMemorySpinner.setEnabled(true);
					}
					dataStoreMemorySpinner.setEnabled(true);
					cortexMemorySpinner.setEnabled(true);
					jmsMemorySpinner.setEnabled(true);

				}
				if (!firstClick) {
					return;
				} else {
					firstClick = false;
					memoryManager.update();
					updateMemoryValues();
					firstClick = true;
				}
			}
		};
		return allocationModeAction;

	}

	// displays all the new values taken from the memorymanager

	public void updateValues() {
		AttributionMode mode = memoryManager.getAttributionMode();
		if (mode.equals(AttributionMode.AUTO)) {
			allocModeBox.setSelectedIndex(0);
		} else if (mode.equals(AttributionMode.SEMIAUTO)) {
			allocModeBox.setSelectedIndex(1);
		} else {
			allocModeBox.setSelectedIndex(2);
		}
		updateMemoryValues();
	}

	public void updateMemoryValues() {
		totalMemorySpinner.setValue(memoryManager.getTotalMemory());
		studioMemorySpinner.setValue(memoryManager.getStudioMemory());
		totalServerMemorySpinner.setValue(memoryManager.getServerTotalMemory());
		seqrepMemorySpinner.setValue(memoryManager.getSeqrepMemory());
		dataStoreMemorySpinner.setValue(memoryManager.getDatastoreMemory());
		cortexMemorySpinner.setValue(memoryManager.getProlineServerMemory());
		jmsMemorySpinner.setValue(memoryManager.getJmsMemory());
		firePropertyChange(STUDIO_PROPERTY, null, memoryManager.getStudioMemory() > 0);
		firePropertyChange(SEQ_REPO_PROPERTY, null, memoryManager.getSeqrepMemory() > 0);
	}

	public void studioBeingActive(boolean b) {
		if (b) {
			studioLabel.setEnabled(true);
			if (!memoryManager.getAttributionMode().equals(AttributionMode.AUTO)) {
				studioMemorySpinner.setEnabled(true);
			}
			studioMemorySpinner.unit.setEnabled(true);
		} else {
			studioLabel.setEnabled(false);
			studioMemorySpinner.setEnabled(false);
			studioMemorySpinner.unit.setEnabled(false);
		}
		repaint();
	}

	public void seqRepBeingActive(boolean b) {
		if (b) {
			seqrepLabel.setEnabled(true);
			if (memoryManager.getAttributionMode().equals(AttributionMode.MANUAL)) {
				seqrepMemorySpinner.setEnabled(true);
			}
			seqrepMemorySpinner.unit.setEnabled(true);
		} else {
			seqrepLabel.setEnabled(false);
			seqrepMemorySpinner.setEnabled(false);
			seqrepMemorySpinner.unit.setEnabled(false);
		}
		repaint();
	}
}
