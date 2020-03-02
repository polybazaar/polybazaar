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

import java.util.Date;

import static ch.epfl.polybazaar.Utilities.*;

public class User {

    private static final String TAG = "User.class";

    private String firstName;
    private String lastName;
    private Date dateOfBirth;
    private String email;

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public String getEmail() {
        return email;
    }

    /**
     * User Constructor
     * @param firstName first name
     * @param lastName last name
     * @param dateOfBirth date of birth
     * @param email email address, unique identifier (key)
     * @throws IllegalArgumentException
     */
    public User(String firstName, String lastName, Date dateOfBirth, String email) throws IllegalArgumentException {
        if (nameIsValid(firstName)) {
            this.firstName = firstName;
        } else {
            throw new IllegalArgumentException("first name has invalid format");
        }
        if (nameIsValid(lastName)) {
            this.lastName = lastName;
        } else {
            throw new IllegalArgumentException("last name has invalid format");
        }
        if (dateIsValid(dateOfBirth)) {
            this.dateOfBirth = dateOfBirth;
        } else {
            throw new IllegalArgumentException("date of birth has invalid format");
        }
        if (emailIsValid(email)) {
            this.email = email;
        } else {
            throw new IllegalArgumentException("email has invalid format");
        }
    }

    /**
     * Add an user to the database, using its email as unique identifier (key)
     * @param user the user
     * @param fb the Firestore database instance
     * @return true if successful
     */
    public boolean storeNewUser(User user, FirebaseFirestore fb) {
        // email already used:
        if (isValidUser(fetchUser(user.email, fb))) {
            return false;
        }
        // new user:
        Task setUser = fb.collection("users").document(user.email).set(user);
        setUser.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void nothing) {
                Log.d(TAG, "successfully added new user");
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "error adding new user", e);
            }
        });
        if (setUser.isSuccessful()) {
            return  true;
        }
        return false;
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

}