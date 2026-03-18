package whistapp.application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import whistapp.domain.cards.Suit;
import whistapp.domain.players.BotDifficulty;

public interface IController {
    void exit();
    void startNewScoreGame(ArrayList<String> playerNames);
    void startNewPlayGame(LinkedHashMap<String, BotDifficulty> playerNamesAndBotDifficulties);
    String getLastTrickString();
    void nextRound();
    void registerNbOfTricksWonPerPlayer(HashMap<String, Integer> nbOfTricksWonPerPlayer);
    ArrayList<String> getPlayerNames();
    void setReshuffledState(boolean reshuffled);
    void registerBids(HashMap<String, String> bids);
    BotDifficulty[] getBotDifficultyOptions();
    String getDealerName();
    String getFinalBidName();
    String[] getFinalBidDeclarers();
    void startPlayingRound();
    void updateScores();
    boolean biddingStabilised();
    int getActivePlayerIndex();
    boolean isAutonomous(int playerIndex);
    void proceedAutonomousBid();
    String[] getPossibleBids();
    boolean bidRequiresTrumpDeclaration(String chosenBid);
    Suit[] getSuits();
    String getLoneProposerName();
    void registerBid(String bid);
    void registerBid(String bid, Suit newTrumpSuit);
    void registerLoneProposer(String proposer);
    String getExistingBids();
    String[] getCards(String playerName);
    String[] getCards();
    int getTricksLeft();
    String getCurrentTrickWinnerName();
    boolean evaluateAndAdvanceTrick();
    boolean trickIsOver();
    void proceedAutonomousCardPlay();
    HashMap<String, String> getCurrentTrickCardsAsStrings();
    String getOriginalTrumpSuitName();
    String getTrumpSuitName();
    String[] getAllowedCardsForCurrentPlayer() ;
    void registerPlayCard(String card);
    HashMap<String, Integer> getRoundScoresPerPlayer();
    String getLastDealtCard();
    String getActivePlayerName();
    HashMap<String, String[]> getOpenMiserieHands();
    HashMap<String, Integer> getGameScoresPerPlayer();
}
