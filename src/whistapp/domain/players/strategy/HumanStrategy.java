package whistapp.domain.players.strategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import whistapp.domain.bids.BidType;
import whistapp.domain.bids.BidTypeWithTrump;
import whistapp.domain.cards.Hand;
import whistapp.domain.interfaces.ICard;
import whistapp.domain.interfaces.IPlayer;
import whistapp.domain.interfaces.IPlayerInputProvider;
import whistapp.domain.round.Context.BidContext;
import whistapp.domain.round.Context.RoundContext;

public class HumanStrategy implements PlayerStrategy {

    private final IPlayerInputProvider inputProvider;

    public HumanStrategy(IPlayerInputProvider inputProvider){
        this.inputProvider = inputProvider;
    }
    /*zijn deze overrides nodig?? */
    //@Override
    public BidTypeWithTrump chooseBid(Hand hand, BidContext context) {
        ICard lastDealtCard = context.getLastDealtCard();
        ArrayList<ICard> handCards = context.getHandCards();
        HashMap<IPlayer, ArrayList<ICard>> openMiserieHands = context.getOpenMiserieHandsForCurrentPlayer();
        LinkedHashMap<IPlayer, BidType> existingBids = context.getExistingBids();

        return inputProvider.chooseBid(lastDealtCard, handCards, openMiserieHands, existingBids, context.getPossibleBids());
    }

    //@Override
    public ICard chooseCard(Hand hand, RoundContext context) {
        return inputProvider.chooseCard(context.getTrumpSuit(), context.getCurrentTrickSuit(), context.getPlayedCards(), 
        context.getHandCards(),  context.getAllowedCardsForCurrentPlayer(), context.getOpenMiserieHandsForCurrentPlayer());
    }

    //@Override
    public boolean isAutonomous() {
        return false;
    }
}
