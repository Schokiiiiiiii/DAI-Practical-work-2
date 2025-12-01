package ch.heigvd.dai.controller;

import ch.heigvd.dai.network.ClientNetwork;
import ch.heigvd.dai.applicationInterface.ClientInterface;

import ch.heigvd.dai.nokenet.CommandName;
import ch.heigvd.dai.nokenet.NokeNetTranslator;
import ch.heigvd.dai.nokenet.ServerAnswer;

import java.io.IOException;

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
    private static final int MAX_RESPONSE_PARTS = 5;  // STATS command has 5 parts: STATS player1 hp1 player2 hp2
    private static final String CONNECTION_LOST_MSG = "Server connection lost. Game ended.";

    /**
     * Update my HP with bounds checking (0 to MAX_HP)
     * @param delta the amount to change HP (negative for damage, positive for heal)
     */
    private void updateMyHp(int delta) {
        myHp = Math.max(0, Math.min(myHp + delta, MAX_HP));
    }

    /**
     * Update other player's HP with bounds checking (0 to MAX_HP)
     * @param delta the amount to change HP (negative for damage, positive for heal)
     */
    private void updateOtherHp(int delta) {
        otherPlayerHp = Math.max(0, Math.min(otherPlayerHp + delta, MAX_HP));
    }

    private void createNetwork(String host, int port) throws IOException {
        this.network = new ClientNetwork(host, port);
        this.ui = new ClientInterface();
        this.translator = new NokeNetTranslator();
    }



    public int run(String host, int port) {
        try {
            createNetwork(host, port);
        } catch (IOException e) {
            System.out.println("Cannot connect to server at " + host + ":" + port + ". Is the server running?");
            return -1;
        }

        username = ui.getUsername();
        ui.setMyUsername(username);

        try {
            network.send(translator.username(username));
        } catch (IOException e) {
            System.out.println(CONNECTION_LOST_MSG);
            network.closeNetwork();
            return -1;
        }

        // Get server's response to username
        if (!handleServerResponse()) {
            network.closeNetwork();
            return -1;
        }

        // Start lobby loop
        manageLobby();

        inGame = true;

        // "Game"/Service loop
        while (inGame) {
            if (myTurn) {
                boolean inGameAction = true;
                while (inGameAction) {

                    ui.showGameMenu();
                    String choice = ui.getUserInput("Choose an option");

                    try {
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
                    } catch (IOException e) {
                        System.out.println(CONNECTION_LOST_MSG);
                        inGame = false;
                        break;
                    }
                }

                if (!inGame) {
                    break;
                }

                // Response to our action

            } else {
                // Wait for the other player's turn
                ui.waitMessage(otherPlayerUsername);

            }
            if (!handleServerResponse()) {
                System.out.println(CONNECTION_LOST_MSG);
                inGame = false;
            }
        }

        network.closeNetwork();
        return 0;
    }

    private boolean handleServerResponse(){
        String rawServerResponse;
        try {
            rawServerResponse = network.receive();
        } catch (IOException e) {
            // Connection error while receiving
            return false;
        }

        if(rawServerResponse == null) {
            // Server closed connection
            return false;
        }

        String[] parsedResponse = parseServerResponse(rawServerResponse);
        ServerAnswer responseType = translator.extractResponse(parsedResponse);

        ui.displayServerAnswer(responseType, parsedResponse);

        switch(responseType){
            case STATS :
                try {
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
                } catch (NumberFormatException e) {
                    System.out.println("Server sent invalid HP values. Connection lost.");
                    return false;
                }
                return true;

            case HIT :
                try {
                    String hitTarget = parsedResponse[1];
                    int damage = Integer.parseInt(parsedResponse[2]);

                    if(hitTarget.equals(username)){
                        updateMyHp(-damage);

                        // We receive damage, so it's the other player who played last
                        myTurn = true;
                    } else {
                        updateOtherHp(-damage);
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
                } catch (NumberFormatException e) {
                    System.out.println("Server sent invalid damage value. Connection lost.");
                    return false;
                }
                return true;

            case HEALED :
                // HEALED <healer_name> <heal_amount>
                try {
                    String healer = parsedResponse[1];
                    int heal = Integer.parseInt(parsedResponse[2]);

                    if(username.equals(healer)){
                        updateMyHp(heal);
                        myTurn = false;
                    } else {
                        updateOtherHp(heal);
                        myTurn = true;
                    }

                    ui.displayGameStats(username, myHp, otherPlayerUsername, otherPlayerHp);
                } catch (NumberFormatException e) {
                    System.out.println("Server sent invalid heal amount. Connection lost.");
                    return false;
                }
                return true;

            case ERROR :
                // The last command sent has failed - server explicitly rejected it
                System.out.println("Server refused connection");
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
        return rawServerResponse.split(" ", MAX_RESPONSE_PARTS);
    }

    private int manageLobby(){
        boolean inLobby = true;
        while(inLobby){
            ui.showLobbyMenu();
            String choice = ui.getUserInput("Choose an option");

            try {
                switch (choice){
                    case "1" :
                        network.send(CommandName.CREATE.toString());
                        // Handle server response
                        if(handleServerResponse()){
                            System.out.println("Waiting for another player to join the game...");
                            return 0;
                        }
                        break;
                    case "2" :
                        network.send(CommandName.JOIN.toString());
                        // Server sends STATS first
                        System.out.println("\nJoining game...");
                        if(handleServerResponse()){
                            // Then server sends OK
                            handleServerResponse();
                            return 0;
                        }
                        break;
                    case "3" :
                        network.send(CommandName.QUIT.toString());
                        network.closeNetwork();
                        return 1;
                    default:
                        System.out.println("Invalid option, try again.");
                }
            } catch (IOException e) {
                System.out.println(CONNECTION_LOST_MSG);
                network.closeNetwork();
                return -1;
            }
        }
        return 0;
    }
}
