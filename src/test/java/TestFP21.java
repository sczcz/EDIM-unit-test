import org.junit.After;
import org.junit.Before;
import org.junit.*;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import client.gui.*;

import javax.swing.*;
import static org.mockito.Mockito.*;

public class TestFP21 {
    private LogInPanel logInPanel;
    private MainFrame mainFrame;
    private LogInFrame logInFrame;
    private JTextField tfUserName;
    private MockedStatic<JOptionPane> mockJOptionPane;


    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mainFrame = mock(MainFrame.class);
        logInFrame = mock(LogInFrame.class);
        tfUserName = mock(JTextField.class);
        logInPanel = new LogInPanel(logInFrame, mainFrame);
        logInPanel.setTfUserName(tfUserName);
        mockJOptionPane = mockStatic(JOptionPane.class);
    }

    @After
    public void tearDown() {
        mainFrame = null;
        logInFrame = null;
        tfUserName = null;
        logInPanel = null;
        mockJOptionPane.close();
    }


    @Test
    public void testLogInWithValidUserName() {
        when(tfUserName.getText()).thenReturn("validUsername");
        logInPanel.logIn();

        verify(mainFrame).sendUser("validUsername");
        verify(logInFrame).closeWindow();
    }


    @Test
    public void testLogInWithEmptyUserName() {
        when(tfUserName.getText()).thenReturn("");
        logInPanel.logIn();
        mockJOptionPane.verify(() -> JOptionPane.showMessageDialog(
                any(), eq("Du måste välja ett användarnamn")));
    }


    @Test
    public void testLogInWithInvalidUserName() {
        when(tfUserName.getText()).thenReturn("invalid username");
        logInPanel.logIn();
        mockJOptionPane.verify(() -> JOptionPane.showMessageDialog(
                any(), eq("Ditt användarnamn får inte innehålla mellanslag")));
    }
}

