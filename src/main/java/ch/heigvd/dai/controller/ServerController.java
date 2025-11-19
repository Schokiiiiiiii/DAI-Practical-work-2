package ch.heigvd.dai.controller;

import ch.heigvd.dai.network.ServerNetwork;

public class ServerController extends Controller implements Runnable{
    private int nbClients;
    private ServerNetwork network;

    public ServerController(ServerNetwork network) {
        this.network = network;
    }

    public void run(){
        System.out.println("Received a connection");

        // Service/Game loop
        while(true){
            if(true) break;
        }


    }

}
