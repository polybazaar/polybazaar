package ch.epfl.polybazaar.database.datastore.mock;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ch.epfl.polybazaar.database.datastore.CollectionSnapshot;
import ch.epfl.polybazaar.database.datastore.DataSnapshot;

public class MockCollectionSnapshot implements CollectionSnapshot {

    private Map<String, Object> data;

    public MockCollectionSnapshot(Map<String, Object> data) {
        this.data = data;
    }

    @Override
    public List<DataSnapshot> getDocuments() {
        List<DataSnapshot> list = new ArrayList<>();
        for (String key : data.keySet()) {
            list.add(new MockDataSnapshot(key, data.get(key)));
        }
        return list;
    }
}
