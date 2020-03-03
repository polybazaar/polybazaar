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

import static ch.epfl.polybazaar.Utilities.isValidUser;

public class UserDatabase {

    private static final String TAG = "User.class";

    private Observable watched;

    private boolean success;

    private User fetchedUser;

    public UserDatabase(){
        watched = new Observable();
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
    public void storeNewUser(User user, FirebaseFirestore fb, Observer o) {
        success = false;
        watched.addObserver(o);
        // email already used:
        // TODO: check if email is already used -> fail
        // new user:
        Task setUser = fb.collection("users").document(user.getEmail()).set(user);
        setUser.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void nothing) {
                Log.d(TAG, "successfully added new user");
                success = true;
                watched.notifyObservers(success);
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "error adding new user", e);
                success = false;
                watched.notifyObservers(success);
            }
        });
        watched.deleteObserver(o);
        return;
    }

    /**
     * Fetch a user from the database
     * @param email the user to fetch's email
     * @param fb the Firestore database to fetch it from
     * @param o the observer, will be notified when request completes
     */
    public void fetchUser(String email, FirebaseFirestore fb, Observer o) {
        success = false;
        fetchedUser = null;
        watched.addObserver(o);
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
                        watched.notifyObservers(fetchedUser);
                    } else {
                        Log.d(TAG, "failed to fetch user data");
                        success = false;
                        fetchedUser = null;
                        watched.notifyObservers(fetchedUser);
                    }
                } else {
                    Log.d(TAG, "database request failed with", task.getException());
                    success = false;
                    fetchedUser = null;
                    watched.notifyObservers(fetchedUser);
                }
            }
        });
        watched.deleteObserver(o);
    }

    /**
     * Deletes a user from the database
     * @param email the users email address
     * @param fb the database
     */
    public void deleteUser(String email, FirebaseFirestore fb, Observer o) {
        success = false;
        watched.addObserver(o);
        Task delete = fb.collection("users").document(email).delete();
        delete.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "user successfully deleted!");
                success = true;
                watched.notifyObservers(success);
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "error deleting user", e);
                success = false;
                watched.notifyObservers(success);
            }
        });
        watched.deleteObserver(o);
    }
}
