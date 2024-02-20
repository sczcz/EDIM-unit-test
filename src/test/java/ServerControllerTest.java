import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import server.Logger;
import server.LoggerGUI;
import server.ServerController;
import server.UserRegister;
import shared.ActivityRegister;
import shared.Buffer;
import shared.User;
import shared.UserType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.HashMap;

public class ServerControllerTest {
    @Mock
    private UserRegister userRegister;
    @Mock
    private ActivityRegister activityRegister;
    @Mock
    private Buffer<Object> receiveBuffer;
    @Mock
    private Buffer<Object> sendBuffer;
    @Mock
    private Logger log;
    @Mock
    private LoggerGUI loggerGUI;

    private ServerController serverController;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        MockitoAnnotations.openMocks(this);
        serverController = new ServerController(8080);

        setPrivateField(serverController, "userRegister", userRegister);
        setPrivateField(serverController, "activityRegister", activityRegister);
        setPrivateField(serverController, "receiveBuffer", receiveBuffer);
        setPrivateField(serverController, "sendBuffer", sendBuffer);
        setPrivateField(serverController, "log", log);
        setPrivateField(serverController, "loggerGUI", loggerGUI);
    }

    private void setPrivateField(Object object, String fieldName, Object value) throws NoSuchFieldException, IllegalAccessException {
        Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(object, value);
    }

    @Test
    void testCheckLoginUser_NewUser() {
        User newUser = new User("newUser");
        when(userRegister.getUserHashMap()).thenReturn(new HashMap<>());

        User resultUser = serverController.checkLoginUser(newUser);

        assertEquals(UserType.SENDWELCOME, resultUser.getUserType());
        verify(userRegister, times(2)).getUserHashMap();
    }

    @Test
    void testCheckLoginUser_ReturningUser() {
        User returningUser = new User("returningUser");
        HashMap<String, User> mockHashMap = new HashMap<>();
        mockHashMap.put("returningUser", returningUser);

        when(userRegister.getUserHashMap()).thenReturn(mockHashMap);

        User resultUser = serverController.checkLoginUser(returningUser);

        assertEquals(UserType.SENDUSER, resultUser.getUserType());
        verify(userRegister, times(3)).getUserHashMap();
    }


}
