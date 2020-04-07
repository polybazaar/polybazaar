package ch.epfl.polybazaar;

import android.content.Intent;

import androidx.test.espresso.intent.Intents;
import androidx.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;

import ch.epfl.polybazaar.UI.SalesOverview;
import ch.epfl.polybazaar.database.callback.SuccessCallback;
import ch.epfl.polybazaar.litelisting.LiteListing;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static ch.epfl.polybazaar.database.datastore.DataStoreFactory.useMockDataStore;
import static ch.epfl.polybazaar.litelisting.LiteListingDatabase.addLiteListing;
import static org.hamcrest.Matchers.hasToString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;


public class SalesOverviewTest {

    @Rule
    public final ActivityTestRule<SalesOverview> activityRule =
            new ActivityTestRule<>(
                    SalesOverview.class,
                    true,
                    false);


    LiteListing litelisting1 = new LiteListing("1", "listing1", "CHF 1", "Furniture");
    LiteListing liteListing2 = new LiteListing("2","listing2","1","Multimedia");
    LiteListing liteListing3 = new LiteListing("3","listing3","1","Computer");
    @Before
    public void init() {
        useMockDataStore();
        litelisting1.save();
        liteListing2.save();
        liteListing3.save();
    }


    @Test
    public void testLiteListingList() {
        Intent intent = new Intent();
        activityRule.launchActivity(intent);

        List<LiteListing> liteListingList =  activityRule.getActivity().getLiteListingList();
        assertEquals(liteListingList.get(0).getListingID(), "1");
        assertEquals(liteListingList.get(0).getTitle(), "listing1");
        assertEquals(liteListingList.get(0).getPrice(), "CHF 1");
    }

    @Test
    public void CorrectItemShownByCategoryAndSubCategories() throws InterruptedException {
        Intent intent = new Intent();
        activityRule.launchActivity(intent);

        onView(withId(R.id.categorySelector)).perform(click());
        onData(hasToString("Multimedia")).perform(click());
        Thread.sleep(1000);
        List<LiteListing> liteListingList =  activityRule.getActivity().getLiteListingList();
        assertEquals(liteListing3.getTitle(),liteListingList.get(0).getTitle());
        assertEquals(liteListingList.size(),2);

    }




}
