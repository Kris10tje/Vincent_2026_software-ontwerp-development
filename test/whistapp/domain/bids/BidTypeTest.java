package whistapp.domain.bids;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BidTypeTest {

    @Test
    void testIsHigherBidThan() {
        // Higher bids should return true
        assertTrue(BidType.PROPOSAL.isHigherBidThan(BidType.PASS));
        assertTrue(BidType.SOLO_SLIM.isHigherBidThan(BidType.SOLO));
        assertTrue(BidType.OPEN_MISERIE.isHigherBidThan(BidType.MISERIE));

        // Lower bids should return false
        assertFalse(BidType.PASS.isHigherBidThan(BidType.PROPOSAL));
        assertFalse(BidType.ABONDANCE_9.isHigherBidThan(BidType.ABONDANCE_11));

        // Equal bids should return false
        assertFalse(BidType.MISERIE.isHigherBidThan(BidType.MISERIE));
    }

    @Test
    void testGetOrder() {
        assertEquals(1, BidType.PASS.getOrder());
        assertEquals(8, BidType.MISERIE.getOrder());
        assertEquals(15, BidType.SOLO_SLIM.getOrder());
        // Free Abondance orders
        assertEquals(4, BidType.ABONDANCE_9.getOrder());
        assertEquals(6, BidType.ABONDANCE_10.getOrder());
        // Original-trump Abondance sits one above the corresponding free variant
        assertEquals(5, BidType.ABONDANCE_9_ORIGINAL_TRUMP.getOrder());
        assertEquals(7, BidType.ABONDANCE_10_ORIGINAL_TRUMP.getOrder());
        // Original-trump is always higher than its free-trump counterpart
        assertTrue(BidType.ABONDANCE_9_ORIGINAL_TRUMP.isHigherBidThan(BidType.ABONDANCE_9));
        assertTrue(BidType.ABONDANCE_12_ORIGINAL_TRUMP.isHigherBidThan(BidType.ABONDANCE_12));
    }

    @Test
    void testToString() {
        assertEquals("Pass", BidType.PASS.toString());
        assertEquals("Proposal", BidType.PROPOSAL.toString());
        assertEquals("Abondance 9", BidType.ABONDANCE_9.toString());
        assertEquals("Abondance 9 (original trump)", BidType.ABONDANCE_9_ORIGINAL_TRUMP.toString());
        assertEquals("Abondance 10", BidType.ABONDANCE_10.toString());
        assertEquals("Abondance 10 (original trump)", BidType.ABONDANCE_10_ORIGINAL_TRUMP.toString());
        assertEquals("Abondance 11", BidType.ABONDANCE_11.toString());
        assertEquals("Abondance 11 (original trump)", BidType.ABONDANCE_11_ORIGINAL_TRUMP.toString());
        assertEquals("Abondance 12", BidType.ABONDANCE_12.toString());
        assertEquals("Abondance 12 (original trump)", BidType.ABONDANCE_12_ORIGINAL_TRUMP.toString());
        assertEquals("Miserie", BidType.MISERIE.toString());
        assertEquals("Open miserie", BidType.OPEN_MISERIE.toString());
        assertEquals("Solo", BidType.SOLO.toString());
        assertEquals("Solo slim", BidType.SOLO_SLIM.toString());
    }

    @Test
    void testIsFreeAbondance() {
        assertTrue(BidType.ABONDANCE_9.isFreeAbondance());
        assertTrue(BidType.ABONDANCE_12.isFreeAbondance());
        assertFalse(BidType.ABONDANCE_9_ORIGINAL_TRUMP.isFreeAbondance());
        assertFalse(BidType.SOLO.isFreeAbondance());
    }

    @Test
    void testIsOriginalTrumpAbondance() {
        assertTrue(BidType.ABONDANCE_9_ORIGINAL_TRUMP.isOriginalTrumpAbondance());
        assertTrue(BidType.ABONDANCE_12_ORIGINAL_TRUMP.isOriginalTrumpAbondance());
        assertFalse(BidType.ABONDANCE_9.isOriginalTrumpAbondance());
        assertFalse(BidType.SOLO.isOriginalTrumpAbondance());
    }
}