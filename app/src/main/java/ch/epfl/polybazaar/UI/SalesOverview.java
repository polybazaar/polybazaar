package ch.epfl.polybazaar.UI;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.polybazaar.R;
import ch.epfl.polybazaar.SaleDetails;
import ch.epfl.polybazaar.category.Category;
import ch.epfl.polybazaar.category.CategoryRepository;
import ch.epfl.polybazaar.category.StringCategory;
import ch.epfl.polybazaar.database.callback.LiteListingCallback;
import ch.epfl.polybazaar.litelisting.LiteListing;

import static ch.epfl.polybazaar.litelisting.LiteListingDatabase.fetchLiteListing;

public class SalesOverview extends AppCompatActivity {

    // TODO: add tests for error cases (ex empty list on database)

    private List<String> IDList;
    private List<LiteListing> liteListingList;
    private LiteListingAdapter adapter;
    private static final int EXTRALOAD = 20;
    private int positionInIDList = 0;
    private Spinner categorySelector;
    private final String DEFAULT_SPINNER_TEXT = "Select category...";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_overview2);

        IDList = new ArrayList<>();
        liteListingList = new ArrayList<>();

        categorySelector = findViewById(R.id.categorySelector);
        setupSpinner(categorySelector, categoriesWithDefaultText(CategoryRepository.categories));

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

        // Initial load
        loadLiteListingOverview();

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

    private void setupSpinner(Spinner spinner, List<Category> categories){
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categories);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Category selectedCategory = (Category)parent.getItemAtPosition(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Do nothing
            }
        });
    }
    private List<Category> categoriesWithDefaultText(List<Category> categories){
        List<Category> categoriesWithDefText = new ArrayList<>(categories);
        categoriesWithDefText.add(0, new StringCategory(DEFAULT_SPINNER_TEXT));
        return categoriesWithDefText;
    }

}


