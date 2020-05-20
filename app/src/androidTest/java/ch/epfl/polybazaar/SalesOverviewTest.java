package ch.epfl.polybazaar;

import android.content.Intent;
import android.view.View;
import android.widget.SeekBar;


import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.rule.ActivityTestRule;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;

import junit.framework.TestCase;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
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

        LiteListing liteListing1 = new LiteListing("1", "listing1", "CHF 1", "Video games");

        liteListing1.save();
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
        Thread.sleep(1000);
        onView(withId(R.id.categoryOverview)).perform(click());
        Thread.sleep(1000);
        onView(withText("Multimedia")).perform(click());
        Thread.sleep(1000);
        pressBack();
        Thread.sleep(1000);
        onView(withId(R.id.categoryButton)).perform(click());
        Thread.sleep(1000);
        LiteListing searchedListing = activityRule.getActivity().getLiteListingList().get(0);
        Thread.sleep(1000);
        assertEquals("listing1",searchedListing.getTitle());
        assertEquals(1,activityRule.getActivity().getLiteListingList().size());
        Intents.release();
    }

    @Test
    public void showFilterPopUp() {
        Intents.init();
        Intent intent = new Intent();
        activityRule.launchActivity(intent);
        onView(withId(R.id.UserClickableFilterMenu)).perform(click());
        onView(withId(R.id.category_popup)).check(matches(withText("Category:")));
        onView(withId(R.id.price_popup)).check(matches(withText("Price:")));
        onView(withId(R.id.age_popup)).check(matches(withText("Online since:")));
        Intents.release();
    }

    @Test
    public void checkSeekBars() {
        Intents.init();
        Intent intent = new Intent();
        activityRule.launchActivity(intent);
        onView(withId(R.id.UserClickableFilterMenu)).perform(click());
        onView(withId(R.id.minPriceSeekBar)).perform(setProgress(100));
        onView(withId(R.id.minPriceSeekBar)).check(matches(withProgress(100)));
        onView(withId(R.id.min_price_value)).check(matches(withText("100")));
        onView(withId(R.id.maxPriceSeekBar)).perform(setProgress(100));
        onView(withId(R.id.maxPriceSeekBar)).check(matches(withProgress(100)));
        onView(withId(R.id.max_price_value)).check(matches(withText("100")));
        onView(withId(R.id.ageSeekBar)).perform(setProgress(5));
        onView(withId(R.id.ageSeekBar)).check(matches(withProgress(5    )));
        onView(withId(R.id.max_days_value)).check(matches(withText("5")));
        Intents.release();
    }


    public static ViewAction setProgress(final int progress) {
        return new ViewAction() {
            @Override
            public void perform(UiController uiController, View view) {
                ((SeekBar) view).setProgress(progress);
            }

            @Override
            public String getDescription() {
                return "Set a progress";
            }

            @Override
            public Matcher<View> getConstraints() {
                return ViewMatchers.isAssignableFrom(SeekBar.class);
            }
        };
    }

    public static Matcher<View> withProgress(final int expectedProgress) {
        return new BoundedMatcher<View, SeekBar>(SeekBar.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("expected: ");
                description.appendText(""+expectedProgress);
            }

            @Override
            public boolean matchesSafely(SeekBar seekBar) {
                return seekBar.getProgress() == expectedProgress;
            }
        };
    }

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


