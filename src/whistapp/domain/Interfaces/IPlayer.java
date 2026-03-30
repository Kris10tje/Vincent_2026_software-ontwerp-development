package whistapp.domain.interfaces;

import whistapp.domain.bids.BidTypeWithTrump;
import whistapp.domain.round.Context.BidContext;
import whistapp.domain.round.Context.RoundContext;

public interface IPlayer {

	String getName();

	/**
	 * Choose which bid to make.
	 * @param context Read-only view of the current round.
	 * @return The chosen bid type.
	 */
	BidTypeWithTrump chooseBid(BidContext context);

	/**
	 * Choose which card to play.
	 * @param context Read-only view of the current round.
	 * @return The chosen card.
	 */
	ICard chooseCard(RoundContext context);

	/**
	 * Whether this strategy is autonomous (no human input needed).
	 */
	boolean isAutonomous();

}
