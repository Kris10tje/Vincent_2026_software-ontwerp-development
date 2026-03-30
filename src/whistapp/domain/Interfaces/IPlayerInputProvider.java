package whistapp.domain.interfaces;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import whistapp.domain.bids.BidType;
import whistapp.domain.bids.BidTypeWithTrump;
import whistapp.domain.cards.Suit;

public interface IPlayerInputProvider {
    ICard chooseCard(Suit originalTrump, Suit activeTrump, LinkedHashMap<IPlayer, ICard> cardsOnTable, ArrayList<ICard> cardsForCurrentPlayer, ArrayList<ICard> allowedCardsForCurrentPlayer,
        HashMap<IPlayer, ArrayList<ICard>> openMiserieHands);
    BidTypeWithTrump chooseBid(ICard lastDealtCard, ArrayList<ICard> cardsForCurrentPlayer, HashMap<IPlayer, ArrayList<ICard>> openMiserieHands,
        LinkedHashMap<IPlayer, BidType> existingBids,  BidType[] possibleBids);
}
