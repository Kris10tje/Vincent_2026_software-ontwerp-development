package whistapp.domain.round;

import whistapp.domain.Trick;
import whistapp.domain.bids.Abondance;
import whistapp.domain.bids.BidType;
import whistapp.domain.cards.Card;
import whistapp.domain.cards.Deck;
import whistapp.domain.cards.Suit;
import whistapp.domain.game.Game;
import whistapp.domain.players.Player;

import java.util.*;

public class PlayRound extends Round {

    private LinkedHashMap<Player, BidType> bids = new LinkedHashMap<>(); // 4 bids per round
    private ArrayList<Trick> tricks = new ArrayList<>();

    private Deck deck;

    /**
     * The trump suit as dealt by the deck — never changes during a round.
     * Always visible to all players.
     */
    private Suit originalTrumpSuit;

    /**
     * The last card as dealt by the deck — never changes during a round.
     * Always visible to all players.
     */
    private Card lastDealtCard;

    /**
     * The trump suit actually used for trick evaluation.
     * For non-Abondance bids this equals originalTrumpSuit (or null for Miserie).
     * For ABONDANCE_X_ORIGINAL_TRUMP it also equals originalTrumpSuit.
     * For free-trump Abondance it starts null and is set when the declarer plays
     * their first card (the suit of that card becomes the active trump).
     */
    private Suit activeTrumpSuit;

    /**
     * True once a free-Abondance declarer has played their first card.
     */
    private boolean abondanceTrumpChosen = false;

    private BidType highestBid;

    private Player dealer;
    /**
     * A player object for keeping track of who's playing.
     * This isn't the same as the dealer.
     */
    private Player currentBiddingPlayer = null;

    /* -------------------------------------------------------------------------- */
    /*                                Constructors                                */
    /* -------------------------------------------------------------------------- */

    /**
     * A constructor for the PlayRound class.
     * Instantiates a deck and dealer and deals the cards.
     *
     * @param players The players playing in this round.
     */
    public PlayRound(ArrayList<Player> players) {

        // Call Round superclass constructor
        super(players);

        // Set the dealer and the first player to begin
        setRandomDealer(players);
        determineAndSetFirstPlayer();

        // Reshuffle the deck
        reshuffleDeck();

    }

    /* -------------------------------------------------------------------------- */
    /*                               Public methods                               */
    /* -------------------------------------------------------------------------- */

    /**
     * A method for processing the bid of an autonomous player.
     */
    public void proceedAutonomousBid() {

        // Check if user input is required. If it is, we can't play autonomously!
        if (!getCurrentBiddingPlayer().isAutonomous()) {
            throw new IllegalStateException("Player is not an autonomous player");
        }

        // Get the autonomous bid
        BidType autonomousBid = getCurrentBiddingPlayer().getAutonomousBid();

        // Submit the bid
        submitBid(autonomousBid, null);
    }

    /**
     * A method for submitting a bid for the current bidding player.
     * This also advances the current bidding player to the next player.
     *
     * @param bidType The bid submitted by the current bidding player.
     */
    public void submitBid(BidType bidType, Suit newTrumpSuit) {

        // Upgrade free-trump Abondance if the chosen suit happens to match the original trump exactly
        if (bidType.isFreeAbondance() && newTrumpSuit == originalTrumpSuit) {
            bidType = bidType.getOriginalTrumpVariant();
        }

        // Save the bid to the map
        bids.put(getCurrentBiddingPlayer(), bidType);

        // Update the highest bid
        if (getHighestBid() == null ||
                bidType.isHigherBidThan(getHighestBid())) {
            setHighestBid(bidType);
        }

        // Solo (and any other changesTrumpSuit bids) record the presented suit at bid time.
        // Abondance does NOT declare trump at bid time — that happens at first-card play.
        if (bidType.changesTrumpSuit()) {
            activeTrumpSuit = newTrumpSuit;
        }

        // Advance to the next player
        advanceCurrentBiddingPlayer();
    }

    /**
     * A simple method checking if some player has bid the proposal bid.
     */
    public boolean hasBeenProposed() {

        // 
        for (Player player : bids.keySet()) {
            if (bids.get(player) == BidType.PROPOSAL) {
                return true;
            }
        }
        return false;
    }

