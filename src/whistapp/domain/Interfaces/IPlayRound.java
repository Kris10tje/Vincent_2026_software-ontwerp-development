package whistapp.domain.interfaces;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import whistapp.domain.Trick;
import whistapp.domain.bids.BidType;
import whistapp.domain.cards.Suit;
import whistapp.domain.players.Player;

public interface IPlayRound extends IRound {
    void proceedAutonomousBid();
    void submitBid(BidType bidType, Suit newTrumpSuit);
    boolean evaluateRoundBids();
    void restartRound();
    void startPlayingRound();
    void processCardPlay(ICard card);
    void processAutonomousCardPlay();
    boolean evaluateAndAdvanceTrick();
    boolean isCurrentTrickOver();
    LinkedHashMap<IPlayer, BidType> getExistingBids();
    BidType[] getPossibleBids();
    Player getDealer();
    ArrayList<ICard> getAllowedCardsForCurrentPlayer();
    Suit getTrumpSuit();
    Suit getOriginalTrumpSuit();
    IPlayer getCurrentTrickWinner();
    ICard getLastDealtCard();
    HashMap<Player, ArrayList<ICard>> getOpenMiserieHands(Player currentPlayer);
    LinkedHashMap<Player, ICard> getCardsFromPreviousTrick();
    int getTricksLeft();
    BidType getHighestBid();
    Player getLoneProposer();
    void registerLoneProposer(Player proposer);
    LinkedHashMap<Player, ICard> getCardsInTrick();
    Trick getCurrentTrick();
    IPlayer getActivePlayer();
}
