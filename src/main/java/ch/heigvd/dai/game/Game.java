package ch.heigvd.dai.game;

import ch.heigvd.dai.controller.ServerController;
import ch.heigvd.dai.network.ServerNetwork;
import ch.heigvd.dai.nokenet.ServerAnswers;

import java.io.IOException;
import java.net.Socket;

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

        String message = ServerAnswers.STATS + " "
                + player1.getUsername() + " " + player1.getNokemonHp() + " "
                + player2.getUsername() + " " + player2.getNokemonHp() + " ";

        player1.sendMessageFromGame(message);
        player2.sendMessageFromGame(message);
    }
    // *******************

    // *** GAME METHODS **
    public void endGame() {

        if (player1.getNokemonHp() == 0)
            player1.addWin();
        else if (player2.getNokemonHp() == 0)
            player2.addWin();

        player1.setNokemonHp(player1.getNokemonMaxHp());
        player2.setNokemonHp(player2.getNokemonMaxHp());
        player1 = null;
        player2 = null;
        turn = null;
    }
    // *******************
}
