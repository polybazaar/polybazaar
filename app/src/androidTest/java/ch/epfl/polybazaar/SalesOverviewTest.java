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
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;

import ch.epfl.polybazaar.UI.SalesOverview;
import ch.epfl.polybazaar.litelisting.LiteListing;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static ch.epfl.polybazaar.database.datastore.DataStoreFactory.useMockDataStore;
import static org.junit.Assert.assertEquals;


public class SalesOverviewTest {

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

}


