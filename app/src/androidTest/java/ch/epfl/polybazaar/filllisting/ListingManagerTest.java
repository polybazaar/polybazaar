package ch.epfl.polybazaar.filllisting;

import android.content.Intent;

import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.rule.ActivityTestRule;

import com.google.android.gms.tasks.Tasks;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.ExecutionException;

import ch.epfl.polybazaar.R;
import ch.epfl.polybazaar.UI.FillListing;
import ch.epfl.polybazaar.category.RootCategoryFactory;
import ch.epfl.polybazaar.listing.Listing;
import ch.epfl.polybazaar.litelisting.LiteListing;
import ch.epfl.polybazaar.login.Authenticator;
import ch.epfl.polybazaar.login.AuthenticatorFactory;
import ch.epfl.polybazaar.login.MockAuthenticator;
import ch.epfl.polybazaar.user.User;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread;
import static ch.epfl.polybazaar.database.datastore.DataStoreFactory.useMockDataStore;
import static org.junit.Assert.assertEquals;
import static androidx.test.espresso.Espresso.pressBack;

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
        RootCategoryFactory.useMockCategory();
        AuthenticatorFactory.setDependency(MockAuthenticator.getInstance());
        Authenticator authenticator = AuthenticatorFactory.getDependency();
        User testUser = new User("nickname", MockAuthenticator.TEST_USER_EMAIL);
        testUser.save();
        authenticator.signIn(MockAuthenticator.TEST_USER_EMAIL, MockAuthenticator.TEST_USER_PASSWORD);
    }

    @Test
    public void testDeleteOldListingAndSubmitNewOne() throws Throwable {

        Intents.init();
        String id = "listingID";
        final Listing listing = new Listing("Title", "Description", "0", MockAuthenticator.TEST_USER_EMAIL,
                "", "Video games", 1.0, 1.0);
        listing.setId(id);
        Tasks.await(listing.saveWithLiteVersion());

        Intent intent = new Intent();
        intent.putExtra("listingID", listing.getId());
        intent.putExtra("listing", listing);
        activityRule.launchActivity(intent);
        onView(withId(R.id.action_home)).perform(click());
        onView(withText("Title")).perform(click());
        Thread.sleep(500);
        onView(withText(R.string.edit)).perform(scrollTo(), click());
        String newTitle = "new title";
        onView(withId(R.id.titleSelector)).perform(scrollTo(), clearText(), typeText(newTitle));
        closeSoftKeyboard();
        runOnUiThread(() -> activityRule.getActivity().findViewById(R.id.selectCategory).performClick());
        Thread.sleep(500);
        onView(withId(R.id.categoriesRecycler)).perform(RecyclerViewActions.actionOnItemAtPosition(0,click()));
        pressBack();
        Thread.sleep(500);
        runOnUiThread(() -> activityRule.getActivity().findViewById(R.id.categoryButton).performClick());
        runOnUiThread(() -> activityRule.getActivity().findViewById(R.id.submitListing).performClick());
        //wait that the listing has been updated
        Thread.sleep(1000);
        Tasks.await(Listing.fetch(id).addOnSuccessListener(result -> assertEquals(newTitle, result.getTitle())));
        Intents.release();
    }

}