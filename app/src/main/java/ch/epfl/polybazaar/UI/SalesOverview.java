package ch.epfl.polybazaar.UI;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import ch.epfl.polybazaar.DataHolder;
import ch.epfl.polybazaar.R;
import ch.epfl.polybazaar.litelisting.LiteListing;

public class SalesOverview extends AppCompatActivity {

    private static final int EXTRALOAD = 20;
    private static final int NUMBEROFCOLUMNS = 2;
    private static final String bundleKey = "userSavedListings";
    private Map<Timestamp, String> listingTimeMap;
    public static final String LISTING_ID = "listingID";
    private List<String> IDList;
    private List<LiteListing> liteListingList;
    private LiteListingAdapter adapter;
    private int positionInIDList = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_overview2);

        listingTimeMap = new TreeMap<>(Collections.reverseOrder());    // store LiteListing IDs in reverse order of creation (most recent first)
        IDList = new ArrayList<>();
        liteListingList = new ArrayList<>();

        // Lookup the recyclerview in activity layout
        RecyclerView rvLiteListings = findViewById(R.id.rvLiteListings);

        // Create adapter passing in the sample LiteListing data
        adapter = new LiteListingAdapter(liteListingList);

        adapter.setOnItemClickListener(view -> {
            int viewID = view.getId();
            String listingID = adapter.getListingID(viewID);
            Intent intent = new Intent(SalesOverview.this, SaleDetails.class);
            intent.putExtra(LISTING_ID, listingID);
            startActivity(intent);
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
                        listingTimeMap.put(l.getTimestamp(), l.getId());
                    }
                }
                // retrieve values from Treemap: litelistings IDs in order: most recent first
                IDList = new ArrayList<>(listingTimeMap.values());
            }
            int size = IDList.size();
            List<Task<LiteListing>> taskList = new ArrayList<>();
            // add fetch tasks in correct display order
            for (int i = positionInIDList; i < (positionInIDList + EXTRALOAD) && i < size; i++) {
                taskList.add(LiteListing.fetch(IDList.get(i)));
                positionInIDList++;
            }
            Tasks.<LiteListing>whenAllSuccess(taskList).addOnSuccessListener(list -> {
                int start = liteListingList.size();
                liteListingList.addAll(list);
                int itemCount = liteListingList.size() - start;
                adapter.notifyItemRangeInserted(start, itemCount);
            });
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
