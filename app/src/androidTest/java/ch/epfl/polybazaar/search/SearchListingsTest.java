package ch.epfl.polybazaar.search;

import android.content.Intent;
import android.view.KeyEvent;

import androidx.test.espresso.intent.Intents;
import androidx.test.rule.ActivityTestRule;

import com.google.android.gms.tasks.Tasks;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import ch.epfl.polybazaar.DataHolder;
import ch.epfl.polybazaar.R;
import ch.epfl.polybazaar.UI.SalesOverview;
import ch.epfl.polybazaar.litelisting.LiteListing;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.pressKey;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static ch.epfl.polybazaar.OfferTests.SLEEP_TIME;
import static ch.epfl.polybazaar.database.datastore.DataStoreFactory.useMockDataStore;
import static junit.framework.TestCase.assertEquals;
import static org.hamcrest.core.AllOf.allOf;

public class SearchListingsTest {

    @Rule
    public final ActivityTestRule<SalesOverview> activityRule =
            new ActivityTestRule<>(
                    SalesOverview.class,
                    true,
                    true);

    @BeforeClass
    public static void init() throws Throwable {
        useMockDataStore();
        LiteListing litelisting1 = new LiteListing("1", "listing1", "1", "Furniture");
        Tasks.await(litelisting1.save());
    }

    @Test
    public void searchTriggersDataHolderTransfer() {
        onView(withId(R.id.search)).perform(typeText("My title")).perform(pressKey(KeyEvent.KEYCODE_ENTER));
        closeSoftKeyboard();
        Map<String, String> map = new LinkedHashMap<>();
        map.put("1", "listing1");
        assertEquals(map, DataHolder.getInstance().getDataMap());
    }

    @Test
    public void searchTriggersSearchActivity() {
        Intents.init();
        onView(withId(R.id.search)).perform(typeText("My title")).perform(pressKey(KeyEvent.KEYCODE_ENTER));
        closeSoftKeyboard();
        intended(allOf(hasComponent(SearchListings.class.getName()), hasAction(Intent.ACTION_SEARCH)));
        Intents.release();
    }

    @Test
    public void searchTriggersNoSearchResults() throws InterruptedException {
        Intents.init();
        activityRule.launchActivity(new Intent());
        onView(withId(R.id.search)).perform(typeText("Dude")).perform(pressKey(KeyEvent.KEYCODE_ENTER));
        closeSoftKeyboard();
        Thread.sleep(SLEEP_TIME);
        intended(hasComponent(NoSearchResults.class.getName()));
        Intents.release();
    }


}
