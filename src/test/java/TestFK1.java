import org.junit.Before;
import org.junit.*;
import client.gui.*;
import javax.swing.*;
import java.util.ArrayList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class TestFK1 {
    private AppPanel appPanel;

    @Before
    public void setUp() {
        MainPanel mainPanel = mock(MainPanel.class);
        appPanel = new AppPanel(mainPanel);
    }

    @Test
    public void testOnlineList(){
        ArrayList<String> usersOnline = new ArrayList<String>();
        usersOnline.add("user1");
        usersOnline.add("user2");
        JList list = appPanel.displayOnlineList(usersOnline);
        String user1 = (String) list.getModel().getElementAt(0);
        String user2 = (String) list.getModel().getElementAt(1);
        assertEquals("user1", user1);
        assertEquals("user2", user2);
    }

}
