package whistapp.domain.game;

import whistapp.domain.interfaces.*;
import whistapp.domain.Transformer;
import whistapp.domain.bids.BidType;
import whistapp.domain.cards.Suit;
import whistapp.domain.players.*;
import whistapp.domain.round.PlayRound;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class PlayGame extends Game<IPlayRound> implements IPlayGame {

    /* -------------------------------------------------------------------------- */
    /*                              Constructors                                  */
    /* -------------------------------------------------------------------------- */

    /**
     * A no-arg constructor for the PlayGame class.
     */
    /*public PlayGame() {
        super();
    }*/
 

    /**
     * A constructor for creating a new game of PlayWhist.
     *
     * @param players A map of players and their respective player type.
     */
    /*
    Design Critique: Constructors and Logic
    You mentioned a concern about "using a bunch of methods inside constructors." You're right to be cautious. In Java (as in C#), constructors should ideally be used for assignment, not orchestration. 
    The Problem: If initializePlayers is called in the Game constructor, the Game object isn't fully "born" yet while it's trying to set up its internal state. This makes unit testing difficult and can lead to issues if you ever use inheritance.
    The Fix: Use a Factory or a Builder pattern. Let the GameController or a specific GameFactory create the players first, then pass a finished List<Player> into the Game constructor. This keeps your domain objects "pure."
    UI Layer: Collects names/types.
    Controller: Receives the data. It uses a PlayerFactory to create the Player objects.
    PlayerFactory: * If type is BOT_LOW, it creates a LowBotStrategy. If type is HUMAN, it creates a HumanStrategy and injects the ConsoleInputProvider.
    Game Object: Is initialized with a completed List<Player>.
     */
    /*public PlayGame(LinkedHashMap<String, PlayerType> players) {
        initializePlayers(players);
    }*/
   public PlayGame(ArrayList<Player> players){
        super(players);
   }

    
    @Override
    protected IPlayRound createRound() {
        return new PlayRound(players);
    }

    /* -------------------------------------------------------------------------- */
    /*                               Public methods                               */
    /* -------------------------------------------------------------------------- */


    public void currentPlayerChooseBid(){
        getCurrentRound().currentPlayerChooseBid();
    }

    /**
     * Evaluates the submitted bids of the current round to determine the final bid and the declarers.
     *
     * @return True, if a final bid was chosen and the playing can start.
     * False, if everyone passed.
     */
    public boolean evaluateRoundBids() {
        return getCurrentRound().evaluateRoundBids();
    }

    /**
     * A method for restarting a round where the bidding phase failed.
     * Dispatches to the PlayRound class.
     */
    public void restartFailedRound() {
        getCurrentRound().restartRound();
    }

    /**
     * A method for starting the playing process of the round.
     * Dispatches to the PlayRound class.
     */
    public void startPlayingRound() {
        getCurrentRound().startPlayingRound();
    }

    /**
     * A method for processing a given card
     * as the card played by the current player of the current round.
     *
     * @param card The card played.
     */
    public void processCardPlay(ICard card) {
        getCurrentRound().processCardPlay(card);
    }

    public void currentPlayerChooseCard(){
        getCurrentRound().currentPlayerChooseCard();
    }

    /**
     * A method for evaluating the current trick of the current round.
     * Dispatches to the PlayRound class.
     * This also advances the round to the next trick.
     *
     * @return True, if the final trick was reached.
     * False, otherwise.
     */
    public boolean evaluateAndAdvanceTrick() {
        return getCurrentRound().evaluateAndAdvanceTrick();
    }

    /**
     * A method for updating the scores based on the current round.
     */
    public void calculateAndUpdateScores() {
        HashMap<IPlayer, Integer> tricksPerIPlayer = new HashMap<>();
        HashMap<Player, Integer> tricksPerPlayer = getCurrentRound().getTricksWon();
        for (Player player : tricksPerPlayer.keySet()) {
            tricksPerIPlayer.put(player, tricksPerPlayer.get(player));
        }
        updateScores(tricksPerIPlayer);
    }

    public boolean bidRequiresTrumpDeclaration(BidType chosenBid) {
        return PlayRound.requiresTrumpInput(chosenBid);
    }

    /**
     * Returns the cards of the last completed trick in play order.
     */
    public LinkedHashMap<IPlayer, ICard> getPreviousTrickCards() {
        return Transformer.transformPlayerMapToIPlayerMap(getCurrentRound().getCardsFromPreviousTrick());
    }


    /* -------------------------------------------------------------------------- */
    /*                               Private methods                              */
    /* -------------------------------------------------------------------------- */

    /**
     * A helper method for transforming incoming maps with
     * Player as a key into maps with the player names as keys.
     */
    private <T> LinkedHashMap<String, T> transformPlayerMapToPlayerNames(HashMap<Player, T> map) {
        LinkedHashMap<String, T> result = new LinkedHashMap<>();
        for (HashMap.Entry<Player, T> entry : map.entrySet()) {
            result.put(entry.getKey().getName(), entry.getValue());
        }
        return result;
    }

    /* -------------------------------------------------------------------------- */
    /*                                   Getters                                  */
    /* -------------------------------------------------------------------------- */

    /**
     * Finds if the player at the given index is autonomous (a bot).
     */
    public boolean isAutonomous(int playerIndex) {
        String playerName = getPlayerNames().get(playerIndex);
        return getPlayerByName(playerName).isAutonomous();
    }

    /**
     * Returns true if the current trick is completely played.
     */
    public boolean isTrickOver() {
        return getCurrentRound().isCurrentTrickOver();
    }

    /**
     * Returns the active chosen bids name.
     * Dispatches to the PlayRound class.
     *
     * @return The name of the final round bid.
     */
    public String getFinalBidName() {
        if (getCurrentRound().getFinalBid() == null) {
            return "Pass";
        }
        return getCurrentRound().getFinalBid().getClass().getSimpleName();
    }

    public IPlayer[] getFinalBidDeclarers() {
        if (getCurrentRound().getFinalBid() == null) {
            return new IPlayer[0];
        }
        return getCurrentRound().getFinalBid().getBidders().stream()
                .map(p -> (IPlayer) p)
                .toArray(IPlayer[]::new);
    }

    public IPlayer getCurrentPlayer() {
        return getCurrentRound().getActivePlayer();
    }

    public IPlayer getDealer() {
        return getCurrentRound().getDealer();
    }

    public Suit getTrumpSuit() {
        return getCurrentRound().getTrumpSuit();
    }

    public Suit getOriginalTrumpSuit() {
        return getCurrentRound().getOriginalTrumpSuit();
    }

    public IPlayer getCurrentTrickWinner() {
        return getCurrentRound().getCurrentTrickWinner();
    }

    /**
     * A getter finding the round scores for each of the players of the game.
     */
    public HashMap<IPlayer, Integer> getRoundScoresPerPlayer() {
        // Create a map for the scores
        HashMap<IPlayer, Integer> scoresPerIPlayer = new HashMap<>();
        HashMap<Player, Integer> scoresPerPlayer = getCurrentRound().processRoundOutcome(getCurrentRound().getTricksWon());

        // Add the scores to the map
        for (Player player : scoresPerPlayer.keySet()) {
            scoresPerIPlayer.put(player, scoresPerPlayer.get(player));
        }

        // Return the map
        return scoresPerIPlayer;
    }

    /**
     * A getter for the active player's name (either bidding or playing)
     * in the current round.
     *
     * @return The name of the active player.
     */
    public IPlayer getActivePlayer() {
        return getCurrentRound().getActivePlayer();
    }

    /**
     * A getter for the cards of the specified player.
     *
     * @return The cards of the specified player, as a list of ICards.
     */
    public ArrayList<ICard> getCardsByPlayer(IPlayer player) {
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null");
        }
        return ((Player) player).getHandCards();
    }

    /**
     * A getter for the trump suit of the current round.
     *
     * @return The trump suit of the current round as a String.
     */
    public Suit getCurrentRoundTrumpSuit() {
        return getCurrentRound().getTrumpSuit();
    }

    /**
     * A getter for the trick cards of the current trick
     * in the current round.
     *
     * @return A map playerName -> cardString
     */
    public HashMap<String, String> getCurrentRoundCurrentTrickCards() {
        HashMap<String, String> result = new HashMap<>();
        LinkedHashMap<Player, ICard> cardsInTrick = getCurrentRound().getCardsInTrick();
        for (Player player : cardsInTrick.keySet()) {
            result.put(player.getName(), cardsInTrick.get(player).toString());
        }
        return result;
    }

    /**
     * A simple getter for finding the cards of the previously played trick in the current round.
     * <p>
     * This method dispatches to the PlayRound class.
     *
     * @return A map playerName -> cardString
     */
    public LinkedHashMap<String, String> getCardsFromPreviousTrick() {
        LinkedHashMap<String, String> result = new LinkedHashMap<>();
        LinkedHashMap<Player, ICard> cards = getCurrentRound().getCardsFromPreviousTrick();
        for (Player player : cards.keySet()) {
            result.put(player.getName(), cards.get(player).toString());
        }
        return result;
    }

    /**
     * Returns the number of tricks left in this round.
     */
    public int getTricksLeft() {
        return getCurrentRound().getTricksLeft();
    }

    /**
     * Returns the highest bid established in this round so far.
     */
    public BidType getHighestBid() {
        return getCurrentRound().getHighestBid();
    }

    /**
     * Returns the string of the name of the lone proposer.
     * @return the name
     */
    public IPlayer getLoneProposer() {
        return getCurrentRound().getLoneProposer();
    }

    /**
     * Processes the lone proposer to play a proposal alone bid.
     * @param proposer The proposer to register.
     */
    public void registerLoneProposer(IPlayer proposer) {
        getCurrentRound().registerLoneProposer((Player) proposer);
    }

}
