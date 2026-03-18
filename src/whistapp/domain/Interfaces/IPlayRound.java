package whistapp.domain.Interfaces;

import java.util.HashMap;
import java.util.LinkedHashMap;

import whistapp.domain.Trick;
import whistapp.domain.bids.BidType;
import whistapp.domain.cards.Card;
import whistapp.domain.cards.Suit;
import whistapp.domain.players.Player;

public interface IPlayRound extends IRound {
    String getActivePlayerName();
    void proceedAutonomousBid();
    void submitBid(BidType bidType, Suit newTrumpSuit);
    boolean evaluateRoundBids();
    void restartRound();
    void startPlayingRound();
    void processCardPlay(String card);
    void processAutonomousCardPlay();
    boolean evaluateAndAdvanceTrick();
    boolean isCurrentTrickOver();
    String getExistingBids();
    BidType[] getPossibleBids();
    Player getDealer();
    String[] getAllowedCardsForCurrentPlayer();
    String getTrumpSuitName();
    String getOriginalTrumpSuitName();
    String getCurrentTrickWinnerName();
    Suit getTrumpSuit();
    Card getLastDealtCard();
    HashMap<Player, String[]> getOpenMiserieHands(Player currentPlayer);
    LinkedHashMap<Player, String> getCardsFromPreviousTrick();
    int getTricksLeft();
    BidType getHighestBid();
    String getLoneProposerName();
    void registerLoneProposer(Player proposer);
    HashMap<Player, String> getCardsInTrick();
    Trick getCurrentTrick();
}
