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
    void startPlayingRound();
    String getFinalBidName();
    IPlayer[] getFinalBidDeclarers();
    IPlayer getDealer();
    boolean isTrickOver();
    boolean evaluateAndAdvanceTrick();
    void calculateAndUpdateScores();
    void processCardPlay(ICard card);
    ArrayList<ICard> getCardsByPlayer(IPlayer player);
    boolean isAutonomous(int playerIndex);
    HashMap<IPlayer, Integer> getRoundScoresPerPlayer();
    Suit getTrumpSuit();
    Suit getOriginalTrumpSuit();
    IPlayer getCurrentTrickWinner();
    IPlayer getActivePlayer();
    boolean bidRequiresTrumpDeclaration(BidType chosenBid);
    IPlayer getLoneProposer();
    void registerLoneProposer(IPlayer proposer);
    boolean evaluateRoundBids();
    void restartFailedRound();
    IPlayer getCurrentPlayer();
    void currentPlayerChooseCard();
    void currentPlayerChooseBid();
}
