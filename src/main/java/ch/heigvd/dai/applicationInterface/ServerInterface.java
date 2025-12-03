package ch.heigvd.dai.applicationInterface;

import ch.heigvd.dai.nokenet.ErrorCode;

import java.util.ArrayList;

public class ServerInterface {

    public static void printPlayers(int id, ArrayList<String> players) {
        System.out.println("[Controller#" + id + "] Current list of users " + players);
    }

    public static void printError(int id, ErrorCode error) {
        System.out.println("[Controller#" + id + "] Raised error " + error);
    }

}
