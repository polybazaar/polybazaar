package ch.epfl.polybazaar.database.datastore;

import java.util.Map;

import ch.epfl.polybazaar.database.Model;

public interface DataSnapshot {

    public boolean exists();

    public Object get(String field);

    public Map<String, Object> data();

    public String getId();

    /**
     * Converts the content of the snapshot to an instance of the given model
     * @param clazz model class
     * @param <T> model type
     * @return model instance
     */
    <T extends Model> T toObject(Class<T> clazz);
}
