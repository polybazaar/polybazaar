package ch.epfl.polybazaar;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.widget.TextView;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

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

    @Test
    public void testOnCreateThrowAnException() {
        Intent intent = new Intent();
        boolean exceptionThrown = false;

        try {
            Bundle b = new Bundle();
            activityRule.getActivity().onCreate(b);
        }catch (NullPointerException e) {
            exceptionThrown = true;
        }
        
        assertEquals(true, exceptionThrown);
    }
}

