package ch.epfl.polybazaar.database.datastore;

import java.util.Map;

public interface DataSnapshot {

    public boolean exists();

    public Object get(String field);

    public Map<String, Object> data();

    public String getId();
}
