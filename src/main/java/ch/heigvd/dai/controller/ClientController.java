package ch.heigvd.dai.controller;

import ch.heigvd.dai.network.ClientNetwork;
import ch.heigvd.dai.applicationInterface.ClientInterface;

import ch.heigvd.dai.game.*;
import ch.heigvd.dai.nokenet.CommandNames;
import ch.heigvd.dai.nokenet.NokeNetTranslator;
import ch.heigvd.dai.nokenet.ServerAnswers;

public class ClientController extends Controller{
    private ClientNetwork network;
    private ClientInterface ui;
    private NokeNetTranslator translator;

    private boolean inGame = false;
    private boolean myTurn = false;
    private String username;

    private void createNetwork() {
        this.network = new ClientNetwork();
        this.ui = new ClientInterface();
        this.translator = new NokeNetTranslator();
    }

    public int run(String host, int port){
        createNetwork();

        ui.showInterface();
        network.send(translator.username(ui.getUsername()));

        // Get server's response to username
        if(!handleServerResponse()) {
            System.out.println("Server refused connection");
            network.closeNetwork();
            return -1;
        }

        // Start lobby loop
        manageLobby();

        inGame = true;

        // "Game"/Service loop
        while(inGame){
            if(myTurn){
                boolean inGameAction = true;
                while(inGameAction) {

                    ui.showGameMenu();
                    String choice = ui.getUserInput("Choose an option");

                    switch (choice) {
                        case "1":
                            network.send(translator.attack());
                            inGameAction = false;
                            break;
                        case "2":
                            network.send(translator.heal());
                            inGameAction = false;
                            break;
                        default:
                            System.out.println("Invalid option, try again.");
                            break;
                    }
                }
            }
        }

        return 0;
    }

    private boolean handleServerResponse(){
        String rawServerResponse = network.receive();
        if(rawServerResponse == null) {
            System.out.println("Connection lost");
            return false;
        }

        String[] parsedResponse = parseServerResponse(rawServerResponse);
        ServerAnswers responseType = translator.extractResponse(parsedResponse);

        ui.displayServerAnswer(responseType, parsedResponse);

        switch(responseType){
            case STATS :
                if(username == parsedResponse[1]){
                    myTurn = true;
                }
                return true;
            case ERROR :
                // The last command sent has failed
                return false;
            case LOST :
                // Game is lost
                return true;
        }
        return true;
    }

    private String[] parseServerResponse(String rawServerResponse){
        // The biggest command is Stats with 5 arguments in total
        return rawServerResponse.split(" ", 5);
    }

    private int manageLobby(){
        boolean inLobby = true;
        while(inLobby){
            ui.showLobbyMenu();
            String choice = ui.getUserInput("Choose an option");

            switch (choice){
                case "1" :
                    network.send(CommandNames.CREATE.toString());
                    // Hadnle server response
                    if(handleServerResponse()){
                        System.out.printf("Waiting for another player to join the game...");
                        return 0;
                    }
                case "2" :
                    network.send(CommandNames.JOIN.toString());
                    if(handleServerResponse()){
                        System.out.println("Joining game...");
                        return 0;
                    }
                case "3" :
                    network.send(CommandNames.QUIT.toString());
                    network.closeNetwork();
                    return 1;
                default:
                    System.out.println("Invalid option, try again.");
            }
        }
        return 0;
    }
}
