package ch.epfl.polybazaar.database.datastore.mock;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.epfl.polybazaar.database.Model;
import ch.epfl.polybazaar.database.datastore.CollectionSnapshot;
import ch.epfl.polybazaar.database.datastore.DataSnapshot;
import ch.epfl.polybazaar.database.datastore.DataStore;

import static ch.epfl.polybazaar.Utilities.getMap;


public class MockDataStore implements DataStore {

    private Map<String,Map<String,Object>> collections;

    private final String TAG = "MockDataStore";
    private int idCount = 0;

    public MockDataStore(){
        collections = new HashMap<>();
    }

    public void reset(){
        collections = new HashMap<>();
    }

    /**
     * create a new mock empty collection
     * @param collectionName obvious
     */
    public void addCollection(String collectionName){
        collections.put(collectionName,new HashMap<>());
    }

    /**
     * put mock data in one collection
     * @param collectionName obvious
     * @param dataId obvious
     * @param data obvious
     */
    public void setupMockData(String collectionName, String dataId, Object data){
        Map<String,Object> document = new HashMap<>();
        document.put(dataId,data);
        collections.put(collectionName,document);
    }

    @Override
    public Task<DataSnapshot> fetch(String collectionPath, String documentPath) {
        Map<String, Object> collection = collections.get(collectionPath);

        if (collection == null)
            return Tasks.forResult(null);
        else
            return Tasks.forResult(new MockDataSnapshot(documentPath, collection.get(documentPath)));
    }

    @Override
    public Task<Void> set(String collectionPath, String documentPath, Model data) {
        Map<String, Object> collection = getOrCreateCollection(collectionPath);

        collection.put(documentPath, data);

        return Tasks.forResult(null);
    }

    @Override
    public Task<Void> delete(String collectionPath, String documentPath) {
        Map<String, Object> collection = collections.get(collectionPath);
        if (collection != null) {
            collection.remove(documentPath);
        }

        return Tasks.forResult(null);
    }

    @Override
    public Task<String> add(String collectionPath, Model data) {
        Map<String, Object> collection = getOrCreateCollection(collectionPath);

        idCount++;
        String id = Integer.toString(idCount);
        collection.put(id, data);

        return Tasks.forResult(id);
    }

    @Override
    public Task<CollectionSnapshot> fetchAll(String collectionPath) {
        Map<String, Object> collection = collections.get(collectionPath);

        if (collection == null)
            return Tasks.forResult(null);
        else {
            List<Object> documents = new ArrayList<>(collection.values());
            return Tasks.forResult(new MockCollectionSnapshot(documents));
        }
    }

    @Override
    public Task<CollectionSnapshot> fetchWithEquals(String collectionPath, String field, String value) {
        //TODO This might only be a temporary solution has this makes use of "getMap"
        Map<String, Object> collection = collections.get(collectionPath);

        if (collection == null)
            return Tasks.forResult(null);

        if (!collections.containsKey(collectionPath)){
            return Tasks.forResult(null);
        }
        List<Object> equals = new ArrayList<>();
        for (Map.Entry<String, Object> entry : collection.entrySet()) {
            Object o = entry.getValue();
            if (getMap(o).containsKey(field) && getMap(o).get(field).equals(value)) {
                equals.add(entry.getValue());
            }
        }
        return Tasks.forResult(new MockCollectionSnapshot(equals));
    }

    @Override
    public Task<CollectionSnapshot> fetchWithEqualsMultiple(String collectionPath, List<String> fields, List<String> values) {
        //TODO This might only be a temporary solution has this makes use of "getMap"
        assert(fields.size()> 0);
        assert(values.size() == fields.size());
        Map<String, Object> collection = collections.get(collectionPath);

        if (collection == null)
            return Tasks.forResult(null);


        if (!collections.containsKey(collectionPath)){
            return Tasks.forResult(null);
        }
        List<Object> equals = new ArrayList<>();

            for (Map.Entry<String, Object> entry : collection.entrySet()) {
                Object o = entry.getValue();
                boolean isIn = true;
                for(int i = 0 ; i < fields.size(); i++){
                    if (!(getMap(o).containsKey(fields.get(i)) && getMap(o).get(fields.get(i)).equals(values.get(i)))) {
                        isIn = false;
                        break;
                    }
                }
                if(isIn){
                    equals.add(entry.getValue());
                }
        }
            System.out.println(equals);
        return Tasks.forResult(new MockCollectionSnapshot(equals));
    }

    // Gets the requested collection if it already exists, otherwise creates a new one
    private Map<String, Object> getOrCreateCollection(String collectionPath) {
        Map<String, Object> collection = collections.get(collectionPath);

        if (collection == null) {
            collection = new HashMap<>();
            collections.put(collectionPath, collection);
        }

        return collection;
    }
}
