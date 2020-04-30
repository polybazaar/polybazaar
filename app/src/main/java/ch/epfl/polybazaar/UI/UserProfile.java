package ch.epfl.polybazaar.UI;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

import ch.epfl.polybazaar.R;
import ch.epfl.polybazaar.Utilities;
import ch.epfl.polybazaar.login.Account;
import ch.epfl.polybazaar.login.Authenticator;
import ch.epfl.polybazaar.login.AuthenticatorFactory;
import ch.epfl.polybazaar.user.User;
import ch.epfl.polybazaar.utilities.ImageTaker;
import ch.epfl.polybazaar.widgets.AddImageDialog;
import ch.epfl.polybazaar.widgets.NoticeDialogListener;
import ch.epfl.polybazaar.widgets.PublishProfileDialog;

import static ch.epfl.polybazaar.UI.SalesOverview.displaySavedListings;
import static ch.epfl.polybazaar.Utilities.displayToast;
import static ch.epfl.polybazaar.Utilities.getUser;
import static ch.epfl.polybazaar.user.User.NO_PROFILE_PICTURE;
import static ch.epfl.polybazaar.utilities.ImageTaker.STRING_IMAGE;
import static ch.epfl.polybazaar.utilities.ImageTaker.IMAGE_AVAILABLE;
import static ch.epfl.polybazaar.utilities.ImageTaker.PICTURE_PREFS;
import static ch.epfl.polybazaar.utilities.ImageTaker.CODE;
import static ch.epfl.polybazaar.utilities.ImageTaker.LOAD_IMAGE;
import static ch.epfl.polybazaar.utilities.ImageTaker.TAKE_IMAGE;
import static ch.epfl.polybazaar.utilities.ImageUtilities.convertBitmapToStringPNG;
import static ch.epfl.polybazaar.utilities.ImageUtilities.convertStringToBitmap;
import static ch.epfl.polybazaar.utilities.ImageUtilities.getRoundedCroppedBitmap;
import static ch.epfl.polybazaar.widgets.MinimalAlertDialog.makeDialog;

public class UserProfile extends AppCompatActivity implements NoticeDialogListener {

    public static final int PROFILE_PIC_SIZE = 500;
    private Authenticator authenticator;
    private Account account;
    private User user;
    private ImageView profilePicView;
    private boolean profilePicChanged;
    private boolean showNewPicDialog;

