package ch.epfl.polybazaar.UI;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.polybazaar.R;
import ch.epfl.polybazaar.SaleDetails;
import ch.epfl.polybazaar.category.Category;
import ch.epfl.polybazaar.category.CategoryRepository;
import ch.epfl.polybazaar.category.RootCategory;
import ch.epfl.polybazaar.category.StringCategory;
import ch.epfl.polybazaar.database.callback.LiteListingCallback;
import ch.epfl.polybazaar.database.callback.StringListCallback;
import ch.epfl.polybazaar.listing.ListingDatabase;
import ch.epfl.polybazaar.litelisting.LiteListing;
import ch.epfl.polybazaar.litelisting.LiteListingDatabase;

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
    private String currentQuery = DEFAULT_SPINNER_TEXT;
    private Category currentCategory = new RootCategory();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_overview2);

        IDList = new ArrayList<>();
        liteListingList = new ArrayList<>();

        //setup the categories list
        categorySelector = findViewById(R.id.categorySelector);
        List<Category> categories = new ArrayList<>(CategoryRepository.categories);
        categories.add(0,new RootCategory());
        setupSpinner(categorySelector,categories);

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
        if(!currentCategory.equals(new RootCategory())){
            loadLiteListingsfromCategory();
        }else{
            loadAllLiteListings();
        }
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


                //refresh currentQuery
                Category selectedCategory = (Category)parent.getItemAtPosition(position);
                //loadLiteListingQueryOverview(selectedCategory.toString());
                currentQuery = selectedCategory.toString();
                currentCategory = selectedCategory;
                loadLiteListingOverview();

                //re-init layout
                liteListingList = new ArrayList<>();
                IDList = new ArrayList<>();
                positionInIDList = 0;
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
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
                rvLiteListings.setLayoutManager(linearLayoutManager);





            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Do nothing
            }
        });
    }

    private void loadLiteListingsfromCategory(){
        List<Category> allCategories = getAllSubCategories(currentCategory);
        allCategories.add(currentCategory);

        for(Category category : allCategories) {
            LiteListingDatabase.queryLiteListingStringEquality("category", category.toString(), result -> {

                for (String l : result) {
                    IDList.add(l);      // create deep copy of ID list if list is empty
                }

                LiteListingCallback callbackLiteListing = new LiteListingCallback() {
                    @Override
                    public void onCallback(LiteListing result) {
                        liteListingList.add(result);
                        adapter.notifyItemInserted(liteListingList.size() - 1);
                    }
                };

                int size = IDList.size();
                for (int i = positionInIDList; i < (positionInIDList + EXTRALOAD) && i < size; i++) {
                    fetchLiteListing(IDList.get(i), callbackLiteListing);
                    positionInIDList++;
                }
            });
        }
    }
    private void loadAllLiteListings(){
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

    List<Category> getAllSubCategories(Category category){
        if(!category.hasSubCategories()){
            return new ArrayList<>();
        }else{
            List<Category> subCategories = category.subCategories();
            List<Category> allCategories = new ArrayList<>(subCategories);
            for(Category cat : subCategories){
                allCategories.addAll(getAllSubCategories(cat));
            }
            return allCategories;
        }
    }

}


