package ch.heigvd.dai.controllers;

import ch.heigvd.dai.applicationInterface.ClientInterface;
import ch.heigvd.dai.network.ClientNetwork;

public class ClientController extends Controller{

    private ClientNetwork network;
    private ClientInterface ui;

    public ClientController(host, port) {
        socket

    }

    private void createNetwork(String host, int port){
        network = new ClientNetwork();
    }

    public int run(String host, int port){
        createNetwork(host, port);


        // ...

        String username = ui.getUserInput();
        network.send(noke.sendUsername(username));
        String status = network.receive();

        // Game loop
        while(true){


        }


        return 0;
    }

    private int checkStatus(String status){
        if(status.equals(noke.ok())){
            return 0;
        }
        return 1;
    }
}
