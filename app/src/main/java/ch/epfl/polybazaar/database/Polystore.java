package ch.epfl.polybazaar.database;

import android.app.Activity;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.Source;
import com.google.firebase.firestore.Transaction;

import java.util.Calendar;
import java.util.HashMap;
import java.util.concurrent.Executor;

import ch.epfl.polybazaar.userdatabase.User;

public class Polystore {

    private boolean useFirestore;

    private FirebaseFirestore fb;

    private HashMap<String, Object> data;

    private Object currentItem;

    private Task<Object> task;

    public Polystore(boolean useFirestore) {
        this.useFirestore = useFirestore;
        fb = FirebaseFirestore.getInstance();
        Calendar cal = Calendar.getInstance();
        cal.set(1945, 12, 13);
        HashMap<String, Object> userCollection = new HashMap<>();
        HashMap<String, Object> listingsCollection = new HashMap<>();
        // Insert new users in the database:
        userCollection.put("john.doe@epfl.ch", new User("John", "Doe", cal, "john.doe@epfl.ch"));
        // Insert new listings in the database:
        listingsCollection.put("john-doe-1", null);
        data = new HashMap<>();
        data.put("users", userCollection);
        data.put("listings", listingsCollection);
        currentItem = null;
        task = null;
    }

    /**
     * Used after calling getDocumentTask/addDocumentTask to retrieve the task
     * @return the actual task if not using Firestore
     */
    public Task<Object> getTask() {
        if (useFirestore) return null;
        else {
            return task;
        }
    }

    /**
     * gets the Document Task, null if using Polystore
     * @param collectionPath obvious
     * @param documentPath obvious
     * @return the Document Task, null if using Polystore
     */
    public Task<DocumentSnapshot> getDocumentTask(@NonNull final String collectionPath, @NonNull final String documentPath) {
        if (useFirestore) return fb.collection(collectionPath).document(documentPath).get();
        else {
            Task<Object> task = new Task<Object>() {
                @Override
                public boolean isComplete() {
                    return true;
                }

                @Override
                public boolean isSuccessful() {
                    return true;
                }

                @Override
                public boolean isCanceled() {
                    return true;
                }

                @RequiresApi(api = Build.VERSION_CODES.N)
                @Nullable
                @Override
                public Object getResult() {
                    return ((HashMap<String, Object>)data.getOrDefault(collectionPath, null)).getOrDefault(documentPath, null);
                }

                @RequiresApi(api = Build.VERSION_CODES.N)
                @Nullable
                @Override
                public <X extends Throwable> Object getResult(@NonNull Class<X> aClass) throws X {
                    return ((HashMap<String, Object>)data.getOrDefault(collectionPath, null)).getOrDefault(documentPath, null);
                }

                @Nullable
                @Override
                public Exception getException() {
                    return null;
                }

                @NonNull
                @Override
                public Task<Object> addOnSuccessListener(@NonNull OnSuccessListener<? super Object> onSuccessListener) {
                    return null;
                }

                @NonNull
                @Override
                public Task<Object> addOnSuccessListener(@NonNull Executor executor, @NonNull OnSuccessListener<? super Object> onSuccessListener) {
                    return null;
                }

                @NonNull
                @Override
                public Task<Object> addOnSuccessListener(@NonNull Activity activity, @NonNull OnSuccessListener<? super Object> onSuccessListener) {
                    return null;
                }

                @NonNull
                @Override
                public Task<Object> addOnFailureListener(@NonNull OnFailureListener onFailureListener) {
                    return null;
                }

                @NonNull
                @Override
                public Task<Object> addOnFailureListener(@NonNull Executor executor, @NonNull OnFailureListener onFailureListener) {
                    return null;
                }

                @NonNull
                @Override
                public Task<Object> addOnFailureListener(@NonNull Activity activity, @NonNull OnFailureListener onFailureListener) {
                    return null;
                }
            };
            this.task = task;
            return null;
        }
    }

