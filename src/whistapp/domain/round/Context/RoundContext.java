package whistapp.domain.round.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import whistapp.domain.cards.Suit;
import whistapp.domain.interfaces.*;

public class RoundContext extends BaseContext {

    private Suit trumpSuit;
    private Suit currentTrickSuit;
    private LinkedHashMap<IPlayer, ICard> playedCards;
    private ArrayList<ICard> allowedCardsForCurrentPlayer;
      
    

    public RoundContext(Suit trumpSuit, Suit currentTrickSuit, LinkedHashMap<IPlayer, ICard> playedCards, ArrayList<ICard> handCards, ArrayList<ICard> allowedCardsForCurrentPlayer, 
        HashMap<IPlayer, ArrayList<ICard>> openMiserieHandsForCurrentPlayer) {
            super(handCards, openMiserieHandsForCurrentPlayer);
            this.trumpSuit = trumpSuit;
            this.currentTrickSuit = currentTrickSuit;
            this.playedCards = playedCards;
            this.allowedCardsForCurrentPlayer = allowedCardsForCurrentPlayer;                       
    }

    /**
     * A getter for the trump suit of the round context.
     */
    public Suit getTrumpSuit() {
        return trumpSuit;
    }

    /**
     * A getter for the leading suit of the current trick.
     */
    public Suit getCurrentTrickSuit() {
        return currentTrickSuit;
    }

    public LinkedHashMap<IPlayer, ICard> getPlayedCards(){
        return playedCards;
    }

    public ArrayList<ICard> getAllowedCardsForCurrentPlayer() {
        return allowedCardsForCurrentPlayer;
    }
    
}
