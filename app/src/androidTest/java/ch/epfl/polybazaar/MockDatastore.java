package ch.epfl.polybazaar;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

import ch.epfl.polybazaar.database.DataSnapshot;
import ch.epfl.polybazaar.database.DataSnapshotCallback;
import ch.epfl.polybazaar.database.Datastore;
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
        collections.put(collectionName,new HashMap<>());
    }

    public void setupMockData(String collectionName,String dataId,Object data){
        Map<String,Object> document = new HashMap<>();
        document.put(dataId,data);
        collections.put(collectionName,document);
    }

    @Override
    public void fetchData(@NonNull String collectionPath, @NonNull String documentPath, @NonNull DataSnapshotCallback callback) {
        if (!collections.containsKey(collectionPath)){callback.onCallback(null);}
        if(!collections.get(collectionPath).containsKey(documentPath)){callback.onCallback(null);}
        Object data = collections.get(collectionPath).get(documentPath);
        DataSnapshot snapshot = new DataSnapshot(true,data);
        callback.onCallback(snapshot);
    }

    @Override
    public void setData(@NonNull String collectionPath, @NonNull String documentPath, @NonNull Object data, @NonNull SuccessCallback callback) {
        if (!collections.containsKey(collectionPath)){callback.onCallback(false);return;}
        if(!collections.get(collectionPath).containsKey(documentPath)){
           collections.get(collectionPath).put(documentPath,data);
        }else{
            collections.get(collectionPath).replace(documentPath,data);
        }
        callback.onCallback(true);

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
