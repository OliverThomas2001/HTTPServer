package Project.Server;

import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.IOException;

import java.net.ServerSocket;
import java.net.Socket;

import Project.Http.HttpRequest;
import Project.Http.HttpResponse;


public class BasicServer{
    private ServerSocket serverSocket;
    private ExecutorService threadPool;
    private volatile Boolean quit = false;

    private static Map<String, RequestHandler> routes = new HashMap<String, RequestHandler>(); // needs to be shared amongst all instances of BasicServer.
    public static void main(String[] args) {

        System.out.println(Arrays.toString(new String("Hello?").split("\\?")));


        BasicServer server = new BasicServer();
        server.addRoute("GET", "/", (req, res) -> {
            res.setHttpVersion("HTTP/1.1");
            res.setStatusCode(200);
            res.addHeader("Origin", "localhost:2000");
            res.addHeader("Content-Type", "text/plain; charset=UTF-8");
            res.addHeader("Content-Length", "12");
            res.addBody(req.getParameterValue("param1"));
        });
        server.addRoute("GET", "/new", (req, res) -> {
            System.out.println("Second route executed");
        });
        
        
        
        server.start(2000, 4); 
    }
    
    public void start(int port, int threadPoolSize) {
        try {
            serverSocket = new ServerSocket(port);
            threadPool = Executors.newFixedThreadPool(threadPoolSize);
            System.out.println("Server listening on port " + serverSocket.getLocalPort());
            
            while (!quit) {
                threadPool.submit(new ClientHandler(serverSocket.accept()));
            }

        } catch(IOException e) {
            e.printStackTrace();
        } finally {
            try{
                System.out.println("Exiting server start loop.");
                threadPool.shutdown();
                System.out.println("Shutting down thread pool.");
                threadPool.awaitTermination(1, TimeUnit.SECONDS); // waits for all threads to terminate for up to 10 seconds.
                System.out.println("Thread pool shut down completed.");
                serverSocket.close();
                System.out.println("Server socket closed.");

            } catch (IOException | InterruptedException e) {
                System.out.println(e.toString());
            }
        }
    }

    public void stop() {
        quit = true;
    }

    public void addRoute(String method, String path, RequestHandler handler) { // route is a combination of method and uri. e.g. "GET /"
        BasicServer.routes.put(method + path, handler);
    }

    public static void getRoutes() {
        System.out.println(Arrays.toString(routes.keySet().toArray()));
    }


    public static Boolean isPermittedRoute(String method, String uri) {
        return BasicServer.routes.containsKey(method + uri);
    }

    public static class ClientHandler implements Runnable {
        private Socket clientSocket;
        private PrintWriter output;
        private BufferedReader input;

        private HttpRequest request;
        // add response here;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        private void initialise() throws IOException{
            input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            output = new PrintWriter(clientSocket.getOutputStream(), true);
        }

        public void run() {
            try {
                this.initialise();

                String requestLine;
                // First line in HTTP request is the "request line"
                if ((requestLine = input.readLine()) != null){
                    System.out.println(requestLine);
                    request = new HttpRequest(requestLine);

                    if (request.getRequestValidity()) {
                        System.out.println("Valid request line");

                        // Reads all subsequent lines sent by the client and adds headers to hashmap.
                        String inputMessage;
                        //  && !inputMessage.isEmpty()
                        while((inputMessage = input.readLine()) != null && !inputMessage.isEmpty()) {
                            // System.out.println(inputMessage);
                            String[] header = inputMessage.split(": ");
                            request.addHeader(header[0], header[1]);
                        }
                        

                        HttpResponse response = new HttpResponse();
                        handleHttpRequest(request.getRequestMethod() + request.getRequestPath(), request, response);

                        this.sendResponse(response);

                    } else {
                        System.out.println("Invalid request line");
                        // Send 400 bad request.
                        HttpResponse response = new HttpResponse();
                        response.setHttpVersion("HTTP/1.1");
                        response.setStatusCode(415);
                        this.sendResponse(response);
                        
                    }
                }

            } catch (IOException e) {
                System.out.println(e.toString());
            } finally {
                
                this.close();
            }
        }

        public void sendResponse(HttpResponse response) {

            // add mandatory headers here e.g if response body exists, add content-length etc.


            output.println(response.getStatusMessage());
            if (response.getStatusCode() < 500) {
                for (String header : response.getHeaderArray()){
                    System.out.println(header);
                    output.println(header);
                }
                output.println("\n");
                if (response.getResponseBody() != null){
                    output.println(response.getResponseBody());
                }
            }
        }

        public void close() {
            try {
                if (input != null) {
                    input.close();
                }

                if (output != null) {
                    output.close();
                }

                if (clientSocket != null) {
                    clientSocket.close();
                }
            } catch (IOException e) {
                System.out.println(e.toString());
            }
        }

        // can likely remove the request parameter from this function just use the instance variable.
        public void handleHttpRequest(String route, HttpRequest req, HttpResponse res) {
            RequestHandler handler = routes.get(route);
            if (handler != null) {
                handler.handleHttpRequest(req, res);
            } else {
                // Send 400 bad request.
            }
        }
    }
}