package ch.heigvd.dai.network;

import ch.heigvd.dai.applicationInterface.ClientInterface;
import ch.heigvd.dai.utils.CommandNames;

// I/O
import java.io.*;
// SOCKET
import java.net.ServerSocket;
// CONCURRENCY
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
// UTILS
import java.nio.charset.StandardCharsets;

public class ServerNetwork {

    private final int PORT;

    public ServerNetwork(int port) {
        PORT = port;
    }

    public int run() {

        // connect to server socket and start virtual thread pool
        try (ServerSocket serverSocket = new ServerSocket(PORT);
             ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {

            System.out.println("Server started on port [" + PORT + "]");

            while (!serverSocket.isClosed()) {
                Socket clientSocket = serverSocket.accept();
                executor.submit(new ClientHandler(clientSocket));
            }

        } catch (IOException e) {
            System.out.println("Error while listening on port [" + PORT + "]: " + e);
            return -1;
        }

        ClientInterface clientInterface = new ClientInterface();

        clientInterface.showInterface();
        clientInterface.getUserInput();

        return 0;
    }

    static class ClientHandler implements Runnable {

        private final Socket socket;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {

            try (socket;
                 BufferedReader in  = new BufferedReader(
                                      new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                 BufferedWriter out = new BufferedWriter(
                                      new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8))) {

                System.out.println("Client connected " + socket.getInetAddress().getHostName() + ":" + socket.getPort());

                while (!socket.isClosed()) {

                    // get user input
                    String userInput = in.readLine();

                    // if user input is null, client disconnected
                    if (userInput == null) {
                        socket.close();
                        continue;
                    }

                    // parse it to extract each part
                    String[] message = userInput.split(" ", 5);

                    // try to get the command
                    CommandNames command = null;
                    try {
                        command = CommandNames.valueOf(message[0].toUpperCase());
                    } catch (IllegalArgumentException e) {
                        // command is null and will be handled by switch
                    }

                    // prepare answer
                    String answer = null;

                    switch (command) {
                        case USERNAME
                        case CREATE
                        case JOIN
                        case QUIT
                        case STATS
                        case ATTACK
                        case HEAL
                    }
                }

            } catch (IOException e) {
                System.out.println("Error with Input/Output stream from socket: " + e);
            }
        }
    }
}
