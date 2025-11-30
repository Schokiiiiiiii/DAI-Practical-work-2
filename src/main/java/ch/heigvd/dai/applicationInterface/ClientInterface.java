package ch.heigvd.dai.applicationInterface;

import ch.heigvd.dai.controller.*;
import ch.heigvd.dai.nokenet.NokeNetTranslator;
import ch.heigvd.dai.nokenet.ServerAnswers;

import java.util.Scanner;

public class ClientInterface extends AnsiColors {

    private Controller controller;
    private NokeNetTranslator translator;
    private String myUsername;

    public ClientInterface() {
        controller = new ClientController();
        translator = new NokeNetTranslator();
    }

    public void setMyUsername(String username) {
        this.myUsername = username;
    }

    public String getUserInput(String inputMessage) {
        System.out.print(inputMessage + " > ");
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }

    public String getUsername(){
        return getUserInput( BOLD + "Enter your username" + RESET);
    }

    public void displayServerAnswer(ServerAnswers command, String[] commandArgs){
        switch(command){
            case ERROR:
                // The protocole always defines the error code as the second argument
                int errorCode = Integer.parseInt(commandArgs[1]);
                displayError(errorCode);
                break;
            case HIT:
                // HIT <usename> <damage received>
                System.out.println(commandArgs[1] + " was hit for " + BRIGHT_RED_F + BOLD + commandArgs[2]
                        + " damage" + RESET);
                break;
            case HEALED:
                // HEALED <usename> <health received>
                System.out.println(commandArgs[1] + " healed for " + BRIGHT_GREEN_F + BOLD + commandArgs[2]
                        + " HP" + RESET);
                break;
            case LOST:
                // LOST <username_who_lost>
                if (commandArgs.length > 1) {
                    String loserUsername = commandArgs[1];
                    if (loserUsername.equals(myUsername)) {
                        // I lost
                        printLost();
                    } else {
                        // Opponent lost, I won
                        printWon();
                    }
                }
                break;
           /* case STATS:
                // STATICS <player1> <hp1> <player2> <hp2>
                String player1 = commandArgs[1];
                int hp1 = Integer.parseInt(commandArgs[2]);
                String player2 = commandArgs[3];
                int hp2 = Integer.parseInt(commandArgs[4]);

                // Diplay game upadte in a little box for better readability in high-tension games.
                System.out.println("====PLAYER STATS====");
                System.out.println(player1 + " : " + hp1 + " HP");
                System.out.println(player2 + " : " + hp2 + " HP");
                System.out.println("====================");
                break;*/
        }
    }

    private void displayError(int errorCode) {
        System.out.println(BRIGHT_RED_B + BLACK_F + "ERROR: " + switch(errorCode) {
            case 10 -> "Command doesn't exist";
            case 210 -> "Username already taken";
            case 310 -> "Game already exists";
            case 320 -> "No game to join";
            case 321 -> "Lobby is full";
            case 410 -> "Can't do this command now";
            default -> "Unknown error (" + errorCode + ")";
        } + RESET);
    }

    public void showLobbyMenu() {
        System.out.println(BRIGHT_GREEN_F + "\n=== Lobby Menu ===" + RESET);
        System.out.println(DARK_YELLOW_F + "1" + RESET + ". CREATE - Create a new game");
        System.out.println(DARK_YELLOW_F + "2" + RESET + ". JOIN - Join existing game");
        System.out.println(DARK_YELLOW_F + "3" + RESET + ". QUIT - Exit");
    }

    public void showGameMenu() {
        System.out.println(DARK_GREEN_B + BLACK_F + BOLD + "    Your Turn    " + RESET);
        System.out.println(DARK_YELLOW_F + "1" + RESET + ". " + BRIGHT_CYAN_F
                +  "ATTACK" + RESET + " - Attack opponent");
        System.out.println(DARK_YELLOW_F + "2" + RESET + ". " + BRIGHT_GREEN_F + "HEAL" + RESET + " - Heal your Nokemon");
    }

    public void displayGameStats(String username, int myHp, String otherPlayerUsername, int otherPlayerHp) {
        if(username == null || otherPlayerUsername == null) return;
        System.out.println();
        System.out.println(BRIGHT_CYAN_F + BOLD + "====Current Game Status====" + RESET);
        System.out.println(BOLD + username + " (You): " + RESET + hpStr(myHp));
        System.out.println(otherPlayerUsername + ": " + hpStr(otherPlayerHp));
        System.out.println(BRIGHT_CYAN_F + BOLD + "===========================" + RESET);
        System.out.println();
    }

    public void waitMessage(String userName){
        if(userName == null) return;
        System.out.println(BRIGHT_MAGENTA_F + "Waiting for " + userName + "'s turn..." + RESET);
    }

    private String hpStr(int hp){
        // Don't prinr below 0 HP
        hp = Math.max(0, hp);
        if(hp < 20){
            return BRIGHT_RED_F + hp + " HP" + RESET;
        } else if(hp < 40) {
            return DARK_YELLOW_F + hp + " HP" + RESET;
        } else {
            return BRIGHT_GREEN_F + hp + " HP" + RESET;
        }
    }

    public void printLost(){
        System.out.println(BRIGHT_RED_F + "=====================================");
        System.out.println("   YOU LOST!   ");
        System.out.println("=====================================" + RESET);
    }

    public void printWon(){
        System.out.println(BRIGHT_GREEN_F + "=====================================");
        System.out.println("   YOU WON!   ");
        System.out.println("=====================================" + RESET);
    }
}
