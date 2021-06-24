package fr.proline.zero.gui;

import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class MemorySpinner extends JSpinner {
	Boolean isMo;
	JLabel unit;

	public void setIsMo(Boolean isMo) {
		this.isMo = isMo;
	}

	public MemorySpinner(Boolean isMo, double value) {
		super();
		setModel(memorySpinnerModel(value));
		unit = new JLabel("Go");
		this.isMo = isMo;

		addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if (((MemorySpinner) e.getSource()).isMo) {
					if ((int) getValue() > 900) {
						setModel((memorySpinnerModel(1.0)));
						((MemorySpinner) e.getSource()).setIsMo(false);
						((MemorySpinner) e.getSource()).unit.setText("Go");
						((MemorySpinner) e.getSource()).getParent().repaint();
					}

				} else {
					if ((double) getValue() < 1.0) {
						setModel((memorySpinnerModel(900)));
						((MemorySpinner) e.getSource()).setIsMo(true);
						((MemorySpinner) e.getSource()).unit.setText("Mo");
						((MemorySpinner) e.getSource()).getParent().repaint();
					}
				}
			}
		});
	}

	public MemorySpinner(Boolean isMo, int value) {
		super();
		setModel(memorySpinnerModel(value));
		unit = new JLabel("Mo");
		this.isMo = isMo;

		addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if (((MemorySpinner) e.getSource()).isMo) {
					if ((int) getValue() > 900) {
						setModel((memorySpinnerModel(1.0)));
						((MemorySpinner) e.getSource()).setIsMo(false);
						((MemorySpinner) e.getSource()).unit.setText("Go");
						((MemorySpinner) e.getSource()).getParent().repaint();
					}

				} else {
					if ((double) getValue() < 1.0) {
						setModel((memorySpinnerModel(900)));
						((MemorySpinner) e.getSource()).setIsMo(true);
						((MemorySpinner) e.getSource()).unit.setText("Mo");
						((MemorySpinner) e.getSource()).getParent().repaint();
					}
				}
			}
		});
	}

	private SpinnerNumberModel memorySpinnerModel(int value) {
		SpinnerNumberModel truc = new SpinnerNumberModel(value, 0, 1000, 100);
		return truc;
	}

	private SpinnerNumberModel memorySpinnerModel(double value) {
		return new SpinnerNumberModel(value, 0.9, 10.0, 0.1);
	}
}
