package ch.heigvd.dai.network;

import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ch.heigvd.dai.controllers.ServerControler;

public class ServerNetwork {
    private ExecutorService executor;

    public ServerNetwork(int port){
        executor = Executors.newVirtualThreadPerTaskExecutor();
    }

    public void runServer(){
        try (ServerSocket serverSocket = new ServerSocket(PORT);
             ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor(); ) {

            while (!serverSocket.isClosed()) {
                Socket clientSocket = serverSocket.accept();
                executor.submit(new ServerControler(this));
            }
        } catch (IOException e) {
            System.out.println("[Server " + SERVER_ID + "] exception: " + e);
        }
    }

    public void send(String message){

    }

    public String receive(){
        return null;
    }

    public boolean isSocketClosed(){
        return serverSocket.isClosed();
    }
}
