package ch.heigvd.dai.commands;

import java.util.concurrent.Callable;
import picocli.CommandLine;

import ch.heigvd.dai.controller.ClientController;

@CommandLine.Command(name = "client", description = "Start the client part of the network game.")
public class Client implements Callable<Integer> {

  @CommandLine.Option(
      names = {"-H", "--host"},
      description = "Host to connect to (default: ${DEFAULT-VALUE}).",
      defaultValue = "localhost")
  protected String host;

  @CommandLine.Option(
      names = {"-p", "--port"},
      description = "Port to use (default: ${DEFAULT-VALUE}).",
      defaultValue = "7270")
  protected int port;

  @Override
  public Integer call() {

      ClientController clientController = new ClientController();

      // define network

      try {
          return clientController.run(host, port);
      } catch (Exception e) {
          System.out.println("Cannot connect to server.");
          return -1;
      }
  }
}
