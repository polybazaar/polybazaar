package ch.epfl.polybazaar.UI;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.polybazaar.DataHolder;
import ch.epfl.polybazaar.MainActivity;
import ch.epfl.polybazaar.R;
import ch.epfl.polybazaar.litelisting.LiteListing;
import ch.epfl.polybazaar.login.Account;

import static ch.epfl.polybazaar.Utilities.checkUserLoggedIn;
import static ch.epfl.polybazaar.Utilities.getUser;

public class SalesOverview extends AppCompatActivity {

    private static final int EXTRALOAD = 20;
    private static final int NUMBEROFCOLUMNS = 2;
    private static final String bundleKey = "userSavedListings";
    private List<String> IDList;
    private List<LiteListing> liteListingList;
    private LiteListingAdapter adapter;
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
        LinearLayoutManager mGridLayoutManager = new GridLayoutManager(this, NUMBEROFCOLUMNS);
        rvLiteListings.setLayoutManager(mGridLayoutManager);

        // Triggered only when new data needs to be appended to the list
        EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener(mGridLayoutManager) {
            @Override
            public void onLoadMore() {
                // Triggered only when new data needs to be appended to the list
                loadLiteListingOverview();
            }
        };
        // Adds the scroll listener to RecyclerView
        rvLiteListings.addOnScrollListener(scrollListener);

        BottomNavigationView bottomNavigationView = findViewById(R.id.activity_main_bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.action_home);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> bottomBar.updateActivity(item.getItemId(), SalesOverview.this));
    }

    @Override
    public void onStart() {
        super.onStart();

        // prepare top menu
        TextView favorites = findViewById(R.id.favoritesOverview);
        favorites.setOnClickListener(v -> {
            if (checkUserLoggedIn(this)) {
                Account user = getUser();
                user.getUserData().addOnSuccessListener(authUser -> {
                    ArrayList<String> favoritesIds = authUser.getFavorites();

                    // the list of favorites of the user is empty
                    if (favoritesIds == null || favoritesIds.isEmpty()) {
                        Toast.makeText(getApplicationContext(), R.string.no_favorites, Toast.LENGTH_SHORT).show() ;
                        // we relaunch the activity with the list of favorites in the bundle
                    } else {
                        displaySavedListings(this, favoritesIds);
                    }
                });
            }
        });

        // activity is launched with a list of litelistings
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if(bundle.getBoolean(bundleKey)) {
                IDList = DataHolder.getInstance().getData();
            }
        }

        // Initial load
        loadLiteListingOverview();
    }


    /**
     * Create a graphical overview of LiteListings from database
     */
    public void loadLiteListingOverview() {
        LiteListing.fetchAll().addOnSuccessListener(result -> {
            if(result == null) {
                return;
            }
            if(IDList.isEmpty()) {
                for (LiteListing l : result) {
                    if (l != null) {
                        IDList.add(l.getId());      // create deep copy of ID list if list is empty
                    }
                }
            }
            int size = IDList.size();
            for (int i = positionInIDList; i < (positionInIDList + EXTRALOAD) && i < size; i++) {
                LiteListing.fetch(IDList.get(i)).addOnSuccessListener(result2 -> {
                    if (result2 != null) {
                        liteListingList.add(result2);
                        adapter.notifyItemInserted(liteListingList.size() - 1);
                    }
                });
                positionInIDList++;
            }
        });
    }

    /**
     * Returns the list of lite listings shown currently
     *
     * @return list of lite listings
     */
    public List<LiteListing> getLiteListingList() {
        return liteListingList;
    }


    /**
     * Display saved listings of the user (if any), they can be favorites or user created own listings
     *
     * @param savedListings the list of saved listings that has to be displayed in Sales Overview
     */
    public static void displaySavedListings(Context context, ArrayList<String> savedListings) {
        DataHolder.getInstance().setData(savedListings);
        Intent intent = new Intent(context, SalesOverview.class);
        Bundle extras = new Bundle();
        extras.putBoolean(bundleKey, true);
        intent.putExtras(extras);
        context.startActivity(intent);
    }

}
