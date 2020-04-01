package ch.epfl.polybazaar.database.datastore.firebase;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Map;

import ch.epfl.polybazaar.database.Model;
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

    @Override
    public Map<String, Object> data() {
        return documentSnapshot.getData();
    }

    @Override
    public String getId() {
        return documentSnapshot.getId();
    }

    @Override
    public <T extends Model> T toObject(Class<T> clazz) {
        return documentSnapshot.toObject(clazz);
    }
}
