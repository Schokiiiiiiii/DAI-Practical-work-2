package ch.heigvd.dai.nokenet;

public class Utils {

    public static ServerAnswer extractResponse(String[] response) {
        if (response == null || response.length == 0) {
            return null;
        }

        try {
            return ServerAnswer.valueOf(response[0].toUpperCase());
        } catch (IllegalArgumentException e) {
            // valueOf throws exception
            return null;
        }
    }

    public static CommandName extractCommand(String[] message) {

        // try to get the command
        CommandName command = null;
        try {
            command = CommandName.valueOf(message[0].toUpperCase());
        } catch (IllegalArgumentException e) {
            // command is null and will be handled by switch
        }

        return command;
    }
}
