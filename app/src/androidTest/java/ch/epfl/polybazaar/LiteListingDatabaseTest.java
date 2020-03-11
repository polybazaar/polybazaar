package ch.epfl.polybazaar;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.polybazaar.database.callback.LiteListingCallback;
import ch.epfl.polybazaar.database.callback.LiteListingCallbackAdapter;
import ch.epfl.polybazaar.litelisting.LiteListing;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(AndroidJUnit4.class)
public class LiteListingDatabaseTest {

    @Test
    public void callListingCallback() {
        final LiteListing[] listing = new LiteListing[1];
        LiteListingCallback callback = new LiteListingCallback() {
            @Override
            public void onCallback(LiteListing result) {
                listing[0] = result;
            }
        };
        LiteListing inside = new LiteListing("test", "none", "22CHF");
        callback.onCallback(inside);
        assertThat(listing[0].getListingID(), is("test"));
        assertThat(listing[0].getTitle(), is("none"));
        assertThat(listing[0].getPrice(), is("22CHF"));
    }

    @Test
    public void callListingCallbackAdapter() {
        final LiteListing[] listing = new LiteListing[1];
        LiteListingCallback callback = new LiteListingCallback() {
            @Override
            public void onCallback(LiteListing result) {
                listing[0] = result;
            }
        };
        LiteListingCallbackAdapter adapter = new LiteListingCallbackAdapter(callback);
    }
}