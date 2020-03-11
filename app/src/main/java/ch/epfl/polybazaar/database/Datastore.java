package ch.epfl.polybazaar.database;

import androidx.annotation.NonNull;

import ch.epfl.polybazaar.database.generic.DocumentSnapshotCallback;

public interface Datastore {

    void fetchData(@NonNull final String collectionPath, @NonNull final String documentPath, @NonNull final DocumentSnapshotCallback callback);
    

}