    /**
     * gets the Document Task, null if using Polystore
     * @param source cache
     * @param collectionPath obvious
     * @param documentPath obvious
     * @return the Document Task, null if using Polystore
     */
    @NonNull
    public Task<DocumentSnapshot> getDocumentTask(@NonNull Source source, @NonNull final String collectionPath, @NonNull final String documentPath) {
        if (useFirestore) return fb.collection(collectionPath).document(documentPath).get(source);
        else {
            Task<Object> task = new Task<Object>() {
                @Override
                public boolean isComplete() {
                    return true;
                }

                @Override
                public boolean isSuccessful() {
                    return true;
                }

                @Override
                public boolean isCanceled() {
                    return true;
                }

                @RequiresApi(api = Build.VERSION_CODES.N)
                @Nullable
                @Override
                public Object getResult() {
                    return ((HashMap<String, Object>)data.getOrDefault(collectionPath, null)).getOrDefault(documentPath, null);
                }

                @RequiresApi(api = Build.VERSION_CODES.N)
                @Nullable
                @Override
                public <X extends Throwable> Object getResult(@NonNull Class<X> aClass) throws X {
                    return ((HashMap<String, Object>)data.getOrDefault(collectionPath, null)).getOrDefault(documentPath, null);
                }

                @Nullable
                @Override
                public Exception getException() {
                    return null;
                }

                @NonNull
                @Override
                public Task<Object> addOnSuccessListener(@NonNull OnSuccessListener<? super Object> onSuccessListener) {
                    return null;
                }

                @NonNull
                @Override
                public Task<Object> addOnSuccessListener(@NonNull Executor executor, @NonNull OnSuccessListener<? super Object> onSuccessListener) {
                    return null;
                }

                @NonNull
                @Override
                public Task<Object> addOnSuccessListener(@NonNull Activity activity, @NonNull OnSuccessListener<? super Object> onSuccessListener) {
                    return null;
                }

                @NonNull
                @Override
                public Task<Object> addOnFailureListener(@NonNull OnFailureListener onFailureListener) {
                    return null;
                }

                @NonNull
                @Override
                public Task<Object> addOnFailureListener(@NonNull Executor executor, @NonNull OnFailureListener onFailureListener) {
                    return null;
                }

                @NonNull
                @Override
                public Task<Object> addOnFailureListener(@NonNull Activity activity, @NonNull OnFailureListener onFailureListener) {
                    return null;
                }
            };
            this.task = task;
            return null;
        }
    }

