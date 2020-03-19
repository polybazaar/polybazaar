package ch.epfl.polybazaar.database.callback;

import java.util.List;

public interface StringListCallback {
    /**
     * Implement onCallback to receive data from the ListingDatabase
     * @param result the callback content, can be null
     */
    void onCallback(List<String> result);
}
