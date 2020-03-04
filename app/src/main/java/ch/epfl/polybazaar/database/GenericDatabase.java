package ch.epfl.polybazaar.database;

import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

public class GenericDatabase{

    private static final String TAG = "GenericDatabase";

    private Polystore polystore;

    public GenericDatabase(boolean useFirestore) {
        polystore = new Polystore(useFirestore);
    }

    /**
     * fetches the data from the database, and calls onCallback when done
     * @param collectionPath collection name
     * @param documentPath document name (ID)
     * @param callback a callback interface implementation
     */
    public void fetchData(@NonNull String collectionPath, @NonNull String documentPath, @NonNull final GenericCallback callback) {
        Task task = polystore.getDocumentTask(collectionPath, documentPath);
        if (task == null) {
            callback.onCallback(polystore.getTask().getResult());
            return;
        }
        task.addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "successfully retrieved data");
                        callback.onCallback(document.toObject(Object.class));
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
     * @param callback a callback interface implementation
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setData(@NonNull String collectionPath, @NonNull String documentPath, @NonNull Object data, @NonNull final GenericCallback callback) {
        Task task = polystore.setDocumentTask(collectionPath, documentPath, data);
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
     * @param callback a callback interface implementation
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void addData(@NonNull String collectionPath, @NonNull Object data, @NonNull final GenericCallback callback) {
        Task task = polystore.addDocumentTask(collectionPath, data);
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
     * @param callback a callback interface implementation
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void deleteData(@NonNull String collectionPath, @NonNull String documentPath, @NonNull final GenericCallback callback) {
        Task task = polystore.deleteDocumentTask(collectionPath, documentPath);
        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void nothing) {
                Log.d(TAG, "successfully deleted data");
                callback.onCallback(true);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "error deleting data", e);
                callback.onCallback(false);
            }
        });
    }

}
