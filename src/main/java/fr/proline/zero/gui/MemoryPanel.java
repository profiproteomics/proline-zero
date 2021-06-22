package fr.proline.zero.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class MemoryPanel extends JPanel {
	private JComboBox<String> allocModeBox;
	private JTextField totalMemoryField;
	private JTextField studioMemoryField;
	private JTextField totalServerMemoryField;
	private JTextField seqrepMemoryField;
	private JTextField dataStoreMemoryField;
	private JTextField cortexMemoryField;
	private JTextField jmsMemoryField;
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

		memoryPanelConstraint.gridy++;
		memoryPanelConstraint.fill = GridBagConstraints.NONE;
		memoryPanelConstraint.weightx = 0;
		add(createAllocationTypePanel(), memoryPanelConstraint);

		memoryPanelConstraint.gridy++;
		add(createAllocationPanel(), memoryPanelConstraint);

		memoryPanelConstraint.gridy++;
		memoryPanelConstraint.weighty = 1;
		add(Box.createHorizontalGlue(), memoryPanelConstraint);

	}

	private JPanel createAllocationTypePanel() {
		// creation du panel et du layout
		JPanel allocTypePanel = new JPanel();

		// creation des widgets
		this.allocModeBox = new JComboBox<String>();
		allocModeBox.setPreferredSize(new Dimension(80, 20));
		allocModeBox.addItem("Auto");
		allocModeBox.addItem("Semi");
		allocModeBox.addItem("Manual");

		// ajout des widgets au layout
		allocTypePanel.add(new JLabel("Allocation mode : ", SwingConstants.RIGHT));
		allocTypePanel.add(allocModeBox);

		return allocTypePanel;
	}

	private JPanel createAllocationPanel() {
		// creation du panel et du layout
		JPanel allocationPanel = new JPanel(new GridBagLayout());
		allocationPanel.setBorder(BorderFactory.createTitledBorder("allocation"));
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
		JPanel totalPanel = new JPanel();

		// creation des widgets
		this.totalMemoryField = new JTextField();
		totalMemoryField.setPreferredSize(new Dimension(50, 20));

		// ajout des widgets au layout
		totalPanel.add(new JLabel("Total memory : ", SwingConstants.RIGHT));
		totalPanel.add(totalMemoryField);
		totalPanel.add(new JLabel("Go"));

		return totalPanel;
	}

	private JPanel createStudioMemoryPanel() {
		// creation du panel et du layout
		JPanel studioPanel = new JPanel();
		studioPanel.setBorder(BorderFactory.createTitledBorder("client"));

		// creation des widgets
		this.studioMemoryField = new JTextField();
		studioMemoryField.setPreferredSize(new Dimension(50, 20));

		// ajout des widgets au layout
		studioPanel.add(new JLabel("Proline Studio : ", SwingConstants.RIGHT));
		studioPanel.add(studioMemoryField);
		studioPanel.add(new JLabel("Go"));

		return studioPanel;
	}

	private JPanel createServerTotalMemoryPanel() {

		// creation du panel et du layout
		JPanel serverTotal = new JPanel(new GridBagLayout());
		serverTotal.setBorder(BorderFactory.createTitledBorder("server"));
		GridBagConstraints serverTotalConstraint = new GridBagConstraints();
		serverTotalConstraint.weighty = 0;
		serverTotalConstraint.weightx = 0;
		serverTotalConstraint.insets = new java.awt.Insets(5, 5, 5, 5);

		// creation des widgets
		this.totalServerMemoryField = new JTextField();
		totalServerMemoryField.setPreferredSize(new Dimension(50, 20));

		// ajout des widgets au layout
		serverTotalConstraint.gridx = 0;
		serverTotalConstraint.gridy = 0;
		serverTotal.add(new JLabel("Server memory : "), serverTotalConstraint);

		serverTotalConstraint.fill = GridBagConstraints.HORIZONTAL;
		serverTotalConstraint.gridx++;
		serverTotalConstraint.weightx = 1;
		serverTotal.add(totalServerMemoryField, serverTotalConstraint);

		serverTotalConstraint.fill = GridBagConstraints.NONE;
		serverTotalConstraint.gridx++;
		serverTotalConstraint.weightx = 0;
		serverTotal.add(new JLabel("Go"), serverTotalConstraint);

		serverTotalConstraint.weightx = 1;
		serverTotalConstraint.gridwidth = 3;
		serverTotalConstraint.gridy++;
		serverTotalConstraint.gridx = 1;
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
		this.seqrepMemoryField = new JTextField();
		seqrepMemoryField.setPreferredSize(new Dimension(50, 20));

		this.dataStoreMemoryField = new JTextField();
		dataStoreMemoryField.setPreferredSize(new Dimension(50, 20));

		this.cortexMemoryField = new JTextField();
		cortexMemoryField.setPreferredSize(new Dimension(50, 20));

		this.jmsMemoryField = new JTextField();
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
		serverAllocation.add(new JLabel("Go"), serverConstraint);

		serverConstraint.gridx = 0;
		serverConstraint.gridy++;
		serverAllocation.add(new JLabel("DataStore : ", SwingConstants.RIGHT), serverConstraint);

		serverConstraint.weightx = 1;
		serverConstraint.gridx++;
		serverAllocation.add(dataStoreMemoryField, serverConstraint);

		serverConstraint.weightx = 0;
		serverConstraint.gridx++;
		serverAllocation.add(new JLabel("Go"), serverConstraint);

		serverConstraint.gridx = 0;
		serverConstraint.gridy++;
		serverAllocation.add(new JLabel("Proline server : ", SwingConstants.RIGHT), serverConstraint);

		serverConstraint.weightx = 1;
		serverConstraint.gridx++;
		serverAllocation.add(cortexMemoryField, serverConstraint);

		serverConstraint.weightx = 0;
		serverConstraint.gridx++;
		serverAllocation.add(new JLabel("Go", SwingConstants.RIGHT), serverConstraint);

		serverConstraint.gridx = 0;
		serverConstraint.gridy++;
		serverAllocation.add(new JLabel("JMS : ", SwingConstants.RIGHT), serverConstraint);

		serverConstraint.weightx = 1;
		serverConstraint.gridx++;
		serverAllocation.add(jmsMemoryField, serverConstraint);

		serverConstraint.weightx = 0;
		serverConstraint.gridx++;
		serverAllocation.add(new JLabel("Go"), serverConstraint);

		return serverAllocation;
	}
}
