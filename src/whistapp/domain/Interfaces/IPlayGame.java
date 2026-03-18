package whistapp.domain.Interfaces;

import java.util.HashMap;
import java.util.LinkedHashMap;

import whistapp.domain.cards.Suit;

public interface IPlayGame extends IGame {
    String getLastTrickString();
    int getTricksLeft();
    void submitBid(String bidType, Suit newTrumpSuit);
    void startPlayingRound();
    String getFinalBidName();
    String[] getFinalBidDeclarers();
    String[] getPossibleBidNames();
    String getDealerName();
    boolean isTrickOver();
    boolean evaluateAndAdvanceTrick();
    void calculateAndUpdateScores();
    void processCardPlay(String card);
    String[] getPlayerCards(String playerName);
    String[] getPlayerCards();
    HashMap<String, String[]> getOpenMiserieHands();
    boolean isAutonomous(int playerIndex);
    void proceedAutonomousBid();
    void processAutonomousCardPlay();
    HashMap<String, Integer> getRoundScoresPerPlayer();
    LinkedHashMap<String, String> getCurrentTrickCardsAsStrings();
    String[] getAllowedCardsForCurrentPlayer();
    String getTrumpSuitName();
    String getOriginalTrumpSuitName();
    String getCurrentTrickWinnerName();
    String getActivePlayerName();
    String getLastDealtCard();
    boolean bidRequiresTrumpDeclaration(String chosenBid);
    String getExistingBids();
    String getLoneProposerName();
    void registerLoneProposer(String proposer);
    boolean evaluateRoundBids();
    void restartFailedRound();
}