    /**
     * Evaluates the submitted bids to determine the final bid and the declarers.
     *
     * @return True, if the final bid was processed and created.
     * False, otherwise. We should keep bidding.
     */
    public boolean evaluateRoundBids() {

        // Verify if the number of bids is correct
        if (bids.size() != Game.getPlayerCount()) {
            throw new IllegalStateException("Number of bids is not equal to number of players");
        }

        // If everyone passed, we return false unless there are only bots, then force one to play proposal
        if (highestBid == BidType.PASS) {
            // Unless all players are bots, then force one bot to play proposal
            int realPlayerCount = 0;
            for (Player player : bids.keySet()) {
                if (!player.isAutonomous()) {
                    realPlayerCount++;
                }
            }
            if (realPlayerCount == 0) {
                highestBid = BidType.PROPOSAL;
                bids.put(getCurrentBiddingPlayer(), BidType.PROPOSAL);
                return evaluateRoundBids();
            }

            // Everyone passed, we return false
            return false;
        }

        // Check if there is a lone proposer, this immediately ends the bidding
        if (highestBid == BidType.PROPOSAL) {
            return true;
        }

        // There was a bid — find the declarers and create the final bid.
        ArrayList<Player> declarers = determineBidDeclarers(bids, getHighestBid());
        setFinalBid(highestBid, declarers, wasFirstTry);

        // Set the active trump suit based on the winning bid:
        if (highestBid.changesTrumpSuit()) {
            // Solo (and similar bids): trump was declared at bid time.
            // activeTrumpSuit is already set inside submitBid, nothing more to do.
        } else if (highestBid.isFreeAbondance()) {
            // Free-trump Abondance: trump is NOT known yet.
            // It will be revealed when the declarer plays their first card.
            activeTrumpSuit = null;
            abondanceTrumpChosen = false;
        } else if (highestBid.isOriginalTrumpAbondance()) {
            // Original-trump Abondance: trump is the original deck trump (no change needed).
            activeTrumpSuit = originalTrumpSuit;
        }
        // All other bids (Proposal, Miserie, etc.) already have activeTrumpSuit
        // initialised in reshuffleDeck(), so nothing more to do.

        return true;
    }


    /**
     * A method for restarting the round after the bidding failed.
     *
     * @throws IllegalStateException Not everyone has bid yet.
     *                               Not everyone passed.
     */
    public void restartRound() throws IllegalStateException {
        // Check if the round failed before
        if (bids.size() != Game.getPlayerCount()) {
            throw new IllegalStateException("Number of bids is not equal to number of players");
        }
        if (!(new HashSet<>(bids.values()).size() == 1 && new HashSet<>(bids.values()).contains(BidType.PASS))) {
            throw new IllegalStateException("Not everyone passed, the round shouldn't get restarted.");
        }

        // Clear all beds and reshuffle deck
        bids.clear();
        reshuffleDeck();

        // The first player to start is the player next to the new dealer
        currentBiddingPlayer = dealer;
        advanceCurrentBiddingPlayer();

        // Since we reshuffled, it wasn't first try
        wasFirstTry = false;

        // Reset the highest bid
        highestBid = null;

    }

    /**
     * A method for starting the playing of the new current round.
     *
     * @throws IllegalStateException The bidding phase hasn't ended.
     */
    public void startPlayingRound() throws IllegalStateException {
        if (getFinalBid() == null) {
            throw new IllegalStateException("The bidding phase hasn't ended.");
        }
        // It's the start of the round, the starting player is the player after the dealer
        // If the bid is abondance, the starting player is the abondance declarer itself
        Player startingPlayer = Player.getNextPlayer(getPlayers(), dealer);
        if (getFinalBid() instanceof Abondance) {
            startingPlayer = finalBid.getBidders().getFirst();
        }
        startNewTrick(startingPlayer);
    }

    /**
     * A method for processing the playing of a given card
     * by the current player in the current trick.
     *
     * @param card The card played.
     */
    public void processCardPlay(String card) {
        getCurrentTrick().playCardFromCurrentPlayerHand(card, getPlayers());

        // If the winning bid is free-trump Abondance and no trump has been chosen yet,
        // the suit of the first card played by the Abondance declarer becomes the trump.
        // By GRASP Information Expert: PlayRound owns trump state, so it handles this here.
        if (!abondanceTrumpChosen && highestBid != null && highestBid.isFreeAbondance()) {
            // The first card in the trick sets the leadSuit — that is the Abondance trump.
            activeTrumpSuit = getCurrentTrick().getLeadSuit();
            abondanceTrumpChosen = true;
        }
    }

    /**
     * A method for processing the autonomous playing of a card in the current trick.
     */
    public void processAutonomousCardPlay() {
        if (!getCurrentPlayingPlayer().isAutonomous()) {
            throw new IllegalStateException("Player is not a BotPlayer");
        }
        // Find the played card by this bot
        String playedCard = getCurrentPlayingPlayer().findAutonomousCard(getCurrentTrick().getLeadSuit());
        // Play the card
        processCardPlay(playedCard);
    }

