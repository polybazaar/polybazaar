package ch.epfl.polybazaar;

import org.junit.Test;

import ch.epfl.polybazaar.listing.Listing;

import static org.junit.Assert.assertEquals;

public class ListingTest {

    Listing listing1 = new Listing("Livre", "Livre d'algèbre linéaire", "22 CHF", "test.user@epfl.ch");

    @Test
    public void checkTitle() {
        assertEquals("Livre", listing1.getTitle());
    }

    @Test
    public void checkDesc() {
        assertEquals("Livre d'algèbre linéaire", listing1.getDescription());
    }


    @Test
    public void checkPrice() {
        assertEquals("22 CHF", listing1.getPrice());
    }

    @Test
    public void checkEmail() {
        assertEquals("test.user@epfl.ch", listing1.getUserEmail());
    }
}
