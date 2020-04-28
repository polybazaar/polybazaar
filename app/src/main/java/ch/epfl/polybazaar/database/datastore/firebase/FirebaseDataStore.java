package ch.epfl.polybazaar.database.datastore.firebase;

import android.annotation.SuppressLint;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.List;
import java.util.Map;

import ch.epfl.polybazaar.database.datastore.CollectionSnapshot;
import ch.epfl.polybazaar.database.datastore.DataSnapshot;
import ch.epfl.polybazaar.database.datastore.DataStore;


public class FirebaseDataStore implements DataStore {



    private static final String TAG = "FirebaseDataStore";

    @SuppressLint("StaticFieldLeak")
    private static final FirebaseFirestore database = FirebaseFirestore.getInstance();

    @Override
    public Task<DataSnapshot> fetch(String collectionPath, String documentPath) {
        return database.collection(collectionPath).document(documentPath)
                .get().onSuccessTask((documentSnapshot ->
                        // TODO fromDocumentSnapshot is probably not necessary
                        Tasks.forResult(new FirebaseDataSnapshot(documentSnapshot))
                ));
    }

    @Override
    public Task<Void> set(String collectionPath, String documentPath, Map<String, Object> data) {
        return database.collection(collectionPath).document(documentPath)
                .set(data);
    }

    @Override
    public Task<Void> delete(String collectionPath, String documentPath) {
        return database.collection(collectionPath).document(documentPath).delete();
    }

    @Override
    public Task<String> add(String collectionPath, Map<String, Object> data) {
        return database.collection(collectionPath)
                .add(data).onSuccessTask((documentReference ->
                        Tasks.forResult(documentReference.getId())
                ));
    }

    @Override
    public Task<CollectionSnapshot> fetchAll(String collectionPath) {
        return database.collection(collectionPath)
                .get().onSuccessTask((queryDocumentSnapshots ->
                        Tasks.forResult(new FirebaseCollectionSnapshot(queryDocumentSnapshots))
                ));
    }

    @Override
    public Task<CollectionSnapshot> fetchWithEquals(String collectionPath, String field, String value) {
        return database.collection(collectionPath).whereEqualTo(field, value)
                .get().onSuccessTask((queryDocumentSnapshots ->
                        Tasks.forResult(new FirebaseCollectionSnapshot(queryDocumentSnapshots))
                ));
    }

    @Override
    public Task<CollectionSnapshot> fetchWithEqualsMultiple(String collectionPath, List<String> fields, List<String> values) {
        assert(fields.size() == values.size());
        assert(fields.size() > 0);
        CollectionReference collectionReference = database.collection(collectionPath);
        Query query = collectionReference.whereEqualTo(fields.get(0), values.get(0));
        for(int i = 1 ; i < fields.size() ; i++){
            query = query.whereEqualTo(fields.get(i), values.get(i));
        }
        return query.get().onSuccessTask((queryDocumentSnapshots ->
                Tasks.forResult(new FirebaseCollectionSnapshot(queryDocumentSnapshots))
        ));
    }

    @Override
    public <T> Task<Void> updateField(String collectionPath, String id, String field, T updatedValue) {
        return database.collection(collectionPath).document(id).update(field, updatedValue);
    }

}

