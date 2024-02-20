import client.ClientCommunicationController;
import client.ClientController;
import shared.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import shared.UserType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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
}
