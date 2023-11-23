package Project;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;

import Project.Server.BasicServer;

public class BasicServerIntegrationTest {
    private static BasicServer server;
    private static String serverAddress = "localhost";
    private static int port = 4000;
    private static int threadPoolSize = 4;

    public void connectionCheck() {
        try {
            Socket socket = new Socket(serverAddress, port);
            Boolean connected = socket.isConnected();
            System.out.println(connected);
            assertTrue(connected);
        } catch (IOException e) {
            System.out.println(e.toString());
        }
    }

    @BeforeClass
    public static void setUp() {

        server = new BasicServer();
        Thread serverThread = new Thread(() -> {
            server.start(port,threadPoolSize);
        });
        serverThread.start();
        try{
            // Give server time to start before starting tests.
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            System.out.println("Setup Error");
            System.out.println(e.toString());
        }
    }

    @Test
    public void testSingleSocketConnection() {
        connectionCheck();
    }

    @Test
    public void testConcurrentConnections() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(threadPoolSize);
        for (int i=0; i<threadPoolSize; i++) {
            executorService.submit(() -> connectionCheck());
        }

        executorService.shutdown();
        executorService.awaitTermination(3, TimeUnit.SECONDS);
    }

    @AfterClass
    public static void tearDown() throws InterruptedException{
        server.stop();
    }
}
