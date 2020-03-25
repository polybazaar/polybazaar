package ch.epfl.polybazaar;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.util.List;

import ch.epfl.polybazaar.UI.SalesOverview;
import ch.epfl.polybazaar.database.callback.ListingCallback;
import ch.epfl.polybazaar.database.callback.StringListCallback;
import ch.epfl.polybazaar.database.callback.SuccessCallback;
import ch.epfl.polybazaar.listing.Listing;

import static ch.epfl.polybazaar.Utilities.convertStringToBitmap;
import static ch.epfl.polybazaar.listing.ListingDatabase.deleteListing;
import static ch.epfl.polybazaar.listing.ListingDatabase.fetchListing;
import static ch.epfl.polybazaar.litelisting.LiteListingDatabase.deleteLiteListing;
import static ch.epfl.polybazaar.litelisting.LiteListingDatabase.fetchLiteListingList;
import static ch.epfl.polybazaar.litelisting.LiteListingDatabase.queryLiteListingStringEquality;

public class SaleDetails extends AppCompatActivity {
    private Button editButton;
    private Button deleteButton;
    private AlertDialog deleteDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale_details);

        final ImageView imageLoading = findViewById(R.id.loadingImage);
        Glide.with(this).load(R.drawable.loading).into(imageLoading);

        retrieveListingFromListingID();
        getSellerInformation();
    }

    void getSellerInformation() {
        runOnUiThread(() -> {
            Button get_seller = findViewById(R.id.contactSel);
            get_seller.setOnClickListener(view -> {
                //TODO check that user is connected
                findViewById(R.id.contactSel).setVisibility(View.INVISIBLE);
                findViewById(R.id.userEmail).setVisibility(View.VISIBLE);
            });

        });
    }

    void retrieveListingFromListingID() {
        Bundle bundle = getIntent().getExtras();
        if(bundle == null)
            bundle = new Bundle();

        //get the uId of the object
        String listingID = bundle.getString("listingID", "-1");
        if(listingID.equals("-1")) {
            fillWithListing(null);
            return;
        }

        ListingCallback callbackListing = result -> {
            fillWithListing(result);
            createEditAndDeleteActions(listingID);
        };
        fetchListing(listingID, callbackListing);
    }

    void fillWithListing(final Listing listing) {
        if(listing == null) {
            Toast toast = Toast.makeText(getApplicationContext(),"Object not found.",Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
            toast.show();
            Intent intent = new Intent(SaleDetails.this, SalesOverview.class);
            startActivity(intent);
        } else {
            runOnUiThread(() -> {
                final ImageView imageLoading = findViewById(R.id.loadingImage);
                //Glide.with(imageLoading).clear(imageLoading);
                imageLoading.setVisibility(View.INVISIBLE);

                //set image
                ImageView image = findViewById(R.id.saleImage);
                image.setVisibility(View.VISIBLE);
                Bitmap bitmapImage = convertStringToBitmap(listing.getStringImage());
                if (bitmapImage != null) {
                    image.setImageBitmap(bitmapImage);
                } else {
                    //TODO image.set.. no picture
                }

                //Set the title
                TextView title_txt = findViewById(R.id.title);
                title_txt.setVisibility(View.VISIBLE);
                title_txt.setText(listing.getTitle());

                //Set the category
                TextView category_txt = findViewById(R.id.category);
                category_txt.setVisibility(View.VISIBLE);
                category_txt.setText("Category: "+listing.getCategory());

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

    private void createEditAndDeleteActions(String listingID) {
        editButton = findViewById(R.id.editButton);
        deleteButton = findViewById(R.id.deleteButton);

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SaleDetails.this);
                builder.setTitle("Delete this listing")
                        .setMessage("You are about to delete this listing. Are you sure you want to continue?")
                        .setPositiveButton("Yes", (dialog, id) -> deleteCurrentListing(listingID))
                        .setNegativeButton("No", (dialog, id) -> dialog.cancel());
                deleteDialog = builder.create();
                deleteDialog.show();
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
            }
        });
    }

    private void deleteCurrentListing(String listingID) {
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
