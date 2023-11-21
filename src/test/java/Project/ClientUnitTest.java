package Project;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;
// import java.lang.Thread;
import org.junit.AfterClass;

import Project.Client.BasicClient;
import Project.Server.BasicServer;

public class ClientUnitTest {
    BasicClient client;
    BasicServer server;
    int port = 2000;
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
    public void hasSuccessfullyConnected() {
        client = new BasicClient("localhost", port);
        client.connect();
        assertTrue(client.isConnected());
    }

    @Test
    public void hasSuccessfullyDisconnected() {
        try {
            client = new BasicClient("localhost", port);
            client.connect();
            client.disconnect();
            assertTrue(client.isClosed());
        } catch (IOException e) {
            System.out.println("Test2 Error");
            System.out.println(e.toString());
        }
    }

    // @Test
    // public void hasRequestSent() {
    //     try {
    //         client = new BasicClient("localhost", port);
    //         client.connect();
    //         client.sendRequest("GET", "/");
    //     } catch (IOException e) {
    //         System.out.println("Test3 Error");
    //         System.out.println(e.toString());
    //     }
    // }

    @After
    public void teardownClient() {
        if (client.isClosed() == false) {
            try {
                client.disconnect();
            } catch (IOException e) {
                System.out.println("Teardown Error");
                System.out.println(e.toString());
            }
        }
    }

    @AfterClass
    public void teardownServer() {
        server.stop();
    }
    


}


