import client.ClientController;
import client.gui.AppPanel;
import client.gui.MainFrame;
import client.gui.MainPanel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import shared.Activity;
import shared.User;
import javax.swing.*;
import java.awt.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AppPanelTest {

    @Mock
    private MainPanel mainPanelMock;
    @Mock
    private MainFrame mainFrameMock;
    @Mock
    private ClientController clientControllerMock;
    @Mock
    private User userMock;
    @Mock
    private Activity activityMock;
    @Mock
    private ImageIcon imageIconMock;
    @Mock
    private Image imageMock;
    @Spy
    @InjectMocks
    private AppPanel appPanel;

    @Test
    public void testShowNotificationCompletedActivity() {

        try (MockedStatic<JOptionPane> mockedStatic = mockStatic(JOptionPane.class)) {
            mockedStatic.when(() -> JOptionPane.showOptionDialog(
                    any(), any(), any(), anyInt(), anyInt(), any(), any(), any())
            ).thenReturn(0);

            setupWhenConditions();

            appPanel.showNotification(activityMock);

            verify(activityMock).setCompleted(true);
            verify(mainPanelMock.getMainFrame().getClientController().getUser()).setDelayedActivity(null);
            verify(mainPanelMock).sendActivityFromGUI(activityMock);
            verify(appPanel, times(1)).updateActivityList(activityMock);
        }
    }

    @Test
    public void testShowNotificationDelayedActivity() {

        try (MockedStatic<JOptionPane> mockedStatic = mockStatic(JOptionPane.class)) {
            mockedStatic.when(() -> JOptionPane.showOptionDialog(
                    any(), any(), any(), anyInt(), anyInt(), any(), any(), any())
            ).thenReturn(1);

            setupWhenConditions();

            appPanel.showNotification(activityMock);

            verify(appPanel, times(1)).stopTimer();
            verify(appPanel, times(1)).startTimer(4, 59);
            verify(activityMock).setCompleted(false);
            verify(mainPanelMock.getMainFrame().getClientController().getUser()).setDelayedActivity(activityMock);
            verify(mainPanelMock).sendActivityFromGUI(activityMock);
        }
    }

    private void setupWhenConditions() {
        when(imageMock.getScaledInstance(anyInt(), anyInt(), anyInt())).thenReturn(imageMock);
        when(imageIconMock.getImage()).thenReturn(imageMock);
        when(activityMock.getActivityImage()).thenReturn(imageIconMock);
        when(activityMock.getActivityName()).thenReturn("TestActivityName");
        when(activityMock.getActivityInstruction()).thenReturn("Test instructions");
        when(mainPanelMock.getMainFrame()).thenReturn(mainFrameMock);
        when(mainPanelMock.getMainFrame().getClientController()).thenReturn(clientControllerMock);
        when(mainPanelMock.getMainFrame().getClientController().getUser()).thenReturn(userMock);
    }
}
