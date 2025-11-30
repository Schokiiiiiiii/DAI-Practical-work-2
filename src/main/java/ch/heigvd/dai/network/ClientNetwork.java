package ch.heigvd.dai.network;

import ch.heigvd.dai.controller.ClientController;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ClientNetwork {

    private final String HOST;
    private final int PORT;

    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;

    private ClientController controller;

    public ClientNetwork(String HOST, int PORT) throws IOException {
        controller = new ClientController();

        this.HOST = HOST;
        this.PORT = PORT;

        // Try to connect to server, throw exception if it fails,
        // should be managed by the controller
        socket = new Socket(HOST, PORT);
        in = new BufferedReader(
                new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        out = new BufferedWriter(
                new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
    }

    public ClientNetwork() throws IOException {
        this("localhost", 7270);
    }

    public boolean isConnected() {
        return socket != null && !socket.isClosed() && socket.isConnected();
    }

    public void send(String message) throws IOException {
        if (!isConnected()) {
            throw new IOException("Not connected to server");
        }
        out.write(message);
        out.newLine();
        out.flush();
    }

    public String receive() throws IOException {
        if (!isConnected()) {
            throw new IOException("Not connected to server");
        }
        return in.readLine();
    }

    public void closeNetwork(){
        try{
            in.close();
            out.close();
            socket.close();
        }catch(Exception ignored){
            // Ignored
        }
    }
}
