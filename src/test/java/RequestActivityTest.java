import client.ClientCommunicationController;
import client.ClientController;
import client.gui.MainFrame;
import shared.Activity;
import shared.User;
import shared.UserType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RequestActivityTest {

    @Mock
    private ClientCommunicationController cccMock;
    @Mock
    private MainFrame mainFrameMock;
    @InjectMocks
    private ClientController clientController;

    @BeforeEach
    public void setup() {
        clientController.createUser("unitTesting");
    }

    @Test
    public void testUserOfflineDelayedActivityNull() {

        clientController.runOffline();
        clientController.getUser().setDelayedActivity(null);

        clientController.requestActivity();

        assertEquals(UserType.OFFLINE, clientController.getUser().getUserType());
        verify(mainFrameMock).showNotification(any(Activity.class));

    }

    @Test
    public void testUserOfflineDelayedActivityNotNull() {

        clientController.runOffline();
        Activity mockDelayedActivity = mock(Activity.class);
        clientController.getUser().setDelayedActivity(mockDelayedActivity);

        clientController.requestActivity();

        assertEquals(UserType.OFFLINE, clientController.getUser().getUserType());
        verify(mainFrameMock).showNotification(mockDelayedActivity);
    }

    @Test
    public void testUserNotOfflineDelayedActivityNull() {

        clientController.getUser().setUserType(UserType.LOGIN);
        clientController.getUser().setDelayedActivity(null);

        clientController.requestActivity();

        assertEquals(UserType.WANTACTIVITY, clientController.getUser().getUserType());
        verify(cccMock, times(2)).sendObject(any(User.class));

    }

    @Test
    public void testUserNotOfflineDelayedActivityNotNull() {

        clientController.getUser().setUserType(UserType.LOGIN);
        Activity mockDelayedActivity = mock(Activity.class);
        clientController.getUser().setDelayedActivity(mockDelayedActivity);

        clientController.requestActivity();

        assertNotEquals(UserType.WANTACTIVITY, clientController.getUser().getUserType());
        assertNotEquals(UserType.OFFLINE, clientController.getUser().getUserType());
        verify(mainFrameMock).showNotification(mockDelayedActivity);

    }
}
