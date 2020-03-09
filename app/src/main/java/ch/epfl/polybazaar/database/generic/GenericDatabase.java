package ch.epfl.polybazaar.database.generic;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import ch.epfl.polybazaar.database.callback.SuccessCallback;

public abstract class GenericDatabase{

    private static final String TAG = "GenericDatabase";

    private static final FirebaseFirestore database = FirebaseFirestore.getInstance();


    /**
     * fetches the data from the database, and calls onCallback when done
     * @param collectionPath collection name
     * @param documentPath document name (ID)
     * @param callback a GenericCallback interface implementation
     */
    public static void fetchData(@NonNull String collectionPath, @NonNull String documentPath, @NonNull final GenericCallback callback) {
        Task task = database.collection(collectionPath).document(documentPath).get();
        task.addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
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

    /**
     * stores data on the database, and calls onCallback when done
     * @param collectionPath collection name
     * @param documentPath document name (ID)
     * @param data the data that should be stored (overwritten)
     * @param callback a SuccessCallback interface implementation
     */
    public static void setData(@NonNull String collectionPath, @NonNull String documentPath, @NonNull Object data, @NonNull final SuccessCallback callback) {
        Task task = database.collection(collectionPath).document(documentPath).set(data);
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
    public static void addData(@NonNull String collectionPath, @NonNull Object data, @NonNull final SuccessCallback callback) {
        Task task = database.collection(collectionPath).add(data);
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
     * deletes data from the database, and calls onCallback when done
     * @param collectionPath collection name
     * @param documentPath document name (ID)
     * @param callback a SuccessCallback interface implementation
     */
    public static void deleteData(@NonNull String collectionPath, @NonNull String documentPath, @NonNull final SuccessCallback callback) {
        Task task = database.collection(collectionPath).document(documentPath).delete();
        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
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
    static public void getAllDataInCollection(@NonNull String collectionPath, @NonNull final GenericCallback callback) {
        Task task = database.collection(collectionPath).get();
        task.addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
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
