package Project;

import org.junit.Before;
import org.junit.Test;

import Project.Client.BasicClient;
import Project.Server.BasicServer;

public class IntegrationTest {
    BasicClient client;
    BasicServer server;
    int port = 3000;
    private static Boolean setUpComplete = false;

    @Before
    public void setUp() {
        if (setUpComplete == true) {
            return;
        }

        server = new BasicServer();
        Thread serverThread = new Thread(() -> {
            server.start(port);
        });
        serverThread.start();
        try{
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            System.out.println("Setup Error");
            System.out.println(e.toString());
        }
        setUpComplete = true;
    }

    @Test
    public void requestSentResponseRecieved() {
        
    }
}
