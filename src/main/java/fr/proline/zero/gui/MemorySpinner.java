package fr.proline.zero.gui;

import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class MemorySpinner extends JSpinner {
	Boolean isMo;
	JLabel unit;
	String name;

	public void setIsMo(Boolean isMo) {
		this.isMo = isMo;
	}

	public MemorySpinner(Boolean isMo, double value, String name) {
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
		this.name = name;
	}

	public MemorySpinner(Boolean isMo, int value, String name) {
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
		return new SpinnerNumberModel(value, 0, 1000, 100);
	}

	private SpinnerNumberModel memorySpinnerModel(double value) {
		return new SpinnerNumberModel(value, 0.9, 100.0, 0.1);
	}

	long getMoLongValue() {
		Long valueMo;
		if (this.isMo) {
			valueMo = ((Number) getValue()).longValue();
		} else {
			valueMo = Math.round(((double) getValue()) * 1024);
		}
		return valueMo;
	}

	public void setValue(Long value) {
		if (value > 950) {
			setIsMo(false);
			unit.setText("Go");

			double doubleValue = ((double) value) / 1000;
			doubleValue = Math.round(doubleValue * 10.0);
			doubleValue = doubleValue / 10.0;
			setValue(doubleValue);

			getParent().repaint();

		} else {
			setIsMo(true);
			unit.setText("Mo");

			int valueInt = (int) Math.round(value / 100.0) * 100;
			setValue(valueInt);

			getParent().repaint();
		}
	}

	public String getName() {
		return this.name;
	}
}
