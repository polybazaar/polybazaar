package ch.epfl.polybazaar.database.generic;

import com.google.firebase.firestore.QuerySnapshot;

public interface QuerySnapshotCallback {
    /**
     * Implement onCallback to receive data from the GenericDatabase
     * @param result the callback content, can be null
     */
    public void onCallback(QuerySnapshot result);
}
