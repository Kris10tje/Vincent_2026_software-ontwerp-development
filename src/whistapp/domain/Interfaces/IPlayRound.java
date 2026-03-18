package whistapp.domain.Interfaces;

import java.util.HashMap;
import java.util.LinkedHashMap;

import whistapp.domain.bids.BidType;
import whistapp.domain.cards.Suit;

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
    IPlayer getDealer();
    String[] getAllowedCardsForCurrentPlayer();
    String getTrumpSuitName();
    String getOriginalTrumpSuitName();
    String getCurrentTrickWinnerName();
    Suit getTrumpSuit();
    ICard getLastDealtCard();
    HashMap<IPlayer, String[]> getOpenMiserieHands(IPlayer currentPlayer);
    LinkedHashMap<IPlayer, String> getCardsFromPreviousTrick();
    int getTricksLeft();
    BidType getHighestBid();
    String getLoneProposerName();
    void registerLoneProposer(IPlayer proposer);
    HashMap<IPlayer, String> getCardsInTrick();
    ITrick getCurrentTrick();
}
