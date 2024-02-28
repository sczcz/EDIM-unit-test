import org.junit.After;
import org.junit.Before;
import org.junit.*;
import org.mockito.MockedStatic;
import client.gui.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

public class TestFP22 {
    private MainPanel mainPanel;
    private MockedStatic<TitledBorder> mockTitledBorder;

    @Before
    public void setUp() {
        mainPanel = new MainPanel(mock(MainFrame.class), "test", "OFFLINE");
        mockTitledBorder = mockStatic(TitledBorder.class);
    }

    @After
    public void tearDown() {
        mainPanel = null;
        mockTitledBorder.close();
    }

    @Test
    public void testUserNameVisibleInGUI() {
        assertNotNull(mainPanel.getBorder());
        mockTitledBorder.verify(() -> BorderFactory.createTitledBorder("Välkommen, test - OFFLINE"));
    }
}
