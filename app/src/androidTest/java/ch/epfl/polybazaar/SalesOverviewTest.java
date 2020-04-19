package ch.epfl.polybazaar;

import android.content.Intent;

import androidx.test.rule.ActivityTestRule;

import com.google.android.gms.tasks.Tasks;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.ExecutionException;

import ch.epfl.polybazaar.UI.SalesOverview;
import ch.epfl.polybazaar.litelisting.LiteListing;

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

        LiteListing litelisting1 = new LiteListing("1", "listing1", "CHF 1", "Furniture");

        litelisting1.save();
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

}
