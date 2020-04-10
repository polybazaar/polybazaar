package ch.epfl.polybazaar.testingUtilities;

import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

import ch.epfl.polybazaar.database.Model;
import ch.epfl.polybazaar.database.ModelTransaction;

import static ch.epfl.polybazaar.listing.ListingDatabase.queryListingStringEquality;
import static ch.epfl.polybazaar.litelisting.LiteListingDatabase.queryLiteListingStringEquality;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNot.not;

public abstract class DatabaseChecksUtilities {

    public static <T extends Model> void  assertDatabaseHasNoEntryWithField(String collection, String field, String value, Class<T> clazz){
        ModelTransaction.fetchAllWithFieldEquality(collection, field, value, clazz).addOnSuccessListener(ts -> assertThat(ts, is(empty())));
    }

    public static <T extends Model> void  assertDatabaseHasAtLeastOneEntryWithField(String collection, String field, String value, Class<T> clazz){
        ModelTransaction.fetchAllWithFieldEquality(collection, field, value, clazz).addOnSuccessListener(ts -> assertThat(ts, is(not(empty()))));
    }
}
