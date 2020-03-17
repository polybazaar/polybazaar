package ch.epfl.polybazaar.database;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import ch.epfl.polybazaar.database.datastore.DataSnapshot;
import ch.epfl.polybazaar.database.datastore.DataSnapshotCallback;
import ch.epfl.polybazaar.database.datastore.DataStore;
import ch.epfl.polybazaar.database.callback.SuccessCallback;


public class MockDataStore implements DataStore {

    private Map<String,Map<String,Object>> collections;

    private final String TAG = "MockDataStore";

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
    public void fetchData(@NonNull String collectionPath, @NonNull String documentPath, @NonNull DataSnapshotCallback callback) {
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
        Object data = Objects.requireNonNull(collections.get(collectionPath)).getOrDefault(documentPath, null);
        MockDataSnapshot snapshot = new MockDataSnapshot(data);
        Log.i(TAG, "Document retrieved successfully");
        callback.onCallback(snapshot);
    }

    @Override
    public void setData(@NonNull String collectionPath, @NonNull String documentPath, @NonNull Object data, @NonNull SuccessCallback callback) {
        if (!collections.containsKey(collectionPath)){
            Log.i(TAG, "Collection does not exist");
            callback.onCallback(false);
            return;
        }
        if(!Objects.requireNonNull(collections.get(collectionPath)).containsKey(documentPath)){
           Objects.requireNonNull(collections.get(collectionPath)).put(documentPath,data);
        }else{
            Objects.requireNonNull(collections.get(collectionPath)).replace(documentPath,data);
        }
        Log.i(TAG, "Data successfully set");
        callback.onCallback(true);
    }

    @Override
    public void addData(@NonNull String collectionPath, @NonNull Object data, @NonNull SuccessCallback callback) {
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
    public void deleteData(@NonNull String collectionPath, @NonNull String documentPath, @NonNull SuccessCallback callback) {
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
}