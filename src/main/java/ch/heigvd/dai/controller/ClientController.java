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

    private String otherPlayerUsername;
    private int myHp;
    private int otherPlayerHp;
    private static final int MAX_HP = 80;

    private void createNetwork() {
        this.network = new ClientNetwork();
        this.ui = new ClientInterface();
        this.translator = new NokeNetTranslator();
    }



    public int run(String host, int port){
        createNetwork();

        username = ui.getUsername();
        ui.setMyUsername(username);
        network.send(translator.username(username));

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
                // Response to our action
                if(!handleServerResponse()){
                    inGame = false;
                }

            } else {
                // Wait for the other player's turn
                ui.waitMessage(otherPlayerUsername);

                if(!handleServerResponse()){
                    inGame = false;
                }
            }
        }

        network.closeNetwork();
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
                String player1Name = parsedResponse[1];
                int player1Hp = Integer.parseInt(parsedResponse[2]);
                String player2Name = parsedResponse[3];
                int player2Hp = Integer.parseInt(parsedResponse[4]);

                if(username.equals(player1Name)){
                    myHp = player1Hp;
                    otherPlayerHp = player2Hp;
                    otherPlayerUsername = player2Name;
                    myTurn = false;
                } else {
                    myHp = player2Hp;
                    otherPlayerHp = player1Hp;
                    otherPlayerUsername = player1Name;
                    myTurn = true;
                }

                // Display initial game stats after both players have joined the game
                ui.displayGameStats(username, myHp, otherPlayerUsername, otherPlayerHp);
                return true;

            case HIT :
                String hitTarget = parsedResponse[1];
                int damage = Integer.parseInt(parsedResponse[2]);

                if(hitTarget.equals(username)){
                    myHp -= damage;

                    // We receive damage, so it's the other player who played last
                    myTurn = true;
                } else {
                    otherPlayerHp -= damage;
                    myTurn = false;
                }

                ui.displayGameStats(username, myHp, otherPlayerUsername, otherPlayerHp);

                // Check for game end
                if(myHp <= 0) {
                    ui.printLost();
                    inGame = false;
                } else if(otherPlayerHp <= 0) {
                    ui.printWon();
                    inGame = false;
                }
                return true;

            case HEALED :
                // HEALED <healer_name> <heal_amount>
                String healer = parsedResponse[1];
                int heal = Integer.parseInt(parsedResponse[2]);

                if(username.equals(healer)){
                    // Not allows exceeding max HP
                    myHp = Math.min(myHp + heal, MAX_HP);
                    myTurn = false;
                } else {
                    otherPlayerHp = Math.min(otherPlayerHp + heal, MAX_HP);
                    myTurn = true;
                }

                ui.displayGameStats(username, myHp, otherPlayerUsername, otherPlayerHp);
                return true;

            case ERROR :
                // The last command sent has failed
                return false;

            case LOST :
                // LOST <username_who_lost>
                inGame = false;
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
                        System.out.println("Waiting for another player to join the game...");
                        return 0;
                    }
                    break;
                case "2" :
                    network.send(CommandNames.JOIN.toString());
                    // Server sends STATS first
                    System.out.println("\nJoining game...");
                    if(handleServerResponse()){
                        // Then server sends OK
                        handleServerResponse();
                        return 0;
                    }
                    break;
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
