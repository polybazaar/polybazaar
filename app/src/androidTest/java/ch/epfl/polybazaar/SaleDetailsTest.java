package ch.epfl.polybazaar;

import android.content.Intent;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.rule.ActivityTestRule;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import ch.epfl.polybazaar.UI.SaleDetails;
import ch.epfl.polybazaar.listing.Listing;
import ch.epfl.polybazaar.listingImage.ListingImage;
import ch.epfl.polybazaar.login.Authenticator;
import ch.epfl.polybazaar.login.AuthenticatorFactory;
import ch.epfl.polybazaar.login.LoginTest;
import ch.epfl.polybazaar.login.MockAuthenticator;
import ch.epfl.polybazaar.testingUtilities.DatabaseChecksUtilities;
import ch.epfl.polybazaar.testingUtilities.DatabaseStoreUtilities;
import ch.epfl.polybazaar.user.User;

import static androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread;
import static ch.epfl.polybazaar.database.datastore.DataStoreFactory.useMockDataStore;
import static ch.epfl.polybazaar.utilities.ImageUtilities.convertBitmapToString;
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

    @BeforeClass
    public void init() {
        useMockDataStore();
        AuthenticatorFactory.setDependency(MockAuthenticator.getInstance());
    }

    @AfterClass
    public void cleanup() {
        MockAuthenticator.getInstance().reset();
    }

    @Test
    public void testFillWithListingAndGetSellerInfo() throws Throwable {
        final Listing listing = new Listing("Algebre linéaire by David C. Lay", "Very good book.", "23", "gu.vrut@epfl.ch", "Furniture");
        Tasks.await(listing.save());
        Intent intent = new Intent();
        intent.putExtra("listingID", listing.getId());
        activityRule.launchActivity(intent);


        runOnUiThread(() -> activityRule.getActivity().fillWithListing(listing));

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
        String strImg = convertBitmapToString(convertDrawableToBitmap(ContextCompat.getDrawable(activityRule.getActivity(), R.drawable.bicycle)));
        ListingImage newImage1 = new ListingImage(strImg, nextId);
        newImage1.setId(newListing.getId());
        ListingImage newImage2 = new ListingImage(strImg, "");
        newImage2.setId(nextId);
        Tasks.whenAll(newImage1.save(), newImage2.save());

        //recreate to load images
        runOnUiThread(() -> activityRule.getActivity().recreate());

        TextView textTitle = activityRule.getActivity().findViewById(R.id.title);
        assertEquals("Title", textTitle.getText().toString());

        TextView textDescr = activityRule.getActivity().findViewById(R.id.description);
        assertEquals("description", textDescr.getText().toString());

        TextView textPrice = activityRule.getActivity().findViewById(R.id.price);
        assertEquals("CHF 0.0", textPrice.getText().toString());

    }

    @Test
    public void favoriteButtonIsDisabledForUnauthenticatedUsers() throws Throwable {

        Listing listing = new Listing("random", "blablabla", "20.00", LoginTest.EMAIL, "");

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

        Tasks.await(auth.createUser("user.test@epfl.ch", "usert", "abcdef"));

        Listing listing = new Listing("random", "blablabla", "20.00", LoginTest.EMAIL, "");

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

        Tasks.await(auth.createUser("user.test@epfl.ch", "usert", "abcdef"));

        Listing listing = new Listing("random", "blablabla", "20.00", LoginTest.EMAIL, "");

        Tasks.await(listing.save());
        String id = listing.getId();
        Intent intent = new Intent();
        intent.putExtra("listingID", id);

        activityRule.launchActivity(intent);

        runOnUiThread(() -> {
            activityRule.getActivity().favorite();
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

}

