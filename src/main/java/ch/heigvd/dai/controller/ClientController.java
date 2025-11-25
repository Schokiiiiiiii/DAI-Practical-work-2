package ch.heigvd.dai.controller;

import ch.heigvd.dai.network.ClientNetwork;
import ch.heigvd.dai.applicationInterface.ClientInterface;

import ch.heigvd.dai.game.*;
import ch.heigvd.dai.nokenet.NokeNetTranslator;

public class ClientController extends Controller{
    private ClientNetwork network;
    private ClientInterface ui;
    private NokeNetTranslator translator;

    private void createNetwork() {
        this.network = new ClientNetwork();
        this.ui = new ClientInterface();
        this.translator = new NokeNetTranslator();
    }

    public int run(String host, int port){
        createNetwork();

        ui.showInterface();
        network.send(translator.username(ui.getUsername()));


        // "Game"/Service loop
        while(true){


            break;
        }

        return 0;
    }

    // Check the status sent by the server
    private boolean checkStatus(String status){
        return true;
    }
}
