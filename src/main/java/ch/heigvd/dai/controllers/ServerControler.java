package ch.heigvd.dai.controllers;

import ch.heigvd.dai.network.ServerNetwork;

public class ServerControler extends Controller implements Runnable{

    private int nbClients;

    public ServerControler(ServerNetwork network){

    }

    public void run(){

        System.out.println("Received a client connection");

        while(true){



        }

    }
}
