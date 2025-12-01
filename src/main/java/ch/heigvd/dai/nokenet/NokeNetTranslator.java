package ch.heigvd.dai.nokenet;

/**
 * Contains all the methods to translate all the NokeNET commands into
 * readable strings.
 */
public class NokeNetTranslator {

    public String username(String username){
        return CommandName.USERNAME + " " + username;
    }

    public CommandName username(){
        return CommandName.USERNAME;
    }

    public String ok(){
        return ServerAnswer.OK.toString();
    }

    public String create(){
        return CommandName.CREATE.toString();
    }

    public String join(){
        return CommandName.JOIN.toString();
    }

    public String quit(){
        return CommandName.QUIT.toString();
    }

    public String attack(){
        return CommandName.ATTACK.toString();
    }

    public String heal(){
        return CommandName.HEAL.toString();
    }

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


        // ...
}
