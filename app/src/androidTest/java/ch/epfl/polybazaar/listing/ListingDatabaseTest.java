package ch.epfl.polybazaar.listing;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import ch.epfl.polybazaar.database.callback.ListingCallback;
import ch.epfl.polybazaar.database.callback.ListingCallbackAdapter;

import static ch.epfl.polybazaar.listing.ListingDatabase.*;
import static ch.epfl.polybazaar.database.datastore.DataStoreFactory.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNull;

@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ListingDatabaseTest {

    @Test
    public void callListingCallback() {
        final Listing[] listing = new Listing[1];
        ListingCallback callback = new ListingCallback() {
            @Override
            public void onCallback(Listing result) {
                listing[0] = result;
            }
        };
        Listing inside = new Listing("test", "none", "22CHF", "m.m@epfl.ch", "Furniture");
        callback.onCallback(inside);
        assertThat(listing[0].getTitle(), is("test"));
        assertThat(listing[0].getUserEmail(), is("m.m@epfl.ch"));
        assertThat(listing[0].getDescription(), is("none"));
        assertThat(listing[0].getPrice(), is("22CHF"));
    }

    @Test
    public void callListingCallbackAdapter() {
        final Listing[] listing = new Listing[1];
        ListingCallback callback = new ListingCallback() {
            @Override
            public void onCallback(Listing result) {
                listing[0] = result;
            }
        };
        ListingCallbackAdapter adapter = new ListingCallbackAdapter(callback);
    }

    private Listing receivedListing;
    @Test
    public void storeAndRetrieveListingTest(){
        useMockDataStore();
        Listing testListing = new Listing("testListing","testDescription","testPrice","test@epfl.ch", "Furniture");
        storeListing(testListing, "testId", result -> assertThat(result, is(true)));
        fetchListing("testId", result -> receivedListing = result);
        assertThat(receivedListing.getTitle(),is("testListing"));
        assertThat(receivedListing.getDescription(),is("testDescription"));
        assertThat(receivedListing.getPrice(),is("testPrice"));
        assertThat(receivedListing.getUserEmail(),is("test@epfl.ch"));
    }

    @Test
    public void deleteListingTest(){
        useMockDataStore();
        Listing testListing = new Listing("testListing","testDescription",
                "testPrice","test@epfl.ch", "Furniture");
        storeListing(testListing, "testId", result -> assertThat(result, is(true)));
        deleteListing("testId", result -> assertThat(result, is(true)));
        deleteListing("testId2", result -> assertThat(result, is(false)));
    }

    @Test
    public void wrongListingIdReturnNull(){
        useMockDataStore();
        fetchListing("wrondId", Assert::assertNull);
    }

    @Test
    public void canQueryCorrectly(){
        useMockDataStore();
        Listing testListing1 = new Listing("testListing1","testDescription",
                "22","test@epfl.ch", "Furniture");
        Listing testListing2 = new Listing("testListing2","testDescription",
                "testPrice2","test@epfl.ch", "Furniture");
        Listing testListing3 = new Listing("testListing3","testDescription",
                "22","test@epfl.ch", "Furniture");
        storeListing(testListing1, "testId1", result -> assertThat(result, is(true)));
        storeListing(testListing2, "testId2", result -> assertThat(result, is(true)));
        storeListing(testListing3, "testId3", result -> assertThat(result, is(true)));
        queryListingStringEquality("price", "22", result -> assertThat(result, containsInAnyOrder("testId1", "testId3")));
    }

}