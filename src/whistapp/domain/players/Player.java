package whistapp.domain.players;

import java.util.HashSet;

import whistapp.domain.bids.BidType;
import whistapp.domain.bids.BidTypeWithTrump;
import whistapp.domain.cards.Card;
import whistapp.domain.cards.Hand;
import whistapp.domain.cards.Suit;
import whistapp.domain.interfaces.ICard;
import whistapp.domain.interfaces.IPlayer;
import whistapp.domain.players.strategy.PlayerStrategy;
import whistapp.domain.round.Context.BidContext;
import whistapp.domain.round.Context.RoundContext;

import java.util.ArrayList;

/**
 * Represents a player in the game of Whist.
 */
public class Player implements IPlayer {

    private String name;
    private int score = 0;

    /**
     * The strategy of this player.
     */
    private final PlayerStrategy playerStrategy;

    /**
     * The hand of this player.
     */
    protected Hand hand;

    /* -------------------------------------------------------------------------- */
    /*                                Constructors                                */
    /* -------------------------------------------------------------------------- */

    /**
     * A basic constructor for a player,
     * only requiring the name and creating a human player.
     */
    //het zal niet meer mogelijk zijn om een player te maken met enkel een naam, aangezien we ook een strategy nodig hebben.
    /*public Player(String name) {
        this(name, PlayerType.HUMAN);
    }*/

    /**
     * A basic constructor for a player.
     */
    //public Player(String name, PlayerType playerType) {
    public Player(String name, PlayerStrategy strategy) {
        //TODO check: kan de naam nog gewijzigd worden achteraf? Of moet dat een final zijn?
        setName(name);
        this.playerStrategy = strategy;
        //setPlayerStrategy(playerType);
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
    public void giveHand(ArrayList<Card> hand) {
        this.hand = new Hand(hand);
    }

    /**
     * A method for playing a given card object.
     *
     * @throws IllegalArgumentException The given card isn't found in this hand or is not allowed.
     * @throws IllegalStateException    The hand is empty.
     */
    public Card playCard(ICard card, Suit currentSuit) throws IllegalArgumentException, IllegalStateException {
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
    public static Player getNextPlayer(ArrayList<Player> players, Player currentPlayer) {
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

    /**
     * Chooses the bid to make. This is done using the strategy of the player.
     * Only bot players will actually use this method.
     *
     * @param context Read-only view of the current round.
     * @return The chosen bid type.
     */
    public BidTypeWithTrump chooseBid(BidContext context) {
        return playerStrategy.chooseBid(hand, context);
    }

    /**
     * Chooses the card to play. This is done using the strategy of the player.
     * Only bot players will actually use this method.
     *
     * @param context Read-only view of the current round.
     * @return The chosen card.
     */
    public ICard chooseCard(RoundContext context) {
        return playerStrategy.chooseCard(hand, context);
    }

    /**
     * Whether this strategy is autonomous (no human input needed).
     */
    public boolean isAutonomous() {
        return playerStrategy.isAutonomous();
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
     * A simple getter that finds the cards in this player's hand.
     */
    public ArrayList<ICard> getHandCards() {
        if (hand == null) {
            return new ArrayList<>();
        }
        return hand.getHandCards();
    }

    /**
     * Returns all legally allowed cards for a given lead suit as domain objects.
     */
    public ArrayList<ICard> getAllowedHandCardsAsCards(Suit leadSuit) {
        if (hand == null) {
            return new ArrayList<>();
        }
        return hand.getAllowedHandCardsAsCards(leadSuit);
    }

    /* -------------------------------------------------------------------------- */
    /*                                 Setters                                    */
    /* -------------------------------------------------------------------------- */

    /**
     * A simple setter for setting of the name of this player.
     */
    private void setName(String name) {
        this.name = name;
    }

    /**
     * A simple setter for setting the score of this player.
     */
    private void setScore(int score) {
        this.score = score;
    }

    /**
     * A setter for the strategy of a player given a player type.
     */
    /*private void setPlayerStrategy(PlayerType playerType) {
        if (playerType == null) {
            throw new IllegalArgumentException("PlayerType cannot be null.");
        }
        this.playerStrategy = playerType.getStrategy();
    }*/
}
