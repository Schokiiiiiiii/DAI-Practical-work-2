package ch.heigvd.dai.network;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ClientNetwork {

    private final Socket socket;
    private final BufferedReader in;
    private final BufferedWriter out;

    public ClientNetwork(String host, int port) throws IOException {

        // Try to connect to server, throw exception if it fails,
        // should be managed by the controller
        socket = new Socket(host, port);
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

    public boolean hasDataAvailable() throws IOException {
        return in.ready();
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
