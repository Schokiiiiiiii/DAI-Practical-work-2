package ch.heigvd.dai.network;

import ch.heigvd.dai.applicationInterface.ClientInterface;

public class ClientNetwork {

    public int run() {

        // connect to socket etc

        ClientInterface clientInterface = new ClientInterface();

        clientInterface.showInterface();
        clientInterface.getUserInput();

        return 0;
    }
}
