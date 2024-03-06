import org.junit.After;
import org.junit.Before;
import org.junit.*;
import client.gui.*;
import shared.Activity;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class TestFÖ3 {
    private AppPanel appPanel;
    private MainPanel mainPanel;

    @Before
    public void setUp() {
        mainPanel = mock(MainPanel.class);
        appPanel = new AppPanel(mainPanel);
    }

    @After
    public void tearDown() {
        mainPanel = null;
        appPanel = null;
    }

    @Test
    public void testShowInfoAboutActivity() {
        Activity test = new Activity("test ");
        test.setActivityName("test");
        test.setActivityInfo("Unittest!");
        test.setActivityInstruction("Gör 10 knäböj (squats). &Glöm inte att ha tyngden på hälarna och att knäna ska peka i samma riktning som tårna.&");
        appPanel.updateActivityList(test);

        JList activityList = appPanel.getActivityList();
        activityList.setSelectedIndex(0);
        ListSelectionListener listener = activityList.getListSelectionListeners()[0];
        ListSelectionEvent event = new ListSelectionEvent(activityList, 0, 0, false);
        listener.valueChanged(event);

        String result = appPanel.getActivityInfo();
        assertEquals(result, "Unittest!");

    }
}
