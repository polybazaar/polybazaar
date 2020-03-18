package ch.epfl.polybazaar.UI;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.polybazaar.R;
import ch.epfl.polybazaar.SaleDetails;
import ch.epfl.polybazaar.database.callback.LiteListingCallback;
import ch.epfl.polybazaar.database.callback.LiteListingListCallback;
import ch.epfl.polybazaar.litelisting.LiteListing;

import static ch.epfl.polybazaar.litelisting.LiteListingDatabase.fetchLiteListing;
import static ch.epfl.polybazaar.litelisting.LiteListingDatabase.fetchLiteListingList;

public class SalesOverview extends AppCompatActivity {

    // TODO: add tests for error cases (ex empty list on database)

    private List<String> IDList;
    private List<LiteListing> liteListingList;
    private LiteListingAdapter adapter;
    private EndlessRecyclerViewScrollListener scrollListener;
    private final int EXTRALOAD = 20;
    private int positionInIDList = 0;

    // Create an anonymous implementation of OnClickListener
    private View.OnClickListener titleClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            int viewID = v.getId();
            String listingID = adapter.getListingID(viewID);
            Intent intent = new Intent(SalesOverview.this, SaleDetails.class);
            intent.putExtra("listingID", listingID);
            startActivity(intent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_overview2);

        IDList = new ArrayList<>();
        liteListingList = new ArrayList<>();

        // Lookup the recyclerview in activity layout
        RecyclerView rvLiteListings = findViewById(R.id.rvLiteListings);
        
        // Create adapter passing in the sample LiteListing data
        adapter = new LiteListingAdapter(liteListingList);
        // Attach the adapter to the recyclerview to populate items
        rvLiteListings.setAdapter(adapter);
        // Set layout manager to position the items
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvLiteListings.setLayoutManager(linearLayoutManager);

        // Initial load
        loadLiteListingOverview();

        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore() {
                // Triggered only when new data needs to be appended to the list
                loadLiteListingOverview();
            }
        };
        // Adds the scroll listener to RecyclerView
        rvLiteListings.addOnScrollListener(scrollListener);

    }


    /**
     * Create a graphical overview of LiteListings from database
     */
    public void loadLiteListingOverview() {
        LiteListingListCallback callbackLiteListingList = new LiteListingListCallback() {

            @Override
            public void onCallback(List<String> result) {
                if(IDList.isEmpty()) {
                    for (String id : result) {
                        IDList.add(id);      // create deep copy of ID list if list is empty
                    }
                }
                LiteListingCallback callbackLiteListing = new LiteListingCallback() {
                    @Override
                    public void onCallback(LiteListing result) {
                        liteListingList.add(result);
                        adapter.notifyItemInserted(liteListingList.size()-1);
                    }
                };
                int size = IDList.size();
                for(int i = positionInIDList; i < (positionInIDList + EXTRALOAD) && i < size; i++) {
                    fetchLiteListing(IDList.get(i), callbackLiteListing);
                    positionInIDList++;
                }
            }
        };
        fetchLiteListingList(callbackLiteListingList);
    }

}
