package ch.heigvd.dai.network;

import ch.heigvd.dai.controller.ServerController;
import ch.heigvd.dai.nokenet.CommandNames;
import picocli.CommandLine;

// I/O
import java.io.*;
// SOCKET
import java.net.ServerSocket;
// CONCURRENCY
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

public class ServerNetwork {

    private final int PORT;

    public ServerNetwork(int port) {
        this.PORT = port;
    }

    public int runServer(){

        // connect to server socket and start virtual thread pool
        try (ServerSocket serverSocket = new ServerSocket(PORT);
             ExecutorService executor  = Executors.newVirtualThreadPerTaskExecutor()) {

            // server started...
            System.out.println("Server started on port [" + PORT + "]");

            // create sockets on arrival and launch a ServerController for it
            while (!serverSocket.isClosed()) {
                Socket clientSocket = serverSocket.accept();
                executor.submit(new ServerController(this, clientSocket));
            }

        } catch (IOException e) {
            System.out.println("Error while listening on port [" + PORT + "]: " + e);
            return -1;
        }

        return 0;
    }

    public static void send(BufferedWriter out, String[] message, CommandNames command){

        // prepare answer
        String answer = null;

        // send answer based on command
        switch (command) {
            case USERNAME:
            case CREATE:
            case JOIN:
            case QUIT:
            case STATS:
            case ATTACK:
            case HEAL:
            default:
        }
    }

    public static String[] receive(BufferedReader in, Socket socket) throws IOException {

        // get user input
        String userInput = in.readLine();

        // if user input is null, client disconnected
        if (userInput == null) {
            socket.close();
            return null;
        }

        // parse it to extract each part
        return userInput.split(" ", 5);
    }
}
