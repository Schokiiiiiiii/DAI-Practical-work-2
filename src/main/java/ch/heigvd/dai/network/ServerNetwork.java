package ch.heigvd.dai.network;

import ch.heigvd.dai.controller.ServerController;

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
    private static int nbThreads = 0;

    private static final char END_OF_FILE = '\n';

    public ServerNetwork(int port) {
        this.PORT = port;
    }

    /**
     * Main run for the server accepting requests
     * @return 0 if it went well, -1 if an issue happened
     */
    public int runServer(){

        // connect to server socket and start virtual thread pool
        try (ServerSocket serverSocket = new ServerSocket(PORT);
             ExecutorService executor  = Executors.newVirtualThreadPerTaskExecutor()) {

            // server started...
            System.out.println("[Server] Started on port " + PORT);

            // create sockets on arrival and launch a ServerController for it
            while (!serverSocket.isClosed()) {

                // accept a new client
                Socket clientSocket = serverSocket.accept();

                // print log message
                System.out.println("[Server] Accepted a new client");

                // try to create a new thread
                try {
                    executor.submit(new ServerController(clientSocket));
                    ++nbThreads;

                    // print log message
                    System.out.println("[Server] Thread successfully launched");
                    System.out.println("[Server] Current number of threads: " + nbThreads);
                } catch (RuntimeException e) {
                    System.out.println("[Server] Error accepting client socket: " + e);
                }
            }

        } catch (IOException e) {
            System.out.println("[Server] Error while listening on port " + PORT + ": " + e);
            return -1;
        }

        return 0;
    }

    /**
     * Decreases the counter of threads by 1
     */
    public static void decreaseNbThreads() {

        if (nbThreads < 0)
            throw new IllegalArgumentException("[Server] nbThreads can't be negative");

        --nbThreads;
    }

    /**
     * Receive a message from buffer and return the parsed version
     * @param in buffer to read from
     * @param socket socket to close in case connection got closed
     * @return table of strings representing the message parsed
     */
    public static String[] receive(BufferedReader in, Socket socket) {

        String[] message = null;

        try {

            // get user input
            String userInput = in.readLine();

            // if user input is null, client disconnected
            if (userInput == null) {
                socket.close();
                return null;
            }

            message = userInput.split(" ", 5);

        } catch (IOException e) {
            System.out.println("[Server] Error while receiving client socket: " + e);
            return null;
        }

        // parse it to extract each part
        return message;
    }

    /**
     * Send a message in buffer
     * @param out buffer to write in
     * @param answer message to write
     * @implNote synchronized because sending result of a P1 action to P2
     *           and answering to a command from P2 at the same time could interfere each other
     */
    public synchronized static int  send (BufferedWriter out, String answer) {

        try {
            out.write(answer + END_OF_FILE);
            out.flush();
        } catch (IOException e) {
            System.out.println("[Server] Error could not send data to client: " + e);
            return -1;
        }

        return 0;
    }
}
