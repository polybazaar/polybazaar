package ch.epfl.polybazaar.database.datastore;

import com.google.android.gms.tasks.Task;

import java.util.List;
import java.util.Map;

import ch.epfl.polybazaar.database.Model;


public interface DataStore {
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
    Task<Void> set(String collectionPath, String documentPath, Map<String, Object> data);

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
    Task<String> add(String collectionPath, Map<String, Object> data);

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
