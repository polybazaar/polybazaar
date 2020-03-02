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
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;

import static ch.epfl.polybazaar.Utilities.*;

public class User {

    private static final String TAG = "User.class";
    private String firstName;
    private String lastName;
    private Date dateOfBirth;
    private String email;

    public User(String firstName, String lastName, Date dateOfBirth, String email) throws IllegalArgumentException {
        if (nameIsValid(firstName)) {
            this.firstName = firstName;
        } else {
            throw new IllegalArgumentException("first name has invalid format");
        }
        ;
        if (nameIsValid(lastName)) {
            this.lastName = lastName;
        } else {
            throw new IllegalArgumentException("last name has invalid format");
        }
        ;
        if (dateIsValid(dateOfBirth)) {
            this.dateOfBirth = dateOfBirth;
        } else {
            throw new IllegalArgumentException("date of birth has invalid format");
        }
        ;
        if (emailIsValid(email)) {
            this.email = email;
        } else {
            throw new IllegalArgumentException("email has invalid format");
        }
        ;
    }

    /**
     * Add an user to the database, using its email as unique identifier (key)
     * @param user the user
     * @param fb the Firestore database instance
     * @return true if successful
     * @throws FirebaseFirestoreException
     */
    public boolean storeNewUser(User user, FirebaseFirestore fb) throws FirebaseFirestoreException {
        try {
            fb.collection("users").add(user).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    return;
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    return;
                }
            });
        } catch (Exception e) {
            throw new FirebaseFirestoreException("failed to store user", FirebaseFirestoreException.Code.UNKNOWN);
        }
        return true;
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
                    Log.d(TAG, "database request failed with ", task.getException());
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