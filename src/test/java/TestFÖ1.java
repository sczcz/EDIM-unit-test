import client.ClientController;
import org.junit.After;
import org.junit.Before;
import org.junit.*;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import client.gui.*;
import shared.Activity;
import shared.User;

import javax.swing.*;

import java.util.LinkedList;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class TestFÖ1 {

    private MainPanel mainPanel = mock(MainPanel.class);
    private MainFrame mainFrame = mock(MainFrame.class);
    private ClientController clientController = mock(ClientController.class);
    private AppPanel appPanel = new AppPanel(mainPanel);

    //Was not able to make the following test fully automated. The user is needed to press the button for
    // "i have done the exercise", when prompted. The test should then be successful.

    @Test
    public void testActivityInformation(){
        User test = new User("testUser");
        Activity activity = new Activity();
        activity.setActivityName("test");
        activity.setActivityInstruction("testa test &test &test");
        activity.setActivityInfo("testInfo");
        activity.createActivityImage("test/superman.jpg");
        when(mainPanel.getMainFrame()).thenReturn(mainFrame);
        when(mainPanel.getMainFrame().getClientController()).thenReturn(clientController);
        when(mainPanel.getMainFrame().getClientController().getUser()).thenReturn(test);

        LinkedList<Activity> testActivity = appPanel.showNotification(activity);
        assertEquals(activity, testActivity.get(0));
    }
}
