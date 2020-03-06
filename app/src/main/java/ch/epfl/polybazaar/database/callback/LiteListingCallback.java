package ch.epfl.polybazaar.database.callback;

import ch.epfl.polybazaar.litelisting.LiteListing;

public interface LiteListingCallback {
    /**
     * Implement onCallback to receive data from the LiteListingDatabase
     * @param result the callback content, can be null
     */
    void onCallback(LiteListing result);
}
