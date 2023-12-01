package HTTPServer;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.Socket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import HTTPServer.Server.BasicServer;

public class BasicServerIntegrationTest {
    private static BasicServer server;
    private static String serverAddress = "localhost";
    private static int port = 4000;
    private static int threadPoolSize = 4;

    public void connectionCheck() {
        try {
            Socket socket = new Socket(serverAddress, port);
            assertTrue(socket.isConnected());
            socket.close();
        } catch (IOException e) {
            System.out.println(e.toString());
        }
    }

    @BeforeClass
    public static void setUp() {

        server = new BasicServer();
        server.addPermittedMethods(new String[]{"GET"});
        server.addRoute("GET", "/noParams/noHeaders/noBody", (req, res) -> {
            res.setHttpVersion("HTTP/1.1");
            res.setStatusCode(200);
        });
        server.addRoute("GET", "/params/noHeaders/noBody", (req, res) -> {
            res.setHttpVersion("HTTP/1.1");
            res.setStatusCode(200);
            res.addBody(req.getParameterValue("param1"));
        });
        server.addRoute("GET", "/noParams/headers/noBody", (req, res) -> {
            res.setHttpVersion("HTTP/1.1");
            res.setStatusCode(200);
            res.addHeader("Origin", "localhost:2000");
            res.addHeader("Content-Type", "text/plain; charset=UTF-8");
            res.addBody("Response Body");
        });
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

    @Test
    public void testRouteNoParamsNoHeadersNoBody() throws IOException, InterruptedException{
        HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create("http://localhost:4000/noParams/noHeaders/noBody"))
        .method("GET", HttpRequest.BodyPublishers.noBody())
        .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        
        assertTrue(response.statusCode() == 200);
    }

    @Test
    public void testRouteParamsNoHeadersNoBody() throws IOException, InterruptedException{
        HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create("http://localhost:4000/params/noHeaders/noBody?param1=value1"))
        .method("GET", HttpRequest.BodyPublishers.noBody())
        .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        assertTrue(response.statusCode() == 200);
        assertTrue("value1".equals(response.body()));
    }

    @Test
    public void testRouteNoParamsHeadersBody() throws IOException, InterruptedException{
        HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create("http://localhost:4000/noParams/headers/noBody"))
        .method("GET", HttpRequest.BodyPublishers.noBody())
        .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        assertTrue(response.statusCode() == 200);
        assertTrue("localhost:2000".equals(response.headers().map().get("Origin").get(0)));
        assertTrue("text/plain; charset=UTF-8".equals(response.headers().map().get("Content-Type").get(0)));
    }



    @AfterClass
    public static void tearDown() throws InterruptedException{
        server.stop();
    }
}
