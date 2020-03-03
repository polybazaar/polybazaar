package ch.epfl.polybazaar;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SalesOverviewTest {

    @Rule
    public final ActivityTestRule<SalesOverview> activityRule =
            new ActivityTestRule<>(
                    SalesOverview.class,
                    true,
                    false);

    @Test
    public void testOnCreate() {
        Intent intent = new Intent();
        Bundle b = new Bundle();
        b.putString("title1", "Algebre Linéaire by David C. Lay" );
        b.putString("price1", "CHF 13");
        intent.putExtras(b);

        activityRule.launchActivity(intent);
        TextView text_title = (TextView)activityRule.getActivity().findViewById(R.id.title1);
        assertEquals("Algebre Linéaire by David C. Lay", text_title.getText().toString());

        TextView text_price = (TextView)activityRule.getActivity().findViewById(R.id.price1);
        assertEquals("CHF 13", text_price.getText().toString());
    }

    @Test(expected = NullPointerException.class)
    public void testOnCreateThrowAnException() {
        Intent intent = new Intent();
        Bundle b = new Bundle();
        activityRule.getActivity().onCreate(b);
    }

}
