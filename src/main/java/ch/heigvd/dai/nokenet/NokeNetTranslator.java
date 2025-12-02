package ch.heigvd.dai.nokenet;

/**
 * Contains all the methods to translate all the NokeNET commands into
 * readable strings.
 */
public class NokeNetTranslator {

    public ServerAnswer extractResponse(String[] response) {
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
}
