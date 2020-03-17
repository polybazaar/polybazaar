package ch.epfl.polybazaar.database.datastore;

public interface DataSnapshotCallback {
    void onCallback(DataSnapshot data);
}
