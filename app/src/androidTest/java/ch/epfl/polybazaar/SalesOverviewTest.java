package ch.epfl.polybazaar;

import android.content.Intent;

import androidx.test.espresso.intent.Intents;
import androidx.test.rule.ActivityTestRule;


import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;

import ch.epfl.polybazaar.UI.SalesOverview;
import ch.epfl.polybazaar.litelisting.LiteListing;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
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

}
