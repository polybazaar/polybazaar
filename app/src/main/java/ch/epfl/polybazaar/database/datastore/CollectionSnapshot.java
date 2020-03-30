package ch.epfl.polybazaar.database.datastore;

import java.util.List;

import ch.epfl.polybazaar.database.Model;

public interface CollectionSnapshot {
    public List<DataSnapshot> getDocuments();

    /**
     * Converts all the objects in the snapshot into a list of model instances
     * @param clazz model class
     * @param <T> model type
     * @return list of model instances
     */
    public <T extends Model> List<T> toObjects(Class<T> clazz);
}
