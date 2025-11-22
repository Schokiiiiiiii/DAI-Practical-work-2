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

    // ...
}
