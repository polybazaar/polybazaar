package ch.epfl.polybazaar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.polybazaar.UI.SalesOverview;
import ch.epfl.polybazaar.UI.SliderAdapter;
import ch.epfl.polybazaar.UI.SliderItem;
import ch.epfl.polybazaar.database.callback.ListingCallback;
import ch.epfl.polybazaar.database.callback.SuccessCallback;
import ch.epfl.polybazaar.listing.Listing;
import ch.epfl.polybazaar.login.FirebaseAuthenticator;

import static ch.epfl.polybazaar.Utilities.convertStringToBitmap;
import static ch.epfl.polybazaar.listing.ListingDatabase.deleteListing;
import static ch.epfl.polybazaar.listing.ListingDatabase.fetchListing;
import static ch.epfl.polybazaar.litelisting.LiteListingDatabase.deleteLiteListing;
import static ch.epfl.polybazaar.litelisting.LiteListingDatabase.queryLiteListingStringEquality;
import static ch.epfl.polybazaar.listingImage.ListingImageDatabase.fetchListingImage;

public class SaleDetails extends AppCompatActivity {
    private Button editButton;
    private Button deleteButton;
    private AlertDialog deleteDialog;

    private ViewPager2 viewPager2;
    private List<String> listStringImage;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale_details);

        listStringImage = new ArrayList<>();

        final ImageView imageLoading = findViewById(R.id.loadingImage);
        Glide.with(this).load(R.drawable.loading).into(imageLoading);

        retrieveListingFromListingID();
        getSellerInformation();
    }

    private void getSellerInformation() {
        runOnUiThread(() -> {
            Button get_seller = findViewById(R.id.contactSel);
            get_seller.setOnClickListener(view -> {
                //TODO check that user is connected
                findViewById(R.id.contactSel).setVisibility(View.INVISIBLE);
                findViewById(R.id.userEmail).setVisibility(View.VISIBLE);
            });

        });
    }

    private void retrieveListingFromListingID() {
        Bundle bundle = getIntent().getExtras();
        if(bundle == null)
            bundle = new Bundle();

        //get the uId of the object
        String listingID = bundle.getString("listingID", "-1");
        if(listingID.equals("-1")) {
            fillWithListing(null);
            return;
        }


        retrieveImages(listingID);
      ListingCallback callbackListing = result -> {
            fillWithListing(result);
            FirebaseAuthenticator fbAuth = FirebaseAuthenticator.getInstance();
            if(!(fbAuth.getCurrentUser() == null) && fbAuth.getCurrentUser().getEmail().equals(result.getUserEmail())){
                createEditAndDeleteActions(result, listingID);
            }
        };

        fetchListing(listingID, callbackListing);
    }

    /**
     * recursive function to retrieve all images
     * @param listingID
     */
    private void retrieveImages(String listingID) {
        fetchListingImage(listingID, result -> {
            if(result == null) {
                drawImages();
                return;
            }
            listStringImage.add(result.getImage());
            if(result.getRefNextImg().equals("")) {
                //last image, we can draw
                drawImages();
            } else {
                //we continue to retrieve
                retrieveImages(result.getRefNextImg());
            }
        });
    }

    private void drawImages() {
        runOnUiThread (()-> {
            final ImageView imageLoading = findViewById(R.id.loadingImage);
            imageLoading.setVisibility(View.INVISIBLE);
            viewPager2 = findViewById(R.id.viewPagerImageSlider);

            List<SliderItem> sliderItems = new ArrayList<>();
            for(String strImg: listStringImage) {
                sliderItems.add(new SliderItem(convertStringToBitmap(strImg)));
            }

            viewPager2.setAdapter(new SliderAdapter(sliderItems, viewPager2));

            viewPager2.setClipToPadding(false);
            viewPager2.setClipChildren(false);
            viewPager2.setOffscreenPageLimit(3);
            viewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

            CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
            compositePageTransformer.addTransformer((page, position) -> {
                float r = 1 - Math.abs(position);
                page.setScaleY(0.85f + r * 0.15f);
            });
            viewPager2.setPageTransformer(compositePageTransformer);

            viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    super.onPageSelected(position);
                    TextView textPageNumber = findViewById(R.id.pageNumber);
                    textPageNumber.setText(String.format("%s/%s", Integer.toString(viewPager2.getCurrentItem() + 1), Integer.toString(listStringImage.size())));
                    textPageNumber.setGravity(Gravity.CENTER);
                }

            });
        });
    }

    /**
     * Fill the UI with the Listing given in parameter
     * @param listing
     */
    public void fillWithListing(final Listing listing) {
        if(listing == null) {
            Toast toast = Toast.makeText(getApplicationContext(),"Object not found.",Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
            toast.show();
            Intent intent = new Intent(SaleDetails.this, SalesOverview.class);
            startActivity(intent);
        } else {
            runOnUiThread(() -> {
                //Set the title
                TextView title_txt = findViewById(R.id.title);
                title_txt.setVisibility(View.VISIBLE);
                title_txt.setText(listing.getTitle());

                //Set the category
                TextView category_txt = findViewById(R.id.category);
                category_txt.setVisibility(View.VISIBLE);
                category_txt.setText(String.format("Category: %s", listing.getCategory()));

                //Set the description
                TextView description_txt = findViewById(R.id.description);
                description_txt.setVisibility(View.VISIBLE);
                description_txt.setText(listing.getDescription());

                //Set the price
                TextView price_txt = findViewById(R.id.price);
                price_txt.setVisibility(View.VISIBLE);
                price_txt.setTextSize(20);
                price_txt.setText(String.format("CHF %s", listing.getPrice()));

                //Set email
                TextView userEmailTextView = findViewById(R.id.userEmail);
                userEmailTextView.setText(listing.getUserEmail());
                userEmailTextView.setVisibility(View.INVISIBLE);
            });
        }
    }


    private void createEditAndDeleteActions(Listing listing, String listingID) {
        editButton = findViewById(R.id.editButton);
        deleteButton = findViewById(R.id.deleteButton);

        editButton.setVisibility(View.VISIBLE);
        deleteButton.setVisibility(View.VISIBLE);

        editButton.setClickable(true);
        deleteButton.setClickable(true);

        deleteButton.setOnClickListener(v -> { //TODO: This could be refactored to use utility functions from package widget
            AlertDialog.Builder builder = new AlertDialog.Builder(SaleDetails.this);
            builder.setTitle("Delete this listing")
                    .setMessage("You are about to delete this listing. Are you sure you want to continue?")
                    .setPositiveButton("Yes", (dialog, id) -> deleteCurrentListing(listingID))
                    .setNegativeButton("No", (dialog, id) -> dialog.cancel());
            deleteDialog = builder.create();
            deleteDialog.show();
        });

        editButton.setOnClickListener(v -> {
            Intent intent = new Intent(SaleDetails.this, FillListingActivity.class);
            intent.putExtra("listingID", listingID);
            intent.putExtra("listing", listing);
            startActivity(intent);
        });
    }

    public void deleteCurrentListing(String listingID) {
        SuccessCallback deletionSuccessCallback = result -> {
            if(result) {
                Toast toast = Toast.makeText(getApplicationContext(),"Listing successfuly deleted",Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
                toast.show();
                Intent SalesOverviewIntent = new Intent(SaleDetails.this, SalesOverview.class);
                startActivity(SalesOverviewIntent);
            }
        };

        deleteListing(listingID, result -> {});
        queryLiteListingStringEquality("listingID", listingID, result -> deleteLiteListing(result.get(0), deletionSuccessCallback));

    }
}
