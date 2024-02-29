package client.gui;

import shared.Activity;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Requirement: K.G.1
 * This is the panel in the frame that contains pretty much all of the components in the GUI.
 *
 * @author Oscar Kareld, Chanon Borgstrom, Carolin Nordstrom
 * @version 1.0
 */
public class AppPanel extends JPanel {
    private MainPanel mainPanel;
    private String[] interval;
    private JLabel lblTimerInfo;
    private JTextArea taActivityInfo;
    private JComboBox cmbTimeLimit;
    private LinkedList<Activity> activities;
    private JList activityList;
    private JScrollPane activityScrollPane;

    private JButton btnLogOut;
    private JButton btnInterval;
    private JPanel intervalPnl;
    private JLabel lblInterval;

    private BorderLayout borderLayout = new BorderLayout();
    private ActionListener listener = new ButtonListener();
    private DefaultListModel activityListModel;

    private Color clrPanels = new Color(142, 166, 192);
    private Color clrMidPanel = new Color(127, 140, 151, 151);

    private Timer timer;
    private int minuteInterval;
    private int secondInterval;

    private DefaultListModel onlineUserListModel;
    private JList onlineList;

    public AppPanel(MainPanel mainPanel) {
        this.mainPanel = mainPanel;
        setupPanel();
        createComponents();
        activities = new LinkedList<>();
    }

    public void setupPanel() {
        setSize(new Dimension(819, 438));
    }

    public void createComponents() {
        setLayout(borderLayout);

        createActivityList();
        createTAActivityInfo();
        createCBTimeLimit();
        createIntervalPanel();

        btnLogOut = new JButton("Logga ut");

        //TODO: Någonting här fungerar inte riktigt som det ska (issue EDIM-
        activityScrollPane = new JScrollPane(activityList);
        activityScrollPane.setPreferredSize(new Dimension(430, 330));
        activityScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);


        add(activityScrollPane, BorderLayout.CENTER);
        add(btnLogOut, BorderLayout.SOUTH);
        add(taActivityInfo, BorderLayout.EAST);
        add(intervalPnl, BorderLayout.WEST);

