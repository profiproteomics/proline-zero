package fr.proline.zero.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import fr.proline.zero.util.MemoryUtils;
import fr.proline.zero.util.MemoryUtils.AttributionMode;

public class MemoryPanel extends JPanel {
	private JComboBox<String> allocModeBox;

	private MemorySpinner totalMemoryField;

	private MemorySpinner studioMemoryField;

	private MemorySpinner totalServerMemoryField;

	private MemorySpinner seqrepMemoryField;

	private MemorySpinner dataStoreMemoryField;

	private MemorySpinner cortexMemoryField;

	private MemorySpinner jmsMemoryField;

	private MemoryUtils memoryManager;

	private JTextArea aide;

	// boolean to prevent the values of changing in an infinite loop
	protected boolean firstClick;

	public MemoryPanel() {
		super();
		firstClick = true;
		initialize();
	}

	private void initialize() {

		// creation du layout
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1;
		c.anchor = GridBagConstraints.NORTHWEST;

		// creation des widgets
		aide = new JTextArea();
		aide.setPreferredSize(new Dimension(300, 75));
		aide.setMinimumSize(new Dimension(300, 75));
		aide.setText("ici est l'aide concernant l'onglet \nallocation de la memoire");
		aide.setEditable(false);

		// ajout des widgets au layout
		add(aide, c);

		c.insets = new java.awt.Insets(20, 15, 0, 15);
		c.gridy++;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1;
		add(createAllocationTypePanel(), c);

		c.gridy++;
		add(createAllocationPanel(), c);

		c.gridy++;
		c.weighty = 1;
		add(Box.createHorizontalGlue(), c);

		// TODO : construire a partir de la lecture du fichier
		memoryManager = new MemoryUtils();
		AttributionMode mode = memoryManager.getAttributionMode();
		if (mode.equals(AttributionMode.AUTO)) {
			allocModeBox.setSelectedIndex(0);
		} else if (mode.equals(AttributionMode.SEMIAUTO)) {
			allocModeBox.setSelectedIndex(1);
		} else {
			allocModeBox.setSelectedIndex(2);
		}
		updateValues();
	}

