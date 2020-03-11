package ch.epfl.polybazaar.database;

import androidx.annotation.NonNull;

import ch.epfl.polybazaar.database.callback.SuccessCallback;

public interface Datastore {

    void fetchData(@NonNull final String collectionPath,
                   @NonNull final String documentPath, @NonNull final DocumentCallback callback);

    void setData(@NonNull final String collectionPath,
                 @NonNull final String documentPath, @NonNull final Object data, @NonNull final SuccessCallback callback);

    void addData(@NonNull final String collectionPath,
                 @NonNull final Object data, @NonNull final SuccessCallback callback);

    void deleteData(@NonNull final String collectionPath,
                    @NonNull final String documentPath, @NonNull final SuccessCallback callback);
}
