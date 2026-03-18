package whistapp.domain.bids;

import whistapp.domain.players.Player;

import java.util.ArrayList;

/**
 * Enum for the different types of bids during the bidding phase of the game.
 * <p>
 * After a player makes a bid, the next player can only make a bid with a higher
 * order, except if the last bid was PASS, MISERIE or OPEN_MISERIE: in those cases,
 * the next player can also make the same bid.
 * <p>
 * Abondance bids with a free trump choice (ABONDANCE_X) can be superseded by the
 * corresponding ABONDANCE_X_ORIGINAL_TRUMP bid, which plays in the deck's original
 * trump suit. The original-trump variant is harder and therefore ranked higher.
 */
public enum BidType {

    PASS(1, false),
    PROPOSAL(2, false),
    ACCEPT(3, false),
    ABONDANCE_9(4, true),
    ABONDANCE_9_ORIGINAL_TRUMP(5, false),
    ABONDANCE_10(6, true),
    ABONDANCE_10_ORIGINAL_TRUMP(7, false),
    MISERIE(8, true),
    ABONDANCE_11(9, true),
    ABONDANCE_11_ORIGINAL_TRUMP(10, false),
    ABONDANCE_12(11, true),
    ABONDANCE_12_ORIGINAL_TRUMP(12, false),
    OPEN_MISERIE(13, true),
    SOLO(14, true),
    SOLO_SLIM(15, false);

    /**
     * An integer used to rank bids by order of power.
     * A bid cannot be superseded by a bid of a lower order.
     */
    private final int order;

    /**
     * True if this bid type requires a suited trump to be declared at bid time
     * (i.e. Solo). Abondance declares trump at first-card time, not at bid time.
     */
    private final boolean changesTrumpSuit;

    /* -------------------------------------------------------------------------- */
    /*                                Constructors                                */
    /* -------------------------------------------------------------------------- */

    BidType(int order, boolean changesTrumpSuit) {
        this.order = order;
        this.changesTrumpSuit = changesTrumpSuit;
    }

    /* -------------------------------------------------------------------------- */
    /*                               Public Methods                               */
    /* -------------------------------------------------------------------------- */

    /**
     * A simple getter that determines if a bid can be a round bid.
     * @return {@code true} if this bid can be a round bid, {@code false} otherwise
     */
    public boolean isValidFinalRoundBid() {
        return (this != PASS);
    }

    /**
     * A simple getter that returns all possible bids a player can make as an array
     * @return all bid types as an array
     */
    public static BidType[] getBidTypes() {
        return BidType.values();
    }

    /**
     * A method creating a Bid from a BidType
     * given the declarers of the bid.
     *
     * @param declarers The declarers for this bid.
     * @return A Bid object corresponding to the BidType.
     */
    public Bid createBidFromBidType(ArrayList<Player> declarers) {
        return switch (this) {
            case PASS -> null;
            case PROPOSAL -> new ProposalAlone(declarers.getFirst());
            case ACCEPT -> {
                if (declarers.size() == 2) {
                    yield new ProposalWithPartner(declarers);
                } else {
                    yield new ProposalAlone(declarers.getFirst());
                }
            }
            case ABONDANCE_9 -> new Abondance(declarers.getFirst(), 9, false);
            case ABONDANCE_9_ORIGINAL_TRUMP -> new Abondance(declarers.getFirst(), 9, true);
            case ABONDANCE_10 -> new Abondance(declarers.getFirst(), 10, false);
            case ABONDANCE_10_ORIGINAL_TRUMP -> new Abondance(declarers.getFirst(), 10, true);
            case MISERIE -> new Miserie(declarers);
            case ABONDANCE_11 -> new Abondance(declarers.getFirst(), 11, false);
            case ABONDANCE_11_ORIGINAL_TRUMP -> new Abondance(declarers.getFirst(), 11, true);
            case ABONDANCE_12 -> new Abondance(declarers.getFirst(), 12, false);
            case ABONDANCE_12_ORIGINAL_TRUMP -> new Abondance(declarers.getFirst(), 12, true);
            case OPEN_MISERIE -> new OpenMiserie(declarers);
            case SOLO -> new Solo(declarers.getFirst());
            case SOLO_SLIM -> new SoloSlim(declarers.getFirst());
        };
    }

    /**
     * Checks if the current bid is higher than the given bid.
     *
     * @param otherBid The bid to compare with.
     * @return True if the current bid is higher than the given bid, false otherwise.
     */
    public boolean isHigherBidThan(BidType otherBid) {
        return this.getOrder() > otherBid.getOrder();
    }

    /**
     * Returns true if this BidType is a free-trump Abondance variant.
     * By GRASP Information Expert, BidType itself knows about its own category.
     */
    public boolean isFreeAbondance() {
        return this == ABONDANCE_9 || this == ABONDANCE_10
                || this == ABONDANCE_11 || this == ABONDANCE_12;
    }

