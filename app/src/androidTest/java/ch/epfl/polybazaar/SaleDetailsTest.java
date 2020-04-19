package ch.epfl.polybazaar;

import android.content.Intent;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.test.rule.ActivityTestRule;

import com.google.android.gms.tasks.Tasks;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.ExecutionException;

import ch.epfl.polybazaar.listing.Listing;
import ch.epfl.polybazaar.listingImage.ListingImage;
import ch.epfl.polybazaar.litelisting.LiteListing;
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
import static ch.epfl.polybazaar.Utilities.convertBitmapToString;
import static ch.epfl.polybazaar.Utilities.convertDrawableToBitmap;
import static ch.epfl.polybazaar.database.datastore.DataStoreFactory.useMockDataStore;
import static ch.epfl.polybazaar.listing.ListingDatabase.storeListing;
import static ch.epfl.polybazaar.listingImage.ListingImageDatabase.storeListingImage;
import static ch.epfl.polybazaar.litelisting.LiteListingDatabase.addLiteListing;
import static java.util.UUID.randomUUID;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SaleDetailsTest {
    private static final int TOAST_LONG_DELAY = 3500;
    private final int SLEEP_TIME = 2000;

    @Rule
    public final ActivityTestRule<SaleDetails> activityRule =
            new ActivityTestRule<>(
                    SaleDetails.class,
                    true,
                    false);

    /**
     * This test will not be relevant with the new UI anymore
     */
    /*
    @Test
    public void testNoBundlePassed () throws InterruptedException {
        Thread.sleep(SLEEP_TIME);
        activityRule.launchActivity(new Intent());

        onView(withText(R.string.object_not_found))
                .inRoot(withDecorView(not(activityRule.getActivity().getWindow().getDecorView())))
                .check(matches(isDisplayed()));
        Thread.sleep(TOAST_LONG_DELAY);
    }
    */

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
    public void testWithMockListing() {

        String listingID1 = randomUUID().toString();
        String listingID2 = randomUUID().toString();

        Intent intent = new Intent();
        intent.putExtra("listingID", listingID1);

        activityRule.launchActivity(intent);
        Listing newListing = new Listing("Title", "description", "0.0", "test.user@epfl.ch", "");
        LiteListing newLiteListing = new LiteListing(listingID1, newListing.getTitle(), newListing.getPrice(), newListing.getCategory());
        ListingImage listingImage1 = new ListingImage(convertBitmapToString(convertDrawableToBitmap(ContextCompat.getDrawable(activityRule.getActivity(), R.drawable.bicycle))), listingID2);
        ListingImage listingImage2 = new ListingImage(convertBitmapToString(convertDrawableToBitmap(ContextCompat.getDrawable(activityRule.getActivity(), R.drawable.bicycle))), "");

        storeListing(newListing, listingID1, result -> {
            assertEquals(true, result);
            addLiteListing(newLiteListing, resultLite -> {
                assertEquals(true, resultLite);
                storeListingImage(listingImage1, listingID1, resultImage1 -> {
                    assertEquals(true, resultImage1);
                    storeListingImage(listingImage2, listingID2, resultImage2 -> {
                        assertEquals(true, resultImage2);
                        try {
                            runOnUiThread(() -> {
                                //recreate to load the new Listing
                                activityRule.getActivity().recreate();
                            });
                        } catch (Throwable throwable) {
                            throwable.printStackTrace();
                        }
                        try {
                            Thread.sleep(SLEEP_TIME);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        TextView textTitle = activityRule.getActivity().findViewById(R.id.title);
                        assertEquals("Title", textTitle.getText().toString());

                        TextView textDescr = activityRule.getActivity().findViewById(R.id.description);
                        assertEquals("description", textDescr.getText().toString());

                        TextView textPrice = activityRule.getActivity().findViewById(R.id.price);
                        assertEquals("CHF 0.0", textPrice.getText().toString());
                    });
                });
            });
        });
    }

    @Test
    public void favoriteButtonIsDisabledForUnauthenticatedUsers() throws ExecutionException, InterruptedException {

        Listing listing = new Listing("random", "blablabla", "20.00", LoginTest.EMAIL, "");

        Tasks.await(listing.save());
        String id = listing.getId();
        Intent intent = new Intent();
        intent.putExtra("listingID", id);

        activityRule.launchActivity(intent);

        onView(withText(R.string.add_favorite)).check(matches(not(isEnabled())));
    }

    @Test
    public void favoriteButtonChangesFavorites() throws ExecutionException, InterruptedException {
        Authenticator auth = AuthenticatorFactory.getDependency();

        Tasks.await(auth.signIn(MockAuthenticator.TEST_USER_EMAIL, MockAuthenticator.TEST_USER_PASSWORD));

        Listing listing = new Listing("random", "blablabla", "20.00", LoginTest.EMAIL, "");

        Tasks.await(listing.save());
        String id = listing.getId();
        Intent intent = new Intent();
        intent.putExtra("listingID", id);

        activityRule.launchActivity(intent);

        onView(withText(R.string.add_favorite)).perform(click());

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

