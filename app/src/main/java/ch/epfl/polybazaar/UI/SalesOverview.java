package ch.epfl.polybazaar.UI;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.polybazaar.R;
import ch.epfl.polybazaar.SaleDetails;
import ch.epfl.polybazaar.database.callback.LiteListingCallback;
import ch.epfl.polybazaar.litelisting.LiteListing;
import ch.epfl.polybazaar.login.AppUser;
import ch.epfl.polybazaar.login.Authenticator;
import ch.epfl.polybazaar.login.AuthenticatorFactory;

import static ch.epfl.polybazaar.litelisting.LiteListingDatabase.fetchLiteListing;

public class SalesOverview extends AppCompatActivity {

    // TODO: add tests for error cases (ex empty list on database)

    private List<String> IDList;
    private List<LiteListing> liteListingList;
    private LiteListingAdapter adapter;
    private static final int EXTRALOAD = 20;
    private int positionInIDList = 0;
    private Authenticator auth;
    private AppUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_overview2);

        IDList = new ArrayList<>();
        liteListingList = new ArrayList<>();

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

    @Override
    public void onStart() {
        super.onStart();

        // get user info
        auth = AuthenticatorFactory.getDependency();
        user = auth.getCurrentUser();

        // prepare top menu
        TextView favorites = findViewById(R.id.favoritesOverview);
        favorites.setOnClickListener(v -> {
            displayFavorites(user);
        });

        // Initial load
        loadLiteListingOverview();
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

    /**
     * Displays the favorite listings of a user if he is logged in or an error message if the user
     * is not logged in or if the favorite list is empty
     * @param user the logged user (can be null)
     */
    public void displayFavorites(AppUser user) {
        if (user == null) {
            Toast toast = Toast.makeText(SalesOverview.this, R.string.sign_in_required, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP, 0, 0);
            toast.show();
        }

        user.getUserData().addOnSuccessListener(authUser -> {
            List<String> favoritesIds = authUser.getFavorites();

            if(favoritesIds == null || favoritesIds.isEmpty()) {
                Toast toast = Toast.makeText(SalesOverview.this, R.string.no_favorites, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 0, 0);
                toast.show();
            }


        });

    }

}
