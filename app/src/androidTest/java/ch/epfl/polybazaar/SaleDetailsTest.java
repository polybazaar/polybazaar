package ch.epfl.polybazaar;

import android.content.Intent;
import android.widget.Button;
import android.widget.TextView;

import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import ch.epfl.polybazaar.database.callback.SuccessCallback;
import ch.epfl.polybazaar.listing.Listing;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread;
import static ch.epfl.polybazaar.listing.ListingDatabase.storeListing;
import static java.util.UUID.randomUUID;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertEquals;

//@RunWith(AndroidJUnit4.class)
public class SaleDetailsTest {

    @Rule
    public final ActivityTestRule<SaleDetails> activityRule =
            new ActivityTestRule<>(
                    SaleDetails.class,
                    true,
                    false);

    @Test
    public void testNoBundlePassed () {
        Intent intent = new Intent();
        activityRule.launchActivity(intent);

        onView(withText("Object not found."))
                .inRoot(withDecorView(not(activityRule.getActivity().getWindow().getDecorView())))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testFillWithListingAndGetSellerInfo() throws Throwable {
        Intent intent = new Intent();
        activityRule.launchActivity(intent);

        final Listing listing = new Listing("Algebre linéaire by David C. Lay", "Very good book.", "CHF 23.-", "gu.vrut@epfl.ch");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activityRule.getActivity().fillWithListing(listing);
            }
        });

        TextView textTitle = (TextView)activityRule.getActivity().findViewById(R.id.title);
        assertEquals("Algebre linéaire by David C. Lay", textTitle.getText().toString());

        TextView textDescr = (TextView)activityRule.getActivity().findViewById(R.id.description);
        assertEquals("Very good book.", textDescr.getText().toString());

        TextView textPrice = (TextView)activityRule.getActivity().findViewById(R.id.price);
        assertEquals("CHF 23.-", textPrice.getText().toString());

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Button but = activityRule.getActivity().findViewById(R.id.contactSel);
                but.performClick();
                TextView contactSel = (TextView)activityRule.getActivity().findViewById(R.id.userEmail);
                assertEquals("gu.vrut@epfl.ch", contactSel.getText().toString());
            }
        });
    }

    /*@Test
    public void testStoreListingAndRetrieveListing() throws InterruptedException {
        CountDownLatch lock = new CountDownLatch(1);
        final String listingID = randomUUID().toString();

        SuccessCallback successCallback = new SuccessCallback() {
            @Override
            public void onCallback(boolean result) {
                assertEquals(true, result);
            }
        };
        storeListing(new Listing("Algebre linéaire by David C. Lay", "Very good book.", "CHF 23.-", "gu.vrut@epfl.ch"),
                            listingID,
                            successCallback);
        lock.await(2000, TimeUnit.MILLISECONDS);

        Intent intent = new Intent();
        intent.putExtra("listingID",listingID);

        activityRule.launchActivity(intent);

        lock.await(2000, TimeUnit.MILLISECONDS);

        TextView text_title = (TextView)activityRule.getActivity().findViewById(R.id.title);
        assertEquals("Algebre linéaire by David C. Lay", text_title.getText().toString());

        TextView text_descr = (TextView)activityRule.getActivity().findViewById(R.id.description);
        assertEquals("Very good book.", text_descr.getText().toString());

        TextView text_price = (TextView)activityRule.getActivity().findViewById(R.id.price);
        assertEquals("CHF 23.-", text_price.getText().toString());
    }*/


    /*@Test
    public void testContactTheSellerNotImplementedYet() throws Throwable {
        CountDownLatch lock = new CountDownLatch(1);
        Intent intent = new Intent();
        intent.putExtra("listingID", "ccceb203-1b23-44b1-8216-07cdc99d2b4a");

        activityRule.launchActivity(intent);
        lock.await(2000, TimeUnit.MILLISECONDS);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Button but = activityRule.getActivity().findViewById(R.id.contactSel);
                but.performClick();
                //TODO complete test
            }
        });
    }*/
}

