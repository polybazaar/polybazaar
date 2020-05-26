package ch.epfl.polybazaar.filllisting;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.epfl.polybazaar.R;
import ch.epfl.polybazaar.UI.FillListing;
import ch.epfl.polybazaar.UI.SalesOverview;
import ch.epfl.polybazaar.category.Category;
import ch.epfl.polybazaar.filestorage.ImageTransaction;
import ch.epfl.polybazaar.listing.Listing;
import ch.epfl.polybazaar.litelisting.LiteListing;
import ch.epfl.polybazaar.login.Account;
import ch.epfl.polybazaar.login.Authenticator;
import ch.epfl.polybazaar.login.AuthenticatorFactory;

import static ch.epfl.polybazaar.Utilities.getUser;
import static ch.epfl.polybazaar.listing.Listing.*;
import static ch.epfl.polybazaar.litelisting.LiteListing.LISTING_ID;
import static ch.epfl.polybazaar.litelisting.LiteListing.NO_THUMBNAIL;
import static ch.epfl.polybazaar.network.InternetCheckerFactory.isInternetAvailable;

import static ch.epfl.polybazaar.utilities.ImageTaker.QUALITY;
import static ch.epfl.polybazaar.utilities.ImageUtilities.resizeImageThumbnail;
import static java.util.UUID.randomUUID;

public class ListingManager {

    // Desired thumbnail width:
    public static final int THUMBNAIL_SIZE = 500;
    private TextView titleSelector;
    private EditText descriptionSelector;
    private EditText priceSelector;
    private FillListing activity;

    public ListingManager(FillListing activity) {
        this.activity = activity;
        if (activity != null) {
            titleSelector = activity.findViewById(R.id.titleSelector);
            descriptionSelector = activity.findViewById(R.id.descriptionSelector);
            priceSelector = activity.findViewById(R.id.priceSelector);
        }
    }

