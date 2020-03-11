package ch.epfl.polybazaar.database;

import androidx.annotation.NonNull;

import ch.epfl.polybazaar.database.generic.DocumentSnapshotCallback;

public class MockDatastore implements Datastore {

    public MockDatastore(){

    }
    @Override
    public void fetchData(@NonNull String collectionPath, @NonNull String documentPath, @NonNull DocumentSnapshotCallback callback) {

    }
}
