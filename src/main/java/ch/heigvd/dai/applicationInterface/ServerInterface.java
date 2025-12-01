package ch.heigvd.dai.applicationInterface;

import ch.heigvd.dai.nokenet.CommandNames;
import ch.heigvd.dai.nokenet.ErrorCode;

import java.util.ArrayList;

public class ServerInterface {

    public static CommandNames extractCommand(String[] message) {

        // try to get the command
        CommandNames command = null;
        try {
            command = CommandNames.valueOf(message[0].toUpperCase());
        } catch (IllegalArgumentException e) {
            // command is null and will be handled by switch
        }

        return command;
    }

    public static void printPlayers(int id, ArrayList<String> players) {
        System.out.println("[Controller#" + id + "] Current list of users " + players);
    }

    public static void printError(int id, ErrorCode error) {
        System.out.println("[Controller#" + id + "] Raised error " + error);
    }

}
