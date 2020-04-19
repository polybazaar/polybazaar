package ch.epfl.polybazaar.database.datastore.firebase;

import com.google.firebase.firestore.DocumentSnapshot;

import ch.epfl.polybazaar.database.datastore.DataSnapshot;

public class FirebaseDataSnapshot implements DataSnapshot {

    private DocumentSnapshot documentSnapshot;

    public FirebaseDataSnapshot(DocumentSnapshot documentSnapshot) {
        this.documentSnapshot = documentSnapshot;
    }

    @Override
    public boolean exists() {
        return documentSnapshot.exists();
    }

    @Override
    public Object get(String field) {
        return documentSnapshot.get(field);
    }
}
