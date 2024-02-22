import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import server.Logger;
import server.LoggerGUI;
import server.ServerController;
import server.UserRegister;
import shared.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Mockito.*;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Random;

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

    @Test
    void testWriteUser() throws IOException, ClassNotFoundException {
        //Path tempFile = Files.createTempFile("test", ".dat");

        User mockUser = new User("mockUser");
        LinkedList<User> mockLinkedList = new LinkedList<>();
        mockLinkedList.add(mockUser);

        when(userRegister.getUserLinkedList()).thenReturn(mockLinkedList);

        serverController.writeUsers("users.dat");

        try (ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(
                Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("users.dat"))))) {
            assertEquals(ois.readInt(), 1);
            User readUser = (User) ois.readObject();
            assertEquals("mockUser", readUser.getUsername());
        }

        //Files.delete(tempFile);
    }

    @Test
    void testReadUser() throws IOException {
        Path testFilePath = Files.createTempFile("test", ".dat");
        //String testFilePath = "src/test/resources/test-users.dat";

        HashMap<String, User> mockedHashMap = new HashMap<>();
        LinkedList<User> mockedLinkedList = new LinkedList<>();

        when(userRegister.getUserHashMap()).thenReturn(mockedHashMap);
        when(userRegister.getUserLinkedList()).thenReturn(mockedLinkedList);

        User mockUser = new User("mockUser");
        mockUser.setUsername("mockUser");

        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(testFilePath.toFile()))) {
            oos.writeInt(1);
            oos.writeObject(mockUser);
            oos.flush();
        }

        serverController.readUsers(testFilePath.toString());

        User readUser = mockedHashMap.get("mockUser");
        assertEquals("mockUser", readUser.getUsername());

        User listUser = mockedLinkedList.getFirst();
        assertEquals("mockUser", listUser.getUsername());

        //Files.delete(tempFile);
    }

    @Test
    void testReadUser_v2() throws IOException, ClassNotFoundException {
        Path testFilePath = Files.createTempFile("test", ".dat");

        HashMap<String, User> mockedHashMap = new HashMap<>();
        LinkedList<User> mockedLinkedList = new LinkedList<>();

        when(userRegister.getUserHashMap()).thenReturn(mockedHashMap);
        when(userRegister.getUserLinkedList()).thenReturn(mockedLinkedList);

        User mockUser = new User("mockUser");
        mockUser.setUsername("mockUser");

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(testFilePath.toFile()))) {
            oos.writeInt(1);
            oos.writeObject(mockUser);
            oos.flush();
        }


        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(testFilePath.toFile()))) {

            int writtenInt = ois.readInt();

            User writtenUser = (User) ois.readObject();
            assertEquals("mockUser", writtenUser.getUsername());

        } catch (ClassNotFoundException e) {
            System.out.println("Andra try catch failade");
        }

        serverController.readUsers(testFilePath.toString());

        User readUser = userRegister.getUserHashMap().get("mockUser");
        assertEquals("mockUser", readUser.getUsername());

        User listUser = userRegister.getUserLinkedList().get(0);
        assertEquals("mockUser", listUser.getUsername());

        Files.delete(testFilePath);
    }

    @Test
    void testSendActivityWithDelayedActivity() throws InterruptedException {
        String username = "mockUser";
        User mockUser = mock(User.class);
        when(mockUser.getUsername()).thenReturn(username);
        Activity delayedActivity = new Activity("delayedActivity");

        when(mockUser.getDelayedActivity()).thenReturn(delayedActivity);

        mockUser.setDelayedActivity(delayedActivity);

        HashMap<String, User> mockedHashMap = new HashMap<>();
        mockedHashMap.put(username, mockUser);

        when(userRegister.getUserHashMap()).thenReturn(mockedHashMap);

        serverController.sendActivity(username);

        verify(sendBuffer).put(delayedActivity);
        verify(mockUser).setDelayedActivity(null);
    }

    @Test
    void testSendActivityWithoutDelayedActivity() throws NoSuchFieldException, IllegalAccessException {
        String username = "mockUser";
        User mockUser = new User(username);
        mockUser.setUsername(username);

        HashMap<String, User> mockUserHashMap = new HashMap<>();
        mockUserHashMap.put(username, mockUser);

        when(userRegister.getUserHashMap()).thenReturn(mockUserHashMap);

        System.out.println("User from userHashMap: " + mockUserHashMap.get(username).getUsername());

        ArgumentCaptor<Activity> activityCaptor = forClass(Activity.class);

        serverController.sendActivity(mockUser.getUsername());

        verify(sendBuffer).put(activityCaptor.capture());
    }


}
