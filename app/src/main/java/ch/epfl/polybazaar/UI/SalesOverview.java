package ch.epfl.polybazaar.UI;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.polybazaar.R;
import ch.epfl.polybazaar.SaleDetails;
import ch.epfl.polybazaar.database.callback.LiteListingCallback;
import ch.epfl.polybazaar.litelisting.LiteListing;

import static ch.epfl.polybazaar.Utilities.checkUserLoggedIn;
import static ch.epfl.polybazaar.favorites.Favorites.displayFavorites;
import static ch.epfl.polybazaar.litelisting.LiteListingDatabase.fetchLiteListing;

public class SalesOverview extends AppCompatActivity {

    private List<String> IDList;
    private List<LiteListing> liteListingList;
    private LiteListingAdapter adapter;
    private static final int EXTRALOAD = 20;
    private int positionInIDList = 0;

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

        adapter.setOnItemClickListener(new LiteListingAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view) {
                int viewID = view.getId();
                String listingID = adapter.getListingID(viewID);
                Intent intent = new Intent(SalesOverview.this, SaleDetails.class);
                intent.putExtra("listingID", listingID);
                startActivity(intent);
            }
        });

        // Attach the adapter to the recyclerview to populate items
        rvLiteListings.setAdapter(adapter);
        // Set layout manager to position the items
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvLiteListings.setLayoutManager(linearLayoutManager);

        // Triggered only when new data needs to be appended to the list
        EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore() {
                // Triggered only when new data needs to be appended to the list
                loadLiteListingOverview();
            }
        };
        // Adds the scroll listener to RecyclerView
        rvLiteListings.addOnScrollListener(scrollListener);
    }

    @Override
    public void onStart() {
        super.onStart();

        // prepare top menu
        TextView favorites = findViewById(R.id.favoritesOverview);
        favorites.setOnClickListener(v -> {
            if(checkUserLoggedIn(this)) {
                displayFavorites(this);
            };
        });

        // activity is launched with a list of favorites
        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            IDList = bundle.getStringArrayList("favoritesList");
        }

        // Initial load
        loadLiteListingOverview();
    }


    /**
     * Create a graphical overview of LiteListings from database
     */
    public void loadLiteListingOverview() {
        LiteListing.retrieveAll().addOnSuccessListener(result -> {
            if(IDList.isEmpty()) {
                for (LiteListing l : result) {
                    IDList.add(l.getId());      // create deep copy of ID list if list is empty
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
        });
    }

    /**
     * Returns the list of lite listings shown currently
     * @return list of lite listings
     */
    public List<LiteListing> getLiteListingList() {
        return liteListingList;
    }


}
