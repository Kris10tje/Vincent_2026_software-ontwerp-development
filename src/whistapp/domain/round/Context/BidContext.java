package whistapp.domain.round.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import whistapp.domain.bids.BidType;
import whistapp.domain.interfaces.ICard;
import whistapp.domain.interfaces.IPlayer;

public class BidContext extends BaseContext {
    private ICard lastDealtCard;
    private LinkedHashMap<IPlayer, BidType> existingBids;
    private BidType[] possibleBids;

    public BidContext(ArrayList<ICard> handCards, HashMap<IPlayer, ArrayList<ICard>> openMiserieHandsForCurrentPlayer, ICard lastDealtCard, LinkedHashMap<IPlayer, BidType> existingBids,
        BidType[] possibleBids){
        super(handCards, openMiserieHandsForCurrentPlayer);
        this.lastDealtCard = lastDealtCard;
        this.existingBids = existingBids;
        this.possibleBids = possibleBids;
    }

    public ICard getLastDealtCard(){
        return lastDealtCard;
    }

    public LinkedHashMap<IPlayer, BidType> getExistingBids(){
        return existingBids;
    }

    public BidType[] getPossibleBids(){
        return possibleBids;
    }
}
