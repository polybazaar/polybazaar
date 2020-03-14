package ch.epfl.polybazaar;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import ch.epfl.polybazaar.database.callback.ListingCallback;
import ch.epfl.polybazaar.listing.Listing;

import static ch.epfl.polybazaar.listing.ListingDatabase.fetchListing;

public class SaleDetails extends AppCompatActivity {

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
            get_seller.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    //TODO check that user is connected
                    findViewById(R.id.contactSel).setVisibility(View.INVISIBLE);
                    findViewById(R.id.userEmail).setVisibility(View.VISIBLE);
                }
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

        ListingCallback callbackListing = new ListingCallback() {
            @Override
            public void onCallback(Listing result) {
                fillWithListing(result);
            }
        };
        fetchListing(listingID, callbackListing);
    }

    void fillWithListing(final Listing listing) {
        if(listing == null) {
            Toast toast = Toast.makeText(getApplicationContext(),"Object not found.",Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
            toast.show();
            return;
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final ImageView imageLoading = findViewById(R.id.loadingImage);
                //Glide.with(imageLoading).clear(imageLoading);
                imageLoading.setVisibility(View.INVISIBLE);

                //set image
                ImageView image = findViewById(R.id.saleImage);
                image.setVisibility(View.VISIBLE);
                Bitmap bitmapImage = listing.getImage();
                if(bitmapImage != null) {
                    image.setImageBitmap(listing.getImage());
                } else {
                    //TODO image.set.. no picture
                }

                //Set the title
                TextView title_txt = findViewById(R.id.title);
                title_txt.setVisibility(View.VISIBLE);
                title_txt.setText(listing.getTitle());

                //Set the description
                TextView description_txt = findViewById(R.id.description);
                description_txt.setVisibility(View.VISIBLE);
                description_txt.setText(listing.getDescription());

                //Set the price
                TextView price_txt = findViewById(R.id.price);
                price_txt.setVisibility(View.VISIBLE);
                price_txt.setTextSize(20);
                price_txt.setText(listing.getPrice());

                //Set email
                TextView userEmailTextView = findViewById(R.id.userEmail);
                userEmailTextView.setText(listing.getUserEmail());
                userEmailTextView.setVisibility(View.INVISIBLE);

            }
        });
    }
}
