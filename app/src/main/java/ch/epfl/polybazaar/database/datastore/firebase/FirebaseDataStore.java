package ch.epfl.polybazaar.database.datastore.firebase;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ch.epfl.polybazaar.database.callback.LiteListingListCallback;
import ch.epfl.polybazaar.database.callback.SuccessCallback;
import ch.epfl.polybazaar.database.datastore.CollectionSnapshotCallback;
import ch.epfl.polybazaar.database.datastore.DataSnapshotCallback;
import ch.epfl.polybazaar.database.datastore.DataStore;


public class FirebaseDataStore implements DataStore {



    private static final String TAG = "FirebaseDataStore";

    @SuppressLint("StaticFieldLeak")
    private static final FirebaseFirestore database = FirebaseFirestore.getInstance();

    public void fetchData(@NonNull final String collectionPath, @NonNull final String documentPath, @NonNull final DataSnapshotCallback callback) {
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

    public void setData(@NonNull final String collectionPath, @NonNull final String documentPath, @NonNull final Object data, @NonNull final SuccessCallback callback) {
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

    public void addData(@NonNull final String collectionPath, @NonNull final Object data, @NonNull final SuccessCallback callback) {
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

    public void deleteData(@NonNull final String collectionPath, @NonNull final String documentPath, @NonNull final SuccessCallback callback) {
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
                                    @NonNull final String equalTo, @NonNull final LiteListingListCallback callback) {
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


}

