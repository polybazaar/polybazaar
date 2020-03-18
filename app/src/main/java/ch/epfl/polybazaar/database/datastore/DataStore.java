package ch.epfl.polybazaar.database.datastore;

import androidx.annotation.NonNull;

import ch.epfl.polybazaar.database.callback.LiteListingListCallback;
import ch.epfl.polybazaar.database.callback.SuccessCallback;


public interface DataStore {

    /**
     * fetches the data from the database, and calls onCallback when done
     * @param collectionPath collection name
     * @param documentPath document name (ID)
     * @param callback a DataSnapshotCallback interface implementation
     */
    void fetchData(@NonNull final String collectionPath,
                   @NonNull final String documentPath, @NonNull final DataSnapshotCallback callback);

    /**
     * stores data on the database, and calls onCallback when done
     * @param collectionPath collection name
     * @param documentPath document name (ID)
     * @param data the data that should be stored (overwritten)
     * @param callback a SuccessCallback interface implementation
     */
    void setData(@NonNull final String collectionPath,
                 @NonNull final String documentPath, @NonNull final Object data, @NonNull final SuccessCallback callback);

    /**
     * stores data on the database, and calls onCallback when done
     * the document id will be chosen randomly
     * @param collectionPath collection name
     * @param data the data that should be stored (overwritten)
     * @param callback a SuccessCallback interface implementation
     */
    void addData(@NonNull final String collectionPath,
                 @NonNull final Object data, @NonNull final SuccessCallback callback);

    /**
     * deletes data from the database, and calls onCallback when done
     * @param collectionPath collection name
     * @param documentPath document name (ID)
     * @param callback a SuccessCallback interface implementation
     */
    void deleteData(@NonNull final String collectionPath,
                    @NonNull final String documentPath, @NonNull final SuccessCallback callback);

    /**
     * gets all document IDs in a given collection, and calls onCallback when done
     * @param collectionPath collection name
     * @param callback a CollectionSnapshotCallback interface implementation
     */
    void getAllDataInCollection(@NonNull final String collectionPath,
                                @NonNull final CollectionSnapshotCallback callback);

    /**
     * Performs a query in which returns all document IDs where their field == equalTo
     * @param collectionPath the path of the collection to perform the query in
     * @param field the field to be checked for equality
     * @param equalTo what field should be equal to
     * @param callback a LiteListingListCallback interface implementation
     */
    void queryStringEquality(@NonNull final String collectionPath, @NonNull final String field,
                                    @NonNull final String equalTo, @NonNull final LiteListingListCallback callback);
}