    /**
     * A simple getter that determines if this BidType is an abondance variant with the original trump trait,
     * meaning the round will have the trump of the last dealt card, instead of the first played card.
     * @return {@code true} if this BidType is an abondance variant with the original trump trait, {@code false} otherwise
     */
    public boolean isOriginalTrumpAbondance() {
        return this == ABONDANCE_9_ORIGINAL_TRUMP || this == ABONDANCE_10_ORIGINAL_TRUMP
                || this == ABONDANCE_11_ORIGINAL_TRUMP || this == ABONDANCE_12_ORIGINAL_TRUMP;
    }

    /**
     * Finds the corresponding Original Trump variant of a free-trump Abondance bid.
     * Returns the same bid if it's not a free-trump Abondance.
     */
    public BidType getOriginalTrumpVariant() {
        return switch (this) {
            case ABONDANCE_9 -> ABONDANCE_9_ORIGINAL_TRUMP;
            case ABONDANCE_10 -> ABONDANCE_10_ORIGINAL_TRUMP;
            case ABONDANCE_11 -> ABONDANCE_11_ORIGINAL_TRUMP;
            case ABONDANCE_12 -> ABONDANCE_12_ORIGINAL_TRUMP;
            default -> this;
        };
    }

    /* -------------------------------------------------------------------------- */
    /*                                  Getters                                   */
    /* -------------------------------------------------------------------------- */

    /**
     * A simple getter for the order index of this BidType.
     * A bid cannot be superseded by a bid of a lower order.
     * @return the order of this BidType
     */
    public int getOrder() {
        return this.order;
    }

    /**
     * A simple getter for if this BidType requires a suited trump to be declared at bid time (i.e. Solo).
     * Abondance declares trump at first-card time, not at bid time.
     * @return {@code true} if this BidType requires a suited trump to be declared at bid time, {@code false} otherwise
     */
    public boolean changesTrumpSuit() {
        return this.changesTrumpSuit;
    }

    /**
     * Determines which bids are still available to be played.
     * <p>
     * By GRASP Information Expert, BidType knows the hierarchy of bids and the rules
     * of the game, so it is responsible for determining what can be bid next.
     *
     * @param bid             The current highest bid.
     * @param hasBeenProposed Whether a PROPOSAL has already been made.
     * @return An array of available BidTypes.
     */
    public static BidType[] getAvailableBidTypes(BidType bid, boolean hasBeenProposed) {
        if (bid == null) {
            return BidType.values();
        }
        ArrayList<BidType> higherBidTypes = new ArrayList<>();
        for (BidType b : BidType.values()) {
            if (b == PASS) {
                // We can always pass
                higherBidTypes.add(b);
            } else if (b == ACCEPT) {
                // We can only accept if someone else proposed
                if (hasBeenProposed && b.isHigherBidThan(bid))
                    higherBidTypes.add(b);
            } else if (b.isHigherBidThan(bid)) {
                higherBidTypes.add(b);
            } else if (bid == b && (bid == MISERIE || bid == OPEN_MISERIE)) {
                // For open miserie and miserie, these can be bid multiple times
                higherBidTypes.add(b);
            }
            // Note: Abondance no longer self-supersedes. ABONDANCE_X_ORIGINAL_TRUMP
            // supersedes ABONDANCE_X via normal order comparison (it has a higher order).
        }
        return higherBidTypes.toArray(new BidType[0]);
    }

    /* -------------------------------------------------------------------------- */
    /*                                  toString                                  */
    /* -------------------------------------------------------------------------- */

    /**
     * Returns the BidType that will parse to the given string with toString
     * @param string the string equal to the BidType youre looking for
     * @return the BidType that matches {@code returnedBidType.toString() == string}
     */
    public static BidType fromString(String string) {
        for (BidType bid : BidType.values()) {
            if (bid.toString().equals(string)) {
                return bid;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return switch (this) {
            case PASS -> "Pass";
            case PROPOSAL -> "Proposal";
            case ACCEPT -> "Accept";
            case ABONDANCE_9 -> "Abondance 9";
            case ABONDANCE_9_ORIGINAL_TRUMP -> "Abondance 9 (original trump)";
            case ABONDANCE_10 -> "Abondance 10";
            case ABONDANCE_10_ORIGINAL_TRUMP -> "Abondance 10 (original trump)";
            case MISERIE -> "Miserie";
            case ABONDANCE_11 -> "Abondance 11";
            case ABONDANCE_11_ORIGINAL_TRUMP -> "Abondance 11 (original trump)";
            case ABONDANCE_12 -> "Abondance 12";
            case ABONDANCE_12_ORIGINAL_TRUMP -> "Abondance 12 (original trump)";
            case OPEN_MISERIE -> "Open miserie";
            case SOLO -> "Solo";
            case SOLO_SLIM -> "Solo slim";
        };
    }

}
