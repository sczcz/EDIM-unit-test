package client.gui;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Requirement: K.G.1
 */
public class LogInFrame extends JFrame {

    public LogInFrame(MainFrame mainFrame) {
        setupFrame(mainFrame);
    }

    public void setupFrame(MainFrame mainFrame) {
        setBounds(0, 0, 200, 200);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.exit(0);
            }
        });
        setLayout(null);
        setTitle("Inloggning EDIM");
        setResizable(true);            // Prevent user from changing the size of the frame.
        setLocationRelativeTo(null);    // Start in the middle of the screen.
        LogInPanel logInPanel = new LogInPanel(this, mainFrame);
        setContentPane(logInPanel);
        pack();
        setVisible(true);
    }

    public void closeWindow() {
        dispose();
    }
}
