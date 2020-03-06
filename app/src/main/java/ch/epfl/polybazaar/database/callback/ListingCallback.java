package ch.epfl.polybazaar.database.callback;

import ch.epfl.polybazaar.listing.Listing;

public interface ListingCallback {
    /**
     * Implement onCallback to receive data from the ListingDatabase
     * @param result the callback content, can be null
     */
    void onCallback(Listing result);
}