package ch.epfl.polybazaar.filllisting;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import ch.epfl.polybazaar.MainActivity;
import ch.epfl.polybazaar.R;
import ch.epfl.polybazaar.UI.SalesOverview;
import ch.epfl.polybazaar.listing.Listing;
import ch.epfl.polybazaar.listingImage.ListingImage;
import ch.epfl.polybazaar.litelisting.LiteListing;
import ch.epfl.polybazaar.login.Account;
import ch.epfl.polybazaar.login.Authenticator;
import ch.epfl.polybazaar.login.AuthenticatorFactory;

import static ch.epfl.polybazaar.Utilities.getUser;
import static ch.epfl.polybazaar.network.InternetCheckerFactory.isInternetAvailable;

import static ch.epfl.polybazaar.utilities.ImageUtilities.resizeStringImageThumbnail;
import static java.util.UUID.randomUUID;

class ListingManager {

    private TextView titleSelector;
    private EditText descriptionSelector;
    private EditText priceSelector;

    private Activity activity;

    ListingManager(Activity activity) {
        this.activity = activity;
        if (activity != null) {
            titleSelector = activity.findViewById(R.id.titleSelector);
            descriptionSelector = activity.findViewById(R.id.descriptionSelector);
            priceSelector = activity.findViewById(R.id.priceSelector);
        }
    }

    void createAndSendListing(Listing newListing, List<String> listStringImage, String stringThumbnail) {
        final String newListingID = randomUUID().toString();
        Authenticator fbAuth = AuthenticatorFactory.getDependency();
        if(fbAuth.getCurrentUser() == null) {
            Toast.makeText(activity.getApplicationContext(), R.string.sign_in_required, Toast.LENGTH_SHORT).show();
            return;
        }
        newListing.setId(newListingID);
        LiteListing newLiteListing = new LiteListing(newListingID, newListing.getTitle(), newListing.getPrice(),
                newListing.getCategory(), stringThumbnail);
        newLiteListing.setId(newListingID);
        newListing.save().addOnSuccessListener(result -> {
            Toast toast = Toast.makeText(activity.getApplicationContext(),"Offer successfully sent!",Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
            toast.show();
        });
        //store images (current has a ref to the next)
        if(listStringImage.size() > 0) {
            String currentId = newListingID;
            String nextId;
            for(int i = 0; i < (listStringImage.size() - 1); i++) {
                nextId = randomUUID().toString();
                ListingImage newListingImage = new ListingImage(listStringImage.get(i), nextId);

                newListingImage.setId(currentId);
                newListingImage.save()
                        .addOnSuccessListener(result -> Log.d("FirebaseDataStore", "successfully stored image"))
                        .addOnFailureListener(e -> Toast.makeText(activity.getApplicationContext(), "Failed to send image", Toast.LENGTH_LONG).show());

                currentId = nextId;
            }
            //store the last without refNextImg
            ListingImage newListingImage = new ListingImage(listStringImage.get(listStringImage.size() - 1), "");
            newListingImage.setId(currentId);
            newListingImage.save();
        }
        newLiteListing.save()
                .addOnSuccessListener(result -> Log.d("FirebaseDataStore", "successfully stored data"))
                .addOnFailureListener(e -> Toast.makeText(activity.getApplicationContext(), "Failed to send listing", Toast.LENGTH_LONG).show());
        Account authAccount = getUser();
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

    private boolean checkFields(List<Spinner> spinnerList){
        return checkTitle() && checkPrice() && checkCategory(spinnerList);
    }

    private boolean checkCategory(List<Spinner> spinnerList) {
        return !spinnerList.get(spinnerList.size()-1).getSelectedItem().toString().equals(R.string.default_spinner_text);
    }

    private boolean checkPrice() {
        return !priceSelector.getText().toString().isEmpty();
    }

    private boolean checkTitle() {
        return !titleSelector.getText().toString().isEmpty();
    }

    Listing makeListing(Double lat, Double lng, List<Spinner> spinnerList) {
        String category = spinnerList.get(spinnerList.size()-1).getSelectedItem().toString();
        Authenticator fbAuth = AuthenticatorFactory.getDependency();
        if (fbAuth.getCurrentUser() != null) {
            String userEmail = fbAuth.getCurrentUser().getEmail();
            return new Listing(titleSelector.getText().toString(), descriptionSelector.getText().toString(),
                    priceSelector.getText().toString(), userEmail, "", category, lat, lng);
        }
        return null;
    }

    //returns false if the listing can't be submitted
    boolean submit(List<Spinner> spinnerList, List<String> listStringImage, String stringThumbnail, Double lat, Double lng) {
        if(!listStringImage.isEmpty()) {
            stringThumbnail = resizeStringImageThumbnail(listStringImage.get(0));
        }
        Context context = activity.getApplicationContext();
        if (!checkFields(spinnerList)) {
            Toast.makeText(context, R.string.incorrect_fields, Toast.LENGTH_SHORT).show();
        }
        else {
            if(isInternetAvailable(context)){
                Listing newListing = makeListing(lat, lng, spinnerList);
                if (newListing != null) {
                    createAndSendListing(newListing, listStringImage, stringThumbnail);
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

    void deleteOldListingAndSubmitNewOne(List<Spinner> spinnerList, List<String> listStringImage, String stringThumbnail, Double lat, Double lng, List<String> listImageID) {
        if (!checkFields(spinnerList)) {
            Toast.makeText(activity.getApplicationContext(), R.string.incorrect_fields, Toast.LENGTH_SHORT).show();
        }
        else{
            Bundle bundle = activity.getIntent().getExtras();
            if(bundle == null){
                return;
            }
            String listingID = bundle.getString("listingID");

            Listing.deleteWithLiteVersion(listingID)
                    .addOnSuccessListener((v) -> submit(spinnerList, listStringImage, stringThumbnail, lat, lng));

            for(String id: listImageID) {
                ListingImage.delete(id);
            }
        }
    }

}
