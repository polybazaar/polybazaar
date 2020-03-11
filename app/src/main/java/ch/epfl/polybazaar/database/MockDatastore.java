package ch.epfl.polybazaar.database;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

import ch.epfl.polybazaar.database.callback.SuccessCallback;
import ch.epfl.polybazaar.database.generic.DocumentSnapshotCallback;

public class MockDatastore implements Datastore {

    Map<String,Map<String,Object>> collections;

    public MockDatastore(){
        collections = new HashMap<>();
    }
    public void reset(){
        collections = new HashMap<>();
    }
    public void addCollection(String collectionName){
        collections.put(collectionName,null);
    }

    public void setupMockData(String collectionName,String dataId,Object data){
        Map<String,Object> document = new HashMap<>();
        document.put(dataId,data);
        collections.put(collectionName,document);
    }

    @Override
    public void fetchData(@NonNull String collectionPath, @NonNull String documentPath, @NonNull DocumentCallback callback) {
        if (!collections.containsKey(collectionPath)){callback.onCallback(null);}
        if(!collections.get(collectionPath).containsKey(documentPath)){callback.onCallback(null);}
        callback.onCallback(collections.get(collectionPath).get(documentPath));

    }

    @Override
    public void setData(@NonNull String collectionPath, @NonNull String documentPath, @NonNull Object data, @NonNull SuccessCallback callback) {
        if (!collections.containsKey(collectionPath)){callback.onCallback(false);}
        if(!collections.get(collectionPath).containsKey(documentPath)){callback.onCallback(false);}
    }

    @Override
    public void addData(@NonNull String collectionPath, @NonNull Object data, @NonNull SuccessCallback callback) {
        if (!collections.containsKey(collectionPath)){callback.onCallback(false);}
        String docPath = Integer.toString ((int)(Math.random()*1000));
        collections.get(collectionPath).put(docPath,data);
        callback.onCallback(true);
    }

    @Override
    public void deleteData(@NonNull String collectionPath, @NonNull String documentPath, @NonNull SuccessCallback callback) {
        if (!collections.containsKey(collectionPath)){callback.onCallback(false);}
        if(!collections.get(collectionPath).containsKey(documentPath)){callback.onCallback(false);}
        collections.get(collectionPath).remove(documentPath);
        callback.onCallback(true);

    }
}
