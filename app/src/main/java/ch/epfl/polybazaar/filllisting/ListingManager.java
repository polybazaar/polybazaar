package ch.epfl.polybazaar.filllisting;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import java.util.List;

import ch.epfl.polybazaar.MainActivity;
import ch.epfl.polybazaar.R;
import ch.epfl.polybazaar.listing.Listing;
import ch.epfl.polybazaar.listingImage.ListingImage;
import ch.epfl.polybazaar.litelisting.LiteListing;
import ch.epfl.polybazaar.login.AppUser;
import ch.epfl.polybazaar.login.Authenticator;
import ch.epfl.polybazaar.login.AuthenticatorFactory;

import static ch.epfl.polybazaar.Utilities.getUser;
import static ch.epfl.polybazaar.user.User.editUser;
import static java.util.UUID.randomUUID;

public class ListingManager {

    private Activity activity;

    ListingManager(Activity activity) {
        this.activity = activity;
    }

    public void createAndSendListing(Listing newListing, List<String> listStringImage, String stringThumbnail) {
        final String newListingID = randomUUID().toString();
        Authenticator fbAuth = AuthenticatorFactory.getDependency();
        if(fbAuth.getCurrentUser() == null) {
            Toast.makeText(activity.getApplicationContext(), R.string.sign_in_required, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(activity.getApplicationContext(), MainActivity.class);
            activity.startActivity(intent);
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

        AppUser authAccount = getUser();
        // update own listings of (logged) user
        if(authAccount != null) {
            authAccount.getUserData().addOnSuccessListener(user -> {
                if (user != null) {
                    user.addOwnListing(newListingID);
                    editUser(user);
                }
            });
        }
    }




}
