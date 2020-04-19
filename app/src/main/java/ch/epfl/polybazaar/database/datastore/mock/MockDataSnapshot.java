package ch.epfl.polybazaar.database.datastore.mock;

import java.util.Map;

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
}
