package ch.epfl.polybazaar.filllisting;

import android.content.Intent;

import androidx.test.rule.ActivityTestRule;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.ExecutionException;

import ch.epfl.polybazaar.R;
import ch.epfl.polybazaar.UI.FillListing;
import ch.epfl.polybazaar.UI.SaleDetails;
import ch.epfl.polybazaar.listing.Listing;
import ch.epfl.polybazaar.login.AuthenticatorFactory;
import ch.epfl.polybazaar.login.MockAuthenticator;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static ch.epfl.polybazaar.database.datastore.DataStoreFactory.useMockDataStore;
import static org.junit.Assert.*;

public class ListingManagerTest {
    @Rule
    public final ActivityTestRule<FillListing> activityRule =
            new ActivityTestRule<>(
                    FillListing.class,
                    true,
                    false);

    @Before
    public void init() {
        useMockDataStore();
        //AuthenticatorFactory.setDependency(MockAuthenticator.getInstance());
    }

    @After
    public void cleanup() {
        //MockAuthenticator.getInstance().reset();
    }

    @Test
    public void testDeleteOldListingAndSubmitNewOne() throws ExecutionException, InterruptedException {
        String id = "listingID";
        final Listing listing = new Listing("Title", "Description", "0", "otherUser@epfl.ch",
                "", "", 1.0, 1.0);
        listing.setId(id);
        Tasks.await(listing.save());

        Intent intent = new Intent();
        intent.putExtra("listingID", listing.getId());
        intent.putExtra("listing", listing);
        activityRule.launchActivity(intent);

        String newTitle = "new title";
        onView(withId(R.id.titleSelector)).perform(scrollTo(), clearText(), typeText(newTitle), closeSoftKeyboard());
        onView(withId(R.id.submitListing)).perform(scrollTo(), click());

        Tasks.await(Listing.fetch(id).addOnSuccessListener(result -> assertEquals(newTitle, result.getTitle())));
    }

}