package whistapp.application;

import java.util.HashMap;
import java.util.LinkedHashMap;

import whistapp.application.Interfaces.IPlayGameController;
import whistapp.domain.Interfaces.IPlayGame;
import whistapp.domain.cards.Suit;
import whistapp.domain.game.PlayGame;
import whistapp.domain.players.BotDifficulty;

public class PlayGameController extends GameController<IPlayGame> implements IPlayGameController {
    public PlayGameController(LinkedHashMap<String, BotDifficulty> playerNamesAndBotDifficulties){
        game = new PlayGame(playerNamesAndBotDifficulties);
    }

    public void updateScores(HashMap<String, Integer> tricksPerPlayer) {
        game.updateScores(tricksPerPlayer);
    }

    public String getLastTrickString() {
        // Check if a game is active
        if (game == null) {
            throw new IllegalStateException("No game is currently active.");
        }

        // Get the last trick from the current round via the Polymorphic interface
        String lastTrick = game.getLastTrickString();
        if (lastTrick == null) {
            throw new IllegalStateException("No tricks have been played yet in this round.");
        }
        return lastTrick;
    }

    public int getTricksLeft() {
        return game.getTricksLeft();
    }

    public void submitBid(String bidType, Suit newTrumpSuit) {
        submitBid(bidType, newTrumpSuit);
    }

    public void startPlayingRound() {
        game.startPlayingRound();
    }

    public String getFinalBidName() {
        return game.getFinalBidName();
    }

    public String[] getFinalBidDeclarers() {
        return game.getFinalBidDeclarers();
    }

    public String[] getPossibleBidNames() {
        return game.getPossibleBidNames();
    }

    public String getDealerName() {
        return game.getDealerName();
    }

    public boolean isTrickOver() {
        return game.isTrickOver();
    }

    public boolean evaluateAndAdvanceTrick() {
        return game.evaluateAndAdvanceTrick();
    }

    public void calculateAndUpdateScores() {
        game.calculateAndUpdateScores();
    }

    public void processCardPlay(String card) {
        game.processCardPlay(card);
    }

    public String[] getPlayerCards(String playerName) {
        return game.getPlayerCards(playerName);
    }

    public String[] getPlayerCards() {
        return game.getPlayerCards();
    }

    public HashMap<String, String[]> getOpenMiserieHands() {
        return game.getOpenMiserieHands();
    }

    public boolean isAutonomous(int playerIndex) {
        return game.isAutonomous(playerIndex);
    }

    public void proceedAutonomousBid() {
        game.proceedAutonomousBid();
    }

    public void processAutonomousCardPlay() {
        game.processAutonomousCardPlay();
    }

    public HashMap<String, Integer> getRoundScoresPerPlayer() {
        return game.getRoundScoresPerPlayer();
    }

    public LinkedHashMap<String, String> getCurrentTrickCardsAsStrings() {
        return game.getCurrentTrickCardsAsStrings();
    }

    public String[] getAllowedCardsForCurrentPlayer() {
        return game.getAllowedCardsForCurrentPlayer();
    }

    public String getTrumpSuitName() {
        return game.getTrumpSuitName();
    }

    public String getOriginalTrumpSuitName() {
        return game.getOriginalTrumpSuitName();
    }

    public String getCurrentTrickWinnerName() {
        return game.getCurrentTrickWinnerName();
    }

    public String getActivePlayerName() {
        return game.getActivePlayerName();
    }

    public String getLastDealtCard() {
        return game.getLastDealtCard();
    }

    public boolean bidRequiresTrumpDeclaration(String chosenBid) {
        return game.bidRequiresTrumpDeclaration(chosenBid);
    }

    public String getExistingBids() {
        return game.getExistingBids();
    }

    public String getLoneProposerName() {
        return game.getLoneProposerName();
    }

    public void registerLoneProposer(String proposer) {
        game.registerLoneProposer(proposer);
    }

    public boolean evaluateRoundBids() {
        return game.evaluateAndAdvanceTrick();
    }

    public void restartFailedRound() {
        game.restartFailedRound();
    }

    /**
     * A function that parses the round bidtype, when everyone has picked a bid.
     *
     * @return {@code true} if bidding has stabilized and at least one person has picked a bid. {@code false} if everone passed and there was a reshuffle.
     */
    public boolean biddingStabilised() {
        try {
            boolean stabilised = game.evaluateRoundBids();
            if (!stabilised) {
                System.out.println("Everyone passed! Reshuffling and dealing new cards...");
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException ignored) {
                }
                game.restartFailedRound();
            }
            return stabilised;
        } catch (IllegalStateException e) {
            return false;
        }
    }

    /**
     * A getter for the index of the player whose turn it currently is.
     *
     * @return the index of the player whose turn it currently is.
     */
    public int getActivePlayerIndex() {
        try {
            String activePlayerName = game.getActivePlayerName();
            return game.getPlayerNames().indexOf(activePlayerName);
        } catch (IllegalStateException e) {
            return -1;
        }
    }
}
