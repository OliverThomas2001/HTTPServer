package Project.Server;

import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;
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

    private static Set<String> pathSet = new HashSet<>(); // A set containing all permissible paths the user has created.
    private static Set<String> permittedMethodSet = new HashSet<>(); // A set containing all permissible methods across entire server.
    private static Map<String, RequestHandler> routes = new HashMap<String, RequestHandler>(); // needs to be shared amongst all instances of BasicServer.
    public static void main(String[] args) {

        System.out.println(Arrays.toString(new String("Hello?").split("\\?")));


        BasicServer server = new BasicServer();
        server.addPermittedMethods(new String[]{"GET"});
        server.addRoute("GET", "/", (req, res) -> {
            res.setHttpVersion("HTTP/1.1");
            res.setStatusCode(200);
            res.addHeader("Origin", "localhost:2000");
            res.addHeader("Content-Type", "text/plain; charset=UTF-8");
            // res.addHeader("Content-Length", "12");
            System.out.println(req.getParameterValue("param1"));
            res.addBody(req.getParameterValue("param1"));
        });
        server.addRoute("GET", "/new", (req, res) -> {
            System.out.println("Second route executed");
        });
        
        
        
        server.start(2000, 4); 
    }

    public void addPermittedMethods(String[] allowedMethodArray) {
        for (String method : allowedMethodArray) {
            permittedMethodSet.add(method);
        }
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
        pathSet.add(path);
    }

    public static void getRoutes() {
        System.out.println(Arrays.toString(routes.keySet().toArray()));
    }

    public static class ClientHandler implements Runnable {
        private Socket clientSocket;
        private PrintWriter output;
        private BufferedReader input;

        private HttpRequest request;
        HttpResponse response = new HttpResponse();

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        public void run() {
            try {
                initialiseStreams(); // saves
                parseIncomingRequest();
                sendResponse();
            } catch (IOException e) {
                System.out.println(e.toString());
            } finally {
                
                this.close();
            }
        }

        private void initialiseStreams() throws IOException{
            input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            output = new PrintWriter(clientSocket.getOutputStream(), true);
        }

        private void parseIncomingRequest() throws IOException{
            String requestLine;
            if ((requestLine = input.readLine()) != null){
                request = new HttpRequest(requestLine);
                if (request.getRequestValidity() == false) {
                    // send 400 error code.
                } else if (!validateHttpVersion(request.getHttpVersion())) {
                    // send 505 error code.
                } else if (!validatePathExistence(request.getRequestPath())) {
                    // send 404 error code.
                } else if (!validateMethodPermittance(request.getRequestMethod())) {
                    // send 501 error code.
                } else if (!validateUriExistence(request.getRequestMethod(), request.getRequestPath())) {
                    // send 405 error code.
                } else {
                    // Reads all subsequent lines sent by the client and adds headers to hashmap.
                    String inputMessage;
                    //  && !inputMessage.isEmpty()
                    while((inputMessage = input.readLine()) != null && !inputMessage.isEmpty()) {
                        // System.out.println(inputMessage);
                        String[] header = inputMessage.split(": ");
                        request.addHeader(header[0], header[1]);
                    }
                    
                    handleHttpRequest();
                }

            }

        }

        private Boolean validatePathExistence(String path) {
            return BasicServer.pathSet.contains(path);
        }

        private Boolean validateMethodPermittance(String method) {
            return BasicServer.permittedMethodSet.contains(method);
        }

        // this checks the user has added a route for the given request method and uri.
        private static Boolean validateUriExistence(String method, String uri) {
            return BasicServer.routes.containsKey(method + uri);
        }

        private Boolean validateHttpVersion(String httpVersion) {
            // At present, I only plan to use version 1.1 but could extend to other versions at a later date.
            return httpVersion.equals("HTTP/1.1");
        }

        // can likely remove the request parameter from this function just use the instance variable.
        private void handleHttpRequest() {
            RequestHandler handler = routes.get(request.getRequestMethod() + request.getRequestPath());
            handler.handleHttpRequest(request, response);
        }

        private void sendResponse() {
            // add mandatory headers here e.g if response body exists, add content-length etc.

            output.println(response.getStatusMessage());
            if (response.getStatusCode() < 400) { // If response is responding with an error.
                for (String header : response.getHeaderArray()){
                    output.println(header);
                }
                output.println();
                if (response.getResponseBody() != null){
                    output.print(response.getResponseBody());
                    output.flush();
                }
            }
        }

        private void close() {
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
    }
}