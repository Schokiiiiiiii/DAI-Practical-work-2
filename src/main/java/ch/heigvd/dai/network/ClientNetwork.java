package ch.heigvd.dai.network;

import ch.heigvd.dai.applicationInterface.ClientInterface;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import ch.heigvd.dai.controllers.ClientController;

public class ClientNetwork {

    private final String HOST;
    private final int PORT;

    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;

    private ClientController controller;

    public ClientNetwork(String HOST, int PORT) {
        controller = new ClientController();

        this.HOST = HOST;
        this.PORT = PORT;

        try{
            socket = new Socket(HOST, PORT);
            in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            out = new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public ClientNetwork() {
        this("localhost", 7270);
    }

    public void send(String message){
        try{
            out.write(message);
            out.newLine();
            out.flush();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public String receive(){
        try{
            return in.readLine();
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public void closeNetwork(){
        try{
            socket.close();
            in.close();
            out.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

 /*   public int run() {

        // connect to socket etc

        ClientInterface ui = new ClientInterface();

        return 0;
    }*/
}
