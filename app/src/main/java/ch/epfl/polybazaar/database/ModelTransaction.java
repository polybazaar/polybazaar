package ch.epfl.polybazaar.database;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.polybazaar.database.datastore.CollectionSnapshot;
import ch.epfl.polybazaar.database.datastore.DataSnapshot;
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
                    if (dataSnapshot.exists()){
                        return Tasks.forResult(toModel(dataSnapshot, clazz));
                    }
                    else return Tasks.forResult(null);
                });
    }

    /**
     * Retrieves all documents that correspond to the criterion
     * @param collection name of the collection
     * @param field name of the field that should be compared
     * @param compareValue value against which the field will be compared
     * @param clazz data model class
     * @param <T> type of the model
     * @return successful task containing a list of model instances fulfilling the criterion. The task
     * fails if the database is unreachable
     */
    public static <T extends Model> Task<List<T>> fetchFieldEquality(String collection, String field, String compareValue, Class<T> clazz) {
        DataStore db = DataStoreFactory.getDependency();
        return db.fetchWithEquals(collection, field, compareValue)
                .onSuccessTask(querySnapshot -> Tasks.forResult(toModels(querySnapshot, clazz)));
    }

    /**
     * Retrieves all documents that correspond to the criteria
     * @param collection name of the collection
     * @param fields name of the fields that should be compared
     * @param compareValues values against which the fields will be compared
     * @param clazz data model class
     * @param <T> type of the model
     * @return successful task containing a list of model instances fulfilling the criteria. The task
     * fails if the database is unreachable
     */
    public static <T extends Model> Task<List<T>> fetchMultipleFieldsEquality(String collection, List<String> fields, List<String> compareValues, Class<T> clazz) {
        DataStore db = DataStoreFactory.getDependency();
        return db.fetchWithEqualsMultiple(collection, fields, compareValues)
                .onSuccessTask(querySnapshot -> Tasks.forResult(toModels(querySnapshot, clazz)));
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
                .onSuccessTask(querySnapshot -> Tasks.forResult(toModels(querySnapshot, clazz)));
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

    // Creates a model base on the query result
    private static <T extends Model> T toModel(DataSnapshot snap, Class<T> clazz) {
        try {
            T model = clazz.newInstance();
            model.fillWith(snap);
            return model;

        } catch (IllegalAccessException | InstantiationException e) {
            return null;
        }
    }

    // Creates a list of models based on the query result
    private static <T extends Model> List<T> toModels(CollectionSnapshot snap, Class<T> clazz) {
        List<T> models = new ArrayList<>();

        for (DataSnapshot dataSnapshot: snap.getDocuments()) {
            models.add(toModel(dataSnapshot, clazz));
        }

        return models;
    }
}
