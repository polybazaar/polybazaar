package ch.epfl.polybazaar.database.callback;

import ch.epfl.polybazaar.listing.Listing;

public interface ListingCallback {
    /**
     * Implement onCallback to receive data from the GenericDatabase
     * @param listing the callback content, can be null
     */
    void onCallback(Listing listing);

}