        btnLogOut.addActionListener(listener);
        btnInterval.addActionListener(listener);
        addActivityListener();
    }

    public void createIntervalPanel() {
        intervalPnl = new JPanel();
        intervalPnl.setLayout(new BorderLayout());
        intervalPnl.setBackground(clrPanels);
        intervalPnl.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED, Color.LIGHT_GRAY, Color.LIGHT_GRAY));

        lblInterval = new JLabel();
        lblTimerInfo = new JLabel();
        JPanel centerPnl = new JPanel();
        centerPnl.setSize(new Dimension(intervalPnl.getWidth(), intervalPnl.getHeight()));
        centerPnl.setBackground(clrPanels);
        updateLblInterval();
        btnInterval = new JButton("Ändra intervall");
        startTimer(Integer.parseInt((String) cmbTimeLimit.getSelectedItem()), 59);
        centerPnl.setLayout(new BorderLayout());

        JPanel north = new JPanel();
        north.setSize(new Dimension(intervalPnl.getWidth(), 50));
        north.setLayout(new BorderLayout());
        north.add(cmbTimeLimit, BorderLayout.WEST);
        north.add(btnInterval, BorderLayout.CENTER);
        north.setBackground(clrPanels);
        centerPnl.add(north, BorderLayout.NORTH);

        //centerPnl.add(cmbTimeLimit, BorderLayout.NORTH);
        //centerPnl.add(btnInterval, BorderLayout.CENTER);

        onlineUserListModel = new DefaultListModel();
        onlineList = new JList<>(onlineUserListModel);
        onlineList.setBackground(clrPanels);
        onlineList.setSize(new Dimension(intervalPnl.getWidth(), intervalPnl.getHeight() - 50));
        onlineList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        Font font = new Font("SansSerif", Font.PLAIN, 14); //Sarseriff
        onlineList.setFont(font);
        onlineList.setBorder(BorderFactory.createTitledBorder("Online: "));
        onlineList.setVisible(true);

        JScrollPane onlineScrollPane = new JScrollPane(onlineList);

        centerPnl.add(onlineScrollPane, BorderLayout.CENTER);
        intervalPnl.add(lblInterval, BorderLayout.NORTH);
        intervalPnl.add(centerPnl, BorderLayout.CENTER);
        intervalPnl.add(lblTimerInfo, BorderLayout.SOUTH);
    }

    public void updateLblInterval() {
        int interval;
        if(cmbTimeLimit.getSelectedItem().equals("Nu")) {
            lblInterval.setText("Aktivt tidsintervall: " + 0 + " minuter");
        }
        else {
            interval = Integer.parseInt((String) cmbTimeLimit.getSelectedItem());
            lblInterval.setText("Aktivt tidsintervall: " + interval + " minuter");
        }

    }

    /**
     * Requirements: F.A.4, F.O.1.1
     */
    public void createCBTimeLimit() {
        interval = new String[]{"Nu", "5", "15", "30", "45", "60"};
        cmbTimeLimit = new JComboBox<>(interval);
        cmbTimeLimit.setSelectedIndex(3);
    }

    /**
     * Requirement: F.A.1, F.A.2, F.A.4, F.O.1.1
     *
     * @param minutes
     * @param seconds
     */
    public void startTimer(int minutes, int seconds) {
        minuteInterval = (timer == null) ? minutes - 1 : minutes;
        secondInterval = seconds;
        int delay = 1000;
        int period = 1000;
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {

            public void run() {
                String time;
                if (secondInterval < 10) {
                    time = String.format("timer: %d:0%d", minuteInterval, secondInterval);
                } else {
                    time = String.format("timer: %d:%d", minuteInterval, secondInterval);
                }
                lblTimerInfo.setText(time);
                decreaseInterval();
            }
        }, delay, period);
    }

    /**
     * Requirement: F.A.1, F.A.2, F.A.4, F.O.1.1
     */
    public void decreaseInterval() {
        secondInterval--;
        if (secondInterval == -1) {
            minuteInterval--;
            if (minuteInterval == -1) {
                stopTimer();
                mainPanel.getMainFrame().getClientController().requestActivity();
            }
            secondInterval = 59;
        }
    }

    /**
     * Requirement: F.A.1, F.A.2, F.A.4, F.O.1.1
     *
     * @param chosenInterval
     */
    public void countTimerInterval(int chosenInterval) {
        int difference = 0;
        if (chosenInterval != 0) {
            if (minuteInterval > chosenInterval) {
                difference = minuteInterval - chosenInterval;
                minuteInterval = minuteInterval - difference - 1;
            } else {
                difference = chosenInterval - minuteInterval;
                minuteInterval = minuteInterval + difference - 1;
            }
            stopTimer();
            startTimer(chosenInterval - 1, 59);
            updateLblInterval();
        }
        else {
            stopTimer();
            startTimer(chosenInterval, 1);
            updateLblInterval();}

    }


    /**
     * Requirements: F.A.4, F.O.1.1
     */
    public void stopTimer() {
        timer.cancel();
    }

    public void createTAActivityInfo() {
        taActivityInfo = new JTextArea();
        taActivityInfo.setBackground(clrPanels);
        taActivityInfo.setPreferredSize(new Dimension(200, 80));
        taActivityInfo.setLineWrap(true);
        taActivityInfo.setWrapStyleWord(true);
        Font font = new Font("SansSerif", Font.PLAIN, 14);
        taActivityInfo.setFont(font);
        taActivityInfo.setEditable(false);
    }

    /**
     * Requirements: F.A.5
     */
    public void createActivityList() {
        activityListModel = new DefaultListModel();
        activityList = new JList<>(activityListModel);
        activityList.setPreferredSize(new Dimension(400, 320));
        activityList.setBorder(BorderFactory.createTitledBorder("Avklarade aktiviteter"));
        activityList.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);
        Font font = new Font("SansSerif", Font.PLAIN, 14);
        activityList.setFont(font);
    }

    public void addActivityListener() {
        activityList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                String activityName = (String) activityList.getSelectedValue();
                String newActivityName = splitActivityNameAndTime(activityName);
                for (Activity activity : activities) {
                    if (activity.getActivityName().equals(newActivityName)) {
                        showActivityInfo(activity.getActivityInfo());
                    }
                }
            }
        });
    }

    public String splitActivityNameAndTime(String activityName) {
        activityName = activityName.replaceAll("[0-9]", "");
        activityName = activityName.replaceAll(":", "");
        activityName = activityName.replaceAll(" ", "");
        return activityName;
    }

    /**
     * Requirements: F.A.5
     *
     * @param activity
     */
    public LinkedList updateActivityList(Activity activity) {
        stopTimer();
        int timerValue;
        if (cmbTimeLimit.getSelectedItem().equals("Nu")) {
            timerValue = 5;
            lblInterval.setText("Aktivt tidsintervall: " + 5 + " minuter");
            cmbTimeLimit.setSelectedIndex(1);
            mainPanel.sendChosenInterval(timerValue);
        } else {
            timerValue= Integer.parseInt((String) cmbTimeLimit.getSelectedItem());
        }
        startTimer(timerValue - 1, 59);
        activities.add(activity);
        activityListModel.addElement(activity.getActivityName() + " " + activity.getTime());
        String newActivityName = splitActivityNameAndTime(activity.getActivityName());
        activity.setActivityName(newActivityName);
        updateUI();
        return activities;
    }

    public void showActivityInfo(String activityInfo) {
        taActivityInfo.setText(activityInfo);
    }

    /**
     * Requirements: F.K.1
     *
     * @param usersOnline
     */
    public JList displayOnlineList(ArrayList<String> usersOnline) {
        onlineList.setListData(usersOnline.toArray());
        return onlineList;
    }

    /**
     * Requirements: F.A.7
     *
     * @param activity
     * @return
     */
    public ImageIcon createActivityIcon(Activity activity) {
        ImageIcon activityIcon = activity.getActivityImage();
        Image image = activityIcon.getImage();
        Image newImg = image.getScaledInstance(150, 150, Image.SCALE_SMOOTH);
        return new ImageIcon(newImg);
    }

    /**
     * Requirement: F.A.1, F.A.1.2, F.A.1.3, F.A.2, F.A.3
     * Sends a notification, with sound, to the user, with an activity to perform.
     * Gives the user the option to snooze the activity or confirm it being done.
     *
     * @param activity The activity to perform
     */
    public void showNotification(Activity activity) {
        Toolkit.getDefaultToolkit().beep();
        ImageIcon activityIcon = createActivityIcon(activity);
        String[] buttons = {"Jag har gjort aktiviteten!", "Påminn mig om fem minuter",};
        String instruction = activity.getActivityInstruction();
        String[] instructions = new String[3];

        if (instruction.contains("&")) {
            instructions = instruction.split("&");
        }

        JFrame frame = new JFrame();
        frame.setAlwaysOnTop(true);

        int answer = welcomePane.showOptionDialog(frame, instructions, activity.getActivityName(),
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, activityIcon, buttons, buttons[0]);
        if (answer == 0) {
            activity.setCompleted(true);
            mainPanel.getMainFrame().getClientController().getUser().setDelayedActivity(null);
            mainPanel.sendActivityFromGUI(activity);
            updateActivityList(activity);
        } else {
            stopTimer();
            startTimer(4, 59);
            activity.setCompleted(false);
            mainPanel.getMainFrame().getClientController().getUser().setDelayedActivity(activity);
            mainPanel.sendActivityFromGUI(activity);
        }
    }


    public class welcomePane extends JOptionPane {
        @Override
        public int getMaxCharactersPerLineCount() {
            return 10;
        }
    }

    public void showOfflineWelcomeMessage(String userName) {
        ImageIcon welcomeIcon = new ImageIcon("imagesClient/exercise.png");
        Image image = welcomeIcon.getImage();
        Image newImg = image.getScaledInstance(100, 100, Image.SCALE_SMOOTH);

        JOptionPane.showMessageDialog(null, "Välkommen " + userName + "!" + "\nEDIM kommer skicka notiser till dig med jämna mellanrum,\n" +
                "med en fysisk aktivitet som ska utföras.\n" +
                "Hur ofta du vill ha dessa notiser kan du ställa in själv.\n \n" +
                "I offlineläge finns begränsad funktionalitet. \nOm du vill nå denna, testa att logga in igen.", "Välkommen till EDIM  - Offline", 2, new ImageIcon(newImg));
    }


    public void showWelcomeMessage(String userName) {
        ImageIcon welcomeIcon = new ImageIcon("imagesClient/exercise.png");
        Image image = welcomeIcon.getImage();
        Image newImg = image.getScaledInstance(100, 100, Image.SCALE_SMOOTH);

        JOptionPane.showMessageDialog(null, "Välkommen " + userName + "!" + "\nEDIM kommer skicka notiser till dig med jämna mellanrum,\n" +
                "med en fysisk aktivitet som ska utföras.\n" +
                "Hur ofta du vill ha dessa notiser kan du ställa in själv.", "Välkommen till EDIM ", 2, new ImageIcon(newImg));
    }

    class ButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            Object click = e.getSource();
            int interval;
            if (click == btnLogOut) {
                mainPanel.logOut();
            }
            if (click == btnInterval) {
                if(cmbTimeLimit.getSelectedItem().equals("Nu")) {
                    interval = 0;
                }
                else  {
                    interval = Integer.parseInt((String) cmbTimeLimit.getSelectedItem());
                    }

                countTimerInterval(interval);
                mainPanel.sendChosenInterval(interval); //TODO: Use this method to send acitvity request when timer reaches 0?!?!
                updateLblInterval();
            }
        }
    }

}
