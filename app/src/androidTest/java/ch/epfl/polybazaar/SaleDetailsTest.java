package ch.epfl.polybazaar;

import android.content.Intent;
import android.widget.TextView;

import androidx.test.rule.ActivityTestRule;

import com.google.android.gms.tasks.Tasks;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.ExecutionException;

import ch.epfl.polybazaar.listing.Listing;
import ch.epfl.polybazaar.login.Authenticator;
import ch.epfl.polybazaar.login.AuthenticatorFactory;
import ch.epfl.polybazaar.login.LoginTest;
import ch.epfl.polybazaar.login.MockAuthenticator;
import ch.epfl.polybazaar.user.User;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread;
import static ch.epfl.polybazaar.database.datastore.DataStoreFactory.useMockDataStore;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SaleDetailsTest {
    private static final int TOAST_LONG_DELAY = 3500;

    @Rule
    public final ActivityTestRule<SaleDetails> activityRule =
            new ActivityTestRule<>(
                    SaleDetails.class,
                    true,
                    false);

    @Test
    public void testNoBundlePassed () throws InterruptedException {
        activityRule.launchActivity(new Intent());

        onView(withText("Object not found."))
                .inRoot(withDecorView(not(activityRule.getActivity().getWindow().getDecorView())))
                .check(matches(isDisplayed()));
        Thread.sleep(TOAST_LONG_DELAY);
    }

    @Before
    public void init() {
        useMockDataStore();
        AuthenticatorFactory.setDependency(MockAuthenticator.getInstance());
    }

    @After
    public void cleanup() {
        MockAuthenticator.getInstance().reset();
    }

    @Test
    public void testFillWithListingAndGetSellerInfo() throws Throwable {
        Intent intent = new Intent();
        activityRule.launchActivity(intent);

        final Listing listing = new Listing("Algebre linéaire by David C. Lay", "Very good book.", "23", "gu.vrut@epfl.ch", "Furniture");
        runOnUiThread(() -> activityRule.getActivity().fillWithListing(listing));

        TextView textTitle = activityRule.getActivity().findViewById(R.id.title);
        assertEquals("Algebre linéaire by David C. Lay", textTitle.getText().toString());

        TextView textDescr = activityRule.getActivity().findViewById(R.id.description);
        assertEquals("Very good book.", textDescr.getText().toString());

        TextView textPrice = activityRule.getActivity().findViewById(R.id.price);
        assertEquals("CHF 23", textPrice.getText().toString());
    }

    @Test
    public void testWithMockListing() throws ExecutionException, InterruptedException {
        Intent intent = new Intent();

        Listing newListing = new Listing("Title", "description", "0.0", "test.user@epfl.ch", "");
        Tasks.await(newListing.saveWithLiteVersion());

        intent.putExtra("listingID", newListing.getId());

        activityRule.launchActivity(intent);

        TextView textTitle = activityRule.getActivity().findViewById(R.id.title);
        assertEquals("Title", textTitle.getText().toString());

        TextView textDescr = activityRule.getActivity().findViewById(R.id.description);
        assertEquals("description", textDescr.getText().toString());

        TextView textPrice = activityRule.getActivity().findViewById(R.id.price);
        assertEquals("CHF 0.0", textPrice.getText().toString());
    }

    @Test
    public void favoriteButtonIsDisabledForUnauthenticatedUsers() throws ExecutionException, InterruptedException {

        Listing listing = new Listing("random", "blablabla", "20.00", LoginTest.EMAIL, "");

        Tasks.await(listing.save());
        String id = listing.getId();
        Intent intent = new Intent();
        intent.putExtra("listingID", id);

        activityRule.launchActivity(intent);

        onView(withText(R.string.add__favorite)).check(matches(not(isEnabled())));
    }

    @Test
    public void favoriteButtonChangesFavorites() throws ExecutionException, InterruptedException {
        Authenticator auth = AuthenticatorFactory.getDependency();

        Tasks.await(auth.createUser("user.test@epfl.ch", "usert", "abcdef"));

        Listing listing = new Listing("random", "blablabla", "20.00", LoginTest.EMAIL, "");

        Tasks.await(listing.save());
        String id = listing.getId();
        Intent intent = new Intent();
        intent.putExtra("listingID", id);

        activityRule.launchActivity(intent);

        onView(withText(R.string.add__favorite)).perform(click());

        // we fetch after each click to make sure the data is actually saved to mock db
        User.fetch(MockAuthenticator.TEST_USER_EMAIL).addOnSuccessListener(user -> {
            assertTrue(user.getFavorites().contains(listing.getId()));
        });

        onView(withText(R.string.remove_favorites)).perform(click());

        User.fetch(MockAuthenticator.TEST_USER_EMAIL).addOnSuccessListener(user -> {
            assertFalse(user.getFavorites().contains(listing.getId()));
        });
    }
}

