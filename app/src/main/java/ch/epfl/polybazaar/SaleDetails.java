package ch.epfl.polybazaar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.polybazaar.UI.SalesOverview;
import ch.epfl.polybazaar.UI.SliderAdapter;
import ch.epfl.polybazaar.UI.SliderItem;
import ch.epfl.polybazaar.database.callback.ListingCallback;
import ch.epfl.polybazaar.database.callback.ListingImageCallback;
import ch.epfl.polybazaar.listing.Listing;
import ch.epfl.polybazaar.listingImage.ListingImage;

import static ch.epfl.polybazaar.Utilities.convertStringToBitmap;
import static ch.epfl.polybazaar.listing.ListingDatabase.fetchListing;
import static ch.epfl.polybazaar.listingImage.ListingImageDatabase.fetchListingImage;

public class SaleDetails extends AppCompatActivity {

    private ViewPager2 viewPager2;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale_details);

        viewPager2 = findViewById(R.id.viewPagerImageSlider);

        List<SliderItem> sliderItems = new ArrayList<>();
        sliderItems.add(new SliderItem(R.drawable.ue_roll));
        sliderItems.add(new SliderItem(R.drawable.ue_roll1));
        sliderItems.add(new SliderItem(R.drawable.ue_roll2));
        sliderItems.add(new SliderItem(R.drawable.ue_roll3));
        sliderItems.add(new SliderItem(R.drawable.ue_roll4));

        viewPager2.setAdapter(new SliderAdapter(sliderItems, viewPager2));

        viewPager2.setClipToPadding(false);
        viewPager2.setClipChildren(false);
        viewPager2.setOffscreenPageLimit(3);
        viewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer((page, position) -> {
            float pageMarginPx = getResources().getDimensionPixelOffset(R.dimen.pageMargin);
            float offsetPx = getResources().getDimensionPixelOffset(R.dimen.offset);
            float offset = position * -(2 * offsetPx + pageMarginPx);
            if (ViewCompat.getLayoutDirection(viewPager2) == ViewCompat.LAYOUT_DIRECTION_RTL) {
                page.setTranslationX(-offset);
            } else {
                page.setTranslationX(offset);
            }
        });
        compositePageTransformer.addTransformer((page, position) -> {
            float r = 1 - Math.abs(position);
            page.setScaleY(0.85f + r * 0.15f);
        });
        viewPager2.setPageTransformer(compositePageTransformer);



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

        ListingCallback callbackListing = result -> fillWithListing(result);
        fetchListing(listingID, callbackListing);

        ListingImageCallback callbackLisingImage = result -> fillWithListingImage(result);
        fetchListingImage(listingID, callbackLisingImage);
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
                /*ImageView image = findViewById(R.id.saleImage);
                image.setVisibility(View.VISIBLE);
                Bitmap bitmapImage = convertStringToBitmap(listing.getStringImage());
                if (bitmapImage != null) {
                    image.setImageBitmap(bitmapImage);
                } else {
                    //TODO image.set.. no picture
                }*/

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

    private void fillWithListingImage(final ListingImage listingImage) {
        if(listingImage == null)
            return;
        /*runOnUiThread(() -> {
            //set image
            ImageView image = findViewById(R.id.saleImage);
            image.setVisibility(View.VISIBLE);
            Bitmap bitmapImage = convertStringToBitmap(listingImage.getImage());
            if (bitmapImage != null) {
                image.setImageBitmap(bitmapImage);
            }
        });*/
    }
}
