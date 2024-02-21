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

import java.io.*;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Objects;

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

        User mockUser = new User("mockUser");
        mockUser.setUsername("mockUser");

        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(testFilePath.toFile()))) {
            oos.writeInt(1);
            oos.writeObject(mockUser);
            oos.flush();
        }


        serverController.readUsers(testFilePath.toString());

        User readUser = userRegister.getUserHashMap().get("mockUser");
        //assertNotNull(readUser);
        assertEquals("mockUser", readUser.getUsername());

        User listUser = userRegister.getUserLinkedList().get(0);
        //assertNotNull(listUser);
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

        System.out.println("Användare skapad: " + mockUser.getUsername());

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(testFilePath.toFile()))) {
            oos.writeInt(1);
            oos.writeObject(mockUser);
            oos.flush();

            System.out.println("Object output stream färdig");
        } catch (Exception e){
            System.out.println("första try catch failade");
        }


        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(testFilePath.toFile()))) {

            System.out.println("Int in file: " + ois.readInt());

            User writtenUser = (User) ois.readObject();
            assertEquals("mockUser", writtenUser.getUsername());

            System.out.println("username: " + writtenUser.getUsername());

        } catch (ClassNotFoundException e) {
            System.out.println("Andra try catch failade");
        }

        serverController.readUsers(testFilePath.toString());
        System.out.println("testFilePath: " + testFilePath.toString());

        User readUser = userRegister.getUserHashMap().get("mockUser");
        System.out.println("Read user: " + readUser);

        assertEquals("mockUser", readUser.getUsername());

        User listUser = userRegister.getUserLinkedList().get(0);
        assertEquals("mockUser", listUser.getUsername());

        Files.delete(testFilePath); // Rensa upp den temporära filen
    }


}
