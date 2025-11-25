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

    public void displayServerAnswer(ServerAnswers command){
        switch(command){
            case OK :
                System.out.println(translator.ok());
                break;
            case HIT:
                break;
            case HEALED:
                break;
            case LOST:
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
}
