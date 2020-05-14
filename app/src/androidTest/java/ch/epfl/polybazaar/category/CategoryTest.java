package ch.epfl.polybazaar.category;

import android.content.Intent;

import androidx.test.annotation.UiThreadTest;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.rule.ActivityTestRule;

import com.google.android.gms.tasks.Tasks;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import ch.epfl.polybazaar.R;
import ch.epfl.polybazaar.UI.FillListing;
import ch.epfl.polybazaar.UI.SalesOverview;
import ch.epfl.polybazaar.litelisting.LiteListing;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static ch.epfl.polybazaar.OfferTests.SLEEP_TIME;
import static ch.epfl.polybazaar.database.datastore.DataStoreFactory.useMockDataStore;
import static junit.framework.TestCase.assertEquals;

public class CategoryTest {


    @Rule
    public final ActivityTestRule<SalesOverview> salesActivityRule =
            new ActivityTestRule<>(
                    SalesOverview.class,
                    true,
                    true);
    public final ActivityTestRule<FillListing> fillListingActivityRule =
            new ActivityTestRule<>(
                    FillListing.class,
                    true,
                    false);

    @BeforeClass
    public static void init() throws Throwable {
        useMockDataStore();
        LiteListing videogamesListing = new LiteListing("2","videogamesListing","2","Video games");
        Tasks.await(videogamesListing.save());

    }


/*
    @Test
    public void selectCategoryFromFillListing() throws InterruptedException {

        Intent intent = new Intent();
        activityRule.launchActivity(intent);
        onView(withId(R.id.selectCategory)).perform(click());
        Thread.sleep(500);
        onView(withId(R.id.categoriesRecycler)).perform(RecyclerViewActions.actionOnItemAtPosition(1,click()));
        pressBack();
        Thread.sleep(500);
        onView(withId(R.id.categoryButton)).perform(click());
        onView(withText("Multimedia")).check(matches(isDisplayed()));
    }*/

}
