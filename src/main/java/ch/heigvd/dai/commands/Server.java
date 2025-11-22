package ch.heigvd.dai.commands;

import java.util.concurrent.Callable;

import ch.heigvd.dai.network.ClientNetwork;
import ch.heigvd.dai.network.ServerNetwork;

import ch.heigvd.dai.controller.ServerController;
import ch.heigvd.dai.network.ServerNetwork;
import picocli.CommandLine;

@CommandLine.Command(name = "server", description = "Start the server part of the network game.")
public class Server implements Callable<Integer> {

    @CommandLine.Option(
        names = {"-p", "--port"},
        description = "Port to use (default: ${DEFAULT-VALUE}).",
        defaultValue = "7270")
    protected int port;

    @Override
    public Integer call() {

        // create an instance of our server network
        ServerNetwork serverNetwork = new ServerNetwork(port);

        // run the server network and return the status
        return serverNetwork.runServer();
    }
}
