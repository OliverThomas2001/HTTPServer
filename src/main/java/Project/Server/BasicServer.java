package Project.Server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.IOException;

import java.net.ServerSocket;
import java.net.Socket;

import Project.HTTP.HTTPRequest;




public class BasicServer{
    private ServerSocket serverSocket;

    public static void main(String[] args) {
        BasicServer server = new BasicServer();
        server.start(2000);
    }
    
    public void start(int port) {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server listening on port " + serverSocket.getLocalPort());
            
            while (true) {
                new ClientHandler(serverSocket.accept()).start();
            }

        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        try {
            if (!serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.out.println(e.toString());
        }
    }

    public static class ClientHandler extends Thread {
        private Socket clientSocket;
        private PrintWriter output;
        private BufferedReader input;

        private HTTPRequest request;

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
                
                // First line in HTTP request is the "request line"
                String requestLine = input.readLine();

                if (HTTPRequest.validateRequestLine(requestLine)) {
                    String[] splitReqLine = requestLine.split(" ");
                    request = new HTTPRequest(splitReqLine[0], splitReqLine[1]);
                } else {
                    // Send 400 bad request.
                }

                // Reads all subsequent lines sent by the client and adds headers to hashmap.
                String inputMessage;
                while((inputMessage = input.readLine()) != null) {
                    String[] header = inputMessage.split(": ");
                    request.addHeader(header[0], header[1]);
                }

                this.sendResponse(requestLine);

                this.close();
            } catch (IOException e) {
                System.out.println(e.toString());
            }
            
        }

        public void sendResponse(String response) {
            output.println(response);
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
    }
}