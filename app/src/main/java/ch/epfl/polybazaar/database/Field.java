package ch.epfl.polybazaar.database;

import java.io.Serializable;
import java.util.Map;

import ch.epfl.polybazaar.database.datastore.DataSnapshot;

/**
 * Abstraction representing an attribute of a model stored on the database
 * @param <T> value type that the field contains
 */
public class Field<T> implements Serializable {
    private final String name;
    private T value;

    /**
     * Constructor for a field with no initial value
     * @param name name of the field
     */
    public Field(String name) {
        this.name = name;
    }

    /**
     * Constructor for a field with an initial value
     * @param name name of the field
     * @param value initial value
     */
    public Field(String name, T value) {
        this(name);
        this.value = value;
    }

    /**
     * Adds the field to a map from field name to its serialized value
     * @param map map on which the field must be added
     */
    public final void addToMap(Map<String, Object> map) {
        map.put(name, serialize());
    }

    /**
     * Turns the field content into a value that is directly supported by the database
     * @return object that can be saved on the database directly
     */
    public Object serialize() {
        return get();
    }

    /**
     * Recreates the field content from a value fetched from the database
     * @param o source object
     * @return valid value for the field
     */
    public T deserialize(Object o) {
        return (T) o;
    }

    /**
     * Gets the content of the field
     * @return value of the field
     */
    public T get() {
        return this.value;
    }

    /**
     * Sets the content of the field
     * @param value value to set
     */
    public void set(T value) {
        this.value = value;
    }

    /**
     * Fills the field with a value contained is a query result
     * @param snap query result
     */
    public final void fillFromSnapshot(DataSnapshot snap) {
        set(deserialize(snap.get(name)));
    }
}
