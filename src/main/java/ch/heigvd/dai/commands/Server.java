package ch.heigvd.dai.commands;

import java.util.concurrent.Callable;

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

      ServerNetwork network = new ServerNetwork(port);

      network.runServer();

      return 1;
  }
}
