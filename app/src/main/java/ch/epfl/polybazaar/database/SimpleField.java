package ch.epfl.polybazaar.database;

import java.io.Serializable;

import ch.epfl.polybazaar.category.Field;

/**
 * Field that contain a basic value that can be saved on the database without any particular
 * transformation
 * @param <T> value type that the field contains
 */
public class SimpleField<T> extends Field<T> {
    private T value;

    /**
     * Constructor for a field with no initial value
     * @param name name of the field
     */
    public SimpleField(String name) {
        super(name);
    }

    /**
     * Constructor for a field with an initial value
     * @param name name of the field
     * @param value initial value
     */
    public SimpleField(String name, T value) {
        this(name);
        this.value = value;
    }

    @Override
    public Object serialize() {
        return get();
    }

    @Override
    public T deserialize(Object o) {
        return (T) o;
    }

    @Override
    public T get() {
        return this.value;
    }

    @Override
    public void set(T value) {
        this.value = value;
    }
}
