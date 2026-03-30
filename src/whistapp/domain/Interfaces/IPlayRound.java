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
    //TODO bekijken om te verwijderen
    void submitBid(BidType bidType, Suit newTrumpSuit);
    boolean evaluateRoundBids();
    void restartRound();
    void startPlayingRound();
    void processCardPlay(ICard card);
    void processAutonomousCardPlay();
    boolean evaluateAndAdvanceTrick();
    boolean isCurrentTrickOver();
    //TODO bekijken om deze te verwijderen
    LinkedHashMap<IPlayer, BidType> getExistingBids();
    //TODO bekijken om te verwijderen
    BidType[] getPossibleBids();
    Player getDealer();
    ArrayList<ICard> getAllowedCardsForCurrentPlayer();
    Suit getTrumpSuit();
    Suit getOriginalTrumpSuit();
    IPlayer getCurrentTrickWinner();
    //TODO bekijken om deze te verwijderen
    ICard getLastDealtCard();
    HashMap<Player, ArrayList<ICard>> getOpenMiserieHands(Player currentPlayer);
    LinkedHashMap<Player, ICard> getCardsFromPreviousTrick();
    int getTricksLeft();
    BidType getHighestBid();
    Player getLoneProposer();
    void registerLoneProposer(Player proposer);
    //TODO bekijken om deze te verwijderen
    LinkedHashMap<Player, ICard> getCardsInTrick();
    Trick getCurrentTrick();
    IPlayer getActivePlayer();
    void currentPlayerChooseCard();
    void currentPlayerChooseBid();
}
