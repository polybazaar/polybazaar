package ch.epfl.polybazaar;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.polybazaar.UI.LiteListingAdapter;
import ch.epfl.polybazaar.litelisting.LiteListing;

import static org.junit.Assert.assertEquals;

public class LiteListingAdapterTest {

        LiteListingAdapter liteListingAdapter;

        @Before
        public void init() {
            LiteListing liteListing1 = new LiteListing("1", "listing1", "CHF 1", "Furniture");
            LiteListing liteListing2 = new LiteListing("2", "listing2", "CHF 1", "Furniture");
            LiteListing liteListing3 = new LiteListing("3", "listing3", "CHF 1", "Furniture");

            List<LiteListing> liteListingList = new ArrayList<>();
            liteListingList.add(liteListing1);
            liteListingList.add(liteListing2);
            liteListingList.add(liteListing3);

            liteListingAdapter = new LiteListingAdapter(liteListingList);
        }


        @Test
        public void getItemCountTest() {
            assertEquals(3, liteListingAdapter.getItemCount());
        }

}
