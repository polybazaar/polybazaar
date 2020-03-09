package ch.epfl.polybazaar;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import ch.epfl.polybazaar.database.callback.ListingCallback;
import ch.epfl.polybazaar.listing.Listing;

import static ch.epfl.polybazaar.listing.ListingDatabase.fetchListing;

public class SaleDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale_details);

        final TextView userEmailTextView = findViewById(R.id.userEmail);
        userEmailTextView.setVisibility(View.INVISIBLE);


        Bundle bundle = getIntent().getExtras();
        if(bundle == null)
            bundle = new Bundle();

        //get the uId of the object
        String listingID = bundle.getString("listingID", "-1");
        if(listingID.equals("-1")) {
            throw new IllegalArgumentException();
        }

        ListingCallback callbackListing = new ListingCallback() {
            @Override
            public void onCallback(Listing result) {
                //set image
                //ImageView image = findViewById(R.id.imageView2);
                //image.setImageResource(result.getImage());

                //Set the title
                TextView title_txt = (TextView)findViewById(R.id.title);
                title_txt.setText(result.getTitle());

                //Set the description
                TextView description_txt = (TextView)findViewById(R.id.description);
                description_txt.setText(result.getDescription());

                //Set the price
                TextView price_txt = (TextView)findViewById(R.id.price);
                price_txt.setTextSize(20);
                price_txt.setText(result.getPrice());

                //Set email
                userEmailTextView.setText(result.getUserEmail());
                userEmailTextView.setVisibility(View.INVISIBLE);
            }
        };
        fetchListing(listingID, callbackListing);


        //Get seller action
        Button get_seller = findViewById(R.id.contact_sel);
        get_seller.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                findViewById(R.id.contact_sel).setVisibility(View.INVISIBLE);
                userEmailTextView.setVisibility(View.VISIBLE);
            }
        });

    }
}
