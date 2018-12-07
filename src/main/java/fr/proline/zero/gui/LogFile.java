package fr.proline.zero.gui;

import fr.proline.zero.util.LogBuffer;
import fr.proline.zero.util.ProlineFiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.nio.file.Files;
import java.util.stream.Stream;

public class LogFile {

    private static Logger logger = LoggerFactory.getLogger(LogFile.class);
    private final int DEFAULT_MAX_LINES_NUMBER = 1000;
    private final int DEFAULT_REFRESH_RATE_MS = 1000;
    private boolean isFrameOpened = false;
    private JFrame frame;

    public LogFile(File logFile) {
        create(logFile, DEFAULT_MAX_LINES_NUMBER);
    }

    public void focus() {
        if (frame != null && isFrameOpened) {
            frame.requestFocus();
        }
    }

    public void close() {
        if (frame != null && isFrameOpened) {
            frame.setVisible(false);
            frame.dispose();
            isFrameOpened = false;
        }
    }

    private void create(File logFile, int maxLinesNumber) {
        // TODO maybe add a textfield to change the number of lines
        // TODO also display somewhere the path of the file

        frame = new JFrame(logFile.getName());
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                close();
            }
        });
        JTextArea container = new JTextArea();
        container.setAutoscrolls(true);
        container.setLineWrap(true);
        container.setBackground(Color.darkGray);
        container.setForeground(Color.WHITE);
//        container.setEnabled(false);

        JScrollPane scrollPane = new JScrollPane(container);
        JScrollBar vScroll = scrollPane.getVerticalScrollBar();
        vScroll.setValue(vScroll.getMaximum());

        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
        frame.setSize(800, 600);
        frame.setIconImage(new ImageIcon(ProlineFiles.DOCUMENT_ICON).getImage());
        isFrameOpened = true;

        new Thread(() -> {
            while (isFrameOpened) {
                LogBuffer buffer = new LogBuffer(maxLinesNumber);
                try (Stream<String> stream = Files.lines(logFile.toPath())) {
                    stream.forEach(line -> buffer.collect(line));
                } catch (Exception e) {
                    // put the stack trace in the buffer
                    buffer.collect("Error while reading the file: " + e.getMessage());
                    for (StackTraceElement stack : e.getStackTrace()) {
                        buffer.collect(stack.toString());
                    }
                }
                SwingUtilities.invokeLater(() -> {
                    boolean scrollToBottom = false;
                    if (vScroll.getValue() == vScroll.getMaximum()) scrollToBottom = true;
                    // update container
                    container.setText(String.join("\n", buffer.contents()));
                    // scroll to the bottom of the scrollpane if the focus was already at the bottom (otherwise we consider that the user is reading the log)
                    if (scrollToBottom) vScroll.setValue(vScroll.getMaximum());
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