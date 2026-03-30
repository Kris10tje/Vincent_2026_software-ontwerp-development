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
    //TODO bekijken om te verwijderen
    void submitBid(BidType bidType, Suit newTrumpSuit);
    void startPlayingRound();
    String getFinalBidName();
    IPlayer[] getFinalBidDeclarers();
    //TODO bekijken om te verwijderen
    BidType[] getPossibleBids();
    IPlayer getDealer();
    boolean isTrickOver();
    boolean evaluateAndAdvanceTrick();
    void calculateAndUpdateScores();
    void processCardPlay(ICard card);
    //TODO bekijken om te verwijderen
    ArrayList<ICard> getCardsByPlayer(IPlayer player);
    //TODO bekijken om te verwijderen
    HashMap<IPlayer, ArrayList<ICard>> getOpenMiserieHands();
    boolean isAutonomous(int playerIndex);
    //TODO bekijken om te verwijderen
    void proceedAutonomousBid();
    //TODO bekijken om te verwijderen
    void processAutonomousCardPlay();
    HashMap<IPlayer, Integer> getRoundScoresPerPlayer();
    //TODO bekijken om deze te verwijderen
    LinkedHashMap<IPlayer, ICard> getCurrentTrickCards();
    //TODO bekijken om deze te verwijderen
    ArrayList<ICard> getAllowedCardsForCurrentPlayer();
    Suit getTrumpSuit();
    Suit getOriginalTrumpSuit();
    IPlayer getCurrentTrickWinner();
    IPlayer getActivePlayer();
    //TODO bekijken om deze te verwijderen
    ICard getLastDealtCard();
    boolean bidRequiresTrumpDeclaration(BidType chosenBid);
    //TODO bekijken om deze te verwijderen
    LinkedHashMap<IPlayer, BidType> getExistingBids();
    IPlayer getLoneProposer();
    void registerLoneProposer(IPlayer proposer);
    boolean evaluateRoundBids();
    void restartFailedRound();
    IPlayer getCurrentPlayer();
    void currentPlayerChooseCard();
    void currentPlayerChooseBid();
}
