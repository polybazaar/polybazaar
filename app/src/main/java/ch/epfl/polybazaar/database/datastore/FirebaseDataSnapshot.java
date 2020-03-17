package ch.epfl.polybazaar.database.datastore;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Map;

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

    @Override
    public Map<String, Object> data() {
        return documentSnapshot.getData();
    }
}
