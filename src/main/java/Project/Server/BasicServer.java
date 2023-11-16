package Project.Server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.IOException;

import java.net.ServerSocket;
import java.net.Socket;

import java.util.ArrayList;




public class BasicServer{
    private ServerSocket serverSocket;
    
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
        private String requestLine;
        private ArrayList<String> requestHeaders = new ArrayList<String>();

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

                // Reads the first line sent by the client
                requestLine = input.readLine();
                System.out.println(requestLine);

                // Reads all subsequent lines sent by the client 
                String inputMessage;
                while((inputMessage = input.readLine()) != null) {
                    requestHeaders.add(inputMessage);
                }

                this.sendResponse(requestLine);
            } catch (IOException e) {
                System.out.println(e.toString());
            }
            
        }

        public String getRequestLine() {
            return this.requestLine;
        }

        public ArrayList<String> getHeaderList() {
            return requestHeaders;
        }

        public void sendResponse(String response) {
            output.println(response);
        }
    }
}
