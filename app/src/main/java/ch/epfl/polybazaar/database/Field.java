package ch.epfl.polybazaar.database;

import java.io.Serializable;
import java.util.Map;

import ch.epfl.polybazaar.database.datastore.DataSnapshot;

public class Field<T> implements Serializable {
    private final String name;
    private T value;

    public Field(String name) {
        this.name = name;
    }

    public Field(String name, T value) {
        this(name);
        this.value = value;
    }

    public void addToMap(Map<String, Object> map) {
        map.put(name, encode());
    }

    public Object encode() {
        return get();
    }

    public T decode(Object o) {
        return (T) o;
    }

    public T get() {
        return this.value;
    }

    public void set(T value) {
        this.value = value;
    }

    public void fillFromSnapshot(DataSnapshot snap) {
        set(decode(snap.get(name)));
    }
}
