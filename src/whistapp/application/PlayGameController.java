package whistapp.application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import whistapp.application.interfaces.IPlayGameController;
import whistapp.domain.bids.BidType;
import whistapp.domain.cards.Card;
import whistapp.domain.interfaces.ICard;
import whistapp.domain.interfaces.IPlayGame;
import whistapp.domain.cards.Suit;
import whistapp.domain.game.PlayGame;
import whistapp.domain.interfaces.IPlayer;
import whistapp.domain.interfaces.IPlayerInputProvider;
import whistapp.domain.players.PlayerType;

public class PlayGameController extends GameController<IPlayGame> implements IPlayGameController {
    
    public PlayGameController(LinkedHashMap<String, PlayerType> playerNamesAndBotDifficulties, IPlayerInputProvider playerInputProvider){
        game = new PlayGame(createPlayerList(playerNamesAndBotDifficulties, playerInputProvider));
    }

    public void updateScores(HashMap<IPlayer, Integer> tricksPerPlayer) {
        game.updateScores(tricksPerPlayer);
    }

    public LinkedHashMap<IPlayer, ICard> getPreviousTrickCards() {
        // Check if a game is active
        if (game == null) {
            throw new IllegalStateException("No game is currently active.");
        }

        return game.getPreviousTrickCards();
    }

    public void submitBid(BidType bidType, Suit newTrumpSuit) {
        game.submitBid(bidType, newTrumpSuit);
    }

    public void startPlayingRound() {
        game.startPlayingRound();
    }

    public String getFinalBidName() {
        return game.getFinalBidName();
    }

    public IPlayer[] getFinalBidDeclarers() {
        return game.getFinalBidDeclarers();
    }

    public BidType[] getPossibleBids() {
        return game.getPossibleBids();
    }

    public IPlayer getDealer() {
        return game.getDealer();
    }

    public boolean isTrickOver() {
        return game.isTrickOver();
    }

    public boolean evaluateAndAdvanceTrick() {
        return game.evaluateAndAdvanceTrick();
    }

    public void calculateAndUpdateScores() {
        game.calculateAndUpdateScores();
    }

    public void processCardPlay(ICard card) {
        game.processCardPlay(card);
    }

    /**
     * Get the cards held by a player by name.
     * 
     * <p>This method returns domain objects (ICard) rather than formatted strings.
     * The UI layer is responsible for formatting these cards for display purposes,
     * maintaining proper Separation of Concerns per GRASP principles.
     * 
     * @param player The player.
     * @return the list of cards held by the player, sorted.
     */
    public ArrayList<ICard> getCardsByPlayer(IPlayer player) {

        // Get ICard interfaces from domain layer
        ArrayList<ICard> cards = game.getCardsByPlayer(player);

        // Sort cards (this shouldn't happen in the domain layer, because
        // it is only of interest for showing it to the user in the CLI).
        Card.sortCards(cards);

        return cards;
    }

    /**
     * Get the cards held by the current active player.
     * 
     * <p>This method returns domain objects (ICard) rather than formatted strings.
     * The UI layer is responsible for formatting these cards for display purposes.
     * 
     * @return the list of cards held by the current player, sorted
     */
    public ArrayList<ICard> getCardsForCurrentPlayer() {
        IPlayer currentPlayer = game.getCurrentPlayer();
        // Use the method above
        return getCardsByPlayer(currentPlayer);
    }

    public HashMap<IPlayer, ArrayList<ICard>> getOpenMiserieHands() {
        return game.getOpenMiserieHands();
    }

    public boolean isAutonomous(int playerIndex) {
        return game.isAutonomous(playerIndex);
    }

    public void proceedAutonomousBid() {
        game.proceedAutonomousBid();
    }

    public void processAutonomousCardPlay() {
        game.processAutonomousCardPlay();
    }

    public HashMap<IPlayer, Integer> getRoundScoresPerPlayer() {
        return game.getRoundScoresPerPlayer();
    }

    public LinkedHashMap<IPlayer, ICard> getCurrentTrickCards() {
        return game.getCurrentTrickCards();
    }

    public ArrayList<ICard> getAllowedCardsForCurrentPlayer() {
        return game.getAllowedCardsForCurrentPlayer();
    }

    public Suit getTrumpSuit() {
        return game.getTrumpSuit();
    }

    public Suit getOriginalTrumpSuit() {
        return game.getOriginalTrumpSuit();
    }

    public IPlayer getCurrentTrickWinner() {
        return game.getCurrentTrickWinner();
    }

    public IPlayer getActivePlayer() {
        return game.getActivePlayer();
    }

    public ICard getLastDealtCard() {
        return game.getLastDealtCard();
    }

    public boolean bidRequiresTrumpDeclaration(BidType chosenBid) {
        return game.bidRequiresTrumpDeclaration(chosenBid);
    }

    public LinkedHashMap<IPlayer, BidType> getExistingBids() {
        return game.getExistingBids();
    }

    public IPlayer getLoneProposer() {
        return game.getLoneProposer();
    }

    public void registerLoneProposer(IPlayer proposer) {
        game.registerLoneProposer(proposer);
    }

    public boolean evaluateRoundBids() {
        return game.evaluateRoundBids();
    }

    public void restartFailedRound() {
        game.restartFailedRound();
    }

    /**
     * A function that parses the round bidtype, when everyone has picked a bid.
     *
     * @return {@code true} if bidding has stabilized and at least one person has picked a bid. {@code false} if everone passed and there was a reshuffle.
     */
    public boolean biddingStabilised() {
        try {
            boolean stabilised = game.evaluateRoundBids();
            if (!stabilised) {
                System.out.println("Everyone passed! Reshuffling and dealing new cards...");
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException ignored) {
                }
                game.restartFailedRound();
            }
            return stabilised;
        } catch (IllegalStateException e) {
            return false;
        }
    }

    /**
     * A getter for the index of the player whose turn it currently is.
     *
     * @return the index of the player whose turn it currently is.
     */
    public int getActivePlayerIndex() {
        try {
            IPlayer activePlayer = game.getActivePlayer();
            return game.getPlayerNames().indexOf(activePlayer.getName());
        } catch (IllegalStateException e) {
            return -1;
        }
    }
}
