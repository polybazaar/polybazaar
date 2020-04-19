package ch.epfl.polybazaar.database.datastore;

import java.util.List;

import ch.epfl.polybazaar.database.Model;

public interface CollectionSnapshot {
    public List<DataSnapshot> getDocuments();
}
