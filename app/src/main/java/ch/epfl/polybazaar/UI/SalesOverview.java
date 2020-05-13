package ch.epfl.polybazaar.UI;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import ch.epfl.polybazaar.DataHolder;
import ch.epfl.polybazaar.R;
import ch.epfl.polybazaar.litelisting.LiteListing;
import ch.epfl.polybazaar.login.Account;
import ch.epfl.polybazaar.login.AuthenticatorFactory;
import ch.epfl.polybazaar.search.SearchListings;
import ch.epfl.polybazaar.user.User;

import static ch.epfl.polybazaar.chat.ChatActivity.removeBottomBarWhenKeyboardUp;
import static ch.epfl.polybazaar.widgets.MinimalAlertDialog.makeDialog;

public class SalesOverview extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private static final int EXTRALOAD = 20;
    private static final int NUMBEROFCOLUMNS = 2;
    private static final String bundleKey = "userSavedListings";
    private Map<Timestamp, String> listingTimeMap;
    private Map<String, String> listingTitleMap;
    private Map<String, String> searchListingTitleMap;
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
        listingTitleMap = new TreeMap<>();
        searchListingTitleMap = new LinkedHashMap<>();
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

        // Display the app bar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.searchbar);
        setSupportActionBar(myToolbar);

        BottomNavigationView bottomNavigationView = findViewById(R.id.activity_main_bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.action_home);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> bottomBar.updateActivity(item.getItemId(), SalesOverview.this));
        removeBottomBarWhenKeyboardUp(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // display the SearchView
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(new
                        ComponentName(this, SearchListings.class)));

        searchView.setOnQueryTextListener(this);

        return true;
    }


    @Override
    public void onStart() {
        super.onStart();

        Intent intent = getIntent();

        // activity is launched with a list of litelistings
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            if (bundle.getBoolean(bundleKey)) {
                IDList = DataHolder.getInstance().getData();
            }
        }

        // Initial load
        loadLiteListingOverview();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        Intent searchIntent = new Intent(this, SearchListings.class);
        searchIntent.putExtra(SearchManager.QUERY, query);

        // transmit listing information to SearchListings class via DataHolder singleton class
        DataHolder.getInstance().setDataMap(searchListingTitleMap);
        // searchIntent.setAction(Intent.ACTION_SEARCH);

        startActivity(searchIntent);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }


    /**
     * Create a graphical overview of LiteListings from database
     */
    public void loadLiteListingOverview() {
        LiteListing.fetchAll().addOnSuccessListener(result -> {
            if (result == null) {
                return;
            }

            // fill maps <Timestamp, listingID> and <listingID, title>
            for (LiteListing l : result) {
                if (l != null) {
                    listingTimeMap.put(l.getTimestamp(), l.getId());
                    listingTitleMap.put(l.getId(), l.getTitle());
                }
            }
            if (IDList.isEmpty()) {
                // retrieve values from Treemap: litelistings IDs in order: most recent first
                IDList = new ArrayList<>(listingTimeMap.values());
            }

            int size = IDList.size();

            // Prepare a  map <listingID, title> sorted by most recent first, for search purposes
            for (int i = 0; i < size; i++) {
                String key = IDList.get(i);
                searchListingTitleMap.put(key, listingTitleMap.get(key).toLowerCase());
            }

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
     * @param text the text in dialog to be shown if there are no matching listings
     */
    public static void displaySavedListings(Context context, ArrayList<String> savedListings, int text) {
        ArrayList<String> displayListings = new ArrayList<>();
        List<Task<LiteListing>> taskList = new ArrayList<>();
        for (String liteListingID : savedListings) {
            taskList.add(LiteListing.fetch(liteListingID));
            LiteListing.fetch(liteListingID).addOnSuccessListener(liteListing -> {
                if (liteListing == null) {
                    Account account = AuthenticatorFactory.getDependency().getCurrentUser();
                    User.fetch(account.getEmail()).addOnSuccessListener(user -> {
                        user.removeFavorite(liteListingID);
                        user.save();
                    });
                } else {
                    displayListings.add(liteListingID);
                }
            });
        }
        Tasks.whenAllComplete(taskList).addOnCompleteListener(aVoid -> {
            if (!displayListings.isEmpty()) {
                DataHolder.getInstance().setData(displayListings);
                Intent intent = new Intent(context, SalesOverview.class);
                Bundle extras = new Bundle();
                extras.putBoolean(bundleKey, true);
                intent.putExtras(extras);
                context.startActivity(intent);
            } else {
                makeDialog(context, text);
            }
        });
    }

}
