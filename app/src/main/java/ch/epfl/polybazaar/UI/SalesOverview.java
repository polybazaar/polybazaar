package ch.epfl.polybazaar.UI;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

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
    private TreeMap<Integer, String> viewIDtoListingIDMap;
    private ScrollView scroll;
    private LinearLayout linearLayout;
    private LiteListingAdapter adapter;
    private EndlessRecyclerViewScrollListener scrollListener;

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
        viewIDtoListingIDMap = new TreeMap<>();
        scroll = new ScrollView(this);
        linearLayout = new LinearLayout(this);

        // Lookup the recyclerview in activity layout
        RecyclerView rvLiteListings = findViewById(R.id.rvLiteListings);

        // TODO: delete
        // initialize LiteListing list
        LiteListing liteListing1 = new LiteListing("1", "title1", "price1");

        liteListingList.add(liteListing1);
        liteListingList.add(liteListing1);
        liteListingList.add(liteListing1);
        liteListingList.add(liteListing1);
        liteListingList.add(liteListing1);
        liteListingList.add(liteListing1);
        liteListingList.add(liteListing1);
        liteListingList.add(liteListing1);
        liteListingList.add(liteListing1);
        liteListingList.add(liteListing1);
        liteListingList.add(liteListing1);
        liteListingList.add(liteListing1);
        liteListingList.add(liteListing1);
        liteListingList.add(liteListing1);
        liteListingList.add(liteListing1);
        liteListingList.add(liteListing1);
        liteListingList.add(liteListing1);
        liteListingList.add(liteListing1);
        liteListingList.add(liteListing1);
        liteListingList.add(liteListing1);


        // Create adapter passing in the sample LiteListing data
        adapter = new LiteListingAdapter(liteListingList);
        // Attach the adapter to the recyclerview to populate items
        rvLiteListings.setAdapter(adapter);
        // Set layout manager to position the items
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvLiteListings.setLayoutManager(linearLayoutManager);

        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                loadNextDataFromApi(page);
            }
        };
        // Adds the scroll listener to RecyclerView
        rvLiteListings.addOnScrollListener(scrollListener);

        // Previous version:
        // create graphical overview of LiteListings
        // createLiteListingOverview();
    }


    public ScrollView getScroll() {
        return scroll;
    }

    public LinearLayout getLinearLayout() {
        return linearLayout;
    }


    // Append the next page of data into the adapter
    // This method probably sends out a network request and appends new data items to your adapter.
    public void loadNextDataFromApi(int offset) {
        // Send an API request to retrieve appropriate paginated data
        //  --> Send the request including an offset value (i.e `page`) as a query parameter.
        //  --> Deserialize and construct new model objects from the API response
        //  --> Append the new data objects to the existing set of items inside the array of items
        //  --> Notify the adapter of the new items made with `notifyItemRangeInserted()`
    }


    /**
     * Set view skeleton of activity screen:
     * a general scrollview with a linear layout as single child
     */
    public void setScrollView() {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        scroll.setLayoutParams(layoutParams);

        LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setGravity(Gravity.BOTTOM);
        linearLayout.setLayoutParams(linearParams);

        scroll.addView(linearLayout);
    }


    /**
     * Create a TextView displaying the LiteListing title and inserts it into the ScrollView
     * @param l The LiteListing whose data need to be displayed
     */
    public void addListingView(LiteListing l) {
        // create a horizontal linear layout that will display title and price on one line
        LinearLayout lineLinearLayout = new LinearLayout(this);
        LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        lineLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
        lineLinearLayout.setLayoutParams(linearParams);

        // create TextView for listing title
        int boxWidth = (int)(getResources().getDimension(R.dimen.title_box_length));
        int boxHeight = TableRow.LayoutParams.WRAP_CONTENT;
        int boxWeight = 1;
        float textSize = getResources().getDimension(R.dimen.overview_text_size);
        TextView textView = createView(boxWidth, boxHeight, boxWeight, textSize, true, true, l.getTitle());

        // create TextView for listing price
        boxWidth = (int)(getResources().getDimension(R.dimen.price_box_length));
        TextView priceView = createView(boxWidth, boxHeight, boxWeight, textSize, true, false, l.getPrice());

        // add title and price to horizontal layout
        lineLinearLayout.addView(textView);
        lineLinearLayout.addView(priceView);

        // add horizontal layout to vertical layout
        linearLayout.addView(lineLinearLayout);

        // add entry in <ViewID, ListingID> map
        viewIDtoListingIDMap.put(textView.getId(), l.getListingID());
    }

    /**
     * Helper method to create a TextView
     * @param boxWidth width of TextView
     * @param boxHeight height of TextView
     * @param boxWeight weight of TextView
     * @param textSize size of text
     * @param ellipsize boolean indicating if text must be truncated
     * @param clickable boolean indicating if the TextView is clickable
     * @param content string for text content of TextView
     * @return the newly created TextView
     */
    public TextView createView(int boxWidth, int boxHeight, int boxWeight, float textSize, boolean ellipsize, boolean clickable, String content) {
        TextView textView = new TextView(this);
        textView.setId(View.generateViewId());
        TableRow.LayoutParams boxParams = new TableRow.LayoutParams(boxWidth, boxHeight, boxWeight);
        textView.setLayoutParams(boxParams);

        textView.setText(content);
        textView.setTextSize(textSize);
        textView.setMaxLines(1);

        if(ellipsize) textView.setEllipsize(TextUtils.TruncateAt.END);

        // set TextView as clickable and call onClick method when clicked
        if(clickable) {
            textView.setClickable(true);
            textView.setOnClickListener(titleClickListener);
        }
        return textView;
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
                        addListingView(result);     // add LiteListing fields to graphical TextView
                    }
                };
                int size = IDList.size();
                for(int i = 0; i < size; i++) {
                    fetchLiteListing(IDList.get(i), callbackLiteListing);
                }
            }
        };
        fetchLiteListingList(callbackLiteListingList);
    }
}
