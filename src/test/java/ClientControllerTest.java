import client.ClientCommunicationController;
import client.ClientController;
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
    @InjectMocks
    private ClientController clientController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
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
}
