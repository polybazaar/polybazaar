package ch.epfl.polybazaar.database.datastore.mock;

import java.util.Map;

import ch.epfl.polybazaar.database.Model;
import ch.epfl.polybazaar.database.datastore.DataSnapshot;


public class MockDataSnapshot implements DataSnapshot {

    private final Map<String, Object> data;
    private final String id;


    public MockDataSnapshot(String id, Map<String, Object> data) {
        this.id = id;
        this.data = data;
    }

    @Override
    public boolean exists() {
        return true;
    }

    @Override
    public Object get(String field) {
        return data.get(field);
    }

    // TODO maybe this should be removed
    @Override
    public Map<String, Object> data() {
        return data;
    }

    @Override
    public String getId() {
        return id;
    }
}