    private EditText nicknameSelector;
    private EditText firstNameSelector;
    private EditText lastNameSelector;
    private EditText phoneNumberSelector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        authenticator = AuthenticatorFactory.getDependency();
        BottomNavigationView bottomNavigationView = findViewById(R.id.activity_main_bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.action_profile);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> bottomBar.updateActivity(item.getItemId(), UserProfile.this));
        if (authenticator.getCurrentUser() == null) {
            Intent notSignedIn = new Intent(this, NotSignedIn.class);
            startActivity(notSignedIn);
        } else {
            nicknameSelector = findViewById(R.id.nicknameSelector);
            firstNameSelector = findViewById(R.id.firstNameSelector);
            lastNameSelector = findViewById(R.id.lastNameSelector);
            phoneNumberSelector = findViewById(R.id.phoneNumberSelector);
            profilePicView = findViewById(R.id.profilePicture);
            profilePicChanged = false;
            showNewPicDialog = false;
            account = authenticator.getCurrentUser();
            User.fetch(account.getEmail()).addOnSuccessListener(returnedUser -> {
                user = returnedUser;
                nicknameSelector.setText(user.getNickName());
                firstNameSelector.setText(user.getFirstName());
                lastNameSelector.setText(user.getLastName());
                phoneNumberSelector.setText(user.getPhoneNumber());
                if (!user.getProfilePicture().equals(NO_PROFILE_PICTURE)) {
                    profilePicView.setImageBitmap(convertStringToBitmap(user.getProfilePicture()));
                    SharedPreferences myPrefs = this.getSharedPreferences(PICTURE_PREFS, MODE_PRIVATE);
                    myPrefs.edit().putString(STRING_IMAGE, user.getProfilePicture()).apply();
                    profilePicChanged = true;
                }
            });
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == LOAD_IMAGE) {
                if (data != null) {
                    getNewImage(data);
                }
            } else if (requestCode == TAKE_IMAGE) {
                if (data != null) {
                    getNewImage(data);
                }
            } else {
                makeDialog(UserProfile.this, R.string.profile_picture_not_updated);
            }
        }
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        intent.putExtra(CODE, requestCode);
        super.startActivityForResult(intent, requestCode);
    }

    private void getNewImage(Intent data) {
        boolean bitmapOK = data.getBooleanExtra(IMAGE_AVAILABLE, false);
        if (bitmapOK) {
            String stringImage = this.getSharedPreferences(PICTURE_PREFS, MODE_PRIVATE).getString(STRING_IMAGE, null);
            if (stringImage != null) {
                Bitmap bitmap = getRoundedCroppedBitmap(convertStringToBitmap(stringImage));
                bitmap = Bitmap.createScaledBitmap(bitmap, PROFILE_PIC_SIZE, PROFILE_PIC_SIZE, true);
                profilePicView.setImageBitmap(bitmap);
                String profilePic = convertBitmapToStringPNG(bitmap);
                this.getSharedPreferences(PICTURE_PREFS, MODE_PRIVATE).edit().putString(STRING_IMAGE, profilePic).apply();
                profilePicChanged = true;
                showNewPicDialog = true;
            } else {
                makeDialog(UserProfile.this, R.string.profile_picture_not_updated);
            }
        } else {
            makeDialog(UserProfile.this, R.string.profile_picture_not_updated);
        }
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        if (dialog instanceof AddImageDialog) {
            startActivityForResult(new Intent(this, ImageTaker.class), TAKE_IMAGE);
        }
        if (dialog instanceof PublishProfileDialog) {
            applyEditProfile();
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        if (dialog instanceof AddImageDialog) {
            startActivityForResult(new Intent(this, ImageTaker.class), LOAD_IMAGE);
        }
    }

    public void changeProfilePicture(View view) {
        AddImageDialog dialog = new AddImageDialog();
        dialog.show(getSupportFragmentManager(), "select image import");
    }

    private void applyEditProfile() {
        String newNickname = nicknameSelector.getText().toString();
        String newFirstName = firstNameSelector.getText().toString();
        String newLastName = lastNameSelector.getText().toString();
        String newPhoneNumber = phoneNumberSelector.getText().toString();
        User editedUser;
        String profilePic;
        if (profilePicChanged) {
            profilePic = this.getSharedPreferences(PICTURE_PREFS, MODE_PRIVATE).getString(STRING_IMAGE, null);
        } else {
            profilePic = NO_PROFILE_PICTURE;
        }
        editedUser = new User(newNickname, user.getEmail(), newFirstName, newLastName, newPhoneNumber, profilePic, user.getOwnListings(), user.getFavorites());
        editedUser.save().addOnSuccessListener(aVoid -> {
            Toast.makeText(getApplicationContext(), R.string.profile_updated, Toast.LENGTH_SHORT).show();
        }).addOnSuccessListener(aVoid -> account.updateNickname(newNickname));
    }

    public void editProfile(View view){
        String newNickname = nicknameSelector.getText().toString();
        String newFirstName = firstNameSelector.getText().toString();
        String newLastName = lastNameSelector.getText().toString();

        if(!Utilities.nickNameIsValid(newNickname)){
            makeDialog(UserProfile.this, R.string.signup_nickname_invalid);
        }
        else if (!Utilities.nameIsValid(newFirstName)){
            makeDialog(UserProfile.this, R.string.invalid_first_name);
        }
        else if (!Utilities.nameIsValid(newLastName)){
            makeDialog(UserProfile.this, R.string.invalid_last_name);
        }
        else{
            if (showNewPicDialog) {
                PublishProfileDialog dialog = new PublishProfileDialog();
                dialog.show(getSupportFragmentManager(), "select image import");
            } else {
                applyEditProfile();
            }
        }
    }

    public void editPassword(View view){
        String currentPassword= ((EditText)findViewById(R.id.currentPassword)).getText().toString();
        String newPassword = ((EditText)findViewById(R.id.newPassword)).getText().toString();
        String confirmNewPassword = ((EditText)findViewById(R.id.confirmNewPassword)).getText().toString();

        if (!newPassword.equals(confirmNewPassword)) {
            makeDialog(UserProfile.this, R.string.signup_passwords_not_matching);
        }
        else if (!Utilities.passwordIsValid(newPassword)){
            makeDialog(UserProfile.this, R.string.signup_passwords_weak);
        }
        else{
            authenticator.signIn(user.getEmail(), currentPassword).addOnSuccessListener(authenticatorResult -> {
                account.updatePassword(newPassword);
                ((EditText)findViewById(R.id.currentPassword)).setText("");
                ((EditText)findViewById(R.id.newPassword)).setText("");
                ((EditText)findViewById(R.id.confirmNewPassword)).setText("");
                Toast.makeText(getApplicationContext(), R.string.password_updated, Toast.LENGTH_SHORT).show();
            }).addOnFailureListener(e -> {
                Toast.makeText(getApplicationContext(), R.string.invalid_current_password, Toast.LENGTH_SHORT).show();
            });
        }
    }

    public void viewOwnListings(View view) {
        Account user = getUser();
        if(user == null) return;
        user.getUserData().addOnSuccessListener(authUser -> {
            ArrayList<String> ownListingsIds = authUser.getOwnListings();

            // the list of user-created listings is empty
            if (ownListingsIds == null || ownListingsIds.isEmpty()) {
                displayToast(this, R.string.no_created_listings, Gravity.CENTER);
                // we relaunch the SalesOverview activity with the list of favorites in the bundle
            } else {
                displaySavedListings(this, ownListingsIds);
            }
        });
    }

    public void viewFavorites(View view) {
        Account user = getUser();
        if(user == null) return;
        user.getUserData().addOnSuccessListener(authUser -> {
                    ArrayList<String> favoritesIds = authUser.getFavorites();
            // the list of user-created listings is empty
            if (favoritesIds == null || favoritesIds.isEmpty()) {
                displayToast(this, R.string.no_favorites, Gravity.CENTER);
                // we relaunch the SalesOverview activity with the list of favorites in the bundle
            } else {
                displaySavedListings(this, favoritesIds);
            }
        });
    }

    public void signOutUser(View view) {
        authenticator.signOut();
        Intent notSignedIn = new Intent(getApplicationContext(), SalesOverview.class);
        startActivity(notSignedIn);
    }
}