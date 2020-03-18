package ch.epfl.polybazaar.litelisting;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

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
    public void storeAndRetrieveLiteListingTest(){
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
    public void deleteLiteListingTest(){
        useMockDataStore();
        LiteListing testListing = new LiteListing("listingID","testTitle","testPrice");
        addLiteListing(testListing, result -> assertThat(result, is(true)));
        fetchLiteListingList(result -> listingID = result.get(0));
        deleteLiteListing(listingID, result -> assertThat(result, is(true)));
        deleteLiteListing("testId2", result -> assertThat(result, is(false)));
    }

    @Test
    public void wrongLiteListingIdReturnNull(){
        useMockDataStore();
        fetchLiteListing("wrondId", Assert::assertNull);
    }

    private List<String> list;
    @Test
    public void canQueryCorrectly(){
        useMockDataStore();
        LiteListing testListing1 = new LiteListing("listingID1","testTitle1","32");
        LiteListing testListing2 = new LiteListing("listingID2","testTitle2","22");
        LiteListing testListing3 = new LiteListing("listingID3","testTitle3","21");
        addLiteListing(testListing1, result -> assertThat(result, is(true)));
        addLiteListing(testListing2, result -> assertThat(result, is(true)));
        addLiteListing(testListing3, result -> assertThat(result, is(true)));
        queryLiteListingStringEquality("price", "21", result -> list = result);
        assertThat(list.size(), is(1));
        fetchLiteListing(list.get(0), result -> assertThat(result.getListingID(), is("listingID3")));
    }
}