    /**
     * adds a document
     * @param collectionPath obvious
     * @param data data to add
     * @return the task of adding the document, null if using Polystore
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    @NonNull
    public Task<DocumentReference> addDocumentTask(@NonNull final String collectionPath, @NonNull Object data) {
        if (useFirestore) return fb.collection(collectionPath).add(data);
        else {
            String doc = Double.toString(Math.random());
            ((HashMap<String, Object>)(this.data.getOrDefault(collectionPath, null))).put(doc, data);
            Task<Object> task = new Task<Object>() {
                @Override
                public boolean isComplete() {
                    return true;
                }

                @Override
                public boolean isSuccessful() {
                    return true;
                }

                @Override
                public boolean isCanceled() {
                    return true;
                }

                @RequiresApi(api = Build.VERSION_CODES.N)
                @Nullable
                @Override
                public Object getResult() {
                    return true;
                }

                @RequiresApi(api = Build.VERSION_CODES.N)
                @Nullable
                @Override
                public <X extends Throwable> Object getResult(@NonNull Class<X> aClass) throws X {
                    return true;
                }

                @Nullable
                @Override
                public Exception getException() {
                    return null;
                }

                @NonNull
                @Override
                public Task<Object> addOnSuccessListener(@NonNull OnSuccessListener<? super Object> onSuccessListener) {
                    return null;
                }

                @NonNull
                @Override
                public Task<Object> addOnSuccessListener(@NonNull Executor executor, @NonNull OnSuccessListener<? super Object> onSuccessListener) {
                    return null;
                }

                @NonNull
                @Override
                public Task<Object> addOnSuccessListener(@NonNull Activity activity, @NonNull OnSuccessListener<? super Object> onSuccessListener) {
                    return null;
                }

                @NonNull
                @Override
                public Task<Object> addOnFailureListener(@NonNull OnFailureListener onFailureListener) {
                    return null;
                }

                @NonNull
                @Override
                public Task<Object> addOnFailureListener(@NonNull Executor executor, @NonNull OnFailureListener onFailureListener) {
                    return null;
                }

                @NonNull
                @Override
                public Task<Object> addOnFailureListener(@NonNull Activity activity, @NonNull OnFailureListener onFailureListener) {
                    return null;
                }
            };
            this.task = task;
            return null;
        }

    }

    /**
     * deletes a document
     * @param collectionPath obvious
     * @param documentPath obvious
     * @return the task of deleting the document
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    @NonNull
    public Task<Void> deleteDocumentTask(@NonNull final String collectionPath, @NonNull final String documentPath) {
        if (useFirestore) return fb.collection(collectionPath).document(documentPath).delete();
        else {
            ((HashMap<String, Object>)(this.data.getOrDefault(collectionPath, null))).remove(documentPath);
            return new Task<Void>() {
                @Override
                public boolean isComplete() {
                    return true;
                }

                @Override
                public boolean isSuccessful() {
                    return true;
                }

                @Override
                public boolean isCanceled() {
                    return true;
                }

                @RequiresApi(api = Build.VERSION_CODES.N)
                @Nullable
                @Override
                public Void getResult() {
                    return null;
                }

                @RequiresApi(api = Build.VERSION_CODES.N)
                @Nullable
                @Override
                public <X extends Throwable> Void getResult(@NonNull Class<X> aClass) throws X {
                    return null;
                }

                @Nullable
                @Override
                public Exception getException() {
                    return null;
                }

                @NonNull
                @Override
                public Task<Void> addOnSuccessListener(@NonNull OnSuccessListener<? super Void> onSuccessListener) {
                    return null;
                }

                @NonNull
                @Override
                public Task<Void> addOnSuccessListener(@NonNull Executor executor, @NonNull OnSuccessListener<? super Void> onSuccessListener) {
                    return null;
                }

                @NonNull
                @Override
                public Task<Void> addOnSuccessListener(@NonNull Activity activity, @NonNull OnSuccessListener<? super Void> onSuccessListener) {
                    return null;
                }

                @NonNull
                @Override
                public Task<Void> addOnFailureListener(@NonNull OnFailureListener onFailureListener) {
                    return null;
                }

                @NonNull
                @Override
                public Task<Void> addOnFailureListener(@NonNull Executor executor, @NonNull OnFailureListener onFailureListener) {
                    return null;
                }

                @NonNull
                @Override
                public Task<Void> addOnFailureListener(@NonNull Activity activity, @NonNull OnFailureListener onFailureListener) {
                    return null;
                }
            };
        }
    }

    /**
     * sets a document
     * @param collectionPath obvious
     * @param documentPath obvious
     * @param data the data to store
     * @return the task of setting the document
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    @NonNull
    public Task<Void> setDocumentTask(@NonNull final String collectionPath, @NonNull final String documentPath, @NonNull Object data) {
        if (useFirestore) return fb.collection(collectionPath).document(documentPath).set(data);
        else {
            ((HashMap<String, Object>)(this.data.getOrDefault(collectionPath, null))).put(documentPath, data);
            return new Task<Void>() {
                @Override
                public boolean isComplete() {
                    return true;
                }

                @Override
                public boolean isSuccessful() {
                    return true;
                }

                @Override
                public boolean isCanceled() {
                    return true;
                }

                @RequiresApi(api = Build.VERSION_CODES.N)
                @Nullable
                @Override
                public Void getResult() {
                    return null;
                }

                @RequiresApi(api = Build.VERSION_CODES.N)
                @Nullable
                @Override
                public <X extends Throwable> Void getResult(@NonNull Class<X> aClass) throws X {
                    return null;
                }

                @Nullable
                @Override
                public Exception getException() {
                    return null;
                }

                @NonNull
                @Override
                public Task<Void> addOnSuccessListener(@NonNull OnSuccessListener<? super Void> onSuccessListener) {
                    return null;
                }

                @NonNull
                @Override
                public Task<Void> addOnSuccessListener(@NonNull Executor executor, @NonNull OnSuccessListener<? super Void> onSuccessListener) {
                    return null;
                }

                @NonNull
                @Override
                public Task<Void> addOnSuccessListener(@NonNull Activity activity, @NonNull OnSuccessListener<? super Void> onSuccessListener) {
                    return null;
                }

                @NonNull
                @Override
                public Task<Void> addOnFailureListener(@NonNull OnFailureListener onFailureListener) {
                    return null;
                }

                @NonNull
                @Override
                public Task<Void> addOnFailureListener(@NonNull Executor executor, @NonNull OnFailureListener onFailureListener) {
                    return null;
                }

                @NonNull
                @Override
                public Task<Void> addOnFailureListener(@NonNull Activity activity, @NonNull OnFailureListener onFailureListener) {
                    return null;
                }
            };
        }
    }

    @NonNull
    public FirebaseApp getApp() {
        if (useFirestore) return fb.getApp();
        else {
            return fb.getApp();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @NonNull
    public CollectionReference collection(@NonNull String collectionPath) {
       if (useFirestore) return fb.collection(collectionPath);
       else {
            currentItem = data.getOrDefault(collectionPath, null);
            return null;
       }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @NonNull
    public DocumentReference document(@NonNull String documentPath) {
        if (useFirestore) return fb.document(documentPath);
        else {
            currentItem = ((HashMap<String, Object>)currentItem).getOrDefault(documentPath, null);
            return null;
        }
    }

    @NonNull
    public Query collectionGroup(@NonNull String collectionId) {
        if (useFirestore) return fb.collectionGroup(collectionId);
        else {
            // NOT IMPLEMENTED
            return null;
        }
    }

    @NonNull
    public <TResult> Task<TResult> runTransaction(@NonNull Transaction.Function<TResult> updateFunction) {
        if (useFirestore) return fb.runTransaction(updateFunction);
        else {
            // NOT IMPLEMENTED
            return null;
        }
    }

    @NonNull
    public Task<Void> runBatch(@NonNull com.google.firebase.firestore.WriteBatch.Function batchFunction) {
        if (useFirestore) return fb.runBatch(batchFunction);
        else {
            // NOT IMPLEMENTED
            return null;
        }
    }

    @NonNull
    public Task<Void> terminate() {
        if (useFirestore) return fb.terminate();
        else {
            // NOT IMPLEMENTED
            return null;
        }
    }

    @NonNull
    public Task<Void> waitForPendingWrites() {
        if (useFirestore) return fb.waitForPendingWrites();
        else {
            // NOT IMPLEMENTED
            return null;
        }
    }

    @NonNull
    public Task<Void> enableNetwork() {
        if (useFirestore) return fb.enableNetwork();
        else {
            // NOT IMPLEMENTED
            return null;
        }
    }

    @NonNull
    public Task<Void> disableNetwork() {
        if (useFirestore) return fb.disableNetwork();
        else {
            // NOT IMPLEMENTED
            return null;
        }
    }

    @NonNull
    public Task<Void> clearPersistence() {
        if (useFirestore) return fb.clearPersistence();
        else {
            // NOT IMPLEMENTED
            return null;
        }
    }

    @NonNull
    public ListenerRegistration addSnapshotsInSyncListener(@NonNull Runnable runnable) {
        if (useFirestore) return fb.addSnapshotsInSyncListener(runnable);
        else {
            // NOT IMPLEMENTED
            return null;
        }
    }

    @NonNull
    public ListenerRegistration addSnapshotsInSyncListener(@NonNull Activity activity, @NonNull Runnable runnable) {
        if (useFirestore) return fb.addSnapshotsInSyncListener(activity, runnable);
        else {
            // NOT IMPLEMENTED
            return null;
        }
    }

    @NonNull
    public ListenerRegistration addSnapshotsInSyncListener(@NonNull Executor executor, @NonNull Runnable runnable) {
        if (useFirestore) return fb.addSnapshotsInSyncListener(executor, runnable);
        else {
            // NOT IMPLEMENTED
            return null;
        }
    }

}
