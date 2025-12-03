package ch.heigvd.dai.game;

import ch.heigvd.dai.controller.ServerController;
import ch.heigvd.dai.nokenet.ServerAnswer;

public class Game {

    // **** ATTRIBUTS ****
    private ServerController player1 = null;
    private ServerController player2 = null;
    private ServerController turn    = null;
    // *******************

    // * BOOLEAN METHODS *
    public boolean isPlayer1(ServerController player) {
        return player == player1;
    }

    public boolean isPlayer2(ServerController player) {
        return player == player2;
    }

    // warning is normal, better to have function this way
    public boolean hasTurn(ServerController player) {
        return turn == player;
    }

    public boolean onePlayerLost() {
        return player1.getNokemonHp() == 0 || player2.getNokemonHp() == 0;
    }
    // *******************

    // ******* GET *******
    public String getOtherPlayerName(ServerController player) {

        if (player != player1 && player != player2)
            throw new RuntimeException("[Game] Cannot get name of other player if not in game");

        return (player1 == player ? player2.getUsername() : player1.getUsername());
    }

    private ServerController getOtherPlayer(ServerController player) {

        if (player != player1 && player != player2)
            throw new RuntimeException("[Game] Cannot other player if not in game");

        return (player1 == player ? player2 : player1);
    }
    // *******************

    // ******* SET *******
    public void setPlayer1(ServerController player) {
        this.player1 = player;
    }

    public void setPlayer2(ServerController player) {
        this.player2 = player;
    }

    public void setTurn(ServerController player) {
        this.turn = player;
    }
    // *******************

    // ** ACTION METHODS *
    public void attackOtherPlayer(ServerController player, int damage) {

        ServerController otherPlayer = getOtherPlayer(player);

        otherPlayer.receiveDamage(damage);
        turn = otherPlayer;
    }

    public void healThisPlayer(ServerController player, int heal) {

        ServerController otherPlayer = getOtherPlayer(player);

        player.receiveHeal(heal);
        turn = otherPlayer;
    }

    public void sendToOtherPlayer(ServerController player, String message) {

        ServerController otherPlayer = getOtherPlayer(player);

        otherPlayer.sendMessageFromGame(message);
    }

    public void sendStatsToBothPlayers() {

        if (player1 == null || player2 == null)
            return;

        String message = ServerAnswer.STATS + " "
                + player1.getUsername() + " " + player1.getNokemonHp() + " "
                + player2.getUsername() + " " + player2.getNokemonHp();

        player1.sendMessageFromGame(message);
        player2.sendMessageFromGame(message);
    }
    // *******************

    // *** GAME METHODS **
    /**
     * Ends the current game by sending the LOST message to the two players,
     * giving the win to the winner
     * Game state is reset.
     * Synchronized because game state is shared data
     */
    public synchronized void endGame() {

        // Determine who lost and send LOST message to both players
        String loserUsername;
        if (player1.getNokemonHp() == 0) {
            loserUsername = player1.getUsername();
        } else {
            loserUsername = player2.getUsername();
        }

        // Send LOST message to both players
        sendLostMessageToBothPlayers(loserUsername);

        // Reset hps
        player1.setNokemonHp(player1.getNokemonMaxHp());
        player2.setNokemonHp(player2.getNokemonMaxHp());

        // Clean game state
        player1 = null;
        player2 = null;
        turn = null;
    }

    /**
     * Sends LOST message to both players
     * @param loserUsername the username of the player who lost
     */
    public void sendLostMessageToBothPlayers(String loserUsername) {

        if (player1 == null || player2 == null)
            return;

        String message = ServerAnswer.LOST + " " + loserUsername;

        player1.sendMessageFromGame(message);
        player2.sendMessageFromGame(message);
    }

    /**
     * Handles a player disconnecting during a game.
     * Notifies the remaining player and make them win.
     * @param disconnectedPlayer the player who disconnected
     * synchronized because game state is shared data
     */
    public synchronized void handlePlayerDisconnect(ServerController disconnectedPlayer) {

        // Check that the player who disconnects is actually in the game (could be a third player waiting in the lobby)
        if (disconnectedPlayer != player1 && disconnectedPlayer != player2)
            return;

        // Handle the case where only one player is in the game (player created game but no one joined yet)
        boolean onlyPlayerOne = (player1 != null && player2 == null);
        boolean onlyPlayerTwo = (player1 == null && player2 != null);

        if (onlyPlayerOne || onlyPlayerTwo) {
            // Clean up the game state
            player1 = null;
            player2 = null;
            turn = null;
            return;
        }

        // Both players are in the game
        ServerController remainingPlayer = getOtherPlayer(disconnectedPlayer);

        // Send LOST message to the remaining player only
        String message = ServerAnswer.LOST + " " + disconnectedPlayer.getUsername();
        remainingPlayer.sendMessageFromGame(message);

        remainingPlayer.setNokemonHp(remainingPlayer.getNokemonMaxHp());

        // Clean game
        player1 = null;
        player2 = null;
        turn = null;
    }
    // *******************
}
