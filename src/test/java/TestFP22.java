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
        mainPanel = new MainPanel(mock(MainFrame.class), "test", "OFFLINE");
    }

    @After
    public void tearDown() {
        mainPanel = null;
    }

    @Test
    public void testUserNameVisibleInGUI() {
        assertNotNull(mainPanel.getBorder());
        assertEquals(mainPanel.getTitle(), "Välkommen, test - OFFLINE");
    }
}
