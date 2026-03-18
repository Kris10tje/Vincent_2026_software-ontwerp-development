package whistapp.domain.game;

import whistapp.domain.Interfaces.*;
import whistapp.domain.bids.BidType;
import whistapp.domain.cards.Suit;
import whistapp.domain.players.BotDifficulty;
import whistapp.domain.players.HighBot;
import whistapp.domain.players.LowBot;
import whistapp.domain.players.Player;
import whistapp.domain.round.PlayRound;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class PlayGame extends Game<IPlayRound> implements IPlayGame {

    /* -------------------------------------------------------------------------- */
    /*                              Constructors                                  */
    /* -------------------------------------------------------------------------- */

    /**
     * A no-arg constructor for the PlayGame class.
     */
    public PlayGame() {
        super();
    }
 

    /**
     * A constructor for creating a new game of PlayWhist.
     *
     * @param players A map of players and their respective bot difficulty (null if human).
     */
    public PlayGame(LinkedHashMap<String, BotDifficulty> players) {
        initializePlayers(players);
    }

    
    @Override
    protected IPlayRound createRound() {
        return new PlayRound(players);
    }

    /* -------------------------------------------------------------------------- */
    /*                               Public methods                               */
    /* -------------------------------------------------------------------------- */


    /**
     * A method starting an autonomous bid in the current round.
     * Dispatches to the PlayRound class.
     */
    public void proceedAutonomousBid() {
        getCurrentRound().proceedAutonomousBid();
    }

    /**
     * A method for submitting a bid for the current player in the current round.
     * Also advances to the next player.
     * Dispatches to the PlayRound class.
     *
     * @param bidType The bid to submit.
     */
    public void submitBid(String bidType, Suit newTrumpSuit) {
        getCurrentRound().submitBid(bidFromString(bidType), newTrumpSuit);
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
    public void processCardPlay(String card) {
        getCurrentRound().processCardPlay(card);
    }

    /**
     * A method for processing an autonomous card play.
     * This fails if the current player requires input.
     */
    public void processAutonomousCardPlay() {
        getCurrentRound().processAutonomousCardPlay();
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
        HashMap<String, Integer> tricksPerPlayerName = new HashMap<>();
        HashMap<IPlayer, Integer> tricksPerPlayer = getCurrentRound().getTricksWon();
        for (IPlayer player : tricksPerPlayer.keySet()) {
            tricksPerPlayerName.put(player.getName(), tricksPerPlayer.get(player));
        }
        updateScores(tricksPerPlayerName);
    }

    public boolean bidRequiresTrumpDeclaration(String chosenBid) {
        return PlayRound.requiresTrumpInput(BidType.fromString(chosenBid));
    }

    /**
     * Returns a string representation of the last completed trick.
     * Dispatches to getCardsFromPreviousTrick().
     */
    public String getLastTrickString() {
        LinkedHashMap<String, String> trick = getCardsFromPreviousTrick();
        if (trick == null || trick.isEmpty()) {
            return null; // Return null if there's no trick
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Last trick (in order of play):\n");
        for (HashMap.Entry<String, String> entry : trick.entrySet()) {
            sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        return sb.toString();
    }


    /* -------------------------------------------------------------------------- */
    /*                               Private methods                              */
    /* -------------------------------------------------------------------------- */

    /**
     * A method to initialize the given players.
     *
     * @param playersMap A map of player names to their BotDifficulty. Null means human.
     */
    private void initializePlayers(HashMap<String, BotDifficulty> playersMap) {

        // Check if the players can be initialized
        validatePlayerInitialization(new ArrayList<>(playersMap.keySet()));

        // Initialize the players using the explicit Creator pattern
        for (HashMap.Entry<String, BotDifficulty> entry : playersMap.entrySet()) {
            String name = entry.getKey();
            BotDifficulty difficulty = entry.getValue();

            if (difficulty == null) {
                this.players.add(new Player(name));
            } else {
                switch (difficulty) {
                    case LOW:
                        this.players.add(new LowBot(name));
                        break;
                    case HIGH:
                        this.players.add(new HighBot(name));
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown bot difficulty: " + difficulty);
                }
            }
        }

        // Initialize the player's scores
        setAllScores(0);

    }

    /**
     * A helper method for transforming incoming maps with
     * Player as a key into maps with the player names as keys.
     */
    private <T> LinkedHashMap<String, T> transformPlayerMapToPlayerNames(HashMap<IPlayer, T> map) {
        LinkedHashMap<String, T> result = new LinkedHashMap<>();
        for (HashMap.Entry<IPlayer, T> entry : map.entrySet()) {
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
     * Returns a string showing every active bid in this round.
     * Dispatches to the PlayRound class.
     *
     * @return the string
     */
    public String getExistingBids() {
        return getCurrentRound().getExistingBids();
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

    public String[] getFinalBidDeclarers() {
        if (getCurrentRound().getFinalBid() == null) {
            return new String[0];
        }
        return getCurrentRound().getFinalBid().getBidders().stream()
                .map(IPlayer::getName)
                .toArray(String[]::new);
    }

    public String[] getPossibleBidNames() {
        BidType[] possibleBids = getCurrentRound().getPossibleBids();
        String[] bidNames = new String[getCurrentRound().getPossibleBids().length];
        for (int i  = 0; i < getCurrentRound().getPossibleBids().length; i++) {
            bidNames[i] = possibleBids[i].toString();
        }
        return bidNames;
    }

    public String getDealerName() {
        return getCurrentRound().getDealer().getName();
    }

    public LinkedHashMap<String, String> getCurrentTrickCardsAsStrings() {
        LinkedHashMap<String, String> trickCards = new LinkedHashMap<>();
        for (Map.Entry<IPlayer, String> entry : getCurrentRound().getCurrentTrick().getCardsAsStrings().entrySet()) {
            trickCards.put(entry.getKey().getName(), entry.getValue());
        }
        return trickCards;
    }

    public String[] getAllowedCardsForCurrentPlayer() {
        return getCurrentRound().getAllowedCardsForCurrentPlayer();
    }

    public String getTrumpSuitName() {
        return getCurrentRound().getTrumpSuitName();
    }

    public String getOriginalTrumpSuitName() {
        return getCurrentRound().getOriginalTrumpSuitName();
    }

    public String getCurrentTrickWinnerName() {
        return getCurrentRound().getCurrentTrickWinnerName();
    }

    /**
     * A getter finding the round scores for each of the players of the game.
     */
    public HashMap<String, Integer> getRoundScoresPerPlayer() {
        // Create a map for the scores
        HashMap<String, Integer> scoresPerPlayerName = new HashMap<>();
        HashMap<IPlayer, Integer> scoresPerPlayer = getCurrentRound().processRoundOutcome(getCurrentRound().getTricksWon());

        // Add the scores to the map
        for (IPlayer player : scoresPerPlayer.keySet()) {
            scoresPerPlayerName.put(player.getName(), scoresPerPlayer.get(player));
        }

        // Return the map
        return scoresPerPlayerName;
    }

    /**
     * A getter for the active player's name (either bidding or playing)
     * in the current round.
     *
     * @return The name of the active player.
     */
    public String getActivePlayerName() {
        return getCurrentRound().getActivePlayerName();
    }

    /**
     * A getter for the cards of the specified player.
     *
     * @return The cards of the specified player, as a list of Strings.
     */
    public String[] getPlayerCards(String playerName) {
        IPlayer player = getPlayerByName(playerName);
        if (player == null) {
            throw new IllegalArgumentException("Player not found: " + playerName);
        }
        return player.getHandCards();
    }

    /**
     * Gets the cards of the currently active player.
     *
     * @return An array containing the string representations of every card in the active players hand.
     */
    public String[] getPlayerCards() {
        return getPlayerCards(getActivePlayerName());
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
     * A getter for the last dealt card in the current round.
     *
     * @return The last dealt card in the current round as a string
     */
    public String getLastDealtCard() {
        return getCurrentRound().getLastDealtCard().toString();
    }

    /**
     * A getter for the trick cards of the current trick
     * in the current round.
     *
     * @return A map playerName -> cardString
     */
    public HashMap<String, String> getCurrentRoundCurrentTrickCards() {
        return transformPlayerMapToPlayerNames(getCurrentRound().getCardsInTrick());
    }

    /**
     * A getter for the cards of the other players currently bidding open miserie.
     *
     * @param currentPlayer The current player.
     * @return A map playerName -> cardStrings
     * never containing currentPlayer as playerName
     */
    public HashMap<String, String[]> getOpenMiserieHands(String currentPlayer) {
        return transformPlayerMapToPlayerNames(getCurrentRound().getOpenMiserieHands(getPlayerByName(currentPlayer)));
    }
    
    /**
     * Gets the hands of the open miserie players.
     *
     * @return A map of player names and their hands.
     */
    public HashMap<String, String[]> getOpenMiserieHands() {
        return transformPlayerMapToPlayerNames(getCurrentRound().getOpenMiserieHands(getPlayerByName(getActivePlayerName())));
    }

    /**
     * A simple getter for finding the cards of the previously played trick in the current round.
     * <p>
     * This method dispatches to the PlayRound class.
     *
     * @return A map playerName -> cardString
     */
    public LinkedHashMap<String, String> getCardsFromPreviousTrick() {
        return transformPlayerMapToPlayerNames(getCurrentRound().getCardsFromPreviousTrick());
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
    public String getLoneProposerName() {
        return getCurrentRound().getLoneProposerName();
    }

    /**
     * Processes the lone proposer to play a proposal alone bid.
     * @param proposer The proposer to register.
     */
    public void registerLoneProposer(String proposer) {
        getCurrentRound().registerLoneProposer(getPlayerByName(proposer));
    }

}
