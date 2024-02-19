package server;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;


/**
 * Requirement: F.S.6
 */
public class LoggerGUI extends JFrame implements PropertyChangeListener {

    private int width;
    private int height;
    private JList<Object> logList;
    private JList<String> onlineList;
    private DefaultListModel<String> defaultListModel;
    private JButton setStart;
    private JButton setEnd;
    private JButton search;
    private LocalDateTime start = null;
    private LocalDateTime end = null;
    private ServerController controller;
    private Timer autoRefresh;

    public LoggerGUI(ServerController controller) {
        this.controller = controller;
        this.width = 800;
        this.height = 600;
        this.setLayout(new BorderLayout());
        this.setResizable(false);
        this.setSize(width, height);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        add(labelPanel(), BorderLayout.NORTH);
        add(logListPanel(), BorderLayout.CENTER);
        add(onlinePanel(), BorderLayout.EAST);
        add(loggerInputs(), BorderLayout.SOUTH);

        ActionListener listener = new ButtonListener();
        autoRefresh = new Timer(1000, listener);
        autoRefresh.start();

        setStart.addActionListener(listener);
        setEnd.addActionListener(listener);
        search.addActionListener(listener);
        this.setVisible(true);
        controller.addListener(this);
    }

    private JPanel labelPanel(){
        JLabel logLabel = new JLabel("Log: ");
        logLabel.setPreferredSize(new Dimension(width, 50));

        JLabel onlineLabel = new JLabel("Online users: ");
        onlineLabel.setPreferredSize(new Dimension(width/3, 50));

        JPanel jPanel = new JPanel();
        jPanel.setMinimumSize(new Dimension(width, 20));
        jPanel.setPreferredSize(new Dimension(width, 20));

        jPanel.setLayout(new BorderLayout());
        jPanel.add(logLabel, BorderLayout.WEST);
        jPanel.add(onlineLabel, BorderLayout.EAST);

        return jPanel;
    }
    private JScrollPane logListPanel() {

        this.logList = new JList<>();
        String defaultFont = logList.getFont().getFontName();
        logList.setFont(new Font(defaultFont, Font.BOLD, 16));

        JScrollPane scrollPane = new JScrollPane(logList);
        scrollPane.setMinimumSize(new Dimension(width, height - 100));
        scrollPane.setPreferredSize(new Dimension(width, height - 100));


        return scrollPane;
    }

    /**
     * Requirement: F.S.2
     * @return
     */
    private JScrollPane onlinePanel() {
        this.defaultListModel = new DefaultListModel<>();
        this.onlineList = new JList<>(defaultListModel);
        String defaultFont = onlineList.getFont().getFontName();
        onlineList.setFont(new Font(defaultFont, Font.BOLD, 16));

        JScrollPane scrollPane = new JScrollPane(onlineList);
        scrollPane.setMinimumSize(new Dimension(width/3, height - 100));
        scrollPane.setPreferredSize(new Dimension(width/3, height - 100));


        return scrollPane;
    }


    private JPanel loggerInputs() {
        JPanel inputs = new JPanel();
        inputs.setLayout(new GridLayout(1, 3));
        this.setStart = new JButton("Start");
        this.setEnd = new JButton("End");
        this.search = new JButton("Search");
        inputs.add(setStart);
        inputs.add(setEnd);
        inputs.add(search);
        return inputs;
    }

    public void listLog(Object[] logEvents) {
        this.logList.setListData(logEvents);
    }

    @Override
    /**
     * Requirement: F.S.2
     */
    public void propertyChange(PropertyChangeEvent evt) {
        if(evt.getPropertyName().equals("New login: ")) {
            String userName = (String) evt.getNewValue();
            defaultListModel.addElement(userName);
        }
        if(evt.getPropertyName().equals("User logged out: ")) {
            String userName = (String) evt.getNewValue();
            defaultListModel.removeElement(userName);
        }
    }

    class ButtonListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == setStart) {
                String input = JOptionPane.showInputDialog(null, "Start interval (yyyy-mm-dd HH:mm");
                if (input != null && !input.equals("")) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                    try {
                        start = LocalDateTime.parse(input, formatter);
                    } catch (DateTimeParseException ex) {
                        start = null;
                    }
                } else {
                    start = null;
                }
            }
            if (e.getSource() == setEnd) {
                String input = JOptionPane.showInputDialog(null, "End interval (yyyy-mm-dd HH:mm)");
                if (input != null && !input.equals("")) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                    try {
                        end = LocalDateTime.parse(input, formatter);
                        autoRefresh.stop();
                    } catch (DateTimeParseException ex) {
                        end = null;
                        if (autoRefresh != null) {
                            autoRefresh.stop();
                            autoRefresh = null;
                        }
                        autoRefresh = new Timer(1000, this);
                        autoRefresh.start();
                    }
                } else {
                    end = null;

                    if (autoRefresh != null) {
                        autoRefresh.stop();
                        autoRefresh = null;
                    }

                    autoRefresh = new Timer(1000, this);
                    autoRefresh.start(); // if end-date not specified, auto-refresh every 1s
                }
            }

            if (e.getSource() == search) {
                controller.callSearchLogger(start, end);
            }

            if (e.getSource() == autoRefresh) {
                controller.callSearchLogger(start, end);
            }

        }
    }

}

