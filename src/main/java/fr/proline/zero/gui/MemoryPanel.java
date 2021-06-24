package fr.proline.zero.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

public class MemoryPanel extends JPanel {
	private JComboBox<String> allocModeBox;

	private MemorySpinner totalMemoryField;

	private MemorySpinner studioMemoryField;

	private MemorySpinner totalServerMemoryField;

	private MemorySpinner seqrepMemoryField;

	private MemorySpinner dataStoreMemoryField;

	private MemorySpinner cortexMemoryField;

	private MemorySpinner jmsMemoryField;

	private JTextArea aide;

	public MemoryPanel() {
		super();
		initialize();
	}

	private void initialize() {

		// creation du layout
		setLayout(new GridBagLayout());
		GridBagConstraints memoryPanelConstraint = new GridBagConstraints();
		memoryPanelConstraint.gridx = 0;
		memoryPanelConstraint.gridy = 0;
		memoryPanelConstraint.fill = GridBagConstraints.BOTH;
		memoryPanelConstraint.weightx = 1;
		memoryPanelConstraint.anchor = GridBagConstraints.NORTH;

		// creation des widgets
		aide = new JTextArea();
		aide.setPreferredSize(new Dimension(300, 75));
		aide.setText("ici est l'aide concernant l'onglet \nallocation de la memoire");
		aide.setEditable(false);

		// ajout des widgets au layout
		add(aide, memoryPanelConstraint);

		memoryPanelConstraint.insets = new java.awt.Insets(5, 0, 0, 0);
		memoryPanelConstraint.gridy++;
		memoryPanelConstraint.fill = GridBagConstraints.HORIZONTAL;
		memoryPanelConstraint.weightx = 1;
		memoryPanelConstraint.anchor = GridBagConstraints.NORTHWEST;
		add(createAllocationTypePanel(), memoryPanelConstraint);

		memoryPanelConstraint.gridy++;
		add(createAllocationPanel(), memoryPanelConstraint);

		memoryPanelConstraint.gridy++;
		memoryPanelConstraint.weighty = 1;
		add(Box.createHorizontalGlue(), memoryPanelConstraint);

	}

	private JPanel createAllocationTypePanel() {
		// creation du panel et du layout
		JPanel allocTypePanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(5, 5, 5, 5);

		// creation des widgets
		this.allocModeBox = new JComboBox<String>();
		allocModeBox.setPreferredSize(new Dimension(100, 20));
		allocModeBox.addItem("Automatic");
		allocModeBox.addItem("Semi-automatic");
		allocModeBox.addItem("Manual");

		// ajout des widgets au layout
		c.gridx = 0;
		allocTypePanel.add(new JLabel("Allocation mode :", SwingConstants.RIGHT), c);

		c.gridx++;
		allocTypePanel.add(allocModeBox, c);

		c.gridx++;
		c.weightx = 1;
		allocTypePanel.add(Box.createHorizontalGlue(), c);
		return allocTypePanel;
	}

	private JPanel createAllocationPanel() {
		// creation du panel et du layout
		JPanel allocationPanel = new JPanel(new GridBagLayout());
		allocationPanel.setBorder(BorderFactory.createTitledBorder("Allocation"));
		GridBagConstraints allocationConstraint = new GridBagConstraints();
		allocationConstraint.weighty = 1;
		allocationConstraint.weightx = 1;
		allocationConstraint.fill = GridBagConstraints.BOTH;

		// ajout des widgets au layout
		allocationConstraint.gridx = 0;
		allocationConstraint.gridy = 0;
		allocationPanel.add(createTotalMemoryPanel(), allocationConstraint);

		allocationConstraint.gridy++;
		allocationPanel.add(createStudioMemoryPanel(), allocationConstraint);

		allocationConstraint.gridy++;
		allocationPanel.add(createServerTotalMemoryPanel(), allocationConstraint);

		return allocationPanel;
	}

	private JPanel createTotalMemoryPanel() {
		// creation du panel et du layout
		JPanel totalPanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(5, 5, 5, 5);

		// creation des widgets
		this.totalMemoryField = new MemorySpinner(false, 3.2);
		totalMemoryField.setPreferredSize(new Dimension(50, 20));

		// ajout des widgets au layout
		c.gridx = 0;
		totalPanel.add(new JLabel("Total memory :", SwingConstants.RIGHT), c);

		c.gridx++;
		c.weightx = 0.1;
		c.insets = new Insets(5, 5, 5, 5);
		totalPanel.add(totalMemoryField, c);

		c.gridx++;
		c.weightx = 0;
		totalPanel.add(totalMemoryField.unit, c);

		c.gridx++;
		c.weightx = 1;
		totalPanel.add(Box.createHorizontalGlue(), c);

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
		this.studioMemoryField = new MemorySpinner(false, 2.0);
		studioMemoryField.setPreferredSize(new Dimension(60, 20));

		// ajout des widgets au layout
		c.gridx = 0;
		studioPanel.add(new JLabel("Proline Studio :", SwingConstants.RIGHT), c);

		c.gridx++;
		c.insets = new java.awt.Insets(5, 5, 5, 5);
		c.weightx = 0.1;
		studioPanel.add(studioMemoryField, c);

		c.gridx++;
		c.weightx = 0;
		studioPanel.add(studioMemoryField.unit, c);

		c.gridx++;
		c.weightx = 1;
		studioPanel.add(Box.createHorizontalGlue(), c);

		return studioPanel;
	}

