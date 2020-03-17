package ch.epfl.polybazaar.litelisting;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.polybazaar.database.callback.LiteListingCallback;
import ch.epfl.polybazaar.database.callback.LiteListingCallbackAdapter;

import static ch.epfl.polybazaar.database.datastore.DataStoreFactory.useMockDataStore;
import static ch.epfl.polybazaar.litelisting.LiteListingDatabase.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNull;

@RunWith(AndroidJUnit4.class)
public class LiteListingDatabaseTest {

    @Test
    public void callListingCallback() {
        final LiteListing[] listing = new LiteListing[1];
        LiteListingCallback callback = new LiteListingCallback() {
            @Override
            public void onCallback(LiteListing result) {
                listing[0] = result;
            }
        };
        LiteListing inside = new LiteListing("test", "none", "22CHF");
        callback.onCallback(inside);
        assertThat(listing[0].getListingID(), is("test"));
        assertThat(listing[0].getTitle(), is("none"));
        assertThat(listing[0].getPrice(), is("22CHF"));
    }

    @Test
    public void callListingCallbackAdapter() {
        final LiteListing[] listing = new LiteListing[1];
        LiteListingCallback callback = new LiteListingCallback() {
            @Override
            public void onCallback(LiteListing result) {
                listing[0] = result;
            }
        };
        LiteListingCallbackAdapter adapter = new LiteListingCallbackAdapter(callback);
    }

    private LiteListing receivedListing;
    private String listingID;
    @Test
    public void storeAndRetrieveListingTest(){
        useMockDataStore();
        LiteListing testListing = new LiteListing("listingID","testTitle","testPrice");
        addLiteListing(testListing, result -> assertThat(result, is(true)));
        fetchLiteListingList(result -> listingID = result.get(0));
        fetchLiteListing(listingID, result -> receivedListing = result);
        assertThat(receivedListing.getTitle(),is("testTitle"));
        assertThat(receivedListing.getPrice(),is("testPrice"));
        assertThat(receivedListing.getListingID(),is("listingID"));
    }

    @Test
    public void deleteListingTest(){
        useMockDataStore();
        LiteListing testListing = new LiteListing("listingID","testTitle","testPrice");
        addLiteListing(testListing, result -> assertThat(result, is(true)));
        fetchLiteListingList(result -> listingID = result.get(0));
        deleteLiteListing(listingID, result -> assertThat(result, is(true)));
        deleteLiteListing("testId2", result -> assertThat(result, is(false)));
    }

    @Test
    public void wrongIdReturnNull(){
        useMockDataStore();
        fetchLiteListing("wrondId", result -> assertNull(result));
    }
}