package ch.epfl.polybazaar.database.datastore;

import androidx.annotation.NonNull;
import ch.epfl.polybazaar.database.callback.SuccessCallback;


public interface DataStore {

    void fetchData(@NonNull final String collectionPath,
                   @NonNull final String documentPath, @NonNull final DataSnapshotCallback callback);

    void setData(@NonNull final String collectionPath,
                 @NonNull final String documentPath, @NonNull final Object data, @NonNull final SuccessCallback callback);

    void addData(@NonNull final String collectionPath,
                 @NonNull final Object data, @NonNull final SuccessCallback callback);

    void deleteData(@NonNull final String collectionPath,
                    @NonNull final String documentPath, @NonNull final SuccessCallback callback);
}
