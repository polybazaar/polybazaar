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

    public void setData(String collectionName,String dataId,Object data){
        Map<String,Object> document = new HashMap<>();
        document.put(dataId,data);
        collections.put(collectionName,document);
    }
    @Override
    public void fetchData(@NonNull String collectionPath, @NonNull String documentPath, @NonNull DocumentSnapshotCallback callback) {

    }

    @Override
    public void fetchData(@NonNull String collectionPath, @NonNull String documentPath, @NonNull DocumentCallback callback) {

    }

    @Override
    public void setData(@NonNull String collectionPath, @NonNull String documentPath, @NonNull Object data, @NonNull SuccessCallback callback) {

    }

    @Override
    public void addData(@NonNull String collectionPath, @NonNull Object data, @NonNull SuccessCallback callback) {

    }

    @Override
    public void deleteData(@NonNull String collectionPath, @NonNull String documentPath, @NonNull SuccessCallback callback) {

    }
}