    /**
     * A method for evaluating the current trick as to who won.
     * The player that won gets an extra trick won in the tricksWon map.
     * The current trick advances to a new trick.
     *
     * @return True if all tricks have been played.
     * False, otherwise.
     */
    public boolean evaluateAndAdvanceTrick() {

        // Determine the trick winner
        Player winner = getCurrentTrick().determineWinner(getTrumpSuit());

        // We add a trick won to the winning player
        tricksWon.put(winner, tricksWon.get(winner) + 1);

        // We only advance if we can.
        if (tricks.size() < Round.getTrickCountPerRound()) {
            startNewTrick(winner);
            return false;
        }

        // All tricks have been played
        return true;

    }

    /**
     * A method for processing the outcome of this round.
     */
    public HashMap<Player, Integer> processRoundOutcome() {
        return super.processRoundOutcome(tricksWon);
    }

    /**
     * A method for finding whether a bid type requires extra input from the user.
     */
    public static boolean requiresTrumpInput(BidType bidType) {
        if (bidType.changesTrumpSuit() &&
                bidType != BidType.MISERIE && bidType != BidType.OPEN_MISERIE) {
            // If it changes the suit and it isn't miserie (which changes the suit to null) it requires trump input.
            return true;
        }
        return false;
    }

    /**
     * Processes the lone proposer to play a proposal alone bid.
     * @param proposer The proposer to register.
     */
    public void registerLoneProposer(Player proposer) {
        // Save the bid to the map
        bids.put(proposer, BidType.ACCEPT);
        highestBid = BidType.ACCEPT;
    }

    /* -------------------------------------------------------------------------- */
    /*                               Private methods                              */
    /* -------------------------------------------------------------------------- */

    /**
     * A method for reshuffling the deck and dealing new cards to all the players.
     */
    private void reshuffleDeck() {

        // Create and shuffle the deck
        deck = new Deck();
        originalTrumpSuit = deck.shuffleGetTrump();
        lastDealtCard = deck.getLastCard();

        // Active trump starts as the original trump (may change for Abondance)
        activeTrumpSuit = originalTrumpSuit;
        abondanceTrumpChosen = false;

        // Hand out the cards
        for (Player player : getPlayers()) {
            player.giveHand(deck.dealHand(Round.getTrickCountPerRound()));
        }

    }

    /**
     * A method for starting a new trick in this round
     * given a starting player ofr the new trick.
     *
     * @param startingPlayer The starting player for the new trick.
     */
    private void startNewTrick(Player startingPlayer) {
        Trick newTrick = new Trick(startingPlayer);
        tricks.add(newTrick);
    }

    /* -------------------------------------------------------------------------- */
    /*                                   Getters                                  */
    /* -------------------------------------------------------------------------- */

    /**
     * A getter for the current trick in this round.
     */
    public Trick getCurrentTrick() {
        return tricks.getLast();
    }

    /**
     * Returns true if the current trick is fully played.
     */
    public boolean isCurrentTrickOver() {
        if (tricks.isEmpty()) {
            return false;
        }
        return getCurrentTrick().isOver();
    }

    /**
     * Returns the number of tricks left to be fully played in this round.
     */
    public int getTricksLeft() {
        int playedCount = isCurrentTrickOver() ? tricks.size() : tricks.size() - 1;
        return Round.getTrickCountPerRound() - playedCount;
    }

    /**
     * A getter for the active trump suit in this round.
     * Returns null if there is no active trump (e.g. Miserie, or Abondance before first card).
     */
    public Suit getTrumpSuit() {
        return activeTrumpSuit;
    }

    /**
     * A getter for the last dealt card in this round.
     */
    public Card getLastDealtCard() {
        return lastDealtCard;
    }

    /**
     * A getter for the dealer.
     */
    public Player getDealer() {
        return dealer;
    }

    /**
     * A getter for the highest bid so far.
     */
    public BidType getHighestBid() {
        return highestBid;
    }

    /**
     * A getter for the cards in this trick.
     */
    public HashMap<Player, String> getCardsInTrick() {
        return getCurrentTrick().getCardsAsStrings();
    }

    /**
     * A getter for the current bidding player in this round.
     * This player is the person that's supposed to bid next.
     */
    public Player getCurrentBiddingPlayer() {
        return currentBiddingPlayer;
    }

    /**
     * A getter for the cards of the current player bidding.
     */
    public String[] getCurrentBiddingPlayersCards() {
        return getCurrentBiddingPlayer().getHandCards();
    }

    /**
     * A getter for the cards of the current player,
     * who is currently playing in the current trick.
     */
    public String[] getCurrentPlayingPlayersCards() {
        return getCurrentPlayingPlayer().getHandCards();
    }

