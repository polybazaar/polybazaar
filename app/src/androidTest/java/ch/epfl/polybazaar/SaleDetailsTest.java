package ch.epfl.polybazaar;

import android.content.Intent;
import android.widget.TextView;

import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import ch.epfl.polybazaar.database.callback.SuccessCallback;
import ch.epfl.polybazaar.listing.Listing;

import static ch.epfl.polybazaar.listing.ListingDatabase.storeListing;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;

//@RunWith(AndroidJUnit4.class)
public class SaleDetailsTest {

    @Rule
    public final ActivityTestRule<SaleDetails> activityRule =
            new ActivityTestRule<>(
                    SaleDetails.class,
                    true,
                    false);

    /*@Test
    public void testOnCreate() {
        Intent intent = new Intent();
        intent.putExtra("image",R.drawable.algebre_lin);
        intent.putExtra("title", "Algebre Linéaire by David C. Lay" );
        intent.putExtra("description", "Never used");
        intent.putExtra("price", "18 CHF");

        activityRule.launchActivity(intent);
        TextView text_title = (TextView)activityRule.getActivity().findViewById(R.id.title);
        assertEquals("Algebre Linéaire by David C. Lay", text_title.getText().toString());

        TextView text_descr = (TextView)activityRule.getActivity().findViewById(R.id.description);
        assertEquals("Never used", text_descr.getText().toString());

        TextView text_price = (TextView)activityRule.getActivity().findViewById(R.id.price);
        assertEquals("18 CHF", text_price.getText().toString());
    }*/

    /*@Test(expected = IllegalArgumentException.class)
    public void testNoBundleAttachedToIntent() {
        Intent intent = new Intent();
        activityRule.launchActivity(intent);
    }*/

    @Test
    public void testStoreListingAndRetrieveListing() throws InterruptedException {
        CountDownLatch lock = new CountDownLatch(1);
        final String listingID = randomUUID().toString();

        SuccessCallback successCallback = new SuccessCallback() {
            @Override
            public void onCallback(boolean result) {
                assertEquals(true, result);
            }
        };
        storeListing(new Listing("A BOOK!!!!!", "A description of this BOOOK!!!!", "Too much", "a.b@epfl.ch"),
                            listingID,
                            successCallback);
        lock.await(2000, TimeUnit.MILLISECONDS);

        Intent intent = new Intent();
        intent.putExtra("listingID",listingID);

        activityRule.launchActivity(intent);

        lock.await(2000, TimeUnit.MILLISECONDS);

        TextView text_title = (TextView)activityRule.getActivity().findViewById(R.id.title);
        assertEquals("A BOOK!!!!!", text_title.getText().toString());

        TextView text_descr = (TextView)activityRule.getActivity().findViewById(R.id.description);
        assertEquals("A description of this BOOOK!!!!", text_descr.getText().toString());

        TextView text_price = (TextView)activityRule.getActivity().findViewById(R.id.price);
        assertEquals("Too much", text_price.getText().toString());
    }


    /*@Test
    public void testContactTheSellerNotImplementedYet() throws Throwable {
        Intent intent = new Intent();
        intent.putExtra("uId", "20");

        activityRule.launchActivity(intent);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Button but = activityRule.getActivity().findViewById(R.id.contact_sel);
                but.performClick();
            }
        });

        onView(withText("This functionality is not implemented yet"))
                .inRoot(withDecorView(not(activityRule.getActivity().getWindow().getDecorView())))
                .check(matches(isDisplayed()));
    }*/
}

