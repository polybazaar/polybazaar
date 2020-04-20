package ch.epfl.polybazaar.database.datastore;

public interface DataSnapshot {

    boolean exists();

    Object get(String field);
}
