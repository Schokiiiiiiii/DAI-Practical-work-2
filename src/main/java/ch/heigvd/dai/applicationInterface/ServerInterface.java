package ch.heigvd.dai.applicationInterface;

import ch.heigvd.dai.nokenet.CommandNames;

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
}
