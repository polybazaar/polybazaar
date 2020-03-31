package ch.epfl.polybazaar.database.datastore.mock;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ch.epfl.polybazaar.database.Model;
import ch.epfl.polybazaar.database.datastore.CollectionSnapshot;
import ch.epfl.polybazaar.database.datastore.DataSnapshot;

public class MockCollectionSnapshot implements CollectionSnapshot {

    private Map<String, Object> data;

    private List<Object> dataList;

    public MockCollectionSnapshot(List<Object> dataList) {
        this.dataList = dataList;
    }

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

    @Override
    public <T extends Model> List<T> toObjects(Class<T> clazz) {
        List<T> result = new ArrayList<>();

        for (Object elt: dataList) {
            result.add((T) elt);
        }
        return result;
    }
}
