package ch.epfl.polybazaar.database.datastore.mock;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.epfl.polybazaar.database.datastore.CollectionSnapshot;
import ch.epfl.polybazaar.database.datastore.DataSnapshot;
import ch.epfl.polybazaar.database.datastore.DataStore;

public class MockDataStore implements DataStore {

    private Map<String,Map<String,Map<String, Object>>> collections;

    private final String TAG = "MockDataStore";
    private int idCount = 0;

    public MockDataStore(){
        collections = new HashMap<>();
    }

    public void reset(){
        collections = new HashMap<>();
    }

    @Override
    public Task<DataSnapshot> fetch(String collectionPath, String documentPath) {
        Map<String, Map<String, Object>> collection = collections.get(collectionPath);

        if (collection == null)
            return Tasks.forResult(null);
        else
            return Tasks.forResult(new MockDataSnapshot(documentPath, collection.get(documentPath)));
    }

    @Override
    public Task<Void> set(String collectionPath, String documentPath, Map<String, Object> data) {
        Map<String, Map<String, Object>> collection = getOrCreateCollection(collectionPath);

        collection.put(documentPath, data);

        return Tasks.forResult(null);
    }

    @Override
    public Task<Void> delete(String collectionPath, String documentPath) {
        Map<String, Map<String, Object>> collection = collections.get(collectionPath);
        if (collection != null) {
            collection.remove(documentPath);
        }

        return Tasks.forResult(null);
    }

    @Override
    public Task<String> add(String collectionPath, Map<String, Object> data) {
        Map<String, Map<String, Object>> collection = getOrCreateCollection(collectionPath);

        idCount++;
        String id = Integer.toString(idCount);
        collection.put(id, data);

        return Tasks.forResult(id);
    }

    @Override
    public Task<CollectionSnapshot> fetchAll(String collectionPath) {
        Map<String, Map<String, Object>> collection = collections.get(collectionPath);

        if (collection == null)
            return Tasks.forResult(null);
        else {
            return Tasks.forResult(new MockCollectionSnapshot(collection));
        }
    }

    @Override
    public Task<CollectionSnapshot> fetchWithEquals(String collectionPath, String field, String value) {
        Map<String, Map<String, Object>> collection = collections.get(collectionPath);

        if (collection == null)
            return Tasks.forResult(null);

        if (!collections.containsKey(collectionPath)){
            return Tasks.forResult(null);
        }
        Map<String, Map<String, Object>> equals = new HashMap<>();

        for (Map.Entry<String, Map<String, Object>> entry : collection.entrySet()) {
            Map<String, Object> o = entry.getValue();
            if (o.containsKey(field) && o.get(field).equals(value)) {
                equals.put(entry.getKey(), entry.getValue());
            }
        }
        return Tasks.forResult(new MockCollectionSnapshot(equals));
    }

    @Override
    public Task<CollectionSnapshot> fetchWithEqualsMultiple(String collectionPath, List<String> fields, List<String> values) {
        assert(fields.size()> 0);
        assert(values.size() == fields.size());
        Map<String, Map<String, Object>> collection = collections.get(collectionPath);

        if (collection == null)
            return Tasks.forResult(null);


        if (!collections.containsKey(collectionPath)){
            return Tasks.forResult(null);
        }
        Map<String, Map<String, Object>> equals = new HashMap<>();

        for (Map.Entry<String, Map<String, Object>> entry: collection.entrySet()) {
            Map<String, Object> o = entry.getValue();
            boolean isIn = true;
            for(int i = 0; i < fields.size(); i++){
                if (!(o.containsKey(fields.get(i)) && o.get(fields.get(i)).equals(values.get(i)))) {
                    isIn = false;
                    break;
                }
            }
            if(isIn){
                equals.put(entry.getKey(), entry.getValue());
            }
        }
        return Tasks.forResult(new MockCollectionSnapshot(equals));
    }

    @Override
    public <T> Task<Void> updateField(String collectionPath, String id, String field, T updatedValue) {
        Map<String, Map<String, Object>> collection = collections.get(collectionPath);
        Map<String, Object> document = collection.get(id);
        document.put(field, updatedValue);
        return Tasks.forResult(null);
    }

    @Override
    public Task<Void> updateMultipleFields(String collectionPath, String id, Map<String, Object> updated) {
        Map<String, Map<String, Object>> collection = collections.get(collectionPath);
        Map<String, Object> document = collection.get(id);
        for(String key : updated.keySet()){
            document.put(key, updated.get(key));
        }
        return Tasks.forResult(null);
    }
    // Gets the requested collection if it already exists, otherwise creates a new one
    private Map<String, Map<String, Object>> getOrCreateCollection(String collectionPath) {
        Map<String, Map<String, Object>> collection = collections.get(collectionPath);

        if (collection == null) {
            collection = new HashMap<>();
            collections.put(collectionPath, collection);
        }

        return collection;
    }
}
