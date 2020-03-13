package ch.epfl.polybazaar.database;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import ch.epfl.polybazaar.database.callback.SuccessCallback;
import ch.epfl.polybazaar.database.generic.DocumentSnapshotCallback;
import ch.epfl.polybazaar.database.generic.QuerySnapshotCallback;
import ch.epfl.polybazaar.database.DataSnapshot;

public abstract class FirebaseDatastore{

    private static final String TAG = "FirebaseDatastore";

    private static final FirebaseFirestore database = FirebaseFirestore.getInstance();


    /**
     * fetches the data from the database, and calls onCallback when done
     * @param collectionPath collection name
     * @param documentPath document name (ID)
     * @param callback a GenericCallback interface implementation
     */
    public static void fetchData(@NonNull final String collectionPath, @NonNull final String documentPath, @NonNull final DataSnapshotCallback callback) {
        Task<DocumentSnapshot> task = database.collection(collectionPath).document(documentPath).get();
        task.addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    DataSnapshot data = new DataSnapshot(true,document.getData());
                    assert document != null;
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

    /**
     * stores data on the database, and calls onCallback when done
     * @param collectionPath collection name
     * @param documentPath document name (ID)
     * @param data the data that should be stored (overwritten)
     * @param callback a SuccessCallback interface implementation
     */
    public static void setData(@NonNull final String collectionPath, @NonNull final String documentPath, @NonNull final Object data, @NonNull final SuccessCallback callback) {
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

    /**
     * stores data on the database, and calls onCallback when done
     * the document id will be chosen randomly
     * @param collectionPath collection name
     * @param data the data that should be stored (overwritten)
     * @param callback a SuccessCallback interface implementation
     */
    public static void addData(@NonNull final String collectionPath, @NonNull final Object data, @NonNull final SuccessCallback callback) {
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

    /**
     * deletes data from the database, and calls onCallback when done
     * @param collectionPath collection name
     * @param documentPath document name (ID)
     * @param callback a SuccessCallback interface implementation
     */
    public static void deleteData(@NonNull final String collectionPath, @NonNull final String documentPath, @NonNull final SuccessCallback callback) {
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

    /**
     * gets all document IDs in a given collection, and calls onCallback when done
     * @param collectionPath collection name
     * @param callback a GenericCallback interface implementation
     */
    public static void getAllDataInCollection(@NonNull final String collectionPath, @NonNull final QuerySnapshotCallback callback) {
        Task<QuerySnapshot> task = database.collection(collectionPath).get();
        task.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot document = task.getResult();
                    assert document != null;
                    if (!document.isEmpty()) {
                        Log.d(TAG, "successfully retrieved data");
                        callback.onCallback(document);
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

}

