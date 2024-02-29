import org.junit.After;
import org.junit.Before;
import org.junit.*;
import client.gui.*;
import shared.Activity;


import java.util.LinkedList;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class TestFA5 {

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
    public void testDisplayOfActivities() {
        Activity test = new Activity("test");
        LinkedList<Activity> list = appPanel.updateActivityList(test);
        assertEquals(test.getActivityName(), list.getLast().getActivityName());

    }
}
