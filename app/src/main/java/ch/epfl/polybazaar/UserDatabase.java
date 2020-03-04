package ch.epfl.polybazaar;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Observable;
import java.util.Observer;

import static ch.epfl.polybazaar.Utilities.*;

/**
 * This class is meant to be used as an observable,
 * which is why all methods need to specify an observer when they are called.
 * Once the query is computed and available, the observer will be notified.
 * The functions isSuccess() and getFetchedUser() can THEN be used to read the query results.
 */
public class UserDatabase extends Observable {

    private static final String TAG = "User.class";

    private boolean success;

    private User fetchedUser;

    public UserDatabase(){
        success = false;
        fetchedUser = null;
    };

    public boolean isSuccess() {
        return success;
    }

    public User getFetchedUser() {
        return fetchedUser;
    }

    /**
     * Add an user to the database, using its email as unique identifier (key)
     * @param user the user
     * @param fb the Firestore database instance
     * @param o the observer, will be notified when request completes
     */
    public void storeNewUser(final User user, final FirebaseFirestore fb, final Observer o) {
        success = false;
        addObserver(o);
        // email already used:
        fb.collection("users").document(user.getEmail()).get()
            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // email already used
                        success = false;
                        notifyObservers();
                    } else {
                        // email invalid:
                        // TODO : check if that is a correct regex:
                        if (!(user.getEmail().matches("[a-zA-Z]+"+"."+"[a-zA-Z]+"+"@epfl.ch"))) {
                            Log.w(TAG, "user email has invalid format");
                            success = false;
                            notifyObservers();
                        }
                        // new user:
                        Task setUser = fb.collection("users").document(user.getEmail()).set(user);
                        setUser.addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void nothing) {
                                Log.d(TAG, "successfully added new user");
                                success = true;
                                notifyObservers();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "error adding new user", e);
                                success = false;
                                notifyObservers();
                            }
                        });
                        deleteObserver(o);
                    }
                }
            }
        });
        return;
    }

    /**
     * Fetch a user from the database
     * @param email the user to fetch's email
     * @param fb the Firestore database to fetch it from
     * @param o the observer, will be notified when request completes
     */
    public void fetchUser(final String email, final FirebaseFirestore fb,final Observer o) {
        success = false;
        fetchedUser = null;
        addObserver(o);
        DocumentReference docRef = fb.collection("user").document(email);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "successfully retrieved user data");
                        success = true;
                        fetchedUser = document.toObject(User.class);
                        notifyObservers();
                    } else {
                        Log.d(TAG, "failed to fetch user data");
                        success = false;
                        fetchedUser = null;
                        notifyObservers();
                    }
                } else {
                    Log.d(TAG, "database request failed with", task.getException());
                    success = false;
                    fetchedUser = null;
                    notifyObservers();
                }
            }
        });
        deleteObserver(o);
    }

    /**
     * Deletes a user from the database
     * @param email the users email address
     * @param fb the database
     */
    public void deleteUser(final String email, final FirebaseFirestore fb, Observer o) {
        success = false;
        addObserver(o);
        Task delete = fb.collection("users").document(email).delete();
        delete.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "user successfully deleted!");
                success = true;
                notifyObservers();
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "error deleting user", e);
                success = false;
                notifyObservers();
            }
        });
        deleteObserver(o);
    }
}
