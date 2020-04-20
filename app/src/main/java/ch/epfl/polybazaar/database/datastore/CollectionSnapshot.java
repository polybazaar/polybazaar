package ch.epfl.polybazaar.database.datastore;

import java.util.List;

public interface CollectionSnapshot {
    List<DataSnapshot> getDocuments();
}
