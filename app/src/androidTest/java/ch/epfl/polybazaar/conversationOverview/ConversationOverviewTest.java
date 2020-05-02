package ch.epfl.polybazaar.conversationOverview;

import org.junit.Test;

import static org.junit.Assert.*;

public class ConversationOverviewTest {
    String otherUser = "other_user@epfl.ch";
    String listingId = "listing_id";

    @SuppressWarnings({"SimplifiableJUnitAssertion", "EqualsWithItself", "ConstantConditions"})
    @Test
    public void testEqualsSpecialCases() {
        ConversationOverview con = new ConversationOverview(otherUser, listingId);
        assertTrue(con.equals(con));
        assertFalse(con.equals(null));
        assertFalse(con.equals(new Object()));
    }

    @Test
    public void testGetterSetter() {
        ConversationOverview con = new ConversationOverview(otherUser, listingId);
        assertEquals(otherUser, con.getSeller());
        assertEquals(listingId, con.getListingID());
    }

    @Test
    public void testHash() {
        ConversationOverview con = new ConversationOverview(otherUser, listingId);
        assertEquals(con.hashCode(), otherUser.hashCode() + listingId.hashCode());
    }
}