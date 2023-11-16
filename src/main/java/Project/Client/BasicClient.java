package Project.Client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.IOException;

import java.net.Socket;


public class BasicClient{
    private Socket socket;
    private PrintWriter output;
    private BufferedReader input;
    private String serverAddress;
    private int port;

    public BasicClient(String address, int portNum){
        serverAddress = address;
        port = portNum;
    }

    public void connect() {
        try {
            socket = new Socket(serverAddress, port);
            output = new PrintWriter(socket.getOutputStream(), true);
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch(IOException e) {
            System.out.println(e.toString());
        } catch(IllegalArgumentException e) {
            System.out.println(e.toString());
        }
    }

    public void disconnect() throws IOException {
        socket.close();
    }

    // Returns true if the connection was successful. (still returns true if the socket is closed after a successful connection)
    public Boolean isConnected(){
        return socket.isConnected();
    }

    public Boolean isClosed() {
        return socket.isClosed();
    }
    
    public void sendRequest(String reqString) throws IOException{
        output.println(reqString);        
    }

    public String recieveResponse() throws IOException{
        String response = input.readLine();
        return response;
    }

    // public void close() {
    //     try {
    //         // input.close();
    //         // output.close();
    //         // socket.close();
    //     } catch (IOException e) {
    //         e.printStackTrace();
    //     }
    // }
}
