package ch.epfl.polybazaar.database;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.SuccessContinuation;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.util.List;

import ch.epfl.polybazaar.database.datastore.CollectionSnapshot;
import ch.epfl.polybazaar.database.datastore.DataStore;
import ch.epfl.polybazaar.database.datastore.DataStoreFactory;

public final class ModelTransaction {
    private ModelTransaction() {}

    /**
     * Retrieves a specific object in the given collection
     * @param collection requested collection
     * @param id id of the object
     * @param clazz data model class
     * @param <T> type of the model
     * @return successful task containing the model instance if it exists, null otherwise. The task
     * fails if the database is unreachable
     */
    public static <T extends Model> Task<T> fetch(String collection, String id, Class<T> clazz) {
        DataStore db = DataStoreFactory.getDependency();
        return db.fetch(collection, id)
                .onSuccessTask(dataSnapshot -> {
                    if (dataSnapshot.exists()) return Tasks.forResult(dataSnapshot.toObject(clazz));
                    else return Tasks.forResult(null);
                });
    }

    public static <T extends Model> Task<List<T>> fetchAllWithFieldEquality(String collection, String field, String compareValue, Class<T> clazz) {
        DataStore db = DataStoreFactory.getDependency();
        return db.fetchWithEquals(collection, field, compareValue)
                .onSuccessTask(querySnapshot -> Tasks.forResult(querySnapshot.toObjects(clazz)));
    }

    public static <T extends Model> Task<List<T>> fetchAllWithMultipleFieldsEquality(String collection, List<String> fields, List<String> compareValues, Class<T> clazz) {
        DataStore db = DataStoreFactory.getDependency();
        return db.fetchWithEqualsMultiple(collection, fields, compareValues)
                .onSuccessTask(querySnapshot -> Tasks.forResult(querySnapshot.toObjects(clazz)));
    }

    /**
     * Retrieves all the data in the given collection
     * @param collection requested collection
     * @param clazz data model class
     * @param <T> type of the model
     * @return successful task containing the a list of model instances. The task
     * fails if the database is unreachable
     */
    public static <T extends Model> Task<List<T>> fetchAll(String collection, Class<T> clazz) {
        DataStore db = DataStoreFactory.getDependency();
        return db.fetchAll(collection)
                .onSuccessTask(querySnapshot -> Tasks.forResult(querySnapshot.toObjects(clazz)));
    }

    /**
     * Deletes the model with given path on the database
     * @param collectionPath collection of the model
     * @param documentPath id of the model
     * @return successful task if the database was reachable
     */
    public static Task<Void> delete(String collectionPath, String documentPath) {
        DataStore db = DataStoreFactory.getDependency();
        return db.delete(collectionPath, documentPath);
    }

    /**
     * Deletes the given model on the database
     * @param model model to delete
     * @return successful task if the database was reachable
     */
    public static Task<Void> delete(Model model) {
        return delete(model.collectionName(), model.getId());
    }
}
