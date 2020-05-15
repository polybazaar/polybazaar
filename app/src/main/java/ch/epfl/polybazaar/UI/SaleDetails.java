package ch.epfl.polybazaar.UI;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.polybazaar.R;
import ch.epfl.polybazaar.chat.ChatActivity;
import ch.epfl.polybazaar.filestorage.ImageTransaction;
import ch.epfl.polybazaar.listing.Listing;
import ch.epfl.polybazaar.listingImage.ListingImage;
import ch.epfl.polybazaar.login.AuthenticatorFactory;
import ch.epfl.polybazaar.map.MapsActivity;
import ch.epfl.polybazaar.saledetails.ImageManager;
import ch.epfl.polybazaar.saledetails.ListingManager;
import ch.epfl.polybazaar.utilities.ImageUtilities;

import static ch.epfl.polybazaar.UI.SubmitOffer.LISTING;
import static ch.epfl.polybazaar.UI.SubmitOffer.sendOffer;
import static ch.epfl.polybazaar.map.MapsActivity.GIVE_LAT_LNG;
import static ch.epfl.polybazaar.map.MapsActivity.LAT;
import static ch.epfl.polybazaar.map.MapsActivity.LNG;
import static ch.epfl.polybazaar.map.MapsActivity.NOLAT;
import static ch.epfl.polybazaar.map.MapsActivity.NOLNG;

public class SaleDetails extends AppCompatActivity {

    ImageManager imageManager;
    ListingManager listingManager;

    private Listing listing;
    private String listingID;
    //private List<Bitmap> listStringImage;
    private List<String> listImageID;

    private double mpLat = NOLAT;
    private double mpLng = NOLNG;
    private int viewIncrement = 0;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale_details);

        imageManager = new ImageManager(this);
        listingManager = new ListingManager(this);
        //listStringImage = new ArrayList<>();
        listImageID = new ArrayList<>();

        findViewById(R.id.ratingBar).setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                listingManager.favorite(listing);
            }
            return true;
        });

        //listStringImage = new ArrayList<>();
        listImageID = new ArrayList<>();

        Glide.with(this).load(R.drawable.loading).into((ImageView)findViewById(R.id.loadingImage));


        retrieveListingFromListingID();
    }


    /**
     * ==============================================================
     * Database fetching :
     * ==============================================================
     */

    private void retrieveListingFromListingID() {
        Bundle bundle = getIntent().getExtras();
        if(bundle == null)
            bundle = new Bundle();

        //get the uId of the object
        String listingID = bundle.getString("listingID", "-1");
        if(listingID.equals("-1")) {
            listingManager.fillWithListing(null);
            return;
        }

        Listing.fetch(listingID).addOnSuccessListener(result -> {
            listing = result;
            this.listingID = listingID;
            retrieveImages();
            listingManager.fillWithListing(listing);
        });
    }

    private void retrieveImages() {
        if (listing.getImagesRefs() != null && listing.getImagesRefs().size() > 0) {
            listing.fetchImages(SaleDetails.this).addOnSuccessListener(bitmaps -> {
                imageManager.drawImages(bitmaps);
            });
        }
    }

    /**
     * ==============================================================
     * Button Actions :
     * ===============================================================
     */

    public void contactSeller(View v) {
        if (AuthenticatorFactory.getDependency().getCurrentUser() == null) {
            Intent notSignedIn = new Intent(getApplicationContext(), NotSignedIn.class);
            startActivity(notSignedIn);
        } else {
            Intent intent = new Intent(SaleDetails.this, ChatActivity.class);
            intent.putExtra(ChatActivity.BUNDLE_LISTING_ID, listingID);
            intent.putExtra(ChatActivity.BUNDLE_RECEIVER_EMAIL, listing.getUserEmail());
            startActivity(intent);
        }
    }

    public void makeOffer(View v) {
        Intent makeOfferIntent = new Intent(this.getApplicationContext(), SubmitOffer.class);
        makeOfferIntent.putExtra(LISTING, listing);
        startActivity(makeOfferIntent);
    }

    public void buyNow(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(SaleDetails.this);
        builder.setTitle(R.string.buy_now)
                .setMessage("Do you want to submit an offer at " + listing.getPrice() + " " +
                        getResources().getString(R.string.currency) + "?")
                .setPositiveButton(R.string.yes, (dialog, id) -> {
                    sendOffer(Double.parseDouble(listing.getPrice()), listing, SaleDetails.this, getApplicationContext());
                    dialog.dismiss();
                })
                .setNegativeButton(R.string.no, (dialog, id) -> dialog.cancel());
        builder.create().show();
    }

    public void viewMP(View v) {
        Intent viewMPIntent = new Intent(this, MapsActivity.class);
        Bundle extras = new Bundle();
        extras.putBoolean(GIVE_LAT_LNG, true);
        extras.putDouble(LAT, mpLat);
        extras.putDouble(LNG, mpLng);
        viewMPIntent.putExtras(extras);
        startActivity(viewMPIntent);
    }

    public void deleteListing(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(SaleDetails.this);
        builder.setTitle("Delete this listing")
                .setMessage("You are about to delete this listing. Are you sure you want to continue?")
                .setPositiveButton(R.string.yes, (dialog, id) -> listingManager.deleteCurrentListing(listingID, listImageID))
                .setNegativeButton(R.string.no, (dialog, id) -> dialog.cancel());
        builder.create().show();
    }

    public void editListing(View v) {
        Intent intent = new Intent(SaleDetails.this, FillListing.class);
        intent.putExtra("listingID", listingID);
        intent.putExtra("listing", listing);
        startActivity(intent);
    }

    /**
     * ==============================================================
     * Meeting Point Management :
     * ==============================================================
     */

    public void setMP() {
        mpLat = listing.getLatitude();
        mpLng = listing.getLongitude();
        if (mpLat != NOLAT && mpLng != NOLNG) {
            findViewById(R.id.viewMP).setVisibility(View.VISIBLE);
        }
    }

    /**
     * ==============================================================
     * Views Count Management :
     * ==============================================================
     */

    @SuppressLint("HardwareIds")
    public void updateViews() {
        //Updates the number of listing's views once per phone
        String android_id = Settings.Secure.getString(getApplication().getContentResolver(), Settings.Secure.ANDROID_ID);
        String haveSeenUsers = listing.getHaveSeenUsers();
        if(!haveSeenUsers.contains(android_id)){
            Listing.updateField("views", this.listingID, listing.getViews()+1);
            Listing.updateField("haveSeenUsers", this.listingID, haveSeenUsers + android_id);
            viewIncrement = 1;
        }
    }

    @SuppressLint("SetTextI18n")
    public void setupNbViews(Listing listing) {
        findViewById(R.id.viewsLabel).setVisibility(View.VISIBLE);
        findViewById(R.id.nbViews).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.nbViews)).setText(Long.toString(listing.getViews()+viewIncrement));
    }

    /**
     * ==============================================================
     * For Testing Only :
     * ==============================================================
     */

    public void applyFillWithListing(Listing listing) {
        listingManager.fillWithListing(listing);
    }

    public void applyFavorite(Listing listing) {
        listingManager.favorite(listing);
    }

}
