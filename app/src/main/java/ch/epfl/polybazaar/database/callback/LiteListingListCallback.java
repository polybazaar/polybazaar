package ch.epfl.polybazaar.database.callback;

import java.util.List;

import ch.epfl.polybazaar.litelisting.LiteListing;

public interface LiteListingListCallback {
    /**
     * Implement onCallback to receive data from the ListingDatabase
     * @param result the callback content, can be null
     */
    void onCallback(List<String> result);
}
