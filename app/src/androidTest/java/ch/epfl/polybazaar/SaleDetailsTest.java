package ch.epfl.polybazaar;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class SaleDetailsTest {

    @Rule
    public final ActivityTestRule<SaleDetails> activityRule =
            new ActivityTestRule<>(
                    SaleDetails.class,
                    true,
                    false);

    @Test
    public void testOnCreate() {
        Intent intent = new Intent();
        Bundle b = new Bundle();
        b.putString("title", "Algebre Linéaire by David C. Lay" );
        b.putString("description", "Never used");
        b.putString("price", "18 CHF");
        intent.putExtras(b);

        activityRule.launchActivity(intent);
        TextView text_title = (TextView)activityRule.getActivity().findViewById(R.id.title);
        assertEquals("Algebre Linéaire by David C. Lay", text_title.getText().toString());

        TextView text_descr = (TextView)activityRule.getActivity().findViewById(R.id.description);
        assertEquals("Never used", text_descr.getText().toString());

        TextView text_price = (TextView)activityRule.getActivity().findViewById(R.id.price);
        assertEquals("18 CHF", text_price.getText().toString());
    }

    @Test(expected = NullPointerException.class)
    public void testOnCreateThrowAnException() {
        Intent intent = new Intent();

        Bundle b = new Bundle();
        activityRule.getActivity().onCreate(b);
    }

    @Test
    public void testDefaultValues() {
        Intent intent = new Intent();
        Bundle b = new Bundle();
        intent.putExtras(b);

        activityRule.launchActivity(intent);
        TextView text_title = (TextView)activityRule.getActivity().findViewById(R.id.title);
        assertEquals("No Title", text_title.getText().toString());

        TextView text_descr = (TextView)activityRule.getActivity().findViewById(R.id.description);
        assertEquals("No description", text_descr.getText().toString());

        TextView text_price = (TextView)activityRule.getActivity().findViewById(R.id.price);
        assertEquals("No price", text_price.getText().toString());
    }

    @Test
    public void contactTheSellerNotImplementedYet() {
        Intent intent = new Intent();
        intent.putExtra("title", "Algebre Linéaire by David C. Lay" );
        intent.putExtra("description", "Never used");
        intent.putExtra("price", "18 CHF");

        activityRule.launchActivity(intent);

        onView(withId(R.id.contact_sel))
                .perform(ViewActions.click());


        /*onView(withText("This functionality is not implemented yet"))
                .inRoot(withDecorView(not(activityRule.getActivity().getWindow().getDecorView())))
                .check(matches(isDisplayed()));*/

    }
}

