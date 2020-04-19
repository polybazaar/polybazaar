package ch.epfl.polybazaar.database;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.remote.Datastore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.epfl.polybazaar.database.datastore.DataSnapshot;
import ch.epfl.polybazaar.database.datastore.DataStore;
import ch.epfl.polybazaar.database.datastore.DataStoreFactory;
import ch.epfl.polybazaar.user.User;

public abstract class Model {
    private List<Field> fields;

    protected void registerFields(List<Field> fields) {
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
     * Saves the model on the database
     * @return successful task if the database was reachable
     */
    public final Task<Void> save() {
        DataStore db = DataStoreFactory.getDependency();
        Map<String, Object> map = new HashMap<>();

        for (Field f: fields) {
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

    public final void fillWith(DataSnapshot snap) {
        for (Field f: fields) {
            f.fillFromSnapshot(snap);
        }
    }
}
