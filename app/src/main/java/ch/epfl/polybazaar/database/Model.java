package ch.epfl.polybazaar.database;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import ch.epfl.polybazaar.database.datastore.DataStore;
import ch.epfl.polybazaar.database.datastore.DataStoreFactory;

public abstract class Model {
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
        if (getId() == null) {
            return db.addData(collectionName(), this)
                    .onSuccessTask(s -> {
                        setId(s);
                        return Tasks.forResult(null);
                    });
        } else {
            return db.setData(collectionName(), getId(), this);
        }
    }

    /**
     * Deletes the model on the database
     * @return successful task if the database was reachable
     */
    public final Task<Void> delete() {
        return ModelTransaction.delete(this);
    }
}
