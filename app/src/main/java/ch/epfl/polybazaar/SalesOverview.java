package ch.epfl.polybazaar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
import ch.epfl.polybazaar.litelisting.LiteListingDatabase;

public class SalesOverview extends AppCompatActivity {

    private LiteListingDatabase lldb = new LiteListingDatabase("liteListings");
    private List<String> IDList = new ArrayList<String>();
    private Map<String, LiteListing> LiteListingMap = new HashMap<>();
    private final AtomicInteger counter = new AtomicInteger(0);
    private final String viewTitle = "title";
    private final int limit = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_overview);

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
                    IDList.add(r);
                }
            }
        };
        lldb.fetchLiteListingList(callbackLiteListingList);
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
                LiteListingMap.put(viewTitle + Integer.toString(i), result);    // View numbering will start at 1
                // TextView title = findViewById(R.id.);
            }
        };
        for(int i = 0; i < 6; i++) {
            lldb.fetchLiteListing(IDList.get(i), callbackLiteListing);
        }
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