    /**
     * A method for finding which cards the current player is legally allowed to play.
     */
    public String[] getAllowedCardsForCurrentPlayer() {
        Suit leadSuit = getCurrentTrick().getLeadSuit();
        return getCurrentPlayingPlayer().getAllowedHandCards(leadSuit)
                .toArray(String[]::new);
    }

    /**
     * Returns the name of the player who would win the current trick
     * based on cards played so far and the active trump suit.
     */
    public String getCurrentTrickWinnerName() {
        return getCurrentTrick().determineWinner(activeTrumpSuit).getName();
    }

    /**
     * Returns the active trump suit name for display.
     * Returns null if there is no active trump yet.
     */
    public String getTrumpSuitName() {
        return activeTrumpSuit == null ? null : activeTrumpSuit.toString();
    }

    /**
     * Returns the original (deck-dealt) trump suit name for display.
     * This never changes during the round.
     */
    public String getOriginalTrumpSuitName() {
        return originalTrumpSuit == null ? null : originalTrumpSuit.toString();
    }

    /**
     * Finds the possible bids for the current bidding player.
     * <p>
     * By GRASP Information Expert, we delegate the logic of determining which bids
     * are still available to the BidType enum, since it knows the bid hierarchy.
     * We only provide the context: what the current highest bid is, and whether
     * a proposal has been made.
     *
     * @return An array of string representations of the available bids.
     */
    public BidType[] getPossibleBids() {
        // Get the BidTypes that can still be played
        return BidType.getAvailableBidTypes(getHighestBid(), hasBeenProposed());
    }

    /**
     * A getter for the current player playing in the current trick.
     */
    public Player getCurrentPlayingPlayer() {
        return getCurrentTrick().getCurrentPlayer();
    }

    /**
     * A getter for the active player's name (either bidding or playing).
     */
    public String getActivePlayerName() {
        if (getFinalBid() == null) {
            return getCurrentBiddingPlayer().getName();
        } else {
            return getCurrentPlayingPlayer().getName();
        }
    }

    /**
     * A getter for the open miserie hands of the players other than the given player.
     * <p>
     * This method dispatches to the Bid class.
     */
    public HashMap<Player, String[]> getOpenMiserieHands(Player currentPlayer) {
        return finalBid.getOpenMiserieHands(currentPlayer);
    }

    /**
     * A getter for finding the cards in the previous trick.
     *
     * @return The cards of the previous trick.
     */
    public LinkedHashMap<Player, String> getCardsFromPreviousTrick() {
        if (tricks.size() <= 1) {
            throw new IllegalStateException("There is no previous trick to show.");
        }
        return tricks.get(tricks.size() - 2).getCardsAsStrings();
    }

    /**
     * Returns the string of the name of the lone proposer.
     *
     * @return The name of the proposer.
     */
    public String getLoneProposerName() {
        if (highestBid == BidType.PROPOSAL) {
            for (Player p : bids.keySet()) {
                if (bids.get(p) == BidType.PROPOSAL) {
                    return p.getName();
                }
            }
        }
        return null;
    }

    /* -------------------------------------------------------------------------- */
    /*                                   Setters                                  */
    /* -------------------------------------------------------------------------- */

    /**
     * A setter for the highest bid in this round.
     */
    protected void setHighestBid(BidType highestBid) {
        this.highestBid = highestBid;
    }

    /**
     * A method for setting the dealer of the cards in this round.
     * The dealer gets chosen from the given list of players.
     *
     * @param players The players playing in this game.
     */
    private void setRandomDealer(ArrayList<Player> players) {
        // The dealer is determined randomly
        Random rand = new Random();
        int index = rand.nextInt(Game.getPlayerCount());
        dealer = players.get(index);
    }

    /**
     * A method for finding and setting the first bidding player.
     */
    private void determineAndSetFirstPlayer() {

        // Determine the dealer
        Player dealer = getDealer();

        // Set the current player to the player next to the dealer
        currentBiddingPlayer = dealer;
        advanceCurrentBiddingPlayer();

    }

    /**
     * A helper method for advancing the current bidding player to the next one in line.
     */
    private void advanceCurrentBiddingPlayer() {
        currentBiddingPlayer = Player.getNextPlayer(getPlayers(), currentBiddingPlayer);
    }

    /**
     * Returns a string showing the bids already made by players this round.
     *
     * @return The string of the existing bids.
     */
    public String getExistingBids() {
        StringBuilder str = new StringBuilder("Currently active bids:");
        for (Player player : bids.keySet()) {
            BidType bid = bids.get(player);
            str.append("\n - ").append(player.getName()).append(": ").append(bid.toString());
        }
        return str.toString();
    }
}
