package ch.heigvd.dai.nokenet;

/**
 * Contains all the methods to translate all the NokeNET commands into
 * readable strings.
 */
public class NokeNetTranslator {

    public String username(String username){
        return CommandNames.USERNAME + username;
    }

    public CommandNames username(){
        return CommandNames.USERNAME;
    }

    public String ok(){
        return "OK";
    }

    public String create(){
        return "CREATE";
    }

    public String join(){
        return "JOIN";
    }

    public String quit(){
        return "QUIT";
    }

    public String attack(){
        return CommandNames.ATTACK.toString();
    }

    public String heal(){
        return CommandNames.HEAL.toString();
    }

    public ServerAnswers extractResponse(String[] response) {
        if (response == null || response.length == 0) {
            return null;
        }

        try {
            return ServerAnswers.valueOf(response[0].toUpperCase());
        } catch (IllegalArgumentException e) {
            // valueOf throws exception
            return null;
        }
    }


        // ...
}
