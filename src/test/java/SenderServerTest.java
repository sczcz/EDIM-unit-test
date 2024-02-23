import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import server.SenderServer;
import server.ServerController;
import server.SocketStreamObject;
import shared.Buffer;

import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class SenderServerTest {
    @Mock
    private HashMap<String, SocketStreamObject> mockSocketHashMap;
    @Mock
    private Buffer<Object> mockSendBuffer;
    @Mock
    private SocketStreamObject mockSocketStreamObject;
    @Mock
    private ObjectOutputStream mockOos;

    private SenderServer senderServer;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        MockitoAnnotations.openMocks(this);
        when(mockSocketStreamObject.getOos()).thenReturn(mockOos);
        mockSocketHashMap.put("user1", mockSocketStreamObject);
        senderServer = new SenderServer(mockSocketHashMap, mockSendBuffer);
    }

    @Test
    void testGenerateThreadPool() {
        int nbrOfConnections = 5;
        senderServer.generateThreadPool(nbrOfConnections);

        int expectedSize = nbrOfConnections +1;
        int actualSize = senderServer.getThreadPoolSize();

        assertEquals(expectedSize, actualSize, "ThreadPool ska innehålla " + expectedSize + " WorkerThreads efter generering.");
    }
}
