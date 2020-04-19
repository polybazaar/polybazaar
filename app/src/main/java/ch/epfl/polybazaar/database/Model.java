package ch.epfl.polybazaar.database;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.util.HashMap;
import java.util.Map;

import ch.epfl.polybazaar.database.datastore.DataSnapshot;
import ch.epfl.polybazaar.database.datastore.DataStore;
import ch.epfl.polybazaar.database.datastore.DataStoreFactory;

public abstract class Model {
    private SimpleField[] fields;

    /**
     * Registers fields that should be fetched and saved on the database
     * @param fields all the fields of the model
     */
    protected void registerFields(SimpleField... fields) {
        this.fields = fields;
    }

    /**
     * Returns the name of the collection corresponding to the model
     * @return model name
     */
    public abstract String collectionName();

    /**
     * Returns the id of the model instance
     * @return model id
     */
    public abstract String getId();

    /**
     * Sets the id of the model instance
     * @param id
     */
    public abstract void setId(String id);

    /**
     * Saves the model on the database. If the model entity has no id (getId() returning null),
     * an ID is automatically created by the database and set (using setId()) when the transaction
     * completes. If the model already has an ID (getId() returning a non-null value), the entity is
     * saved under this ID. This method can be used to create a new entity on the database or save
     * modifications to an already existing one.
     * @return successful task if the database was reachable
     */
    public final Task<Void> save() {
        DataStore db = DataStoreFactory.getDependency();
        Map<String, Object> map = new HashMap<>();

        for (SimpleField f: fields) {
            f.addToMap(map);
        }

        if (getId() == null) {
            return db.add(collectionName(), map)
                    .onSuccessTask(s -> {
                        setId(s);
                        return Tasks.forResult(null);
                    });
        } else {
            return db.set(collectionName(), getId(), map);
        }
    }

    /**
     * Deletes the model on the database
     * @return successful task if the database was reachable
     */
    public final Task<Void> delete() {
        return ModelTransaction.delete(this);
    }

    /**
     * Fills the model with the result of a database query according to the model fields
     * @param snap result from the database
     */
    public final void fillWith(DataSnapshot snap) {
        for (SimpleField f: fields) {
            f.fillFromSnapshot(snap);
        }
    }
}
