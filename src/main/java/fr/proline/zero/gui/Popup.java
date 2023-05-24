package fr.proline.zero.gui;

import java.awt.*;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

public class Popup  {

    private static final String DEFAULT_TITLE = "Proline Zero";

    private static void show(JTextPane message, String title, int messageType) {
        // splash screen should not be modal when popup is showed otherwise it would hide the popup (if splash screen is closed already, it makes no difference)
        SplashScreen.setModal(false);
        // display the popup
        JOptionPane.showMessageDialog(null, message, title, messageType);
        // set splash screen modal again after popup is closed
        SplashScreen.setModal(true);
    }

    private static void show(String message, String title, int messageType) {
        SplashScreen.setModal(false);
        JOptionPane.showMessageDialog(null, message, title, messageType);
        SplashScreen.setModal(true);
    }


    public static boolean okCancel(String message) {
        SplashScreen.setModal(false);
        int r = JOptionPane.showConfirmDialog(null, message, DEFAULT_TITLE, JOptionPane.OK_CANCEL_OPTION);
        SplashScreen.setModal(true);
        return (r == JOptionPane.OK_OPTION);
    }

    public static boolean yesNo(String message) {
        SplashScreen.setModal(false);
        int r = JOptionPane.showConfirmDialog(null, message, DEFAULT_TITLE, JOptionPane.YES_NO_OPTION);
        SplashScreen.setModal(true);
        return (r == JOptionPane.YES_OPTION);
    }
    public static boolean yesNoCenterTOWindow(Component parent, String message) {
        SplashScreen.setModal(false);
        int r = JOptionPane.showConfirmDialog(parent, message, DEFAULT_TITLE, JOptionPane.YES_NO_OPTION);
        SplashScreen.setModal(true);
        return (r == JOptionPane.YES_OPTION);
    }


    public static int yesNoClose(String message){
        SplashScreen.setModal(false);
        int r=JOptionPane.showConfirmDialog(null,message,DEFAULT_TITLE,JOptionPane.YES_NO_OPTION);
        SplashScreen.setModal(true);
        return r;
    }
    public static int yesNoCloseCenterToWindonw(Component parent,String message){
        SplashScreen.setModal(false);
        int r=JOptionPane.showConfirmDialog(parent,message,DEFAULT_TITLE,JOptionPane.YES_NO_OPTION);
        SplashScreen.setModal(true);
        return r;
    }

    public static boolean optionYesNO(String message, String[] options) {
        SplashScreen.setModal(false);
        int r = JOptionPane.showOptionDialog(null, message, DEFAULT_TITLE, JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, message);
        SplashScreen.setModal(true);
        return (r == JOptionPane.OK_OPTION);
    }


    public static void error(String message) {
        error(message, null);
    }

    public static void error(Throwable t) {
        error(t.toString() + ": " + t.getMessage(), t);
    }

    public static void error(String message, Throwable t) {
        try {
            // try to make a nice message with colors and limited width
            JTextPane jTextPane = new JTextPane();
            Document document = jTextPane.getDocument();
            SimpleAttributeSet attrs = new SimpleAttributeSet();
            StyleConstants.setBold(attrs, true);
            document.insertString(document.getLength(), "Proline Zero has failed with the following message:\n", attrs);
            StyleConstants.setBold(attrs, false);
            StyleConstants.setForeground(attrs, Color.RED);
            document.insertString(document.getLength(), message + "\n", attrs);
            // add stacktrace
            if (t != null) {
                StyleConstants.setForeground(attrs, Color.BLACK);
                StyleConstants.setItalic(attrs, true);
                for (StackTraceElement e : t.getStackTrace()) {
                    document.insertString(document.getLength(), "\tat " + e.toString() + "\n", attrs);
                }
            }
            document.insertString(document.getLength(), "\nPlease read the log file for more detailed information", new SimpleAttributeSet());

            jTextPane.setSize(new Dimension(480, 10));
            jTextPane.setPreferredSize(new Dimension(480, jTextPane.getPreferredSize().height));
            Popup.show(jTextPane, "Proline Zero error", JOptionPane.ERROR_MESSAGE);
        } catch (BadLocationException ble) {
            // if it fails, just print the basic message
            String fullMessage = "Proline Zero has failed with the following message: \"" + message + "\"\n"
                    + "Please read the log file for more information";
            Popup.show(fullMessage, "Proline Zero error", JOptionPane.ERROR_MESSAGE);
            ble.printStackTrace();
        }
    }

    public static void warning(String message) {
        Popup.show(message, DEFAULT_TITLE, JOptionPane.WARNING_MESSAGE);
    }

    public static void info(String message) {
        Popup.show(message, DEFAULT_TITLE, JOptionPane.INFORMATION_MESSAGE);
    }




}
