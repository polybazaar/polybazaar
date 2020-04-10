package ch.epfl.polybazaar.database.datastore.mock;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import ch.epfl.polybazaar.database.Model;
import ch.epfl.polybazaar.database.callback.StringListCallback;
import ch.epfl.polybazaar.database.callback.SuccessCallback;
import ch.epfl.polybazaar.database.datastore.CollectionSnapshot;
import ch.epfl.polybazaar.database.datastore.CollectionSnapshotCallback;
import ch.epfl.polybazaar.database.datastore.DataSnapshot;
import ch.epfl.polybazaar.database.datastore.DataSnapshotCallback;
import ch.epfl.polybazaar.database.datastore.DataStore;
import ch.epfl.polybazaar.listing.ListingDatabase;
import ch.epfl.polybazaar.listingImage.ListingImageDatabase;
import ch.epfl.polybazaar.litelisting.LiteListingDatabase;
import ch.epfl.polybazaar.user.UserDatabase;

import static ch.epfl.polybazaar.Utilities.getMap;
import static ch.epfl.polybazaar.Utilities.getOrDefaultMap;
import static ch.epfl.polybazaar.Utilities.getOrDefaultObj;


public class MockDataStore implements DataStore {

    private Map<String,Map<String,Object>> collections;

    private final String TAG = "MockDataStore";
    private int idCount = 0;

    public MockDataStore(){
        collections = new HashMap<>();
        addCollection(ListingDatabase.listingCollectionName);
        addCollection(ListingImageDatabase.listingImageCollectionName);
        addCollection(UserDatabase.userCollectionName);
        addCollection(LiteListingDatabase.liteListingCollectionName);
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
    public void fetch(@NonNull String collectionPath, @NonNull String documentPath, @NonNull DataSnapshotCallback callback) {
        if (!collections.containsKey(collectionPath)){
            Log.i(TAG, "Collection does not exist");
            callback.onCallback(null);
            return;
        }
        if(!Objects.requireNonNull(collections.get(collectionPath)).containsKey(documentPath)){
            Log.i(TAG, "Document does not exist");
            callback.onCallback(null);
            return;
        }
        Object data = Objects.requireNonNull(getOrDefaultObj(Objects.requireNonNull(collections.get(collectionPath)), documentPath));
        MockDataSnapshot snapshot = new MockDataSnapshot(documentPath, data);
        Log.i(TAG, "Document retrieved successfully");
        callback.onCallback(snapshot);
    }

    @Override
    public void set(@NonNull String collectionPath, @NonNull String documentPath, @NonNull Object data, @NonNull SuccessCallback callback) {
        if (!collections.containsKey(collectionPath)){
            Log.i(TAG, "Collection does not exist");
            callback.onCallback(false);
            return;
        }
        if(!Objects.requireNonNull(collections.get(collectionPath)).containsKey(documentPath)){
           Objects.requireNonNull(collections.get(collectionPath)).put(documentPath,data);
        }else{
            Objects.requireNonNull(collections.get(collectionPath)).remove(documentPath);
            Objects.requireNonNull(collections.get(collectionPath)).put(documentPath,data);
        }
        Log.i(TAG, "Data successfully set");
        callback.onCallback(true);
    }

    @Override
    public void add(@NonNull String collectionPath, @NonNull Object data, @NonNull SuccessCallback callback) {
        if (!collections.containsKey(collectionPath)){
            Log.i(TAG, "Collection does not exist");
            callback.onCallback(false);
            return;
        }
        String docPath = Integer.toString ((int)(Math.random()*1000));
        Objects.requireNonNull(collections.get(collectionPath)).put(docPath,data);
        Log.i(TAG, "Data successfully set");
        callback.onCallback(true);
    }

    @Override
    public void delete(@NonNull String collectionPath, @NonNull String documentPath, @NonNull SuccessCallback callback) {
        if (!collections.containsKey(collectionPath)){
            Log.i(TAG, "Collection does not exist");
            callback.onCallback(false);
            return;
        }
        if(!Objects.requireNonNull(collections.get(collectionPath)).containsKey(documentPath)){
            Log.i(TAG, "Document does not exist");
            callback.onCallback(false);
            return;
        }
        Objects.requireNonNull(collections.get(collectionPath)).remove(documentPath);
        Log.i(TAG, "Document successfully deleted");
        callback.onCallback(true);
    }

    @Override
    public void getAllDataInCollection(@NonNull String collectionPath, @NonNull CollectionSnapshotCallback callback) {
        if (!collections.containsKey(collectionPath)){
            Log.i(TAG, "Collection does not exist");
            callback.onCallback(null);
            return;
        }
        callback.onCallback(new MockCollectionSnapshot(getOrDefaultMap(collections, collectionPath)));
    }

    @Override
    public void queryStringEquality(@NonNull String collectionPath, @NonNull String field,
                                    @NonNull String equalTo, @NonNull StringListCallback callback) {
        if (!collections.containsKey(collectionPath)){
            Log.i(TAG, "Collection does not exist");
            callback.onCallback(null);
            return;
        }
        Map<String, Object> collection = collections.get(collectionPath);
        List<String> list = new ArrayList<>();
        assert collection != null;
        for (Map.Entry<String, Object> entry : collection.entrySet()) {
            Object o = entry.getValue();
            if (getMap(o).containsKey(field) && getMap(o).get(field).equals(equalTo)) {
                list.add(entry.getKey());
            }
        }
        Log.i(TAG, "Query successful");
        callback.onCallback(list);
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
        // TODO this does not work
        return fetchAll(collectionPath);
    }

    @Override
    public Task<CollectionSnapshot> fetchWithEqualsMultiple(String collectionPath, List<String> fields, List<String> values) {
        // TODO this does not work. This is only a stub implementation
        return fetchAll(collectionPath);
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
