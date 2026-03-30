
package whistapp.application.interfaces;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import whistapp.domain.bids.BidType;
import whistapp.domain.cards.Suit;
import whistapp.domain.interfaces.ICard;
import whistapp.domain.interfaces.IPlayer;

public interface IPlayGameController extends IGameController {
    LinkedHashMap<IPlayer, ICard> getPreviousTrickCards();
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
    boolean biddingStabilised();
    int getActivePlayerIndex();
    void currentPlayerChooseCard();
    void currentPlayerChooseBid();
}
