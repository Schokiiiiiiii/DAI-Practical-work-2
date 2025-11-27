package ch.heigvd.dai.controller;

import ch.heigvd.dai.network.ServerNetwork;
import ch.heigvd.dai.applicationInterface.ServerInterface;
import ch.heigvd.dai.game.*;
import ch.heigvd.dai.nokenet.CommandNames;
import ch.heigvd.dai.nokenet.ErrorCode;
import ch.heigvd.dai.nokenet.ServerAnswers;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Random;

public class ServerController extends Controller implements Runnable{

    // *** NETWORK ***
    private final Socket socket;
    private final BufferedReader in;
    private final BufferedWriter out;
    // ***************

    // **** USER *****
    private String username = null;
    private Nokemon nokemon = null;
    private int wins        = 0;
    // ***************

    // *** SERVER ****
    private static final Game game                  = new Game();
    private static final ArrayList<String> players  = new ArrayList<>();
    private static final Random random              = new Random();
    // ***************

    // * CONSTRUCTOR *
    public ServerController(Socket socket) {
        this.socket = socket;
        try {
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
        } catch (IOException e) {
            System.out.println("[Controller] Error creating streams: " + e);
            throw new RuntimeException("[Controller] Could not create the controller for the socket");
        }
    }
    // ***************

    // ***** GET *****
    public String getUsername() {
        return username;
    }
    public int getNokemonHp() { return nokemon.getHp(); }
    public int getNokemonMaxHp()  { return nokemon.getMaxHp(); }
    // ***************

    // ***** SET *****
    public void setNokemonHp(int hp) { nokemon.setHp(hp); }
    public void addWin() { ++wins; }
    // ***************

    // * NOKEMON SET *
    /**
     * Player's Nokemon receive damage based on parameter
     * @param damage int points of damage received
     */
    public void receiveDamage(int damage) {
        if (nokemon != null)
            nokemon.setHp(Math.max(nokemon.getHp() - damage, 0));
    }

    /**
     * Player's Nokemon receive damage based on parameter
     * @param heal int points of heal received
     */
    public void receiveHeal(int heal) {
        if (nokemon != null)
            nokemon.setHp(Math.min(nokemon.getHp() + heal, nokemon.getMaxHp()));
    }
    // ***************

    // ***** RUN *****
    /**
     * Main run loop for a socket connected to a client
     */
    @Override
    public void run(){

        // confirm controller is running
        System.out.println("[Controller] connected to client");

        // try-with-resources
        try (socket;
             in;
             out) {

            // keep answering until socket is closed
            while (!socket.isClosed()) {

                // read a message from the client
                String[] message = ServerNetwork.receive(in, socket);

                // if message is null, socket got closed
                if (message == null)
                    continue;

                // handle message
                handleMessage(message);
            }
        } catch (IOException e) {
            System.out.println("[Controller] Error reading/writing from socket: " + e);
        }

        // remove the player at the end
        players.remove(username);
    }
    // ***************

    // *** METHODS ***
    /**
     * Chose the correct answer based on the message received from the client
     * @param message table of strings representing the message parsed
     * @throws IOException can throw if message can't be sent
     */
    public void handleMessage(String[] message) throws IOException {

        // try to read command from message
        CommandNames command = ServerInterface.extractCommand(message);

        // switch over the possible commands
        String answer = switch (command) {
            case USERNAME   -> setName(message[1]);
            case CREATE     -> createGame();
            case JOIN       -> joinGame();
            case ATTACK     -> attack();
            case HEAL       -> heal();
            default         -> ServerAnswers.ERROR + " " + ErrorCode.NOT_COMMAND;
        };

        // send answer
        ServerNetwork.send(out, answer);
    }

    /**
     * Sets the username for that player
     * @param name string the player wants as a name
     * @return string representing the answer
     * @implNote synchronized because players is shared data
     */
    private synchronized String setName(String name) {

        if (username == null) {
            return ServerAnswers.ERROR + " " + ErrorCode.NOT_NOW;
        } else if (players.contains(name)) {
            return ServerAnswers.ERROR + " " + ErrorCode.USERNAME_TAKEN;
        }

        players.add(name);
        return ServerAnswers.OK.toString();
    }

    /**
     * Sets the player as player1 in the game
     * @return string representing the answer
     * @implNote synchronized because game is shared data
     */
    private synchronized String createGame() {

        if (username == null || game.isPlayer1(this)) {
            return ServerAnswers.ERROR + " " + ErrorCode.NOT_NOW;
        } else if (!game.isPlayer1(null)) {
            return ServerAnswers.ERROR + " " + ErrorCode.EXISTING_LOBBY;
        }

        // set first player
        game.setPlayer1(this);
        
        return ServerAnswers.OK.toString();
    }

    /**
     * Sets the player as player2 in the game
     * @return string representing the answer
     * @implNote synchronized because game is shared data
     */
    private synchronized String joinGame() {

        // chosing his name or already ingame
        if (username == null || game.isPlayer1(this) || game.isPlayer2(this)) {
            return ServerAnswers.ERROR + " " + ErrorCode.NOT_NOW;
        // no player 1 (lobby not created)
        } else if (game.isPlayer1(null)) {
            return ServerAnswers.ERROR + " " + ErrorCode.NO_LOBBY;
        // already a player 2
        } else if (!game.isPlayer2(null)) {
            return ServerAnswers.ERROR + " " + ErrorCode.LOBBY_FULL;
        }

        // set second player and turn
        game.setPlayer2(this);
        game.setTurn(this);

        // TODO send STATS

        return ServerAnswers.OK.toString();
    }

    /**
     * Attacks the other player in the game
     * @return string representing the answer
     */
    private String attack() {

        // if it's not our turn, error
        if (!game.hasTurn(this)) {
            return ServerAnswers.ERROR + " " + ErrorCode.NOT_NOW;
        }

        // calculate random damage
        int damage = Nokemon.BASE_DAMAGE + random.nextInt(Nokemon.DEVIATION_DAMAGE);

        // attack other player
        game.attackOtherPlayer(this, damage);

        // if one player lost, end game
        if (game.onePlayerLost())
            game.endGame();

        // return the result of the attack
        // TODO send to both
        return ServerAnswers.HIT + " " +
                game.getOtherPlayerName(this) + " " +
                damage;
    }

    /**
     * Heals this player
     * @return string representing the answer
     */
    private String heal() {

        // if it's not our turn, error
        if (!game.hasTurn(this)) {
            return ServerAnswers.ERROR + " " + ErrorCode.NOT_NOW;
        }

        // calculate random heal
        int heal = Nokemon.BASE_HEAL + random.nextInt(Nokemon.DEVIATION_HEAL);

        // heal this player
        game.healThisPlayer(this, heal);

        // return the result of the heal
        // TODO send to both
        return ServerAnswers.HEALED + " " +
                username + " " +
                heal;
    }
    // ***************
}
