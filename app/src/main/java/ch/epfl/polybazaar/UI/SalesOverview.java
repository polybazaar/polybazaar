package ch.epfl.polybazaar.UI;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SearchView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import ch.epfl.polybazaar.DataHolder;
import ch.epfl.polybazaar.R;
import ch.epfl.polybazaar.category.Category;
import ch.epfl.polybazaar.category.CategoryFragment;
import ch.epfl.polybazaar.category.RootCategoryFactory;
import ch.epfl.polybazaar.litelisting.LiteListing;
import ch.epfl.polybazaar.login.Account;
import ch.epfl.polybazaar.login.Authenticator;
import ch.epfl.polybazaar.login.AuthenticatorFactory;
import ch.epfl.polybazaar.saledetails.ListingManager;
import ch.epfl.polybazaar.search.SearchListings;
import ch.epfl.polybazaar.user.User;
import ch.epfl.polybazaar.utilities.SatCompassShakeDetector;

import static ch.epfl.polybazaar.chat.ChatActivity.removeBottomBarWhenKeyboardUp;
import static ch.epfl.polybazaar.widgets.MinimalAlertDialog.makeDialog;

public class SalesOverview extends AppCompatActivity implements CategoryFragment.CategoryFragmentListener,SearchView.OnQueryTextListener{

