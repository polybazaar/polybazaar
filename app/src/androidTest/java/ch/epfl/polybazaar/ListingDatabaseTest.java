package ch.epfl.polybazaar;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.polybazaar.database.datastore.DataStore;
import ch.epfl.polybazaar.database.datastore.DataStoreFactory;
import ch.epfl.polybazaar.database.MockDataStore;
import ch.epfl.polybazaar.database.callback.ListingCallback;
import ch.epfl.polybazaar.database.callback.ListingCallbackAdapter;
import ch.epfl.polybazaar.listing.Listing;
import static ch.epfl.polybazaar.listing.ListingDatabase.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNull;

@RunWith(AndroidJUnit4.class)
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
        Listing inside = new Listing("test", "none", "22CHF", "m.m@epfl.ch");
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


    private boolean storeSuccess;
    private Listing receivedListing;
    @Test
    public void storeAndRetrieveListingTest(){
        MockDataStore myDatastore = new MockDataStore();
        myDatastore.addCollection("listings");
        DataStoreFactory.setDependency(myDatastore);
        Listing testListing = new Listing("testListing","testDescription","testPrice","test@epfl.ch");
        storeListing(testListing, "testId", result -> storeSuccess = result);
        assertThat(storeSuccess,is(true));
        fetchListing("testId", result -> receivedListing = result);
        assertThat(receivedListing.getTitle(),is("testListing"));
        assertThat(receivedListing.getDescription(),is("testDescription"));
        assertThat(receivedListing.getPrice(),is("testPrice"));
        assertThat(receivedListing.getUserEmail(),is("test@epfl.ch"));
    }

    private boolean deleteSuccess;
    @Test
    public void deleteListingTest(){
        Listing testListing = new Listing("testListing","testDescription",
                "testPrice","test@epfl.ch");
        MockDataStore myDataStore = new MockDataStore();
        myDataStore.addCollection("listings");
        myDataStore.setupMockData("listings","testId", testListing);
        DataStoreFactory.setDependency(myDataStore);
        deleteListing("testId", result -> deleteSuccess = result);
        assertThat(deleteSuccess,is(true));
    }

    private Listing wrongListing;
    @Test
    public void wrongIdReturnNull(){
        MockDataStore myDatastore = new MockDataStore();
        myDatastore.addCollection("listings");
        DataStoreFactory.setDependency(myDatastore);
        DataStore db = DataStoreFactory.getDependency();
        fetchListing("wrondId", result -> wrongListing = result);
        assertNull(wrongListing);
    }

}