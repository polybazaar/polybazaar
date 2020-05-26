package ch.epfl.polybazaar.UI;

import android.content.Intent;
import android.graphics.Bitmap;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.rule.ActivityTestRule;

import com.google.android.gms.tasks.Tasks;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import ch.epfl.polybazaar.R;
import ch.epfl.polybazaar.filestorage.ImageTransaction;
import ch.epfl.polybazaar.listing.Listing;
import ch.epfl.polybazaar.login.Authenticator;
import ch.epfl.polybazaar.login.AuthenticatorFactory;
import ch.epfl.polybazaar.login.LoginTest;
import ch.epfl.polybazaar.login.MockAuthenticator;
import ch.epfl.polybazaar.map.MapsActivity;
import ch.epfl.polybazaar.testingUtilities.DatabaseStoreUtilities;
import ch.epfl.polybazaar.user.User;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread;
import static ch.epfl.polybazaar.database.datastore.DataStoreFactory.useMockDataStore;
import static ch.epfl.polybazaar.utilities.ImageUtilities.convertDrawableToBitmap;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class SaleDetailsTest {
    public static final float DELTA = 0.1f;

    @Rule
    public final ActivityTestRule<SaleDetails> activityRule =
            new ActivityTestRule<>(
                    SaleDetails.class,
                    true,
                    false);

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
        final Listing listing = new Listing("Algebre linéaire by David C. Lay", "Very good book.", "23", "gu.vrut@epfl.ch", "Multimedia");
        Tasks.await(listing.save());
        Intent intent = new Intent();
        intent.putExtra("listingID", listing.getId());
        activityRule.launchActivity(intent);

        runOnUiThread(() -> activityRule.getActivity().applyFillWithListing(listing));

        TextView textTitle = activityRule.getActivity().findViewById(R.id.title);
        assertEquals("Algebre linéaire by David C. Lay", textTitle.getText().toString());

        TextView textDescr = activityRule.getActivity().findViewById(R.id.description);
        assertEquals("Very good book.", textDescr.getText().toString());

        TextView textPrice = activityRule.getActivity().findViewById(R.id.price);
        assertEquals("CHF 23", textPrice.getText().toString());
    }

    @Test
    public void testWithMockListing() throws Throwable {
        Intent intent = new Intent();

        Listing newListing = new Listing("Title", "description", "0.0", "test.user@epfl.ch", "");
        Tasks.await(newListing.saveWithLiteVersion());

        intent.putExtra("listingID", newListing.getId());
        activityRule.launchActivity(intent);

        String nextId = randomUUID().toString();
        String nextId2 = randomUUID().toString();
        List<String> list = new ArrayList<>();
        list.add(nextId);
        list.add(nextId2);
        Bitmap img = convertDrawableToBitmap(ContextCompat.getDrawable(activityRule.getActivity(), R.drawable.bicycle));
        Tasks.whenAll(ImageTransaction.store(nextId, img, 100, activityRule.getActivity().getApplicationContext()),
                ImageTransaction.store(nextId2, img, 100, activityRule.getActivity().getApplicationContext()));
        Tasks.whenAll(Listing.updateField(Listing.IMAGES_REFS, newListing.getId(), list));

        activityRule.finishActivity();
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
    public void favoriteButtonIsDisabledForUnauthenticatedUsers() throws Throwable {

        Listing listing = new Listing("random", "blablabla", "20.00", LoginTest.EMAIL, "Multimedia");

        Tasks.await(listing.save());
        String id = listing.getId();
        Intent intent = new Intent();
        intent.putExtra("listingID", id);

        activityRule.launchActivity(intent);

        runOnUiThread(() -> assertEquals(0f, ((RatingBar)activityRule.getActivity().findViewById(R.id.ratingBar)).getRating(), DELTA));
    }

    @Test
    public void favoriteButtonChangesFavorites() throws Throwable {
        Authenticator auth = AuthenticatorFactory.getDependency();

        Tasks.await(auth.createUser("user.test@epfl.ch", "usertool", "abcdef"));

        Listing listing = new Listing("random", "blablabla", "20.00", LoginTest.EMAIL, "Multimedia");

        Tasks.await(listing.save());
        String id = listing.getId();
        Intent intent = new Intent();
        intent.putExtra("listingID", id);

        activityRule.launchActivity(intent);

        runOnUiThread(() -> activityRule.getActivity().findViewById(R.id.ratingBar).performClick());

        // we fetch after each click to make sure the data is actually saved to mock db
        User.fetch(MockAuthenticator.TEST_USER_EMAIL).addOnSuccessListener(user -> {
            assertTrue(user.getFavorites().contains(listing.getId()));
        });

        runOnUiThread(() -> activityRule.getActivity().findViewById(R.id.ratingBar).performClick());

        User.fetch(MockAuthenticator.TEST_USER_EMAIL).addOnSuccessListener(user -> {
            assertFalse(user.getFavorites().contains(listing.getId()));
        });
    }

    @Test
    public void testPutInFavorite() throws Throwable {
        MockAuthenticator auth = MockAuthenticator.getInstance();

        Tasks.await(auth.createUser("user.test@epfl.ch", "usertool", "abcdef"));

        Listing listing = new Listing("random", "blablabla", "20.00", LoginTest.EMAIL, "Multimedia");

        Tasks.await(listing.save());
        String id = listing.getId();
        Intent intent = new Intent();
        intent.putExtra("listingID", id);

        activityRule.launchActivity(intent);

        runOnUiThread(() -> {
            activityRule.getActivity().applyFavorite(listing);
            assertNotEquals(0f, (((RatingBar)activityRule.getActivity().findViewById(R.id.ratingBar)).getRating()));
        });
    }


    @Test
    public void viewsIncrementCorrectly() throws Throwable {
        MockAuthenticator auth = MockAuthenticator.getInstance();
        Tasks.await(auth.signIn(MockAuthenticator.TEST_USER_EMAIL, MockAuthenticator.TEST_USER_PASSWORD));

        String id = "myid";
        DatabaseStoreUtilities.storeNewListing("My listing",  MockAuthenticator.TEST_USER_EMAIL, id);
        Intent intent = new Intent();
        intent.putExtra("listingID", id);
        activityRule.launchActivity(intent);

        runOnUiThread(() -> assertEquals("1", ((TextView)activityRule.getActivity().findViewById(R.id.nbViews)).getText()));

        auth.signOut();
    }

    @Test
    public void viewLabelNotDisplayedWhenNotLoggedIn() throws Throwable {
        String id = "myid";
        DatabaseStoreUtilities.storeNewListing("My listing",  MockAuthenticator.TEST_USER_EMAIL, id);
        Intent intent = new Intent();
        intent.putExtra("listingID", id);
        activityRule.launchActivity(intent);

        runOnUiThread(() -> assertEquals(ViewMatchers.Visibility.GONE.getValue(), ((TextView)activityRule.getActivity().findViewById(R.id.nbViews)).getVisibility()));
    }

    @Test
    public void viewLabelNotDisplayedForOtherUsers() throws Throwable {
        MockAuthenticator auth = MockAuthenticator.getInstance();
        Tasks.await(auth.signIn(MockAuthenticator.TEST_USER_EMAIL, MockAuthenticator.TEST_USER_PASSWORD));

        String id = "myid";
        DatabaseStoreUtilities.storeNewListing("My listing",  "anotherepfl.user@epfl.ch", id);
        Intent intent = new Intent();
        intent.putExtra("listingID", id);
        activityRule.launchActivity(intent);

        runOnUiThread(() -> assertEquals(ViewMatchers.Visibility.GONE.getValue(), ((TextView)activityRule.getActivity().findViewById(R.id.nbViews)).getVisibility()));

        auth.signOut();
    }

    @Test
    public void testSetupSellerContact() throws ExecutionException, InterruptedException {
        MockAuthenticator auth = MockAuthenticator.getInstance();
        Tasks.await(auth.signIn(MockAuthenticator.TEST_USER_EMAIL, MockAuthenticator.TEST_USER_PASSWORD));
        DatabaseStoreUtilities.storeNewUser(MockAuthenticator.TEST_USER_NICKNAME, MockAuthenticator.TEST_USER_EMAIL);
        String id = "listingID";
        String otherUser = "anotherepfl.user@epfl.ch";
        String message = "Hi!";
        DatabaseStoreUtilities.storeNewListing("My listing", otherUser , id);
        DatabaseStoreUtilities.storeNewMessage(otherUser, MockAuthenticator.TEST_USER_EMAIL, id, message);
        Intent intent = new Intent();
        intent.putExtra("listingID", id);
        activityRule.launchActivity(intent);

        onView(withId(R.id.contactSel)).perform(scrollTo(), click());

        onView(withText(message)).check(matches(isDisplayed()));
    }

    @Test
    public void testFillWithListingNull() {
        Intent intent = new Intent();
        activityRule.launchActivity(intent);

        onView(withText(R.string.object_not_found)).check(matches(isDisplayed()));
        Intents.init();
        onView(withText(R.string.back)).perform(click());
        intended(hasComponent(SalesOverview.class.getName()));
        Intents.release();
    }

    @Test
    public void testDeleteButton() throws ExecutionException, InterruptedException {
        MockAuthenticator auth = MockAuthenticator.getInstance();
        Tasks.await(auth.signIn(MockAuthenticator.TEST_USER_EMAIL, MockAuthenticator.TEST_USER_PASSWORD));

        String id = "listingID";
        DatabaseStoreUtilities.storeNewListing("My listing", MockAuthenticator.TEST_USER_EMAIL , id);
        Intent intent = new Intent();
        intent.putExtra("listingID", id);
        activityRule.launchActivity(intent);

        onView(withId(R.id.deleteButton)).perform(scrollTo(), click());
        onView(withText(R.string.yes)).perform(click());

        Listing.fetch(id).addOnSuccessListener(Assert::assertNull);
    }

    @Test
    public void testEditButton() throws ExecutionException, InterruptedException {
        MockAuthenticator auth = MockAuthenticator.getInstance();
        Tasks.await(auth.signIn(MockAuthenticator.TEST_USER_EMAIL, MockAuthenticator.TEST_USER_PASSWORD));

        String id = "listingID";
        DatabaseStoreUtilities.storeNewListing("My listing", MockAuthenticator.TEST_USER_EMAIL , id);
        Intent intent = new Intent();
        intent.putExtra("listingID", id);
        activityRule.launchActivity(intent);

        Intents.init();
        onView(withId(R.id.editButton)).perform(scrollTo(), click());
        intended(hasComponent(FillListing.class.getName()));
        Intents.release();
    }

    @Test
    public void testRemoveFromFavorite() throws Throwable {
        MockAuthenticator auth = MockAuthenticator.getInstance();
        Tasks.await(auth.signIn(MockAuthenticator.TEST_USER_EMAIL, MockAuthenticator.TEST_USER_PASSWORD));

        String id = "listingID";
        Listing listing = new Listing("Title", "Description", "0", MockAuthenticator.TEST_USER_EMAIL, "Multimedia");
        listing.setId(id);
        Tasks.await(listing.save());

        Tasks.await(auth.getCurrentUser().getUserData().addOnSuccessListener(user -> user.addFavorite(listing.getId())));
        Intent intent = new Intent();
        intent.putExtra("listingID", id);
        activityRule.launchActivity(intent);

        onView(withId(R.id.ratingBar)).perform(scrollTo(), click());

        auth.getCurrentUser().getUserData().addOnSuccessListener(user -> {
            assertFalse(user.getFavorites().contains(listing.getId()));
        });
    }

    @Test
    public void testSetupViewMP() throws ExecutionException, InterruptedException {
        String id = "listingID";
        Listing listing = new Listing("Title", "Description", "0", "otherUser@epfl.ch",
                 "Multimedia", 1.0, 1.0);
        listing.setId(id);
        Tasks.await(listing.save());

        Intent intent = new Intent();
        intent.putExtra("listingID", id);
        activityRule.launchActivity(intent);

        Intents.init();
        onView(withId(R.id.viewMP)).perform(scrollTo(), click());
        intended(hasComponent(MapsActivity.class.getName()));
        Intents.release();
    }

    @Test
    public void testClickOnViewPager() throws Throwable {
        String id = "listingID";
        final Listing listing = new Listing("Title", "Description", "0", "otherUser@epfl.ch",
                 "Multimedia", 1.0, 1.0);
        listing.setId(id);
        Tasks.await(listing.save());

        Intent intent = new Intent();
        intent.putExtra("listingID", id);
        activityRule.launchActivity(intent);

        onView(withId(R.id.viewPagerImageSlider)).perform(scrollTo(), click());
    }
}

