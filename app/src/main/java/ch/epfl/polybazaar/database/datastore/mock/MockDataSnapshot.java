package ch.epfl.polybazaar.database.datastore.mock;

import java.util.Map;

import ch.epfl.polybazaar.database.datastore.DataSnapshot;

import static ch.epfl.polybazaar.Utilities.getMap;
import static ch.epfl.polybazaar.Utilities.getOrDefaultObj;

public class MockDataSnapshot implements DataSnapshot {

    private Map<String, Object> data;

    private String ID;

    public MockDataSnapshot(String ID, Object data) {
        this.ID = ID;
        this.data = getMap(data);
    }

    @Override
    public boolean exists() {
        return true;
    }

    @Override
    public Object get(String field) {
        return getOrDefaultObj(data, field);
    }

    @Override
    public Map<String, Object> data() {
        return data;
    }

    @Override
    public String getId() {
        return ID;
    }
}
