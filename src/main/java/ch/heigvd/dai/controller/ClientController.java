package ch.heigvd.dai.controller;

import ch.heigvd.dai.network.ClientNetwork;
import ch.heigvd.dai.applicationInterface.ClientInterface;

import ch.heigvd.dai.game.*;

public class ClientController extends Controller{
    private ClientNetwork network;
    private ClientInterface ui;

    private void createNetwork() {
        network = new ClientNetwork();
    }

    public int run(String host, int port){
        createNetwork();

        // "Game"/Service loop
        while(true){

            if(true) break;
        }

        return 0;
    }

    // Check the status sent by the server
    private boolean checkStatus(String status){
        return true;
    }
}
