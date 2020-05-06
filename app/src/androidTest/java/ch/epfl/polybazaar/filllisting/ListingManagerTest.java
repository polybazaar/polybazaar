package ch.epfl.polybazaar.filllisting;

import android.content.Intent;

import androidx.test.rule.ActivityTestRule;

import com.google.android.gms.tasks.Tasks;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.ExecutionException;

import ch.epfl.polybazaar.R;
import ch.epfl.polybazaar.UI.FillListing;
import ch.epfl.polybazaar.listing.Listing;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread;
import static ch.epfl.polybazaar.database.datastore.DataStoreFactory.useMockDataStore;
import static org.junit.Assert.assertEquals;

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
    }

    @Test
    public void testDeleteOldListingAndSubmitNewOne() throws Throwable {
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
        onView(withId(R.id.titleSelector)).perform(scrollTo(), clearText(), typeText(newTitle));
        closeSoftKeyboard();
        runOnUiThread(() -> activityRule.getActivity().findViewById(R.id.submitListing).performClick());
        //wait that the listing has been updated
        Thread.sleep(1000);
        Tasks.await(Listing.fetch(id).addOnSuccessListener(result -> assertEquals(newTitle, result.getTitle())));
    }

}