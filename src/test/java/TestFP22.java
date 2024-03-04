import org.junit.After;
import org.junit.Before;
import org.junit.*;
import client.gui.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class TestFP22 {
    private MainPanel mainPanel;

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
        mainPanel = null;
    }

    @Test
    public void testUserNameVisibleInGUI() {
        mainPanel = new MainPanel(mock(MainFrame.class), "test", "OFFLINE");
        assertNotNull(mainPanel.getBorder());
        assertEquals(mainPanel.getTitle(), "Välkommen, test - OFFLINE");
    }

    @Test
    public void testUserNameVisibleInGUI2() {
        mainPanel = new MainPanel(mock(MainFrame.class), "test", "ONLINE");
        assertNotNull(mainPanel.getBorder());
        assertEquals(mainPanel.getTitle(), "Välkommen, test - ONLINE");
    }
}
