package ch.epfl.polybazaar.search;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Map;

import ch.epfl.polybazaar.DataHolder;
import ch.epfl.polybazaar.R;
import ch.epfl.polybazaar.UI.SalesOverview;

import static ch.epfl.polybazaar.UI.SalesOverview.displaySavedListings;

public class SearchListings extends AppCompatActivity {

    Map<String, String> searchListingTitleMap;
    ArrayList<String> sortedIDs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            performSearch(query);
        }
        else {
            startActivity(new Intent(SearchListings.this, SalesOverview.class));
        }
    }

    public void performSearch(String query) {
        searchListingTitleMap = DataHolder.getInstance().getDataMap();
        sortedIDs = new ArrayList<>();

        for (Map.Entry<String, String> entry: searchListingTitleMap.entrySet()) {
            if(entry.getValue().contains(query)) {
                sortedIDs.add(entry.getKey());
            }
        }

        if(sortedIDs.isEmpty()) {
            Toast toast = Toast.makeText(SearchListings.this, R.string.no_results, Toast.LENGTH_LONG);
            toast.show();
        }
        displaySavedListings(this, sortedIDs);
    }

}
