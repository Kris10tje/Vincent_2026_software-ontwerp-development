package whistapp.usecase;

import org.junit.jupiter.api.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;

import whistapp.application.interfaces.IController;
import whistapp.application.interfaces.IPlayGameController;
import whistapp.domain.cards.Suit;
import whistapp.domain.cards.Value;
import whistapp.domain.interfaces.ICard;
import whistapp.domain.interfaces.IPlayer;
import whistapp.ui.IInputOutputProvider;
import whistapp.ui.TestClasses.TestPlayGameCLI;

/**
 * Scenario tests for Use Case 3: View last played trick.
 *
 * <p>Each test maps to a numbered step in the assignment UC description.
 * Comments like {@code // step 4a.} indicate which UC step is exercised.
 *
 * <p>Mock strategy:
 * - One human player and three bots. The human player plays exactly one card,
 *   then requests to view the last trick.
 * - {@code mockGame.isAutonomous()} returns {@code false} only for the human player index (0).
 * - {@code mockGame.getPreviousTrickCards()} returns cards that should be displayed on request.
 * - The human selects "View Last Trick" (the last option shown by the CLI).
 */
public class ViewLastPlayedTrickTest {

    private IInputOutputProvider mockInputOutput;
    private IController mockController;
    private IPlayGameController mockGame;
    private TestPlayGameCLI cli;
    private IPlayer humanPlayer;

    public ViewLastPlayedTrickTest() {
        mockInputOutput = mock(IInputOutputProvider.class);
        mockController = mock(IController.class);
        mockGame = mock(IPlayGameController.class);

        humanPlayer = mockPlayer("Human");
        cli = new TestPlayGameCLI(mockController, mockInputOutput);
        cli.setGame(mockGame);
    }

    @Test
    @DisplayName("Precondition: at least one trick has been played")
    void testPrecondition_previousTrickCardsExist() {
        // step (precondition).: The system only allows viewing the last trick
        // if at least one trick has already been played.

        // Arrange: a previous trick has been played and is available
        ICard card1 = mockCard(Value.SEVEN, Suit.HEARTS);
        ICard card2 = mockCard(Value.ACE, Suit.SPADES);

        IPlayer bot1 = mockPlayer("Bot1");
        IPlayer bot2 = mockPlayer("Bot2");

        LinkedHashMap<IPlayer, ICard> previousTrick = new LinkedHashMap<>();
        previousTrick.put(bot1, card1);
        previousTrick.put(bot2, card2);

        when(mockGame.getPreviousTrickCards()).thenReturn(previousTrick);

        // Assert: the previous trick is non-empty (precondition is met)
        assert !mockGame.getPreviousTrickCards().isEmpty();

        verify(mockGame, atLeastOnce()).getPreviousTrickCards();
    }

    @Test
    @DisplayName("Steps 1-2: Human selects 'View Last Trick' and system shows previous trick cards")
    void testSteps1to2_humanRequestsAndSystemShowsLastTrick() {
        // step 1.: The user selects "View last trick" from the card-play menu.
        // step 2.: The system shows the previous trick.

        // Arrange
        ICard card1 = mockCard(Value.SEVEN, Suit.HEARTS);
        ICard card2 = mockCard(Value.ACE, Suit.SPADES);
        IPlayer bot1 = mockPlayer("Bot1");

        LinkedHashMap<IPlayer, ICard> prevTrick = new LinkedHashMap<>();
        prevTrick.put(bot1, card1);

        when(mockGame.getPreviousTrickCards()).thenReturn(prevTrick);

        // The human player is at index 0: not autonomous
        when(mockGame.isAutonomous(anyInt())).thenReturn(true); // all others are bots
        when(mockGame.isAutonomous(0)).thenReturn(false);

        // Trick loop: 3 bots (autonomous) + 1 human acting once (view, then play)
        when(mockGame.isTrickOver()).thenReturn(
            false, false, false, // 3 bots
            false,               // 1 human (view then play)
            true                 // end
        );
        when(mockGame.getActivePlayerIndex()).thenReturn(1, 2, 3, 0);

        // Setup the active human player
        when(mockGame.getActivePlayer()).thenReturn(humanPlayer);
        when(mockGame.getOriginalTrumpSuit()).thenReturn(Suit.HEARTS);
        when(mockGame.getTrumpSuit()).thenReturn(Suit.HEARTS);
        when(mockGame.getCurrentTrickCards()).thenReturn(new LinkedHashMap<>());
        when(mockGame.getOpenMiserieHands()).thenReturn(new HashMap<>());

        // Human has one card in hand
        ArrayList<ICard> hand = new ArrayList<>();
        hand.add(card1);
        when(mockGame.getCardsForCurrentPlayer()).thenReturn(hand);
        when(mockGame.getAllowedCardsForCurrentPlayer()).thenReturn(hand);

        when(mockInputOutput.readLine()).thenReturn(
                "",           // showRound(): Bidding phase ended
                "",           // Iteration 4: getReady()
                "2",          // Iteration 4: getChoice() -> "View Last Trick"
                "",           // Iteration 4: showLastTrick() return
                "1",          // Iteration 4: getChoice() -> Card #1 (matches after view)
                "",           // End of trick: "Press enter to continue"
                "", "", "", "", "" // Safety padding
        );

        // Also set up trick termination: 1 trick remains, then it's over
        when(mockGame.evaluateAndAdvanceTrick()).thenReturn(false, true);
        when(mockGame.getCurrentTrickWinner()).thenReturn(bot1);
        when(mockGame.biddingStabilised()).thenReturn(true);
        when(mockGame.getLoneProposer()).thenReturn(null);
        when(mockGame.getDealer()).thenReturn(humanPlayer);
        when(mockGame.getFinalBidName()).thenReturn("Pass");
        when(mockGame.getFinalBidDeclarers()).thenReturn(new IPlayer[]{});
        when(mockGame.getScoresPerPlayer()).thenReturn(new HashMap<>());
        when(mockGame.getPlayers()).thenReturn(new ArrayList<>(Arrays.asList(humanPlayer)));

        // Act
        cli.callShowRound();

        // step 2.: Verify the previous trick cards are shown
        verify(mockInputOutput).writeLine(contains("Last trick (in order of play):"));
        verify(mockInputOutput).writeLine(contains("Press enter to return."));
    }

