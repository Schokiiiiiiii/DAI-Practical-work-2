package ch.heigvd.dai.network;

import ch.heigvd.dai.applicationInterface.ClientInterface;

import java.io.BufferedReader;
import java.io.BufferedWriter;
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

    public ClientNetwork(String HOST, int PORT) {
        this.HOST = HOST;
        this.PORT = PORT;

        try{
            socket = new Socket(HOST, PORT);
            in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            out = new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
        }catch(Exception e){

        }
    }

    public ClientNetwork() {
        this("localhost", 7270);
    }

    public int run() {

        // connect to socket etc

        ClientInterface ui = new ClientInterface();

        return 0;
    }
}
