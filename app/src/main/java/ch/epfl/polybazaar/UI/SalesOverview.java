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

        /*LiteListing liteListing1 = new LiteListing("1", "listing1", "CHF 1");
        LiteListing liteListing2 = new LiteListing("2", "listing2", "CHF 1");
        LiteListing liteListing3 = new LiteListing("3", "listing3", "CHF 1");
        LiteListing liteListing4 = new LiteListing("4", "listing4", "CHF 1");
        LiteListing liteListing5 = new LiteListing("5", "listing5", "CHF 1");
        LiteListing liteListing6 = new LiteListing("6", "listing6", "CHF 1");
        LiteListing liteListing7 = new LiteListing("7", "listing7", "CHF 1");
        LiteListing liteListing8 = new LiteListing("8", "listing8", "CHF 1");
        LiteListing liteListing9 = new LiteListing("9", "listing9", "CHF 1");
        LiteListing liteListing10 = new LiteListing("10", "listing10", "CHF 1");
        LiteListing liteListing11 = new LiteListing("11", "listing11", "CHF 1");
        LiteListing liteListing12 = new LiteListing("12", "listing12", "CHF 1");
        LiteListing liteListing13 = new LiteListing("13", "listing13", "CHF 1");
        LiteListing liteListing14 = new LiteListing("14", "listing14", "CHF 1");
        LiteListing liteListing15 = new LiteListing("15", "listing15", "CHF 1");
        LiteListing liteListing16 = new LiteListing("16", "listing16", "CHF 1");
        LiteListing liteListing17 = new LiteListing("17", "listing17", "CHF 1");
        LiteListing liteListing18 = new LiteListing("18", "listing18", "CHF 1");
        LiteListing liteListing19 = new LiteListing("19", "listing19", "CHF 1");
        LiteListing liteListing20 = new LiteListing("20", "listing20", "CHF 1");
        LiteListing liteListing21 = new LiteListing("21", "listing21", "CHF 1");
        LiteListing liteListing22 = new LiteListing("22", "listing22", "CHF 1");
        LiteListing liteListing23 = new LiteListing("23", "listing23", "CHF 1");
        LiteListing liteListing24 = new LiteListing("24", "listing24", "CHF 1");
        LiteListing liteListing25 = new LiteListing("25", "listing25", "CHF 1");

        liteListingList.add(liteListing1);
        liteListingList.add(liteListing2);
        liteListingList.add(liteListing3);
        liteListingList.add(liteListing4);
        liteListingList.add(liteListing5);
        liteListingList.add(liteListing6);
        liteListingList.add(liteListing7);
        liteListingList.add(liteListing8);
        liteListingList.add(liteListing9);
        liteListingList.add(liteListing10);
        liteListingList.add(liteListing11);
        liteListingList.add(liteListing12);
        liteListingList.add(liteListing13);
        liteListingList.add(liteListing14);
        liteListingList.add(liteListing15);
        liteListingList.add(liteListing16);
        liteListingList.add(liteListing17);
        liteListingList.add(liteListing18);
        liteListingList.add(liteListing19);
        liteListingList.add(liteListing20);
        liteListingList.add(liteListing21);
        liteListingList.add(liteListing22);
        liteListingList.add(liteListing23);
        liteListingList.add(liteListing24);
        liteListingList.add(liteListing25);*/



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
        createLiteListingOverview();

        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore() {
                // Triggered only when new data needs to be appended to the list
                int curSize = adapter.getItemCount();

                /*List<LiteListing> moreLiteListings = new ArrayList<>();
                LiteListing liteListing26 = new LiteListing("26", "listing26", "CHF 1");
                LiteListing liteListing27 = new LiteListing("27", "listing27", "CHF 1");
                LiteListing liteListing28 = new LiteListing("28", "listing28", "CHF 1");
                LiteListing liteListing29 = new LiteListing("29", "listing29", "CHF 1");
                LiteListing liteListing30 = new LiteListing("30", "listing30", "CHF 1");
                moreLiteListings.add(liteListing26);
                moreLiteListings.add(liteListing27);
                moreLiteListings.add(liteListing28);
                moreLiteListings.add(liteListing29);
                moreLiteListings.add(liteListing30);
                liteListingList.addAll(moreLiteListings);

                adapter.notifyItemRangeInserted(curSize, moreLiteListings.size());*/
            }
        };
        // Adds the scroll listener to RecyclerView
        rvLiteListings.addOnScrollListener(scrollListener);
    }



    /**
     * Create a graphical overview of LiteListings from database
     */
    public void createLiteListingOverview() {
        LiteListingListCallback callbackLiteListingList = new LiteListingListCallback() {

            @Override
            public void onCallback(List<String> result) {
                for(String id : result) {
                    IDList.add(id);      // create deep copy of ID list
                }
                LiteListingCallback callbackLiteListing = new LiteListingCallback() {
                    @Override
                    public void onCallback(LiteListing result) {
                        liteListingList.add(result);
                    }
                };
                int size = IDList.size();
                for(int i = 0; i < size; i++) {
                    fetchLiteListing(IDList.get(i), callbackLiteListing);
                }
                adapter.notifyItemRangeInserted(adapter.getItemCount(), size);
            }
        };
        fetchLiteListingList(callbackLiteListingList);
    }








}
