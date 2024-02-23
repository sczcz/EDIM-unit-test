import client.ClientCommunicationController;
import client.ClientController;
import client.gui.MainFrame;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import shared.Activity;
import shared.ActivityRegister;
import shared.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import shared.UserType;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClientControllerTest {

    @Mock
    private ClientCommunicationController cccMock;
    @Mock
    private User userMock;
    @Mock
    private MainFrame mainFrameMock;
    @InjectMocks
    private ClientController clientController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        clientController.setMainFrame(mainFrameMock);
        clientController.createUser("unitTesting");
    }

    @Test
    public void testCreateUser() {

        assertEquals("unitTesting", clientController.getUser().getUsername());
    }

    @Test
    public void testLogIn() {

        assertEquals(UserType.LOGIN, clientController.getUser().getUserType());
        verify(cccMock, times(1)).sendObject(any(User.class));
    }

    @Test
    public void testLogOut() {

        clientController.logOut();

        assertEquals(UserType.LOGOUT, clientController.getUser().getUserType());
        verify(cccMock, times(2)).sendObject(any(User.class));
    }

    @Test
    public void testRunOffline() {

        clientController.runOffline();

        assertEquals(UserType.OFFLINE, clientController.getUser().getUserType());
        assertNotNull(clientController.getActivityRegister());
    }

    @Test
    public void testGetOfflineActivity() {

        clientController.runOffline();
        clientController.getUser().setDelayedActivity(null);
        ActivityRegister mockActivityRegister = mock(ActivityRegister.class);
        clientController.setActivityRegister(mockActivityRegister);
        ImageIcon mockImage1 = mock(ImageIcon.class);
        ImageIcon mockImage2 = mock(ImageIcon.class);
        ImageIcon mockImage3 = mock(ImageIcon.class);
        List<Activity> activityList = Arrays.asList(
                new Activity("Activity1", "Instruction1", "Info1", "unitTesting", mockImage1),
                new Activity("Activity2", "Instruction2", "Info2", "unitTesting", mockImage2),
                new Activity("Activity3", "Instruction3", "Info3", "unitTesting", mockImage3)
        );
        when(mockActivityRegister.getSize()).thenReturn(3);
        when(mockActivityRegister.getActivityRegister()).thenReturn(new LinkedList<>(activityList));

        Activity testResult = clientController.getOfflineActivity();

        assertNotNull(testResult);
        assertTrue(clientController.getActivityRegister().getActivityRegister().stream()
                .anyMatch(a -> a.getActivityName().equals(testResult.getActivityName()) &&
                        a.getActivityInstruction().equals(testResult.getActivityInstruction()) &&
                        a.getActivityInfo().equals(testResult.getActivityInfo()) &&
                        a.getActivityUser().equals(testResult.getActivityUser()) &&
                        a.getActivityImage() == testResult.getActivityImage()));
    }

    @Test
    public void testReceiveUserNew() {

        when(userMock.getUserType()).thenReturn(UserType.SENDWELCOME);

        clientController.receiveUser(userMock);

        assertEquals(userMock, clientController.getUser());
        assertEquals(UserType.SENDWELCOME, clientController.getUser().getUserType());
        verify(mainFrameMock, times(1)).sendWelcomeMessage();
    }

    @Test
    public void testReceiveUserOld() {

        when(userMock.getUserType()).thenReturn(UserType.WANTACTIVITY);

        clientController.receiveUser(userMock);

        assertEquals(userMock, clientController.getUser());
        assertNotEquals(UserType.SENDWELCOME, clientController.getUser().getUserType());
        verify(mainFrameMock, times(0)).sendWelcomeMessage();
    }

    @Test
    public void testReceiveOnlineList() {

        User user1 = new User("user1");
        User user2 = new User("user2");
        ArrayList<User> usersOnline = new ArrayList<>();
        usersOnline.add(user1);
        usersOnline.add(user2);

        clientController.receiveOnlineList(usersOnline);

        verify(mainFrameMock).showUsersOnline(argThat(argument -> argument.contains("user1") && argument.contains("user2")));
    }

    @Test
    public void testSetInterval() {
        int interval = 30;

        clientController.setInterval(interval);

        assertEquals(interval, clientController.getUser().getNotificationInterval());
        assertEquals(UserType.SENDINTERVAL, clientController.getUser().getUserType());
        verify(cccMock, times(2)).sendObject(clientController.getUser());
    }

}
