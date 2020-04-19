package ch.epfl.polybazaar.category;

import java.util.Map;

import ch.epfl.polybazaar.database.datastore.DataSnapshot;

/**
 * Abstraction representing an attribute of a model stored on the database
 * @param <T> value type that the field contains
 */
public abstract class Field<T> {
    private final String name;

    public Field(String name) {
        this.name = name;
    }

    /**
     * Fills the field with a value contained is a query result
     * @param snap query result
     */
    public final void fillFromSnapshot(DataSnapshot snap) {
        set(deserialize(snap.get(name)));
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
    public abstract Object serialize();

    /**
     * Recreates the field content from a value fetched from the database
     * @param o source object
     * @return valid value for the field
     */
    public abstract T deserialize(Object o);

    /**
     * Gets the content of the field
     * @return value of the field
     */
    public abstract T get();

    /**
     * Sets the content of the field
     * @param value value to set
     */
    public abstract void set(T value);
}
