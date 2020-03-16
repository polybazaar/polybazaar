package ch.epfl.polybazaar.database;

import androidx.annotation.NonNull;

<<<<<<< HEAD
import ch.epfl.polybazaar.database.callback.SuccessCallback;
=======
import com.google.firebase.firestore.DocumentSnapshot;

import ch.epfl.polybazaar.database.callback.SuccessCallback;
import ch.epfl.polybazaar.database.generic.DocumentSnapshotCallback;
>>>>>>> 997e2eb79f7c4293c72f095d72265156786b5e19

public interface Datastore {

    void fetchData(@NonNull final String collectionPath,
                   @NonNull final String documentPath, @NonNull final DataSnapshotCallback callback);

    void setData(@NonNull final String collectionPath,
                 @NonNull final String documentPath, @NonNull final Object data, @NonNull final SuccessCallback callback);

    void addData(@NonNull final String collectionPath,
                 @NonNull final Object data, @NonNull final SuccessCallback callback);

    void deleteData(@NonNull final String collectionPath,
                    @NonNull final String documentPath, @NonNull final SuccessCallback callback);
}
