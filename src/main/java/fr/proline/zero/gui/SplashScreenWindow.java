package fr.proline.zero.gui;

import fr.proline.zero.util.ProlineFiles;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.*;

public class SplashScreenWindow extends JWindow {
	private static final long serialVersionUID = 1L;
	private JProgressBar progressBar = new JProgressBar();

	public SplashScreenWindow() {
		this.setAlwaysOnTop(true);
		try {
			JPanel southPanel = new JPanel();
			JLabel imageLabel = new JLabel();
			imageLabel.setIcon(new ImageIcon(ProlineFiles.SPLASHSCREEN_IMAGE));
			this.getContentPane().setLayout(new BorderLayout());
			southPanel.setLayout(new FlowLayout());
			southPanel.setBackground(Color.WHITE);
			this.getContentPane().add(imageLabel, BorderLayout.CENTER);
			this.getContentPane().add(southPanel, BorderLayout.SOUTH);
			progressBar.setPreferredSize(new Dimension(300, 25));
			southPanel.add(progressBar, null);
			this.pack();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void setProgressMax(int maxProgress) {
		progressBar.setMaximum(maxProgress);
	}

	public void setProgress(int progress) {
		final int theProgress = progress;
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				progressBar.setValue(theProgress);
			}
		});
	}

	public void setProgress(String message, int progress) {
		setProgress(progress);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				progressBar.setValue(progress);
				setMessage(message);
			}
		});
	}

	public void setProgress(String message) {
		int progress = progressBar.getValue() + 1;
		setProgress(progress);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				progressBar.setValue(progress);
				setMessage(message);
			}
		});
	}

	public void setScreenVisible(boolean visible) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				setVisible(visible);
			}
		});
	}

	private void setMessage(String message) {
		if (message == null) {
			message = "";
			progressBar.setStringPainted(false);
		} else {
			progressBar.setStringPainted(true);
		}
		progressBar.setString(message);
	}
}