    private static final int EXTRALOAD = 20;
    private static final int NUMBEROFCOLUMNS = 2;
    private static final String bundleKey = "userSavedListings";
    private static final String referenceSearchList = "referenceSearchList";
    private static final float FILTER_ELEVATION = 10;
    private static final int PRICEMIN = 0;
    private static final int PRICEMAX = 1000;
    private static final int PRICESTEP = 10;
    private static final int DAYSMIN = 0;
    private static final int DAYSMAX = 90;
    private static final int DAYSSTEP = 5;
    private static final int MAXDAYSFILTER = 1000;
    private Map<Timestamp, String> listingTimeMap;
    private Map<String, String> listingTitleMap;
    private Map<String, String> searchListingTitleMap;
    private Map<String, Double> listingPriceMap;
    private Map<String, Date> listingDateMap;
    public static final String LISTING_ID = "listingID";
    private List<String> IDList;
    private List<LiteListing> liteListingList;
    private ArrayList<String> liteListingFilterList;
    private LiteListingAdapter adapter;
    private int positionInIDList = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_overview2);

        listingTimeMap = new TreeMap<>(Collections.reverseOrder());    // store LiteListing IDs in reverse order of creation (most recent first)
        listingTitleMap = new TreeMap<>();
        listingPriceMap = new TreeMap<>();
        listingDateMap = new TreeMap<>();
        searchListingTitleMap = new LinkedHashMap<>();
        IDList = new ArrayList<>();
        liteListingList = new ArrayList<>();
        liteListingFilterList = new ArrayList<>();
        RootCategoryFactory.useJSONCategory(getApplicationContext());

        RootCategoryFactory.useJSONCategory(getApplicationContext());

        TextView catButton = findViewById(R.id.categoryOverview);
        catButton.setOnClickListener(view->{
            FragmentManager fragmentManager = getSupportFragmentManager();
            CategoryFragment categoryFragment = CategoryFragment.newInstance(RootCategoryFactory.getDependency(),
                   R.id.salesOverview_fragment_container,fragmentManager.getBackStackEntryCount());
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.addToBackStack(null)
                    .add(R.id.salesOverview_fragment_container,categoryFragment).commit();

        });

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

        SatCompassShakeDetector.start(this);
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

    /**
     * Display the pop-up filter window. Dismissed when the user clicks outside of it
     * @param view the parent view to which the pop-up window is attached
     */
    public void onButtonShowPopupWindowClick(View view) {

        // setContentView(R.layout.filter_pop_up_window);

        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.filter_pop_up_window, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);

        // show the popup window
        View parent = findViewById(R.id.UserClickableFilterMenu);
        popupWindow.setElevation(FILTER_ELEVATION);
        popupWindow.showAtLocation(parent, Gravity.TOP | Gravity.RIGHT, 0, 0);

        // add OnSeekBarChangeListener on seekbars
        SeekBar priceMinSeekBar = popupWindow.getContentView().findViewById(R.id.minPriceSeekBar);
        SeekBar priceMaxSeekBar = popupWindow.getContentView().findViewById(R.id.maxPriceSeekBar);
        SeekBar daysMaxSeekBar = popupWindow.getContentView().findViewById(R.id.ageSeekBar);

        priceMinSeekBar.setMax(PRICEMAX);
        priceMaxSeekBar.setMax(PRICEMAX);
        daysMaxSeekBar.setMax(DAYSMAX);

        TextView priceMinValue = popupWindow.getContentView().findViewById(R.id.min_price_value);
        TextView priceMaxValue = popupWindow.getContentView().findViewById(R.id.max_price_value);
        TextView daysMaxValue = popupWindow.getContentView().findViewById(R.id.max_days_value);


        priceMinSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int value = (progress * (PRICEMAX - PRICEMIN)) / PRICEMAX;
                int displayValue = ((value + PRICEMIN) / PRICESTEP) * PRICESTEP;
                priceMinValue.setText(String.valueOf(displayValue));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        priceMaxSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int value = (progress * (PRICEMAX - PRICEMIN)) / PRICEMAX;
                int displayValue = ((value + PRICEMIN) / PRICESTEP) * PRICESTEP;
                priceMaxValue.setText(String.valueOf(displayValue));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        daysMaxSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int value = (progress * (DAYSMAX - DAYSMIN)) / DAYSMAX;
                int displayValue = ((value + DAYSMIN) / DAYSSTEP) * DAYSSTEP;
                daysMaxValue.setText(String.valueOf(displayValue));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        Button filter =  popupWindow.getContentView().findViewById(R.id.filterButton);
        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double minPrice;
                double maxPrice;
                int maxDays;

                TextView minPriceView = popupWindow.getContentView().findViewById(R.id.min_price_value);
                String minPriceText = minPriceView.getText().toString();
                if(minPriceText.isEmpty()) {
                    minPrice = Double.MIN_VALUE;
                } else {
                    minPrice = Double.parseDouble(minPriceText);
                }

                TextView maxPriceView = popupWindow.getContentView().findViewById(R.id.max_price_value);
                String maxPriceText = maxPriceView.getText().toString();
                if(maxPriceText.isEmpty()) {
                    maxPrice = Double.MAX_VALUE;
                } else {
                    maxPrice = Double.parseDouble(maxPriceText);
                }

                TextView maxDaysView = popupWindow.getContentView().findViewById(R.id.max_days_value);
                String maxDaysText = maxDaysView.getText().toString();
                if(maxDaysText.isEmpty()) {
                    maxDays = MAXDAYSFILTER;
                } else {
                    maxDays = Integer.parseInt(maxDaysText);
                }

                if(minPrice > maxPrice) {
                    makeDialog(SalesOverview.this, R.string.price_filter_seekbar);
                } else {
                    filterListings(minPrice, maxPrice, maxDays);
                }
            }
        });

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
            if(bundle.getBoolean(referenceSearchList)) {
                searchListingTitleMap = DataHolder.getInstance().getDataMap();
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
        searchIntent.setAction(Intent.ACTION_SEARCH);
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
            Calendar calendar = Calendar.getInstance(); // Now
            calendar.add(Calendar.DAY_OF_YEAR, -1);
            Date expDate =  calendar.getTime();
            // fill maps <Timestamp, listingID> and <listingID, title>
            for (LiteListing l : result) {
                if (l != null) {
                    // delete sold listings older than a week
                    if ((l.getPrice().equals(getResources().getString(R.string.sold))) && (l.getTimeSold() != null) && (l.getTimeSold().toDate().before(expDate))) {
                        ListingManager.deleteCurrentListing(l.getId(), false, this);
                    } else {
                        listingTimeMap.put(l.getTimestamp(), l.getId());
                        listingTitleMap.put(l.getId(), l.getTitle());
                        try{
                            listingPriceMap.put(l.getId(), Double.parseDouble(l.getPrice()));
                        }
                        catch (NumberFormatException nfe){
                            listingPriceMap.put(l.getId(), -1.0);
                        }
                        listingDateMap.put(l.getId(), l.getTimestamp().toDate());
                    }
                }
            }
            if (IDList.isEmpty()) {
                // retrieve values from Treemap: litelistings IDs in order: most recent first
                IDList = new ArrayList<>(listingTimeMap.values());
            }

            int size = IDList.size();

            // Prepare a  map <listingID, title> sorted by most recent first, for search purposes
            if(searchListingTitleMap.isEmpty()) {
                for (int i = 0; i < size; i++) {
                    String key = IDList.get(i);
                    searchListingTitleMap.put(key, listingTitleMap.get(key).toLowerCase());
                }
            }

            List<Task<LiteListing>> taskList = new ArrayList<>();
            // add fetch tasks in correct display order
            for (int i = positionInIDList; i < (positionInIDList + EXTRALOAD) && i < size; i++) {
                taskList.add(LiteListing.fetch(IDList.get(i)).onSuccessTask(liteListing -> {
                    return liteListing.fetchThumbnail(SalesOverview.this).onSuccessTask(r ->
                            Tasks.forResult(liteListing)
                    );
                }));
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
    public static void displaySavedListings(Context context, ArrayList<String> savedListings, int text) {
        ArrayList<String> displayListings = new ArrayList<>();
        List<Task<LiteListing>> taskList = new ArrayList<>();
        for (String liteListingID : savedListings) {
            taskList.add(
            LiteListing.fetch(liteListingID).addOnSuccessListener(liteListing -> {
                if (liteListing == null) {
                    Account account = AuthenticatorFactory.getDependency().getCurrentUser();
                    User.fetch(account.getEmail()).addOnSuccessListener(user -> {
                        user.removeFavorite(liteListingID);
                        user.deleteOwnListing(liteListingID);
                        user.save();
                    });
                } else {
                    displayListings.add(liteListingID);
                }
            }));
        }
        Tasks.whenAllComplete(taskList).addOnCompleteListener(aVoid -> {
            if (!displayListings.isEmpty()) {
                DataHolder.getInstance().setData(displayListings);
                Intent intent = new Intent(context, SalesOverview.class);
                Bundle extras = new Bundle();
                extras.putBoolean(bundleKey, true);
                extras.putBoolean(referenceSearchList, true);
                intent.putExtras(extras);
                context.startActivity(intent);
            } else {
                makeDialog(context, text);
            }
        });
    }


    @Override
    public void onCategoryFragmentInteraction(Category category) {

        Intent categoryIntent = new Intent(this, SearchListings.class);
        categoryIntent.putExtra("category", category.toString());
        startActivity(categoryIntent);
    }

    /**
     * Filter listings according to user selection in filter popupwindow
     * @param minPrice the minimum price of the listing
     * @param maxPrice the maximum price of the listing
     * @param maxDays the maximum age of the listing
     */
    public void filterListings(double minPrice, double maxPrice, int maxDays) {
        liteListingFilterList.clear();
        Calendar calendar = Calendar.getInstance(); // Now
        calendar.add(Calendar.DAY_OF_YEAR, -maxDays);
        Date expDate =  calendar.getTime();

        for(Map.Entry<String, Double> entry : listingPriceMap.entrySet()) {
            String listingID = entry.getKey();
            double listingPrice = entry.getValue();
            Date listingDate = listingDateMap.get(listingID);

            if (listingPrice >= minPrice && listingPrice <= maxPrice && listingDate.after(expDate)) {
                liteListingFilterList.add(listingID);
            }
        }
        displaySavedListings(this, liteListingFilterList, R.string.no_match_found);
    }

}