	private JPanel createServerTotalMemoryPanel() {

		// creation du panel et du layout
		JPanel serverTotal = new JPanel(new GridBagLayout());
		serverTotal.setBorder(BorderFactory.createTitledBorder("server"));
		GridBagConstraints serverTotalConstraint = new GridBagConstraints();
		serverTotalConstraint.anchor = GridBagConstraints.NORTHWEST;
		serverTotalConstraint.weighty = 0;
		serverTotalConstraint.weightx = 0;
		serverTotalConstraint.insets = new java.awt.Insets(5, 5, 5, 5);

		// creation des widgets
		this.totalServerMemoryField = new MemorySpinner(false, 1.5);
		totalServerMemoryField.setPreferredSize(new Dimension(53, 20));

		// ajout des widgets au layout
		serverTotalConstraint.gridx = 0;
		serverTotalConstraint.gridy = 0;
		serverTotalConstraint.gridwidth = 2;
		serverTotal.add(new JLabel("Server memory :"), serverTotalConstraint);

		serverTotalConstraint.fill = GridBagConstraints.HORIZONTAL;
		serverTotalConstraint.insets = new java.awt.Insets(5, 5, 5, 5);
		serverTotalConstraint.gridx++;
		serverTotalConstraint.gridx++;
		serverTotalConstraint.gridwidth = 1;
		serverTotalConstraint.weightx = 0;
		serverTotal.add(totalServerMemoryField, serverTotalConstraint);

		serverTotalConstraint.fill = GridBagConstraints.HORIZONTAL;
		serverTotalConstraint.gridx++;
		serverTotalConstraint.weightx = 0;
		serverTotal.add(totalServerMemoryField.unit, serverTotalConstraint);

		serverTotalConstraint.gridx++;
		serverTotalConstraint.weightx = 1;
		serverTotal.add(Box.createHorizontalGlue(), serverTotalConstraint);

		serverTotalConstraint.gridx = 0;
		serverTotalConstraint.gridy++;
		serverTotal.add(Box.createHorizontalStrut(20), serverTotalConstraint);

		serverTotalConstraint.gridx = 1;
		serverTotalConstraint.gridwidth = 3;
		serverTotalConstraint.insets = new java.awt.Insets(5, 5, 5, 0);
		serverTotal.add(createServerDetailPanel(), serverTotalConstraint);

		return serverTotal;
	}

	private JPanel createServerDetailPanel() {
		// creation du panel et du layout
		JPanel serverAllocation = new JPanel(new GridBagLayout());
		serverAllocation.setBorder(BorderFactory.createTitledBorder(""));
		GridBagConstraints serverConstraint = new GridBagConstraints();
		serverConstraint.fill = GridBagConstraints.HORIZONTAL;
		serverConstraint.weighty = 1;
		serverConstraint.insets = new java.awt.Insets(5, 5, 5, 5);

		// creation des widgets
		this.seqrepMemoryField = new MemorySpinner(true, 500);
		seqrepMemoryField.setPreferredSize(new Dimension(50, 20));

		this.dataStoreMemoryField = new MemorySpinner(true, 500);
		dataStoreMemoryField.setPreferredSize(new Dimension(50, 20));

		this.cortexMemoryField = new MemorySpinner(true, 500);
		cortexMemoryField.setPreferredSize(new Dimension(50, 20));

		this.jmsMemoryField = new MemorySpinner(true, 500);
		jmsMemoryField.setPreferredSize(new Dimension(50, 20));

		// TODO : griser si seqrep non coché
		// ajout des widgets au layout
		serverConstraint.gridy = 0;
		serverConstraint.gridx = 0;
		serverAllocation.add(new JLabel("Sequence repository : ", SwingConstants.RIGHT), serverConstraint);

		serverConstraint.weightx = 1;
		serverConstraint.gridx++;
		serverAllocation.add(seqrepMemoryField, serverConstraint);

		serverConstraint.weightx = 0;
		serverConstraint.gridx++;
		serverAllocation.add(seqrepMemoryField.unit, serverConstraint);

		serverConstraint.gridx = 0;
		serverConstraint.gridy++;
		serverAllocation.add(new JLabel("DataStore : ", SwingConstants.RIGHT), serverConstraint);

		serverConstraint.weightx = 1;
		serverConstraint.gridx++;
		serverAllocation.add(dataStoreMemoryField, serverConstraint);

		serverConstraint.weightx = 0;
		serverConstraint.gridx++;
		serverAllocation.add(dataStoreMemoryField.unit, serverConstraint);

		serverConstraint.gridx = 0;
		serverConstraint.gridy++;
		serverAllocation.add(new JLabel("Proline server : ", SwingConstants.RIGHT), serverConstraint);

		serverConstraint.weightx = 1;
		serverConstraint.gridx++;
		serverAllocation.add(cortexMemoryField, serverConstraint);

		serverConstraint.weightx = 0;
		serverConstraint.gridx++;
		serverAllocation.add(cortexMemoryField.unit, serverConstraint);

		serverConstraint.gridx = 0;
		serverConstraint.gridy++;
		serverAllocation.add(new JLabel("JMS : ", SwingConstants.RIGHT), serverConstraint);

		serverConstraint.weightx = 1;
		serverConstraint.gridx++;
		serverAllocation.add(jmsMemoryField, serverConstraint);

		serverConstraint.weightx = 0;
		serverConstraint.gridx++;
		serverAllocation.add(jmsMemoryField.unit, serverConstraint);

		return serverAllocation;
	}
}
