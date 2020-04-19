package ch.epfl.polybazaar.database.datastore.mock;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ch.epfl.polybazaar.database.Model;
import ch.epfl.polybazaar.database.datastore.CollectionSnapshot;
import ch.epfl.polybazaar.database.datastore.DataSnapshot;

public class MockCollectionSnapshot implements CollectionSnapshot {

    private Map<String, Map<String, Object>> data;

    public MockCollectionSnapshot(Map<String, Map<String, Object>> data) {
        this.data = data;
    }

    @Override
    public List<DataSnapshot> getDocuments() {
        List<DataSnapshot> list = new ArrayList<>();
        for (String id: data.keySet()) {
            list.add(new MockDataSnapshot(id, data.get(id)));
        }
        return list;
    }
}
