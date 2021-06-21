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
	private JComboBox<String> allocMode;
	private JTextField totalMemory;
	private JTextField studioMemory;
	private JTextField totalServerMemory;
	private JTextField seqrepMemory;
	private JTextField dataStoreMemory;
	private JTextField cortexMemory;
	private JTextField jmsMemory;
	private JTextArea aide;

	public MemoryPanel() {
		super();
		aide = new JTextArea();
		aide.setPreferredSize(new Dimension(300, 75));
		this.allocMode = new JComboBox<String>();
		allocMode.setPreferredSize(new Dimension(80, 20));
		allocMode.addItem("Auto");
		allocMode.addItem("Semi");
		allocMode.addItem("Manual");
		this.totalMemory = new JTextField();
		totalMemory.setPreferredSize(new Dimension(50, 20));
		this.studioMemory = new JTextField();
		studioMemory.setPreferredSize(new Dimension(50, 20));
		this.totalServerMemory = new JTextField();
		totalServerMemory.setPreferredSize(new Dimension(50, 20));
		this.seqrepMemory = new JTextField();
		seqrepMemory.setPreferredSize(new Dimension(50, 20));
		this.dataStoreMemory = new JTextField();
		dataStoreMemory.setPreferredSize(new Dimension(50, 20));
		this.cortexMemory = new JTextField();
		cortexMemory.setPreferredSize(new Dimension(50, 20));
		this.jmsMemory = new JTextField();
		jmsMemory.setPreferredSize(new Dimension(50, 20));
		initialize();
	}

	private void initialize() {

		setLayout(new GridBagLayout());
		GridBagConstraints memoryPanelConstraint = new GridBagConstraints();
		memoryPanelConstraint.gridx = 0;
		memoryPanelConstraint.gridy = 0;
		memoryPanelConstraint.fill = GridBagConstraints.BOTH;
		memoryPanelConstraint.weightx = 1;
		memoryPanelConstraint.anchor = GridBagConstraints.NORTH;

		// bandeau d'aide
		// TODO : texte à changer et centrer avec une icone
		aide.setText("ici est l'aide concernant l'onglet allocation de la memoire");
		aide.setEditable(false);
		add(aide, memoryPanelConstraint);
		memoryPanelConstraint.gridy++;

		// allocation type panel
		memoryPanelConstraint.fill = GridBagConstraints.NONE;
		memoryPanelConstraint.weightx = 0;
		JPanel allocTypePanel = new JPanel();
		allocTypePanel.add(new JLabel("Allocation mode : ", SwingConstants.RIGHT));
		allocTypePanel.add(allocMode);

		add(allocTypePanel, memoryPanelConstraint);

		// memory allocation panel
		JPanel allocationPanel = new JPanel();
		allocationPanel.setBorder(BorderFactory.createTitledBorder("allocation"));
		allocationPanel.setLayout(new GridBagLayout());
		GridBagConstraints allocationConstraint = new GridBagConstraints();
		allocationConstraint.gridx = 0;
		allocationConstraint.gridy = 0;
		allocationConstraint.fill = GridBagConstraints.BOTH;
		allocationConstraint.weighty = 1;
		allocationConstraint.weightx = 1;

		// total memory panel
		JPanel totalPanel = new JPanel();
		totalPanel.add(new JLabel("Total memory : ", SwingConstants.RIGHT));
		totalPanel.add(totalMemory);
		totalPanel.add(new JLabel("Go"));

		allocationPanel.add(totalPanel, allocationConstraint);
		allocationConstraint.gridy++;

		// studio memory panel
		JPanel studioPanel = new JPanel();
		studioPanel.setBorder(BorderFactory.createTitledBorder("client"));
		studioPanel.add(new JLabel("Proline Studio : ", SwingConstants.RIGHT));
		studioPanel.add(studioMemory);
		studioPanel.add(new JLabel("Go"));

		allocationPanel.add(studioPanel, allocationConstraint);
		allocationConstraint.gridy++;

		// server memory panel
		JPanel serverTotal = new JPanel();
		serverTotal.setBorder(BorderFactory.createTitledBorder("server"));
		serverTotal.setLayout(new GridBagLayout());
		GridBagConstraints serverTotalConstraint = new GridBagConstraints();
		serverTotalConstraint.weighty = 0;
		serverTotalConstraint.weightx = 0;
		serverTotalConstraint.gridx = 0;
		serverTotalConstraint.gridy = 0;
		JLabel serverTotalLabel = new JLabel("Server memory : ");
		serverTotal.add(serverTotalLabel, serverTotalConstraint);

		serverTotalConstraint.fill = GridBagConstraints.HORIZONTAL;
		serverTotalConstraint.gridx++;
		serverTotalConstraint.weightx = 1;
		serverTotal.add(totalServerMemory, serverTotalConstraint);

		serverTotalConstraint.fill = GridBagConstraints.NONE;
		serverTotalConstraint.gridx++;
		serverTotalConstraint.weightx = 0;
		serverTotal.add(new JLabel("Go"), serverTotalConstraint);

		// server memory allocation panel
		JPanel serverAllocation = new JPanel();
		serverAllocation.setLayout(new GridBagLayout());
		GridBagConstraints serverConstraint = new GridBagConstraints();

		serverConstraint.fill = GridBagConstraints.HORIZONTAL;
		serverConstraint.gridy = 0;
		serverConstraint.gridx = 0;
		serverConstraint.weighty = 1;

		// TODO : griser si seqrep non coché
		serverAllocation.add(new JLabel("Sequence repository : ", SwingConstants.RIGHT), serverConstraint);

		serverConstraint.weightx = 1;
		serverConstraint.gridx++;
		serverAllocation.add(seqrepMemory, serverConstraint);

		serverConstraint.weightx = 0;
		serverConstraint.gridx++;
		serverAllocation.add(new JLabel("Go"), serverConstraint);

		serverConstraint.gridx = 0;
		serverConstraint.gridy++;
		serverAllocation.add(new JLabel("DataStore : ", SwingConstants.RIGHT), serverConstraint);

		serverConstraint.weightx = 1;
		serverConstraint.gridx++;
		serverAllocation.add(dataStoreMemory, serverConstraint);

		serverConstraint.weightx = 0;
		serverConstraint.gridx++;
		serverAllocation.add(new JLabel("Go"), serverConstraint);

		serverConstraint.gridx = 0;
		serverConstraint.gridy++;
		serverAllocation.add(new JLabel("Proline server : ", SwingConstants.RIGHT), serverConstraint);

		serverConstraint.weightx = 1;
		serverConstraint.gridx++;
		serverAllocation.add(cortexMemory, serverConstraint);

		serverConstraint.weightx = 0;
		serverConstraint.gridx++;
		serverAllocation.add(new JLabel("Go", SwingConstants.RIGHT), serverConstraint);

		serverConstraint.gridx = 0;
		serverConstraint.gridy++;
		serverAllocation.add(new JLabel("JMS : ", SwingConstants.RIGHT), serverConstraint);

		serverConstraint.weightx = 1;
		serverConstraint.gridx++;
		serverAllocation.add(jmsMemory, serverConstraint);

		serverConstraint.weightx = 0;
		serverConstraint.gridx++;
		serverAllocation.add(new JLabel("Go"), serverConstraint);
		serverAllocation.setBorder(BorderFactory.createTitledBorder(""));

		serverTotalConstraint.weightx = 1;
		serverTotalConstraint.gridwidth = 3;
		serverTotalConstraint.gridy++;
		serverTotalConstraint.gridx = 1;

		serverTotal.add(serverAllocation, serverTotalConstraint);

		allocationPanel.add(serverTotal, allocationConstraint);

		memoryPanelConstraint.gridy++;
		add(allocationPanel, memoryPanelConstraint);
		memoryPanelConstraint.gridy++;
		memoryPanelConstraint.weighty = 1;
		add(Box.createHorizontalGlue(), memoryPanelConstraint);

	}

}
