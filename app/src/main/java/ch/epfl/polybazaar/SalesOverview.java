package ch.epfl.polybazaar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import ch.epfl.polybazaar.database.callback.LiteListingCallback;
import ch.epfl.polybazaar.database.callback.LiteListingListCallback;
import ch.epfl.polybazaar.litelisting.LiteListing;

import static ch.epfl.polybazaar.litelisting.LiteListingDatabase.fetchLiteListingList;

public class SalesOverview extends AppCompatActivity {

    private List<String> IDList = new ArrayList<String>();
    private Map<String, LiteListing> LiteListingMap = new HashMap<>();
    private final AtomicInteger counter = new AtomicInteger(0);
    private String viewTitle = "title";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_overview2);

        createScrollView();
        createLiteListingIDList();
        createLiteListingMap();

    }

    /**
     * create a list of LiteListingIDs
     */
    public void createLiteListingIDList() {
        LiteListingListCallback callbackLiteListingList = new LiteListingListCallback() {

            @Override
            public void onCallback(List<String> result) {
                for(String r : result) {
                    IDList.add(r);      // create deep copy of ID list
                }
            }
        };
        fetchLiteListingList(callbackLiteListingList);
    }


    /**
     * create a map of <UI View ID, LiteListings>
     */
    public void createLiteListingMap() {
        LiteListingCallback callbackLiteListing = new LiteListingCallback() {
            @Override
            public void onCallback(LiteListing result) {
                int i = 0;
                while(true) {
                    i = counter.get();
                    if(counter.compareAndSet(i, i+1)) break;
                }
                LiteListingMap.put(viewTitle + i, result);    // TextView numbering will start at 1
                // viewTitle += i;
                // TextView viewTitle = (TextView)findViewById(R.id.viewTitle);
                // see how we can create textviews in java
            }
        };
        for(int i = 0; i < 6; i++) {
            // fetchLiteListing(IDList.get(i), callbackLiteListing);
        }
    }


    /**
     * create view skeleton of activity screen:
     * a general scrollview with a linear layout as single child
     */
    public void createScrollView() {
        ScrollView scroll = new ScrollView(getApplicationContext());
        scroll.setLayoutParams(new ScrollView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        LinearLayout linearLayout = new LinearLayout(getApplicationContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setGravity(Gravity.BOTTOM);

        scroll.addView(linearLayout);
    }


    public void onClick(View v) {
        TextView itemName = findViewById(v.getId());
        Intent intent = new Intent(SalesOverview.this, SaleDetails.class);
        intent.putExtra("title", itemName.getText().toString());
        intent.putExtra("description", "Never used");
        intent.putExtra("price", "18 CHF");
        startActivity(intent);
    }


}