	private JPanel createAllocationTypePanel() {
		// creation du panel et du layout
		JPanel allocTypePanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.HORIZONTAL;

		c.insets = new java.awt.Insets(2, 10, 0, 0);

		// creation des widgets
		this.allocModeBox = new JComboBox<String>();
		allocModeBox.setPreferredSize(new Dimension(100, 20));
		allocModeBox.addItem("Automatic");
		allocModeBox.addItem("Semi-automatic");
		allocModeBox.addItem("Manual");
		allocModeBox.addActionListener(allocationModeAction());

		// ajout des widgets au layout
		c.gridx = 0;
		allocTypePanel.add(new JLabel("Allocation mode :", SwingConstants.RIGHT), c);

		c.gridx++;
		c.insets = new java.awt.Insets(0, 10, 0, 0);
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
		GridBagConstraints c = new GridBagConstraints();
		c.weighty = 1;
		c.weightx = 1;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new java.awt.Insets(20, 0, 0, 0);

		// ajout des widgets au layout
		c.gridx = 0;
		c.gridy = 0;
		allocationPanel.add(createTotalMemoryPanel(), c);

		c.gridy++;
		allocationPanel.add(createStudioMemoryPanel(), c);

		c.gridy++;
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

		// creation des widgets
		this.totalMemoryField = new MemorySpinner(false, 3.2, "totalMemoryField");
		totalMemoryField.setPreferredSize(new Dimension(50, 20));
		totalMemoryField.addChangeListener(updateMemory());

		// ajout des widgets au layout
		c.gridx = 0;
		totalPanel.add(new JLabel("Total memory :", SwingConstants.RIGHT), c);

		c.gridx++;
		c.insets = new Insets(5, 5, 5, 5);
		totalPanel.add(totalMemoryField, c);

		c.gridx++;
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
		this.studioMemoryField = new MemorySpinner(false, 2.0, "studioMemoryField");
		studioMemoryField.setPreferredSize(new Dimension(50, 20));
		studioMemoryField.setEnabled(false);
		studioMemoryField.addChangeListener(updateMemory());

		// ajout des widgets au layout
		c.gridx = 0;
		studioPanel.add(new JLabel("Proline Studio :", SwingConstants.RIGHT), c);

		c.gridx++;
		c.insets = new java.awt.Insets(5, 5, 5, 5);
		studioPanel.add(studioMemoryField, c);

		c.gridx++;
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
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new java.awt.Insets(5, 5, 5, 5);

		// creation des widgets
		this.totalServerMemoryField = new MemorySpinner(false, 1.5, "totalServerMemoryField");
		totalServerMemoryField.setPreferredSize(new Dimension(50, 20));
		totalServerMemoryField.setEnabled(false);
		totalServerMemoryField.addChangeListener(updateMemory());

		// ajout des widgets au layout
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 2;
		serverTotal.add(new JLabel("Server memory :"), c);

		c.gridx++;
		c.gridx++;
		c.gridwidth = 1;
		serverTotal.add(totalServerMemoryField, c);

		c.gridx++;
		serverTotal.add(totalServerMemoryField.unit, c);

		c.gridx++;
		c.weightx = 1;
		serverTotal.add(Box.createHorizontalGlue(), c);

		c.gridx = 0;
		c.gridy++;
		c.weightx = 0;
		serverTotal.add(Box.createHorizontalStrut(20), c);

		c.gridx++;
		c.gridwidth = 3;
		c.weightx = 1;
		c.insets = new java.awt.Insets(5, 5, 5, 0);
		serverTotal.add(createServerDetailPanel(), c);
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
		this.seqrepMemoryField = new MemorySpinner(true, 500, "seqrepMemoryField");
		seqrepMemoryField.setPreferredSize(new Dimension(50, 20));
		seqrepMemoryField.setEnabled(false);
		seqrepMemoryField.addChangeListener(updateMemory());

		this.dataStoreMemoryField = new MemorySpinner(true, 500, "dataStoreMemoryField");
		dataStoreMemoryField.setPreferredSize(new Dimension(50, 20));
		dataStoreMemoryField.setEnabled(false);
		dataStoreMemoryField.addChangeListener(updateMemory());

		this.cortexMemoryField = new MemorySpinner(true, 500, "cortexMemoryField");
		cortexMemoryField.setPreferredSize(new Dimension(50, 20));
		cortexMemoryField.setEnabled(false);
		cortexMemoryField.addChangeListener(updateMemory());

		this.jmsMemoryField = new MemorySpinner(true, 500, "jmsMemoryField");
		jmsMemoryField.setPreferredSize(new Dimension(50, 20));
		jmsMemoryField.setEnabled(false);
		jmsMemoryField.addChangeListener(updateMemory());

		// TODO : griser si seqrep non coché
		// ajout des widgets au layout
		serverConstraint.gridy = 0;
		serverConstraint.gridx = 0;
		serverAllocation.add(new JLabel("Sequence repository : ", SwingConstants.RIGHT), serverConstraint);

		serverConstraint.gridx++;
		serverAllocation.add(seqrepMemoryField, serverConstraint);

		serverConstraint.gridx++;
		serverAllocation.add(seqrepMemoryField.unit, serverConstraint);

		serverConstraint.gridx = 0;
		serverConstraint.gridy++;
		serverAllocation.add(new JLabel("DataStore : ", SwingConstants.RIGHT), serverConstraint);

		serverConstraint.gridx++;
		serverAllocation.add(dataStoreMemoryField, serverConstraint);

		serverConstraint.gridx++;
		serverAllocation.add(dataStoreMemoryField.unit, serverConstraint);

		serverConstraint.gridx = 0;
		serverConstraint.gridy++;
		serverAllocation.add(new JLabel("Proline server : ", SwingConstants.RIGHT), serverConstraint);

		serverConstraint.gridx++;
		serverAllocation.add(cortexMemoryField, serverConstraint);

		serverConstraint.gridx++;
		serverAllocation.add(cortexMemoryField.unit, serverConstraint);

		serverConstraint.gridx = 0;
		serverConstraint.gridy++;
		serverAllocation.add(new JLabel("JMS : ", SwingConstants.RIGHT), serverConstraint);

		serverConstraint.gridx++;
		serverAllocation.add(jmsMemoryField, serverConstraint);

		serverConstraint.gridx++;
		serverAllocation.add(jmsMemoryField.unit, serverConstraint);

		return serverAllocation;
	}

