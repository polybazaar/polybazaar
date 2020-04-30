package ch.epfl.polybazaar.UI;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.polybazaar.R;
import ch.epfl.polybazaar.chat.ChatActivity;
import ch.epfl.polybazaar.listing.Listing;
import ch.epfl.polybazaar.listingImage.ListingImage;
import ch.epfl.polybazaar.login.Account;
import ch.epfl.polybazaar.login.Authenticator;
import ch.epfl.polybazaar.login.AuthenticatorFactory;
import ch.epfl.polybazaar.map.MapsActivity;
import ch.epfl.polybazaar.user.User;

import static ch.epfl.polybazaar.map.MapsActivity.GIVE_LAT_LNG;
import static ch.epfl.polybazaar.map.MapsActivity.LAT;
import static ch.epfl.polybazaar.map.MapsActivity.LNG;
import static ch.epfl.polybazaar.map.MapsActivity.NOLAT;
import static ch.epfl.polybazaar.map.MapsActivity.NOLNG;
import static ch.epfl.polybazaar.utilities.ImageUtilities.convertStringToBitmap;

public class SaleDetails extends AppCompatActivity {
    private Button editButton;
    private Button deleteButton;
    private AlertDialog deleteDialog;
    //used for favorite
    private RatingBar ratingBar;
    private ImageView imageLoading;
    private Button contactSelButton;
    private Button viewMP;

    private String listingID;

    private double mpLat = NOLAT;
    private double mpLng = NOLNG;

    private ViewPager2 viewPager2;
    private List<String> listStringImage;
    private List<String> listImageID;

