package ch.epfl.polybazaar.litelisting;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import ch.epfl.polybazaar.litelisting.LiteListing;

import static org.junit.Assert.assertEquals;
public class LiteListingTest {

    LiteListing listing1 = new LiteListing("someID", "Livre d'algèbre linéaire", "22 CHF", "Furniture");

    @Test
    public void checkTitle() {
        assertEquals("Livre d'algèbre linéaire", listing1.getTitle());
    }

    @Test
    public void checkDesc() {
        assertEquals("someID", listing1.getListingID());
    }

    @Test
    public void checkPrice() {
        assertEquals("22 CHF", listing1.getPrice());
    }

}