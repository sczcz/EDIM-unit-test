package client.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 * Requirement: K.G.1
 */
public class LogInPanel extends JPanel {
    private JLabel lblUserName, lblWelcome;
    private JTextField tfUserName;
    private JButton btnLogIn;
    private JButton btnExit;
    private MainFrame mainFrame;
    private LogInFrame logInFrame;

    public LogInPanel(LogInFrame logInFrame, MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.logInFrame = logInFrame;
        createComponents();
    }

    public void createComponents() {
        lblUserName = new JLabel("Användarnamn: ");
        tfUserName = new JTextField();
        tfUserName.setPreferredSize(new Dimension(100, 30));
        btnLogIn = new JButton("Logga in");
        btnExit = new JButton("Avsluta");
        setLayout(new FlowLayout(4));
        add(lblUserName, FlowLayout.LEFT);
        add(tfUserName, FlowLayout.CENTER);
        add(btnLogIn);
        add(btnExit);
        tfUserName.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
              logIn();
            }
        });
        btnExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    logInFrame.dispose();
                    Thread.sleep(3000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                System.exit(0);

            }
        });
        btnLogIn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
              logIn();
            }
        });


    }

    /**
     * Requirement: F.P.2
     */
    public void logIn(){
        String userName = tfUserName.getText();
        if (!userName.equals("") && !userName.contains(" ")) {
            mainFrame.sendUser(userName);
            mainFrame.createMainFrame();
            logInFrame.closeWindow();
            if (mainFrame.checkIfOffline()) {
                mainFrame.sendWelcomeMessage();
            }
        } else if (userName.equals("")) {
            JOptionPane.showMessageDialog(null, "Du måste välja ett användarnamn");
        } else {
            JOptionPane.showMessageDialog(null, "Ditt användarnamn får inte innehålla mellanslag");

        }
    }

}