    private Listing listing;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale_details);

        editButton = findViewById(R.id.editButton);
        deleteButton = findViewById(R.id.deleteButton);
        imageLoading = findViewById(R.id.loadingImage);
        contactSelButton = findViewById(R.id.contactSel);
        viewPager2 = findViewById(R.id.viewPagerImageSlider);
        viewMP = findViewById(R.id.viewMP);
        ratingBar = findViewById(R.id.ratingBar);
        ratingBar.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                favorite();
            }
            return true;
        });
        BottomNavigationView bottomNavigationView = findViewById(R.id.activity_main_bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.action_add_item);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> bottomBar.updateActivity(item.getItemId(), SaleDetails.this));

        listStringImage = new ArrayList<>();
        listImageID = new ArrayList<>();

        Glide.with(this).load(R.drawable.loading).into(imageLoading);

        retrieveListingFromListingID();
        setupViewMP();
        setupSellerContact();
    }

    private void setupSellerContact() {
        runOnUiThread(() -> {
            contactSelButton.setOnClickListener(view -> {
                Intent intent = new Intent(SaleDetails.this, ChatActivity.class);
                intent.putExtra(ChatActivity.bundleLisitngId, listingID);
                intent.putExtra(ChatActivity.bundleReceiverEmail, listing.getUserEmail());
                startActivity(intent);
            });
        });

    }

    private void setupViewMP() {
        viewMP.setOnClickListener(view -> {
            Intent viewMPIntent = new Intent(this, MapsActivity.class);
            Bundle extras = new Bundle();
            extras.putBoolean(GIVE_LAT_LNG, true);
            extras.putDouble(LAT, mpLat);
            extras.putDouble(LNG, mpLng);
            viewMPIntent.putExtras(extras);
            startActivity(viewMPIntent);
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

        Listing.fetch(listingID).addOnSuccessListener(result -> {
            listing = result;
            this.listingID = listingID;
            retrieveImages(listingID);
            fillWithListing(result);
        });
    }


    /**
     * recursive function to retrieve all images
     * @param listingID ID of the image
     */
    private void retrieveImages(String listingID) {
        listImageID.add(listingID);
        ListingImage.fetch(listingID).addOnSuccessListener(result -> {
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
            List<SliderItem> sliderItems = new ArrayList<>();
            if (!listStringImage.isEmpty()) {
                viewPager2.setVisibility(View.VISIBLE);
                imageLoading.setVisibility(View.GONE);
                findViewById(R.id.pageNumber).setVisibility(View.VISIBLE);
                for (String strImg : listStringImage) {
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
            } else {
                findViewById(R.id.imageDisplay).setVisibility(View.GONE);
            }
        });
    }

    /**
     * Fill the UI with the Listing given in parameter
     * @param listing
     */
        public void fillWithListing(final Listing listing) {
        if(listing == null) {
            Toast toast = Toast.makeText(getApplicationContext(),R.string.object_not_found,Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
            toast.show();
            Intent intent = new Intent(SaleDetails.this, SalesOverview.class);
            startActivity(intent);
        } else {

            //Set Meeting Point
            mpLat = listing.getLatitude();
            mpLng = listing.getLongitude();
            if (mpLat != NOLAT && mpLng != NOLNG) {
                viewMP.setVisibility(View.VISIBLE);
            }

            runOnUiThread(() -> {
                //Set the title
                TextView title_txt = findViewById(R.id.title);
                title_txt.setText(listing.getTitle());

                //Set the description
                LinearLayout description = findViewById(R.id.descriptionLayout);
                TextView description_txt = findViewById(R.id.description);
                if (!(listing.getDescription().trim().length() == 0
                        || listing.getDescription().isEmpty()
                        || listing.getDescription() == null )) {
                    description.setVisibility(View.VISIBLE);
                    description_txt.setText(listing.getDescription());
                }

                //Set the price
                TextView price_txt = findViewById(R.id.price);
                price_txt.setText(String.format("CHF %s", listing.getPrice()));

                // Set seller information
                ImageView sellerPicture  = findViewById(R.id.sellerProfilePicture);
                TextView sellerNickname  = findViewById(R.id.sellerNickname);
                User.fetch(listing.getUserEmail()).addOnSuccessListener(result -> {
                    if (!result.getProfilePicture().equals(User.NO_PROFILE_PICTURE)) {
                        sellerPicture.setImageBitmap(convertStringToBitmap(result.getProfilePicture()));
                    }
                    if (result.getNickName() != null) {
                        sellerNickname.setText(result.getNickName());
                    }
                });

                // Enable logged in features
                Account authUser = AuthenticatorFactory.getDependency().getCurrentUser();
                if(authUser != null){
                    ratingBar.setVisibility(View.VISIBLE);
                    String sellerEmail = listing.getUserEmail();
                    authUser.getUserData().addOnSuccessListener(user -> {
                        List<String> favorites = user.getFavorites();
                        if (favorites.contains(listing.getId())){
                            ratingBar.setRating(1);
                        }
                    });
                    if(authUser.getEmail().equals(sellerEmail)){
                        createEditAndDeleteActions(listing, listingID);
                        contactSelButton.setVisibility(View.GONE);
                    } else{
                        contactSelButton.setVisibility(View.VISIBLE);
                        findViewById(R.id.editButtonsLayout).setVisibility(View.GONE);
                    }
                } else {
                    ratingBar.setVisibility(View.INVISIBLE);
                    ratingBar.setClickable(false);
                }
            });
        }
    }


    private void createEditAndDeleteActions(Listing listing, String listingID) {
        findViewById(R.id.editButtonsLayout).setVisibility(View.VISIBLE);

        deleteButton.setOnClickListener(v -> { 
            //TODO: This could be refactored to use utility functions from package widget
            AlertDialog.Builder builder = new AlertDialog.Builder(SaleDetails.this);
            builder.setTitle("Delete this listing")
                    .setMessage("You are about to delete this listing. Are you sure you want to continue?")
                    .setPositiveButton("Yes", (dialog, id) -> deleteCurrentListing(listingID))
                    .setNegativeButton("No", (dialog, id) -> dialog.cancel());
            deleteDialog = builder.create();
            deleteDialog.show();
        });

        editButton.setOnClickListener(v -> {
            Intent intent = new Intent(SaleDetails.this, FillListing.class);
            intent.putExtra("listingID", listingID);
            intent.putExtra("listing", listing);
            startActivity(intent);
        });
    }

    private void deleteCurrentListing(String listingID) {
        Listing.deleteWithLiteVersion(listingID).addOnSuccessListener(result -> {
                Toast toast = Toast.makeText(getApplicationContext(),R.string.deleted_listing, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
                toast.show();

                Authenticator fbAuth = AuthenticatorFactory.getDependency();
                Account authAccount = fbAuth.getCurrentUser();

                authAccount.getUserData().addOnSuccessListener(user -> {
                    user.deleteOwnListing(listingID);
                    user.save();
                });

                Intent SalesOverviewIntent = new Intent(SaleDetails.this, SalesOverview.class);
                startActivity(SalesOverviewIntent);
        });
        //delete all images
        for(String id: listImageID) {
            ListingImage.delete(id);
        }
    }

    /**
     * Adds the listing to favorites, or removes it from the user's favorites if it is already
     * a favorite
     */
    public void favorite() {
        Account authUser = AuthenticatorFactory.getDependency().getCurrentUser();
        //if it's 0 set 1 and vice versa
        ratingBar.setRating((ratingBar.getRating() + 1) % 2);
        //must be connected
        if (authUser != null) {
            authUser.getUserData().addOnSuccessListener(user -> {
                if (ratingBar.getRating() == 1) {
                    user.addFavorite(listing);
                } else {
                    user.removeFavorite(listing);
                }
                user.save();
            });
        }
    }
}