    @Test
    @DisplayName("Step 3: After viewing the trick, the human continues playing")
    void testStep3_gameResumesAfterViewingTrick() {
        // step 3.: After viewing the trick, the system returns the human to the card-play menu.

        ICard card1 = mockCard(Value.SEVEN, Suit.HEARTS);
        IPlayer bot1 = mockPlayer("Bot1");

        LinkedHashMap<IPlayer, ICard> prevTrick = new LinkedHashMap<>();
        prevTrick.put(bot1, card1);

        when(mockGame.getPreviousTrickCards()).thenReturn(prevTrick);
        when(mockGame.isAutonomous(anyInt())).thenReturn(true);
        when(mockGame.isAutonomous(0)).thenReturn(false);
        when(mockGame.isTrickOver()).thenReturn(
            false, false, false, // 3 bots
            false,               // 1 human (view then play)
            true                 // end
        );
        when(mockGame.getActivePlayerIndex()).thenReturn(1, 2, 3, 0);

        when(mockInputOutput.readLine()).thenReturn(
                "",           // showRound(): Bidding phase ended
                "",           // Iteration 4: getReady()
                "2",          // Iteration 4: getChoice() -> "View Last Trick"
                "",           // Iteration 4: showLastTrick() return
                "1",          // Iteration 4: getChoice() -> Card #1 (matches after view)
                "",           // End of trick: "Press enter to continue"
                "", "", "", "", "" // Safety padding
        );
        when(mockGame.getActivePlayer()).thenReturn(humanPlayer);
        when(mockGame.getOriginalTrumpSuit()).thenReturn(Suit.HEARTS);
        when(mockGame.getTrumpSuit()).thenReturn(Suit.HEARTS);
        when(mockGame.getCurrentTrickCards()).thenReturn(new LinkedHashMap<>());
        when(mockGame.getOpenMiserieHands()).thenReturn(new HashMap<>());

        ArrayList<ICard> hand = new ArrayList<>();
        hand.add(card1);
        when(mockGame.getCardsForCurrentPlayer()).thenReturn(hand);
        when(mockGame.getAllowedCardsForCurrentPlayer()).thenReturn(hand);

        when(mockGame.getCurrentTrickWinner()).thenReturn(bot1);
        when(mockGame.evaluateAndAdvanceTrick()).thenReturn(false, true);
        when(mockGame.biddingStabilised()).thenReturn(true);
        when(mockGame.getLoneProposer()).thenReturn(null);
        when(mockGame.getDealer()).thenReturn(humanPlayer);
        when(mockGame.getFinalBidName()).thenReturn("Pass");
        when(mockGame.getFinalBidDeclarers()).thenReturn(new IPlayer[]{});
        when(mockGame.getScoresPerPlayer()).thenReturn(new HashMap<>());
        when(mockGame.getPlayers()).thenReturn(new ArrayList<>(Arrays.asList(humanPlayer)));



        // Act
        cli.callShowRound();

        // step 3.: The human's card play is processed after returning from trick view
        verify(mockGame).processCardPlay(any(ICard.class));
        verify(mockInputOutput, times(2)).writeLine(contains("Winner of this trick:"));
    }

    /* ---------------------------------------------------------------------- */

    private IPlayer mockPlayer(String name) {
        IPlayer p = mock(IPlayer.class);
        when(p.getName()).thenReturn(name);
        return p;
    }

    private ICard mockCard(Value value, Suit suit) {
        ICard card = mock(ICard.class);
        when(card.getValue()).thenReturn(value);
        when(card.getSuit()).thenReturn(suit);
        when(card.toString()).thenReturn(value + " of " + suit);
        return card;
    }
}
