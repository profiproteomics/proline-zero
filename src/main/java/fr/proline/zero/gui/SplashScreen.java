package fr.proline.zero.gui;

import javax.swing.UIManager;

public class SplashScreen {
	
	private static SplashScreenWindow splashScreen;
	private static boolean isVisible = false;
	
	public static void initialize() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		splashScreen = new SplashScreenWindow();
		splashScreen.setLocationRelativeTo(null);
		splashScreen.setScreenVisible(true);
		isVisible = true;
	}
	
	public static void stop(long timeout) {
		if(splashScreen != null) {
			try {
				Thread.sleep(timeout);
			} catch (Exception e) {
				e.printStackTrace();
			}
			splashScreen.dispose();
			splashScreen = null;
			isVisible = false;
		}
	}
	
	public static void stop() {
		stop(0);
	}
	
	public static boolean isVisible() {
		return isVisible;
	}
	
	public static void setProgressMax(int maxProgress) {
		splashScreen.setProgressMax(maxProgress);
	}
	
	public static void setProgress(String message) {
		splashScreen.setProgress(message);
	}
	
	public static void setModal(boolean value) {
		if(isVisible) splashScreen.setAlwaysOnTop(value);
	}

}
