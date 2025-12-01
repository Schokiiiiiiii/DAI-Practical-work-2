package ch.heigvd.dai.controller;

import ch.heigvd.dai.network.ServerNetwork;
import ch.heigvd.dai.applicationInterface.ServerInterface;
import ch.heigvd.dai.game.*;
import ch.heigvd.dai.nokenet.CommandName;
import ch.heigvd.dai.nokenet.ErrorCode;
import ch.heigvd.dai.nokenet.ServerAnswer;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Random;

public class ServerController extends Controller implements Runnable{

    // ** CONTROLLER *
    private final int id;
    private static int nextId = 0;
    // ***************

    // *** NETWORK ***
    private final Socket socket;
    private final BufferedReader in;
    private final BufferedWriter out;
    // ***************

    // **** USER *****
    private String username = null;
    private Nokemon nokemon = null;
    // ***************

    // *** SERVER ****
    private static final Game game                  = new Game();
    private static final ArrayList<String> players  = new ArrayList<>();
    private static final Random random              = new Random();
    // ***************

    // * CONSTRUCTOR *
    public ServerController(Socket socket) {
        this.id = nextId++;
        this.socket = socket;
        try {
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
        } catch (IOException e) {
            System.out.println("[Controller#" + id + "] Error creating streams: " + e);
            throw new RuntimeException("[Controller#" + id + "] Could not create the controller for the socket");
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

    /**
     * Check if this player is currently in an active game
     * @return true if player is player1 or player2 in the game
     */
    private boolean isInGame() {
        return game.isPlayer1(this) || game.isPlayer2(this);
    }
    // ***************

    // ***** RUN *****
    /**
     * Main run loop for a socket connected to a client
     */
    @Override
    public void run(){

        // confirm controller is running
        System.out.println("[Controller#" + id + "] Connected to client");

        // try-with-resources
        try (socket;
             in;
             out) {

            // keep answering until socket is closed
            while (!socket.isClosed()) {

                // read a message from the client
                String[] message = ServerNetwork.receive(in, socket);

                // if message is null, socket got close, meaning the client disconnected
                if (message == null) {
                    break;
                }

                // print log message
                System.out.println("[Controller#" + id + "] Received a message from client");

                // handle message
                if (handleMessage(message) < 0) {
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("[Controller#" + id + "] Error reading/writing from socket: " + e);
        } finally {
            // Cleanup even if exception occurs
            handleDisconnect();
            ServerNetwork.decreaseNbThreads();
        }
    }
    // ***************

    // *** METHODS ***
    /**
     * Chose the correct answer based on the message received from the client
     * @param message table of strings representing the message parsed
     */
    public int handleMessage(String[] message) {

        // try to read command from message
        CommandName command = ServerInterface.extractCommand(message);

        // switch over the possible commands
        String answer = switch (command) {
            case USERNAME   -> setName(message[1]);
            case CREATE     -> createGame();
            case JOIN       -> joinGame();
            case ATTACK     -> attack();
            case HEAL       -> heal();
            case QUIT       -> handleQuit();
            default         -> {
                System.out.println("[Controller#" + id + "] Unknown command: " + command);
                yield ServerAnswer.ERROR + " " + ErrorCode.NOT_COMMAND.getCode();
            }
        };

        // send answer
        return ServerNetwork.send(out, answer);
    }

    /**
     * Sets the username for that player
     * @param name string the player wants as a name
     * @return string representing the answer
     * @implNote synchronized because players is shared data
     */
    private synchronized String setName(String name) {

        // if already a username
        if (username != null) {
            ServerInterface.printError(id, ErrorCode.NOT_NOW);
            return ServerAnswer.ERROR + " " + ErrorCode.NOT_NOW.getCode();
        // if username is already taken
        } else if (players.contains(name)) {
            ServerInterface.printError(id, ErrorCode.USERNAME_TAKEN);
            return ServerAnswer.ERROR + " " + ErrorCode.USERNAME_TAKEN.getCode();
        }

        // fix username and add player to the list
        this.username = name;
        players.add(name);

        // print message log
        System.out.println("[Controller#" + id + "] Player chose the username '" + username + "'");
        ServerInterface.printPlayers(id, players);

        return ServerAnswer.OK.toString();
    }

    /**
     * Sets the player as player1 in the game
     * @return string representing the answer
     * @implNote synchronized because game is shared data
     */
    private String createGame() {
        // Game object is shared
        synchronized(game) {

            // if choosing name or already in game
            if (username == null || isInGame()) {
                ServerInterface.printError(id, ErrorCode.NOT_NOW);
                return ServerAnswer.ERROR + " " + ErrorCode.NOT_NOW.getCode();
            // no player 1 (lobby not created)
            } else if (!game.isPlayer1(null)) {
                ServerInterface.printError(id, ErrorCode.EXISTING_LOBBY);
                return ServerAnswer.ERROR + " " + ErrorCode.EXISTING_LOBBY.getCode();
            }

            // initialize nokemon and set first player
            this.nokemon = new Nokemon();
            game.setPlayer1(this);

            // print message log
            System.out.println("[Controller#" + id + "] Player 1 '" + username + "' created the game");

            return ServerAnswer.OK.toString();
        }
    }

    /**
     * Sets the player as player2 in the game
     * @return string representing the answer
     * @implNote synchronized because game is shared data
     */
    private String joinGame() {
        // Game object is shared
        synchronized(game) {

            // choosing his name or already in game
            if (username == null || isInGame()) {
                ServerInterface.printError(id, ErrorCode.NOT_NOW);
                return ServerAnswer.ERROR + " " + ErrorCode.NOT_NOW.getCode();
            // no player 1 (lobby not created)
            } else if (game.isPlayer1(null)) {
                ServerInterface.printError(id, ErrorCode.NO_LOBBY);
                return ServerAnswer.ERROR + " " + ErrorCode.NO_LOBBY.getCode();
            // already a player 2
            } else if (!game.isPlayer2(null)) {
                ServerInterface.printError(id, ErrorCode.LOBBY_FULL);
                return ServerAnswer.ERROR + " " + ErrorCode.LOBBY_FULL.getCode();
            }

            // initialize nokemon and set second player and turn
            this.nokemon = new Nokemon();
            game.setPlayer2(this);
            game.setTurn(this);

            // send stats to both players
            game.sendStatsToBothPlayers();

            // print message log
            System.out.println("[Controller#" + id + "] Player 2 '" + username + "' joined the game");
            System.out.println("[Controller#" + id + "] Game started");

            return ServerAnswer.OK.toString();
        }
    }

    /**
     * Attacks the other player in the game
     * @return string representing the answer
     */
    private String attack() {

        // if it's not our turn
        if (!game.hasTurn(this)) {
            ServerInterface.printError(id, ErrorCode.NOT_NOW);
            return ServerAnswer.ERROR + " " + ErrorCode.NOT_NOW.getCode();
        }

        // calculate random damage
        int damage = Nokemon.BASE_DAMAGE + random.nextInt(Nokemon.DEVIATION_DAMAGE);

        // attack other player
        game.attackOtherPlayer(this, damage);

        // build message
        String message = ServerAnswer.HIT + " " +
                game.getOtherPlayerName(this) + " " +
                damage;

        // print message log
        System.out.println("[Controller#" + id + "] Sent message '" + message + "' to both players");

        // send result of attack to other player
        game.sendToOtherPlayer(this, message);

        // if one player lost, end game
        if (game.onePlayerLost())
            game.endGame();

        // return the result of the attack
        return message;
    }

    /**
     * Heals this player
     * @return string representing the answer
     */
    private String heal() {

        // if it's not our turn
        if (!game.hasTurn(this)) {
            ServerInterface.printError(id, ErrorCode.NOT_NOW);
            return ServerAnswer.ERROR + " " + ErrorCode.NOT_NOW.getCode();
        }

        // calculate random heal
        int heal = Nokemon.BASE_HEAL + random.nextInt(Nokemon.DEVIATION_HEAL);

        // heal this player
        game.healThisPlayer(this, heal);

        // build message
        String message = ServerAnswer.HEALED + " " +
                            username + " " +
                            heal;

        // print message log
        System.out.println("[Controller#" + id + "] Sent message '" + message + "' to both players");

        // send result of heal to other player
        game.sendToOtherPlayer(this, message);

        // return the result of heal
        return message;
    }

    public void sendMessageFromGame(String message) {
        ServerNetwork.send(out, message);
    }

    /**
     * Handle a player quitting the game.
     * If in a running game, tell the opponent and make them the winner.
     * @return string for the server's answer (OK or ERROR)
     */
    private String handleQuit() {

        // Current player in game then disconnect normally
        if (isInGame()) {
            game.handlePlayerDisconnect(this);
        }

        // Return OK to acknowledge the QUIT, then the connection will close
        return ServerAnswer.OK.toString();
    }

    /**
     * Handles a player disconnecting.
     * If in a running game, tell the opponent and make them the winner.
     * Removes the player from the list.
     * synchronized because players list is shared
     */
    private synchronized void handleDisconnect() {

        // Player finished registering
        if (username == null) {
            return;
        }

        // Player in an active game, tell the opponent
        if (isInGame()) {
            game.handlePlayerDisconnect(this);
        }

        // Remove the player from the players list
        players.remove(username);

        // print message log
        System.out.println("[Controller#" + id + "] Player " + username + " disconnected");
        ServerInterface.printPlayers(id, players);
    }
    // ***************
}
