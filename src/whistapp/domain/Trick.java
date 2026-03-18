package whistapp.domain;

import whistapp.domain.Interfaces.*;
import whistapp.domain.cards.Suit;
import whistapp.domain.game.Game;
import whistapp.domain.players.Player;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class Trick implements ITrick {

    private final LinkedHashMap<IPlayer, ICard> playedCards;

    private Suit leadSuit;

    private IPlayer currentPlayer;

    /* -------------------------------------------------------------------------- */
    /*                                Constructors                                */
    /* -------------------------------------------------------------------------- */

    public Trick(IPlayer startingPlayer) {
        playedCards = new LinkedHashMap<>();
        currentPlayer = startingPlayer;
    }

    /* -------------------------------------------------------------------------- */
    /*                              Public methods                                */
    /* -------------------------------------------------------------------------- */

    /**
     * A method for adding cards to this trick.
     *
     * @param card    The card to add.
     * @param players The players playing this trick.
     * @throws IllegalStateException    All players already played.
     * @throws IllegalArgumentException The given player already played a card.
     */
    protected void addCard(ICard card, ArrayList<IPlayer> players)
            throws IllegalStateException, IllegalArgumentException {
        if (playedCards.size() >= Game.NUMBER_OF_PLAYERS) {
            throw new IllegalStateException("Can't add a card to a full trick.");
        }
        if (playedCards.containsKey(currentPlayer)) {
            throw new IllegalArgumentException("Player already played.");
        }
        if (playedCards.isEmpty()) {
            // Cards is empty, adding this card will update the leading suit
            leadSuit = card.getSuit();
        }
        playedCards.put(currentPlayer, card);

        // We update the current player.
        currentPlayer = Player.getNextPlayer(players, currentPlayer);
    }

    /**
     * A method for playing a card from the current player's hand.
     * The current player gets moved to the next player automatically.
     *
     * @param card    The card to play.
     * @param players The players playing in this game.
     * @throws IllegalStateException    All players already played.
     * @throws IllegalArgumentException The given player already played a card.
     */
    public void playCardFromCurrentPlayerHand(String card, ArrayList<IPlayer> players)
            throws IllegalStateException, IllegalArgumentException {
        ICard playedCard = getCurrentPlayer().playCard(card, getLeadSuit());
        addCard(playedCard, players);
    }

    /**
     * A method determining the winning player of this trick
     * given the trump suit in the current round.
     *
     * @param trumpSuit The current trump suit of the round,
     *                  if there is no trump suit, this should be null.
     * @return The winner of this trick.
     * @throws IllegalStateException Not all players have played a card in this trick.
     */
    public IPlayer determineWinner(Suit trumpSuit) throws IllegalStateException {
        if (playedCards.size() != Game.NUMBER_OF_PLAYERS) {
            throw new IllegalStateException("Can't determine winner when not all players have played.");
        }

        IPlayer winner = null;
        ICard winningCard = null;

        // Because we use LinkedHashMap, this iterates in the exact order cards were played
        for (Map.Entry<IPlayer, ICard> entry : playedCards.entrySet()) {
            IPlayer player = entry.getKey();
            ICard card = entry.getValue();

            // The leader's card sets the baseline to beat
            if (winningCard == null) {
                winningCard = card;
                winner = player;
                continue;
            }

            // Update winner if the newly evaluated card beats the current winning card
            if (beatsWinningCard(card, winningCard, trumpSuit)) {
                winningCard = card;
                winner = player;
            }
        }

        return winner;
    }

    /* -------------------------------------------------------------------------- */
    /*                               Private methods                              */
    /* -------------------------------------------------------------------------- */

    /**
     * A method for checking if a given card beats another
     * when playing a certain trump suit.
     *
     * @param newCard     The new card to check for.
     * @param winningCard The already winning card before.
     * @param trumpSuit   The current trump suit of the round.
     * @return True if the new card beats the other card,
     * False otherwise.
     */
    private boolean beatsWinningCard(ICard newCard, ICard winningCard, Suit trumpSuit) {
        // These null-checks are added for if there is no trump suit
        boolean isNewCardTrump = trumpSuit != null && newCard.getSuit().equals(trumpSuit);
        boolean isWinningCardTrump = trumpSuit != null && winningCard.getSuit().equals(trumpSuit);

        // A trump card always beats a non-trump card
        if (isNewCardTrump && !isWinningCardTrump) {
            return true;
        }

        // A non-trump card cannot beat a trump card
        if (!isNewCardTrump && isWinningCardTrump) {
            return false;
        }

        // A non-trump card of a different suit than the lead suit cannot win (discard/slough)
        if (!isNewCardTrump && !newCard.getSuit().equals(leadSuit)) {
            return false;
        }

        // If both are trumps, or both are the lead suit, the highest numeric value wins
        return newCard.getValue().getNumericValue() > winningCard.getValue().getNumericValue();
    }

    /* -------------------------------------------------------------------------- */
    /*                                   Getters                                  */
    /* -------------------------------------------------------------------------- */

    /**
     * A simple getter for finding the cards played per player.
     */
    public LinkedHashMap<IPlayer, String> getCardsAsStrings() {
        LinkedHashMap<IPlayer, String> cards = new LinkedHashMap<>();
        for (IPlayer player : this.playedCards.keySet()) {
            cards.put(player, this.playedCards.get(player).toString());
        }
        return cards;
    }

    /**
     * A simple getter for finding hte leading suit of this trick.
     */
    public Suit getLeadSuit() {
        return leadSuit;
    }

    /**
     * A simple getter for finding the current player playing in this trick.
     */
    public IPlayer getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * A simple method to check if the trick is fully played.
     */
    public boolean isOver() {
        return playedCards.size() == Game.NUMBER_OF_PLAYERS;
    }
}
