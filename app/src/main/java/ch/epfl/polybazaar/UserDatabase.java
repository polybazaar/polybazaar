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

    private Observable watched = new Observable();

    private boolean success = false;

    private User fetchedUser = null;

    public static UserDatabase getInstance() {
        return getInstance();
    }

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
        watched.addObserver(o);
        // email already used:
        if (isValidUser(fetchUser(user.getEmail(), fb))) {
            success = false;
            this.notify();
            watched.deleteObserver(o);
            return;
        }
        // new user:
        Task setUser = fb.collection("users").document(user.getEmail()).set(user);
        setUser.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void nothing) {
                Log.d(TAG, "successfully added new user");
                success = true;
                watched.notifyObservers();
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "error adding new user", e);
                success = false;
                watched.notifyObservers();
            }
        });
        watched.deleteObserver(o);
        return;
    }

    /**
     * Fetch a user from the database
     * @param email the user to fetch's email
     * @param fb the Firestore database to fetch it from
     * @return the user data, null if unsuccessfull
     */
    public User fetchUser(String email, FirebaseFirestore fb) {
        DocumentReference docRef = fb.collection("user").document(email);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "successfully retrieved user data");
                    } else {
                        Log.d(TAG, "failed to fetch user data");
                    }
                } else {
                    Log.d(TAG, "database request failed with", task.getException());
                }
            }
        });
        if (docRef.get().isSuccessful()) {
            DocumentSnapshot document = docRef.get().getResult();
            if (document.exists()) {
                User user = document.toObject(User.class);
                return user;
            }
        }
        return null;
    }

    /**
     * Deletes a user from the database
     * @param email the users email address
     * @param fb the database
     * @return true if successful
     */
    public static boolean deleteUser(String email, FirebaseFirestore fb) {
        Task delete = fb.collection("users").document(email).delete();
        delete.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "user successfully deleted!");
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "error deleting user", e);
            }
        });
        if (delete.isSuccessful()) {
            return true;
        }
        return false;
    }
}
