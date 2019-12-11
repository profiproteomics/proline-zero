package fr.proline.zero.gui;

import fr.proline.zero.util.LogBuffer;
import fr.proline.zero.util.ProlineFiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import javax.swing.text.Caret;

public class LogFile {

    private static Logger logger = LoggerFactory.getLogger(LogFile.class);
    private final int DEFAULT_MAX_LINES_NUMBER = 1000;
    private final int DEFAULT_REFRESH_RATE_MS = 5000;
    private boolean m_isFrameOpened = false;
    private JFrame m_frame;
    private final File m_logFile;
    private JTextArea m_logTextArea;
    private JScrollBar m_verticalScroll;

    public LogFile(File logFile) {
        m_logFile = logFile;
        create(logFile, DEFAULT_MAX_LINES_NUMBER);
    }

    public void focus() {
        if (m_frame != null && m_isFrameOpened) {
            m_frame.requestFocus();
        } else {
            m_isFrameOpened = true;
            m_frame.setVisible(true);
            updateLog(DEFAULT_MAX_LINES_NUMBER);
        }
    }

    public void close() {
        if (m_frame != null && m_isFrameOpened) {
            m_frame.setVisible(false);
            m_frame.dispose();
            m_isFrameOpened = false;
        }
    }

    private void create(File logFile, int maxLinesNumber) {
        // TODO maybe add a textfield to change the number of lines
        m_frame = new JFrame(logFile.getName());
        m_frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        m_frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                close();
            }
        });
        JTextField fileInfoField = new JTextField("Path: " + logFile.getAbsolutePath());
        m_logTextArea = new JTextArea();
        m_logTextArea.setAutoscrolls(true);
        m_logTextArea.setLineWrap(true);
        m_logTextArea.setBackground(Color.darkGray);
        m_logTextArea.setForeground(Color.WHITE);
//        container.setEnabled(false);

        JScrollPane scrollPane = new JScrollPane(m_logTextArea);

        m_verticalScroll = scrollPane.getVerticalScrollBar();

        m_verticalScroll.setValue(m_verticalScroll.getMaximum());
        m_frame.getContentPane().add(fileInfoField, BorderLayout.NORTH);
        m_frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
        m_frame.pack();
        m_frame.setVisible(true);
        m_frame.setSize(800, 600);
        m_frame.setIconImage(new ImageIcon(ProlineFiles.DOCUMENT_ICON).getImage());
        m_isFrameOpened = true;
        updateLog(maxLinesNumber);

    }

    private void updateLog(int maxLinesNumber) {
        new Thread(() -> {
            while (m_isFrameOpened) {
                LogBuffer buffer = new LogBuffer(maxLinesNumber);
                try {
                    BufferedReader b = new BufferedReader(new FileReader(m_logFile));
                    String readLine = "";
                    while ((readLine = b.readLine()) != null) {
                        buffer.collect(readLine);
                    }
                } catch (Exception e) {
                    // put the stack trace in the buffer
                    buffer.collect("Error while reading the file: " + e.getMessage());
                    for (StackTraceElement stack : e.getStackTrace()) {
                        buffer.collect(stack.toString());
                    }
                }
                SwingUtilities.invokeLater(() -> {
                    synchronized (this) {
                        boolean updateScroll = false;
                        int knobValue = m_verticalScroll.getValue();
                        int yBounds = m_verticalScroll.getBounds().height;
                        int offSet = 10;
                        int max = m_verticalScroll.getMaximum();
                        int pos = 0;
                        if (knobValue + yBounds + offSet < max) {
                            updateScroll = true;
                            pos = m_logTextArea.getCaretPosition();
                        }
                        // update container
                        m_logTextArea.setText(String.join("\n", buffer.contents()));//automatically at bottom
                        // scroll to the bottom of the scrollpane if the focus was already at the bottom (otherwise we consider that the user is reading the log)
                        if (updateScroll) {
                            m_logTextArea.setCaretPosition(pos);
                        }
                    }
                });
                try {
                    Thread.sleep(DEFAULT_REFRESH_RATE_MS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
