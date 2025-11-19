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

    public ClientNetwork(String HOST, int PORT) {
        this.HOST = HOST;
        this.PORT = PORT;
    }

    public ClientNetwork() {
        this("localhost", 7270);
    }

    public int run() {

        // connect to socket etc

        ClientInterface ui = new ClientInterface();

        try (Socket socket = new Socket(HOST, PORT);
             BufferedReader in =
                     new BufferedReader(
                             new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             BufferedWriter out =
                     new BufferedWriter(
                             new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8)); )
        {


        }
        catch (Exception e){
            e.printStackTrace();
        }



        return 0;
    }
}
