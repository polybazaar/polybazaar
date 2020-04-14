package ch.epfl.polybazaar.database.datastore.firebase;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ch.epfl.polybazaar.database.Model;
import ch.epfl.polybazaar.database.callback.StringListCallback;
import ch.epfl.polybazaar.database.callback.SuccessCallback;
import ch.epfl.polybazaar.database.datastore.CollectionSnapshot;
import ch.epfl.polybazaar.database.datastore.CollectionSnapshotCallback;
import ch.epfl.polybazaar.database.datastore.DataSnapshot;
import ch.epfl.polybazaar.database.datastore.DataSnapshotCallback;
import ch.epfl.polybazaar.database.datastore.DataStore;


public class FirebaseDataStore implements DataStore {



    private static final String TAG = "FirebaseDataStore";

    @SuppressLint("StaticFieldLeak")
    private static final FirebaseFirestore database = FirebaseFirestore.getInstance();

    public void fetch(@NonNull final String collectionPath, @NonNull final String documentPath, @NonNull final DataSnapshotCallback callback) {
        Task<DocumentSnapshot> task = database.collection(collectionPath).document(documentPath).get();
        task.addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    assert document != null;
                    FirebaseDataSnapshot data = new FirebaseDataSnapshot(document);
                    if (document.exists()) {
                        Log.d(TAG, "successfully retrieved data");
                        callback.onCallback(data);
                    } else {
                        Log.d(TAG, "data does not exist");
                        callback.onCallback(null);
                    }
                } else {
                    Log.d(TAG, "failed to fetch data");
                    callback.onCallback(null);
                }
            }
        });
    }

    public void set(@NonNull final String collectionPath, @NonNull final String documentPath, @NonNull final Object data, @NonNull final SuccessCallback callback) {
        Task<Void> task = database.collection(collectionPath).document(documentPath).set(data);
        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void nothing) {
                Log.d(TAG, "successfully stored data");
                callback.onCallback(true);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "error storing data", e);
                callback.onCallback(false);
            }
        });
    }

    public void add(@NonNull final String collectionPath, @NonNull final Object data, @NonNull final SuccessCallback callback) {
        Task<DocumentReference> task = database.collection(collectionPath).add(data);
        task.addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference nothing) {
                Log.d(TAG, "successfully stored data");
                callback.onCallback(true);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "error storing data", e);
                callback.onCallback(false);
            }
        });
    }

    public void delete(@NonNull final String collectionPath, @NonNull final String documentPath, @NonNull final SuccessCallback callback) {
        Task<Void> task = database.collection(collectionPath).document(documentPath).delete();
        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void nothing) {
                Log.d(TAG, "successfully deleted data");
                callback.onCallback(true);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "error deleting data");
                callback.onCallback(false);
            }
        });
    }

    public void getAllDataInCollection(@NonNull final String collectionPath, @NonNull final CollectionSnapshotCallback callback) {
        Task<QuerySnapshot> task = database.collection(collectionPath).get();
        task.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot document = task.getResult();
                    assert document != null;
                    if (!document.isEmpty()) {
                        Log.d(TAG, "successfully retrieved data");
                        callback.onCallback(new FirebaseCollectionSnapshot(document));
                    } else {
                        Log.d(TAG, "data does not exist");
                        callback.onCallback(null);
                    }
                } else {
                    Log.d(TAG, "failed to fetch data");
                    callback.onCallback(null);
                }
            }
        });
    }

    public void queryStringEquality(@NonNull final String collectionPath, @NonNull final String field,
                                    @NonNull final String equalTo, @NonNull final StringListCallback callback) {
        Task<QuerySnapshot> task = database.collection(collectionPath).whereEqualTo(field, equalTo).get();
        task.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult() == null) {
                        callback.onCallback(null);
                    } else {
                        Log.d(TAG, "Query successful");
                        List<String> list  = new ArrayList<>();
                        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                            list.add(document.getId());
                        }
                        callback.onCallback(list);
                    }
                } else {
                    Log.d(TAG, "Error performing query ", task.getException());
                }
            }
        });
    }

    @Override
    public Task<DataSnapshot> fetch(String collectionPath, String documentPath) {
        return database.collection(collectionPath).document(documentPath)
                .get().onSuccessTask((documentSnapshot ->
                        // TODO fromDocumentSnapshot is probably not necessary
                        Tasks.forResult(new FirebaseDataSnapshot(documentSnapshot))
                ));
    }

    @Override
    public Task<Void> set(String collectionPath, String documentPath, Model data) {
        return database.collection(collectionPath).document(documentPath)
                .set(data);
    }

    @Override
    public Task<Void> delete(String collectionPath, String documentPath) {
        return database.collection(collectionPath).document(documentPath).delete();
    }

    @Override
    public Task<String> add(String collectionPath, Model data) {
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
}