    private void createAndSendListing(Listing newListing, List<Bitmap> listImage, String thumbnailRef, String listingID) {
        final String newListingID = randomUUID().toString();
        Authenticator fbAuth = AuthenticatorFactory.getDependency();
        if(fbAuth.getCurrentUser() == null) {
            Toast.makeText(activity.getApplicationContext(), R.string.sign_in_required, Toast.LENGTH_SHORT).show();
            return;
        }
        // Send Listing:
        if (listingID != null) {
            newListing.setId(listingID);
        } else {
            newListing.setId(newListingID);
        }
        newListing.save().addOnSuccessListener(result -> {
            Toast toast = Toast.makeText(activity.getApplicationContext(),"Offer successfully sent!",Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
            toast.show();
        });
        //store images
        storeImages(listImage, newListing.getId());
        // SendLiteListing
        LiteListing newLiteListing = new LiteListing(newListing.getId(), newListing.getTitle(), newListing.getPrice(),
                newListing.getCategory());
        if (listingID != null) {
            newLiteListing.setId(listingID);
        } else {
            newLiteListing.setId(newListing.getId());
        }
        newLiteListing.save()
                .addOnSuccessListener(result -> Log.d("FirebaseDataStore", "successfully stored data"))
                .addOnFailureListener(e -> Toast.makeText(activity.getApplicationContext(), "Failed to send listing", Toast.LENGTH_LONG).show());
        Account authAccount = getUser();
        LiteListing.updateField(LiteListing.THUMBNAIL_REF, newLiteListing.getId(), thumbnailRef);
        // update own listings of (logged) user
        if(authAccount != null) {
            authAccount.getUserData().addOnSuccessListener(user -> {
                if (user != null) {
                    user.addOwnListing(newListingID);
                    user.save();
                }
            });
        }
    }

    private void storeImages(List<Bitmap> listImage, String listingID) {
        if(listImage.size() > 0) {
            String nextId;
            List<String> idList = new ArrayList<>();
            for(int i = 0; i <= (listImage.size() - 1); i++) {
                nextId = randomUUID().toString();
                idList.add(nextId);
                ImageTransaction.store(nextId, listImage.get(i), QUALITY, activity.getApplicationContext())
                        .addOnSuccessListener(result -> Log.d("FirebaseDataStore", "successfully stored image"))
                        .addOnFailureListener(e -> Toast.makeText(activity.getApplicationContext(), "Failed to send image", Toast.LENGTH_LONG).show());
            }
            Listing.updateField(IMAGES_REFS, listingID, idList);
        }
    }

    private boolean checkFields(){
        return checkTitle() && checkPrice();
    }

    private boolean checkPrice() {
        boolean ok = !priceSelector.getText().toString().isEmpty();
        if (!ok) {
            priceSelector.setBackground(activity.getResources().getDrawable(R.drawable.boxed_red, activity.getTheme()));
        } else {
            priceSelector.setBackground(activity.getResources().getDrawable(R.drawable.boxed, activity.getTheme()));
        }
        return ok;
    }

    private boolean checkTitle() {
        boolean ok = !titleSelector.getText().toString().isEmpty();
        if (!ok) {
            titleSelector.setBackground(activity.getResources().getDrawable(R.drawable.boxed_red, activity.getTheme()));
        } else {
            titleSelector.setBackground(activity.getResources().getDrawable(R.drawable.boxed, activity.getTheme()));
        }
        return ok;
    }

    public Listing makeListing(Double lat, Double lng, Category cat) {
        String category = cat.toString();
        Authenticator fbAuth = AuthenticatorFactory.getDependency();
        if (fbAuth.getCurrentUser() != null) {
            String userEmail = fbAuth.getCurrentUser().getEmail();
            return new Listing(titleSelector.getText().toString(), descriptionSelector.getText().toString(),
                    priceSelector.getText().toString(), userEmail, "", category, lat, lng);
        }
        return null;
    }

    /**
     * Overloads submit with an option to specify the listing id
     */
    public boolean submit(Category category, List<Bitmap> listImage, Double lat, Double lng, String listingID) {
        String thumbnailRef = NO_THUMBNAIL;
        if(!listImage.isEmpty()) {
            Bitmap thumbnail = resizeImageThumbnail(listImage.get(0));
            thumbnailRef = randomUUID().toString();
            ImageTransaction.store(thumbnailRef, thumbnail, QUALITY, activity.getApplicationContext());
        }
        Context context = activity.getApplicationContext();
        if (!checkFields()) {
            Toast.makeText(context, R.string.incorrect_fields, Toast.LENGTH_SHORT).show();
        }
        else {
            if(isInternetAvailable(context)){
                Listing newListing = makeListing(lat, lng, category);
                if (newListing != null) {
                    createAndSendListing(newListing, listImage, thumbnailRef, listingID);
                    Intent SalesOverviewIntent = new Intent(activity, SalesOverview.class);
                    activity.startActivity(SalesOverviewIntent);
                } else {
                    Toast.makeText(activity.getApplicationContext(), R.string.incorrect_fields, Toast.LENGTH_SHORT).show();
                }
            }else{
                return false;
            }
        }
        return true;
    }

    /**
     * Submits a new listing and assigns a random listing_ID
     * @param category obvious
     * @param listImage obvious
     * @param lat obvious
     * @param lng obvious
     * @return true if successful
     */
    public boolean submit(Category category, List<Bitmap> listImage, Double lat, Double lng) {
        return submit(category, listImage, lat, lng, null);
    }

    public void deleteOldListingAndSubmitNewOne(Category category, List<Bitmap> listImage, Double lat, Double lng) {
        Bundle bundle = activity.getIntent().getExtras();
        if (bundle == null) {
            return;
        }
        String listingID = bundle.getString(FillListing.LISTING_ID);
        Listing.fetch(listingID).addOnSuccessListener(listing -> {
            // Delete images:
            if (listing != null && listing.getImagesRefs() != null) {
                for (String ref : listing.getImagesRefs()) {
                    ImageTransaction.delete(ref);
                }
            }
            LiteListing.fetch(listingID).addOnSuccessListener(liteListing -> {
                if (liteListing != null) {
                    if (liteListing.getThumbnailRef() != null) {
                        ImageTransaction.delete(liteListing.getThumbnailRef());
                    }
                    Listing.deleteWithLiteVersion(listingID).addOnSuccessListener(result -> {
                        submit(category, listImage, lat, lng, listingID);
                    });
                }
            });
        });
        Intent SalesOverviewIntent = new Intent(activity, SalesOverview.class);
        activity.startActivity(SalesOverviewIntent);
    }
}