	// method called when the value of the allocation mode is changed
	private ActionListener allocationModeAction() {
		ActionListener allocationModeAction = new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if (allocModeBox.getSelectedItem().equals("Automatic")) {

					memoryManager.setAttributionMode(AttributionMode.AUTO);

					firstClick = false;
					memoryManager.update(memoryManager.getTotalMemory());
					updateValues();
					firstClick = true;

					totalMemoryField.setEnabled(true);
					studioMemoryField.setEnabled(false);
					totalServerMemoryField.setEnabled(false);
					seqrepMemoryField.setEnabled(false);
					dataStoreMemoryField.setEnabled(false);
					cortexMemoryField.setEnabled(false);
					jmsMemoryField.setEnabled(false);
				} else if (allocModeBox.getSelectedItem().equals("Semi-automatic")) {

					memoryManager.setAttributionMode(AttributionMode.SEMIAUTO);

					totalMemoryField.setEnabled(false);
					studioMemoryField.setEnabled(true);
					totalServerMemoryField.setEnabled(true);
					seqrepMemoryField.setEnabled(false);
					dataStoreMemoryField.setEnabled(false);
					cortexMemoryField.setEnabled(false);
					jmsMemoryField.setEnabled(false);
				} else {

					memoryManager.setAttributionMode(AttributionMode.MANUAL);

					totalMemoryField.setEnabled(false);
					studioMemoryField.setEnabled(true);
					totalServerMemoryField.setEnabled(false);
					seqrepMemoryField.setEnabled(true);
					dataStoreMemoryField.setEnabled(true);
					cortexMemoryField.setEnabled(true);
					jmsMemoryField.setEnabled(true);

				}
			}
		};
		return allocationModeAction;

	}

	// method called when the value of memoryspinner is changed
	// changes the value in the memorymanager and calls for the update of all other
	// values
	private ChangeListener updateMemory() {
		ChangeListener adjustMemory = new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if (!firstClick) {
					return;
				} else {
					firstClick = false;
					long updatedSourceValue = ((MemorySpinner) e.getSource()).getMoLongValue();

					switch (((MemorySpinner) e.getSource()).getName()) {
					case "totalMemoryField":
						memoryManager.setTotalMemory(updatedSourceValue);
						break;
					case "studioMemoryField":
						memoryManager.setStudioBeingChanged(true);
						memoryManager.setStudioMemory(updatedSourceValue);
						break;
					case "totalServerMemoryField":
						memoryManager.setServerTotalMemory(updatedSourceValue);
						break;
					case "seqrepMemoryField":
						memoryManager.setSeqrepMemory(updatedSourceValue);
						break;
					case "dataStoreMemoryField":
						memoryManager.setDatastoreMemory(updatedSourceValue);
						break;
					case "cortexMemoryField":
						memoryManager.setProlineServerMemory(updatedSourceValue);
						break;
					case "jmsMemoryField":
						memoryManager.setJmsMemory(updatedSourceValue);
						break;
					}
					memoryManager.update(updatedSourceValue);
					updateValues();
					memoryManager.setStudioBeingChanged(false);
					firstClick = true;
				}
			}
		};
		return adjustMemory;
	}

	// displays all the new values taken from the memorymanager
	private void updateValues() {
		totalMemoryField.setValue(memoryManager.getTotalMemory());
		studioMemoryField.setValue(memoryManager.getStudioMemory());
		totalServerMemoryField.setValue(memoryManager.getServerTotalMemory());
		seqrepMemoryField.setValue(memoryManager.getSeqrepMemory());
		dataStoreMemoryField.setValue(memoryManager.getDatastoreMemory());
		cortexMemoryField.setValue(memoryManager.getProlineServerMemory());
		jmsMemoryField.setValue(memoryManager.getJmsMemory());
	}

}
