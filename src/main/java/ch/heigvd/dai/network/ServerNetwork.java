package ch.heigvd.dai.network;

import java.io.*;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import java.net.Socket;
import java.nio.charset.StandardCharsets;

import ch.heigvd.dai.controller.ServerController;

public class ServerNetwork {

    private ExecutorService executor;
    private final int PORT;

    private ServerSocket serverSocket;
    private BufferedReader in;
    private BufferedWriter out;

    public ServerNetwork(int port) {
        executor = Executors.newVirtualThreadPerTaskExecutor();
        this.PORT = port;
    }

    public ServerNetwork() {
        this(7270);
    }

    public void runServer(){
        try{
            this.serverSocket = new ServerSocket(PORT);

            // Creates a thread for each client that connects
            while (!serverSocket.isClosed()) {
                Socket clientSocket = serverSocket.accept();
                executor.submit(new ServerController(this));
            }
        } catch (IOException e) {
            System.out.println("exception: " + e);
        }
    }

    public void send(String massage){

    }

    public String receive(){
        return null;
    }

    public boolean isSocketClosed(){
         return true;
    }

    public void closeNetwork(){
        try{
            serverSocket.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
