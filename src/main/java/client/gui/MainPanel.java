package client.gui;

import shared.Activity;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * Requirement: K.G.1
 */
public class MainPanel extends JPanel {

    private MainFrame mainFrame;
    private AppPanel appPanel;
    private String userName;
    private Color backGroundColor;

    public MainPanel(MainFrame mainFrame, String userName, String status) {
        this.mainFrame = mainFrame;
        this.userName = userName;
        backGroundColor = new Color(134, 144, 154, 145); //64, 87, 139
        setupPanel(status);
        appPanel = new AppPanel(this);
        showAppPanel();
    }

    public void setupPanel(String status) {
        setSize(new Dimension(819, 438));
        setBackground(backGroundColor);
        TitledBorder tb = BorderFactory.createTitledBorder("Välkommen, " + userName + " - " + status);
        if (status.equals("OFFLINE")) {
            tb.setTitleColor(Color.RED);
        } else {
            tb.setTitleColor(new Color(0x339F02));
        }
        setBorder(tb);
    }

    public void showAppPanel() {
        add(appPanel);
    }

    public AppPanel getAppPanel() {
        return appPanel;
    }

    public void logOut() {
        mainFrame.logOut();
    }

    public void sendActivityFromGUI(Activity activity) {
        mainFrame.sendActivityFromGUI(activity);
    }

    public void sendChosenInterval(int interval) {
        mainFrame.sendChosenInterval(interval);
    }

    public MainFrame getMainFrame() {
        return mainFrame;
    }
}
