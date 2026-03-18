package whistapp.domain.players;

import java.util.HashSet;

import whistapp.domain.Interfaces.ICard;
import whistapp.domain.Interfaces.IPlayer;
import whistapp.domain.bids.BidType;
import whistapp.domain.cards.Hand;
import whistapp.domain.cards.Suit;

import java.util.ArrayList;

/**
 * Represents a player in the game of Whist.
 */
public class Player implements IPlayer {

    private String name;
    private int score = 0;

    /**
     * The hand of this player.
     */
    protected Hand hand;

    /* -------------------------------------------------------------------------- */
    /*                                Constructors                                */
    /* -------------------------------------------------------------------------- */

    /**
     * A basic constructor for a player.
     */
    public Player(String name) {
        setName(name);
    }

    /* -------------------------------------------------------------------------- */
    /*                               Public methods                               */
    /* -------------------------------------------------------------------------- */

    /**
     * A method for updating the score for a player.
     *
     * <p><b>Note:</b> This can be negative
     * because players can lose points when they lose.
     */
    public void updateScore(int points) {
        this.score += points;
    }

    /**
     * A method for receiving a hand, a list of 13 cards.
     */
    public void giveHand(ArrayList<ICard> hand) {
        this.hand = new Hand(hand);
    }

    /**
     * A method for playing a given card.
     *
     * @throws IllegalArgumentException The given card isn't found in this hand.
     * @throws IllegalStateException    The hand is empty.
     */
    public ICard playCard(String card, Suit currentSuit) throws IllegalArgumentException, IllegalStateException {
        if (hand == null || hand.isEmpty()) {
            throw new IllegalStateException("Hand is empty, can't play a card.");
        }
        if (!hand.isValidCardForPlaying(card, currentSuit)) {
            throw new IllegalArgumentException("Card is not valid, can't play this card.");
        }
        return hand.playCard(card);
    }

    /**
     * A simple getter finding the next player in line to play or bid.
     *
     * @param players       The players playing.
     * @param currentPlayer The player that is currently playing.
     * @return The player playing after the current player.
     */
    public static IPlayer getNextPlayer(ArrayList<IPlayer> players, IPlayer currentPlayer) {
        if (currentPlayer == null || !players.contains(currentPlayer)) {
            throw new IllegalStateException("Can't advance player when there is no current player.");
        }
        int newIndex = players.indexOf(currentPlayer) + 1;
        newIndex = newIndex > players.size() - 1 ? 0 : newIndex;
        return players.get(newIndex);
    }

    /**
     * A method for checking if the players are valid.
     *
     * @param players The names of the players.
     * @return {@code true} if the players are valid, {@code false} otherwise.
     */
    public static boolean playersValid(ArrayList<String> players) {

        // HashSet for checking duplicates
        HashSet<String> set = new HashSet<String>();

        // Check if the players are valid
        for (String player : players) {

            // No duplicates and all valid individual names
            if (!set.add(player) || !playerNameValid(player)) {
                return false;
            }

        }

        // No violations -> valid
        return true;

    }

    /**
     * A simple helper method for checking if an individual
     * player name is valid.
     *
     * @param player The name of the player.
     * @return {@code true} if the player name is valid, {@code false} otherwise.
     */
    public static boolean playerNameValid(String player) {
        return player.matches("^[A-Za-z]+$");
    }

    /* -------------------------------------------------------------------------- */
    /*                                 Getters                                    */
    /* -------------------------------------------------------------------------- */

    /**
     * A simple getter for the name of this player.
     */
    public String getName() {
        return name;
    }

    /**
     * A simple getter for the score of this player.
     */
    public int getScore() {
        return score;
    }

    /**
     * A method for knowing if a Player
     * requires some input from the CLI.
     */
    public boolean isAutonomous() {
        return false;
    }

    /**
     * A method for getting the bid from an autonomous player.
     * By default, a player is not autonomous and this throws an exception.
     *
     * @throws UnsupportedOperationException if the player is not autonomous.
     */
    public BidType getAutonomousBid() {
        throw new UnsupportedOperationException("This player is not autonomous and cannot bid automatically.");
    }

    /**
     * A method for getting the card to play from an autonomous player.
     * By default, a player is not autonomous and this throws an exception.
     *
     * @param leadSuit The suit currently leading the trick.
     * @throws UnsupportedOperationException if the player is not autonomous.
     */
    public String findAutonomousCard(Suit leadSuit) {
        throw new UnsupportedOperationException("This player is not autonomous and cannot play automatically.");
    }

    /**
     * A simple getter that finds the cards in this player's hand.
     */
    public String[] getHandCards() {
        if (hand == null) {
            return new String[0];
        }
        return hand.getHandCards().toArray(String[]::new);
    }

    /**
     * Returns the strings of all legally allowed cards given the trick's lead suit.
     */
    public ArrayList<String> getAllowedHandCards(Suit leadSuit) {
        if (hand == null) {
            return new ArrayList<>();
        }
        return hand.getAllowedHandCards(leadSuit);
    }

    /* -------------------------------------------------------------------------- */
    /*                                 Setters                                    */
    /* -------------------------------------------------------------------------- */

    /**
     * A simple setter for setting of the name of this player.
     */
    protected void setName(String name) {
        this.name = name;
    }

    /**
     * A simple setter for setting the score of this player.
     */
    protected void setScore(int score) {
        this.score = score;
    }

}
