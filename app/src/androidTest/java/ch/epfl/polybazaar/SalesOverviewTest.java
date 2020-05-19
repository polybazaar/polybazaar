package ch.epfl.polybazaar;

import android.content.Intent;

import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.rule.ActivityTestRule;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;

import junit.framework.TestCase;

import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import ch.epfl.polybazaar.UI.SalesOverview;
import ch.epfl.polybazaar.listing.Listing;
import ch.epfl.polybazaar.litelisting.LiteListing;
import ch.epfl.polybazaar.login.AuthenticatorFactory;
import ch.epfl.polybazaar.login.MockAuthenticator;
import ch.epfl.polybazaar.testingUtilities.DatabaseStoreUtilities;
import ch.epfl.polybazaar.user.User;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread;
import static ch.epfl.polybazaar.database.datastore.DataStoreFactory.useMockDataStore;
import static ch.epfl.polybazaar.login.MockAuthenticator.TEST_USER_EMAIL;
import static ch.epfl.polybazaar.login.MockAuthenticator.TEST_USER_PASSWORD;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;


public class SalesOverviewTest {

    public static final int SLEEP_TIME = 1000;
    @Rule
    public final ActivityTestRule<SalesOverview> activityRule =
            new ActivityTestRule<>(
                    SalesOverview.class,
                    true,
                    false);

    @Before
    public void init() {
        useMockDataStore();

        LiteListing litelisting1 = new LiteListing("1", "listing1", "CHF 1", "Video games");

        litelisting1.save();
    }


    @Test
    public void testLiteListingList() {
        Intents.init();
        Intent intent = new Intent();
        activityRule.launchActivity(intent);

        List<LiteListing> liteListingList =  activityRule.getActivity().getLiteListingList();
        assertEquals(liteListingList.get(0).getListingID(), "1");
        assertEquals(liteListingList.get(0).getTitle(), "listing1");
        assertEquals(liteListingList.get(0).getPrice(), "CHF 1");
        Intents.release();
    }

    @Test
    public void selectCategoryFromSalesOverview() throws InterruptedException {
        Intents.init();
        Intent intent = new Intent();
        activityRule.launchActivity(intent);
        onView(withId(R.id.categoryOverview)).perform(click());
        Thread.sleep(500);
        //onView(withId(R.id.categoriesRecycler)).perform(RecyclerViewActions.actionOnItemAtPosition(1,click()));
        onView(withText("Multimedia")).perform(click());

        pressBack();
        Thread.sleep(500);
        onView(withId(R.id.categoryButton)).perform(click());
        LiteListing searchedListing = activityRule.getActivity().getLiteListingList().get(0);
        assertEquals("listing1",searchedListing.getTitle());
        assertEquals(1,activityRule.getActivity().getLiteListingList().size());
        Intents.release();
    }

    @Test
    public void deleteSoldListing() throws InterruptedException {
        AuthenticatorFactory.setDependency(MockAuthenticator.getInstance());
        AuthenticatorFactory.getDependency().signIn(TEST_USER_EMAIL, TEST_USER_PASSWORD);
        DatabaseStoreUtilities.storeNewUser(MockAuthenticator.TEST_USER_NICKNAME, TEST_USER_EMAIL);
        Listing testListing = new Listing("test", "test", "SOLD"
                , MockAuthenticator.TEST_USER_EMAIL, "none");
        Calendar.getInstance().getTime();
        Calendar calendar = Calendar.getInstance(); // Now
        calendar.add(Calendar.DAY_OF_YEAR, -2);
        Date soldDate = calendar.getTime();
        testListing.saveWithLiteVersion();
        Thread.sleep(SLEEP_TIME);
        LiteListing.updateField(LiteListing.TIME_SOLD, testListing.getId(), new Timestamp(soldDate));
        Thread.sleep(SLEEP_TIME);
        Intents.init();
        Intent intent = new Intent();
        activityRule.launchActivity(intent);
        Thread.sleep(SLEEP_TIME);
        Listing.fetch(testListing.getId()).addOnSuccessListener(Assert::assertNull);
        LiteListing.fetch(testListing.getId()).addOnSuccessListener(Assert::assertNull);
        Intents.release();
    }

}
