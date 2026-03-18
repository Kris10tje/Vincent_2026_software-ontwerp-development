package whistapp.domain.Interfaces;

import java.util.ArrayList;

import whistapp.domain.bids.BidType;
import whistapp.domain.cards.Suit;

public interface IPlayer {
    String getName();
    boolean isAutonomous();
    void updateScore(int points);
    String[] getHandCards();
    ICard playCard(String card, Suit currentSuit);
    void giveHand(ArrayList<ICard> hand);
    ArrayList<String> getAllowedHandCards(Suit leadSuit);
    String findAutonomousCard(Suit leadSuit);
    BidType getAutonomousBid();
}
