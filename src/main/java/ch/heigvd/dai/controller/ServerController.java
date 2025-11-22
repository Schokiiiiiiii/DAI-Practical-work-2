package ch.heigvd.dai.controller;

import ch.heigvd.dai.network.ServerNetwork;
import ch.heigvd.dai.applicationInterface.ServerInterface;
import ch.heigvd.dai.game.*;
import ch.heigvd.dai.nokenet.CommandNames;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ServerController extends Controller implements Runnable{

    private final Socket socket;

    public ServerController(ServerNetwork serverNetwork, Socket socket) {
        this.socket = socket;
    }

    public void run(){

        // confirm controller is running
        System.out.println("Controller connected to client");

        // open buffers from socket
        try (socket;
             BufferedReader in  = new BufferedReader(
                     new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             BufferedWriter out = new BufferedWriter(
                     new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8))) {

            // keep answering until socket is closed
            while (!socket.isClosed()) {

                // read a message from the client
                String[] message = ServerNetwork.receive(in, socket);

                // if message is null, socket got closed
                if (message == null)
                    continue;

                // try to read command from it
                CommandNames command = ServerInterface.extractCommand(message);

                // send command
                // TODO separate reading of the command and sending command to client
                ServerNetwork.send(out, message, command);
            }

        } catch (IOException e) {
            System.out.println("Error with Input/Output stream from socket: " + e);
        }
    }
}
