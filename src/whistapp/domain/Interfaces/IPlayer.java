package whistapp.domain.interfaces;

import whistapp.domain.bids.BidType;
import whistapp.domain.round.RoundContext;

public interface IPlayer {

	String getName();

	/**
	 * Choose which bid to make.
	 * @param context Read-only view of the current round.
	 * @return The chosen bid type.
	 */
	BidType chooseBid(RoundContext context);

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
