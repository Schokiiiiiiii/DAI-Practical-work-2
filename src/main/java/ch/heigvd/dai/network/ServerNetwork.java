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
            System.out.println("[Server] started on port [" + PORT + "]");

            // create sockets on arrival and launch a ServerController for it
            while (!serverSocket.isClosed()) {
                Socket clientSocket = serverSocket.accept();
                try {
                    executor.submit(new ServerController(clientSocket));
                } catch (RuntimeException e) {
                    System.out.println("[Server] Error accepting client socket: " + e);
                }
            }

        } catch (IOException e) {
            System.out.println("[Server] Error while listening on port [" + PORT + "]: " + e);
            return -1;
        }

        return 0;
    }

    /**
     * Receive a message from buffer and return the parsed version
     * @param in buffer to read from
     * @param socket socket to close in case connection got closed
     * @return table of strings representing the message parsed
     * @throws IOException reading the buffer or closing the socket could throw
     */
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

    /**
     * Send a message in buffer
     * @param out buffer to write in
     * @param answer message to write
     * @throws IOException writing in the buffer could throw
     */
    public synchronized static void send (BufferedWriter out, String answer) throws IOException {
        out.write(answer);
    }
}
