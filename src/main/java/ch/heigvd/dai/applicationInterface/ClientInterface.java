package ch.heigvd.dai.applicationInterface;

import ch.heigvd.dai.controller.*;
import ch.heigvd.dai.nokenet.CommandNames;
import ch.heigvd.dai.nokenet.NokeNetTranslator;
import ch.heigvd.dai.nokenet.ServerAnswers;

import java.util.Scanner;

public class ClientInterface {

    private Controller controller;
    private NokeNetTranslator translator;

    public ClientInterface() {
        controller = new ClientController();
        translator = new NokeNetTranslator();
    }

    public void showInterface() {
        System.out.println("Menu");
    }

    public String getUserInput(String inputMessage) {
        System.out.print(inputMessage + " > ");
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }

    public String getUsername(){
        return getUserInput("Enter your username");
    }

    public void displayServerAnswer(ServerAnswers command, String[] commandArgs){
        switch(command){
            case OK :
                System.out.println(translator.ok());
                break;
            case ERROR:
                // The protocole always defines the error code as the second argument
                int errorCode = Integer.parseInt(commandArgs[1]);
                displayError(errorCode);
            case HIT:
                // HIT <usename> <damage received>
                System.out.println(commandArgs[1] + " was hit for " + commandArgs[2] + " damage");
                break;
            case HEALED:
                // HEALED <usename> <health received>
                System.out.println(commandArgs[1] + " healed for " + commandArgs[2] + " HP");
                break;
            case LOST:
                System.out.println("lost");
                break;
            case STATS:
                // STATICS <player1> <hp1> <player2> <hp2>
                String player1 = commandArgs[1];
                int hp1 = Integer.parseInt(commandArgs[2]);
                String player2 = commandArgs[3];
                int hp2 = Integer.parseInt(commandArgs[4]);

                // Diplay game upadte in a little box for better readability in high-tension games.
                System.out.println("====Game Update====");
                System.out.println(player1 + " : " + hp1 + " HP");
                System.out.println(player2 + " : " + hp2 + " HP");
                System.out.println("==================");
                break;
        }
    }

    private void displayError(int errorCode) {
        System.out.println("ERROR: " + switch(errorCode) {
            case 10 -> "Command doesn't exist";
            case 210 -> "Username already taken";
            case 310 -> "Lobby already exists";
            case 320 -> "No lobby to join";
            case 321 -> "Lobby is full";
            case 410 -> "Can't do this command now";
            default -> "Unknown error (" + errorCode + ")";
        });
    }

    public void showLobbyMenu() {
        System.out.println("\n=== Lobby Menu ===");
        System.out.println("1. CREATE - Create a new game");
        System.out.println("2. JOIN - Join existing game");
        System.out.println("3. QUIT - Exit");
    }

    public void showGameMenu() {
        System.out.println("\n=== Your Turn ===");
        System.out.println("1. ATTACK - Attack opponent");
        System.out.println("2. HEAL - Heal your Nokemon");
    }
}
