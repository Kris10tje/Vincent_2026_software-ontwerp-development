package whistapp.domain.interfaces;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import whistapp.domain.bids.BidType;
import whistapp.domain.cards.Suit;
import whistapp.domain.players.Player;

public interface IPlayGame extends IGame {
    LinkedHashMap<IPlayer, ICard> getPreviousTrickCards();
    int getTricksLeft();
    void submitBid(BidType bidType, Suit newTrumpSuit);
    void startPlayingRound();
    String getFinalBidName();
    IPlayer[] getFinalBidDeclarers();
    BidType[] getPossibleBids();
    IPlayer getDealer();
    boolean isTrickOver();
    boolean evaluateAndAdvanceTrick();
    void calculateAndUpdateScores();
    void processCardPlay(ICard card);
    ArrayList<ICard> getCardsByPlayer(IPlayer player);
    HashMap<IPlayer, ArrayList<ICard>> getOpenMiserieHands();
    boolean isAutonomous(int playerIndex);
    void proceedAutonomousBid();
    void processAutonomousCardPlay();
    HashMap<IPlayer, Integer> getRoundScoresPerPlayer();
    LinkedHashMap<IPlayer, ICard> getCurrentTrickCards();
    ArrayList<ICard> getAllowedCardsForCurrentPlayer();
    Suit getTrumpSuit();
    Suit getOriginalTrumpSuit();
    IPlayer getCurrentTrickWinner();
    IPlayer getActivePlayer();
    ICard getLastDealtCard();
    boolean bidRequiresTrumpDeclaration(BidType chosenBid);
    LinkedHashMap<IPlayer, BidType> getExistingBids();
    IPlayer getLoneProposer();
    void registerLoneProposer(IPlayer proposer);
    boolean evaluateRoundBids();
    void restartFailedRound();
    IPlayer getCurrentPlayer();
}
