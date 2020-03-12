package ch.epfl.polybazaar;

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
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import ch.epfl.polybazaar.database.callback.LiteListingCallback;
import ch.epfl.polybazaar.database.callback.LiteListingListCallback;
import ch.epfl.polybazaar.litelisting.LiteListing;

import static ch.epfl.polybazaar.litelisting.LiteListingDatabase.fetchLiteListing;
import static ch.epfl.polybazaar.litelisting.LiteListingDatabase.fetchLiteListingList;

public class SalesOverview extends AppCompatActivity {

    // TODO: add tests for error cases (ex empty list on database)

    private List<String> IDList;
    private List<LiteListing> liteListingList;
    TreeMap<Integer, String> viewIDtoListingIDMap;
    ScrollView scroll;
    LinearLayout linearLayout;

    // Create an anonymous implementation of OnClickListener
    private View.OnClickListener titleClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            int viewID = v.getId();
            String listingID = viewIDtoListingIDMap.get(viewID);
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

        setScrollView();

        // attach scrollView to ConstraintLayout
        ConstraintLayout constraintLayout = findViewById(R.id.rootContainer);
        if (constraintLayout != null) {
            constraintLayout.addView(scroll);
        }

        // create graphical overview of LiteListings
        createLiteListingOverview();
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
