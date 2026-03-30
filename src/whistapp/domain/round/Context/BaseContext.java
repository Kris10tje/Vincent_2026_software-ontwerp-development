package whistapp.domain.round.Context;

import java.util.ArrayList;
import java.util.HashMap;

import whistapp.domain.interfaces.ICard;
import whistapp.domain.interfaces.IPlayer;

public abstract class BaseContext {
    private HashMap<IPlayer, ArrayList<ICard>> openMiserieHandsForCurrentPlayer;
    private ArrayList<ICard> handCards;

    public BaseContext(ArrayList<ICard> handCards, HashMap<IPlayer, ArrayList<ICard>> openMiserieHandsForCurrentPlayer){
        this.openMiserieHandsForCurrentPlayer = openMiserieHandsForCurrentPlayer;
        this.handCards = handCards;
    }

    public HashMap<IPlayer, ArrayList<ICard>> getOpenMiserieHandsForCurrentPlayer(){
        return openMiserieHandsForCurrentPlayer;
    }

     public ArrayList<ICard> getHandCards(){
        return handCards;
    }
}
