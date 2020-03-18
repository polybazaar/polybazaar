package ch.epfl.polybazaar;

import android.content.Intent;
import android.widget.Button;
import android.widget.TextView;

import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;

import ch.epfl.polybazaar.listing.Listing;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertEquals;

public class SaleDetailsTest {

    @Rule
    public final ActivityTestRule<SaleDetails> activityRule =
            new ActivityTestRule<>(
                    SaleDetails.class,
                    true,
                    false);

    @Test
    public void testNoBundlePassed () {
        activityRule.launchActivity(new Intent());

        onView(withText("Object not found."))
                .inRoot(withDecorView(not(activityRule.getActivity().getWindow().getDecorView())))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testFillWithListingAndGetSellerInfo() throws Throwable {
        Intent intent = new Intent();
        activityRule.launchActivity(intent);

        final Listing listing = new Listing("Algebre linéaire by David C. Lay", "Very good book.", "23", "gu.vrut@epfl.ch");
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
        assertEquals("CHF 23", textPrice.getText().toString());

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
}

