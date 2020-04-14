package ch.epfl.polybazaar.database.datastore;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;

import java.util.List;

import ch.epfl.polybazaar.database.Model;
import ch.epfl.polybazaar.database.callback.StringListCallback;
import ch.epfl.polybazaar.database.callback.SuccessCallback;


public interface DataStore {

    /**
     * fetches the data from the database, and calls onCallback when done
     * @param collectionPath collection name
     * @param documentPath document name (ID)
     * @param callback a DataSnapshotCallback interface implementation
     */
    void fetch(@NonNull final String collectionPath,
               @NonNull final String documentPath, @NonNull final DataSnapshotCallback callback);

    /**
     * stores data on the database, and calls onCallback when done
     * @param collectionPath collection name
     * @param documentPath document name (ID)
     * @param data the data that should be stored (overwritten)
     * @param callback a SuccessCallback interface implementation
     */
    void set(@NonNull final String collectionPath,
             @NonNull final String documentPath, @NonNull final Object data, @NonNull final SuccessCallback callback);

    /**
     * stores data on the database, and calls onCallback when done
     * the document id will be chosen randomly
     * @param collectionPath collection name
     * @param data the data that should be stored (overwritten)
     * @param callback a SuccessCallback interface implementation
     */
    void add(@NonNull final String collectionPath,
             @NonNull final Object data, @NonNull final SuccessCallback callback);

    /**
     * deletes data from the database, and calls onCallback when done
     * @param collectionPath collection name
     * @param documentPath document name (ID)
     * @param callback a SuccessCallback interface implementation
     */
    void delete(@NonNull final String collectionPath,
                @NonNull final String documentPath, @NonNull final SuccessCallback callback);

    /**
     * gets all document IDs in a given collection, and calls onCallback when done
     * @param collectionPath collection name
     * @param callback a CollectionSnapshotCallback interface implementation
     */
    void getAllDataInCollection(@NonNull final String collectionPath,
                                @NonNull final CollectionSnapshotCallback callback);

    /**
     * Performs a query which returns all document IDs where their field == equalTo
     * @param collectionPath the path of the collection to perform the query in
     * @param field the field to be checked for equality
     * @param equalTo what field should be equal to
     * @param callback a StringListCallback interface implementation
     */
    void queryStringEquality(@NonNull final String collectionPath, @NonNull final String field,
                                    @NonNull final String equalTo, @NonNull final StringListCallback callback);

    /**
     * Fetches the requested data
     * @param collectionPath collection
     * @param documentPath document id
     * @return task that fails if the database is unreachable
     */
    Task<DataSnapshot> fetch(String collectionPath, String documentPath);

    /**
     * Writes the requested data. The data will be written at the requested bath, even if it already exists
     * @param collectionPath collection
     * @param documentPath document id
     * @param data data to write
     * @return task that fails if the database is unreachable
     */
    Task<Void> set(String collectionPath, String documentPath, Model data);

    /**
     * Deletes the requested data
     * @param collectionPath collection
     * @param documentPath document id
     * @return task that fails if the database is unreachable
     */
    Task<Void> delete(String collectionPath, String documentPath);

    /**
     * Adds the requested data in the database. The id will be chosen automatically
     * @param collectionPath collection
     * @param data data to write
     * @return task that fails if the database is unreachable
     */
    Task<String> add(String collectionPath, Model data);

    /**
     * Fetches all the data in a collection
     * @param collectionPath collection
     * @return task that fails if the database is unreachable
     */
    Task<CollectionSnapshot> fetchAll(String collectionPath);

    /**
     * Fetches all the data in a collection that respects an equality
     * @param collectionPath collection
     * @param field field to be compared
     * @param value value that the field must contain so that the data gets retrieved
     * @return task that fails if the database is unreachable
     */
    Task<CollectionSnapshot> fetchWithEquals(String collectionPath, String field, String value);

    /**
     * Fetches all the data in a collection that respects equalities on multiple fields
     * @param collectionPath collection
     * @param fields List of fields name to be compared
     * @param values values that the fields must contain so that the data is retrieved
     * @return task that fails if the database is unreachable
     */
    Task<CollectionSnapshot> fetchWithEqualsMultiple(String collectionPath, List<String> fields, List<String> values);
}
