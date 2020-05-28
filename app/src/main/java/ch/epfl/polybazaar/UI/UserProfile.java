package ch.epfl.polybazaar.UI;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;

import ch.epfl.polybazaar.R;
import ch.epfl.polybazaar.filestorage.ImageTransaction;
import ch.epfl.polybazaar.login.Account;
import ch.epfl.polybazaar.login.AuthenticationUtils;
import ch.epfl.polybazaar.login.Authenticator;
import ch.epfl.polybazaar.login.AuthenticatorFactory;
import ch.epfl.polybazaar.user.User;
import ch.epfl.polybazaar.utilities.ImageTaker;
import ch.epfl.polybazaar.utilities.InputValidity;
import ch.epfl.polybazaar.widgets.AddImageDialog;
import ch.epfl.polybazaar.widgets.NoticeDialogListener;
import ch.epfl.polybazaar.widgets.PublishProfileDialog;

import static ch.epfl.polybazaar.UI.SalesOverview.displaySavedListings;
import static ch.epfl.polybazaar.Utilities.getUser;
import static ch.epfl.polybazaar.chat.ChatActivity.removeBottomBarWhenKeyboardUp;
import static ch.epfl.polybazaar.user.User.NO_PROFILE_PICTURE;
import static ch.epfl.polybazaar.utilities.ImageTaker.CODE;
import static ch.epfl.polybazaar.utilities.ImageTaker.IMAGE_AVAILABLE;
import static ch.epfl.polybazaar.utilities.ImageTaker.LOAD_IMAGE;
import static ch.epfl.polybazaar.utilities.ImageTaker.PICTURE_PREFS;
import static ch.epfl.polybazaar.utilities.ImageTaker.QUALITY;
import static ch.epfl.polybazaar.utilities.ImageTaker.STRING_IMAGE;
import static ch.epfl.polybazaar.utilities.ImageTaker.TAKE_IMAGE;
import static ch.epfl.polybazaar.utilities.ImageUtilities.convertBitmapToStringPNG;
import static ch.epfl.polybazaar.utilities.ImageUtilities.convertStringToBitmap;
import static ch.epfl.polybazaar.utilities.ImageUtilities.getRoundedCroppedBitmap;
import static ch.epfl.polybazaar.utilities.InputValidity.nameIsValid;
import static ch.epfl.polybazaar.utilities.InputValidity.nicknameValidity;
import static ch.epfl.polybazaar.utilities.InputValidity.passwordValidity;
import static ch.epfl.polybazaar.widgets.MinimalAlertDialog.makeDialog;
import static java.util.UUID.randomUUID;

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
        if (AuthenticationUtils.checkAccessAuthorization(this)) {
            nicknameSelector = findViewById(R.id.nicknameSelector);
            firstNameSelector = findViewById(R.id.firstNameSelector);
            lastNameSelector = findViewById(R.id.lastNameSelector);
            phoneNumberSelector = findViewById(R.id.phoneNumberSelector);
            profilePicView = findViewById(R.id.profilePicture);
            profilePicView.setOnClickListener(v -> changeProfilePicture());
            profilePicChanged = false;
            showNewPicDialog = false;
            account = authenticator.getCurrentUser();
            User.fetch(account.getEmail()).addOnSuccessListener(returnedUser -> {
                user = returnedUser;
                nicknameSelector.setText(user.getNickName());
                firstNameSelector.setText(user.getFirstName());
                lastNameSelector.setText(user.getLastName());
                phoneNumberSelector.setText(user.getPhoneNumber());
                if (!user.getProfilePictureRef().equals(NO_PROFILE_PICTURE)) {
                    ImageTransaction.fetch(user.getProfilePictureRef(), this.getApplicationContext()).addOnSuccessListener(bitmap -> {
                        profilePicView.setImageBitmap(bitmap);
                        SharedPreferences myPrefs = this.getSharedPreferences(PICTURE_PREFS, MODE_PRIVATE);
                        myPrefs.edit().putString(STRING_IMAGE, convertBitmapToStringPNG(bitmap)).apply();
                        profilePicChanged = true;
                    });
                }
            });
        }
        BottomNavigationView bottomNavigationView = findViewById(R.id.activity_main_bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.action_profile);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> bottomBar.updateActivity(item.getItemId(), UserProfile.this));
        removeBottomBarWhenKeyboardUp(this);
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
                Bitmap bitmap = Bitmap.createScaledBitmap(getRoundedCroppedBitmap(convertStringToBitmap(stringImage)), PROFILE_PIC_SIZE, PROFILE_PIC_SIZE, true);
                profilePicView.setImageBitmap(bitmap);
                this.getSharedPreferences(PICTURE_PREFS, MODE_PRIVATE).edit().putString(STRING_IMAGE, convertBitmapToStringPNG(bitmap)).apply();
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

    public void changeProfilePicture() {
        AddImageDialog dialog = new AddImageDialog();
        dialog.show(getSupportFragmentManager(), "select image import");
    }

    private void applyEditProfile() {
        String newNickname = nicknameSelector.getText().toString();
        String newFirstName = firstNameSelector.getText().toString();
        String newLastName = lastNameSelector.getText().toString();
        String newPhoneNumber = phoneNumberSelector.getText().toString();
        User editedUser;
        String profilePicRef;
        if (profilePicChanged) {
                if (!user.getProfilePictureRef().equals(NO_PROFILE_PICTURE)) {
                    profilePicRef = user.getProfilePictureRef();
                    ImageTransaction.delete(profilePicRef);
                } else {
                    profilePicRef = randomUUID().toString();
                }
                Bitmap bitmap = convertStringToBitmap(this.getSharedPreferences(PICTURE_PREFS, MODE_PRIVATE).
                        getString(STRING_IMAGE, null));
            ImageTransaction.storePNG(profilePicRef, bitmap, QUALITY, this.getApplicationContext());
        } else {
            profilePicRef = NO_PROFILE_PICTURE;
        }
        editedUser = new User(newNickname, user.getEmail(), newFirstName, newLastName, newPhoneNumber, profilePicRef, user.getOwnListings(), user.getFavorites());
        editedUser.save().addOnSuccessListener(aVoid -> {
            Toast.makeText(getApplicationContext(), R.string.profile_updated, Toast.LENGTH_SHORT).show();
        }).addOnSuccessListener(aVoid -> account.updateNickname(newNickname));
    }

    public void editProfile(View view){
        String newNickname = nicknameSelector.getText().toString();
        String newFirstName = firstNameSelector.getText().toString();
        String newLastName = lastNameSelector.getText().toString();

        TextInputLayout nicknameInputLayout = findViewById(R.id.newNicknameInputLayout);
        TextInputLayout firstNameInputLayout = findViewById(R.id.newFirstNameInputLayout);
        TextInputLayout lastNameInputLayout = findViewById(R.id.newLastNameInputLayout);

        boolean allValid = true;

        //Below, tags are set for testing purpose as there is no easy way to check the error of an Input Layout from android.material
        if(!nicknameValidity(newNickname, getApplicationContext()).equals("")){
            nicknameInputLayout.setError(nicknameValidity(newNickname, getApplicationContext()));
            nicknameInputLayout.setTag(InputValidity.ERROR);
            allValid = false;
        }
        else{
            nicknameInputLayout.setError(null);
        }

        if (!nameIsValid(newFirstName)){
            firstNameInputLayout.setError(getString(R.string.invalid_first_name));
            firstNameInputLayout.setTag(InputValidity.ERROR);
            allValid = false;
        }
        else{
            firstNameInputLayout.setError(null);
        }
        if (!nameIsValid(newLastName)){
            lastNameInputLayout.setError(getString(R.string.invalid_last_name));
            lastNameInputLayout.setTag(InputValidity.ERROR);
            allValid = false;
        }
        else{
            lastNameInputLayout.setError(null);
        }

        if(allValid){
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
        TextInputLayout newPasswordInputLayout = findViewById(R.id.newPasswordInputLayout);
        TextInputLayout confirmNewPasswordInputLayout = findViewById(R.id.confirmNewPasswordInputLayout);
        TextInputLayout currentPasswordInputLayout = findViewById(R.id.currentPasswordInputLayout);
        boolean allValid = true;

        if (!passwordValidity(newPassword, getApplicationContext()).equals("")) {
            newPasswordInputLayout.setError(passwordValidity(newPassword, getApplicationContext()));
            allValid = false;
        }
        else{
            newPasswordInputLayout.setError(null);
        }

        if (!newPassword.equals(confirmNewPassword)) {
            confirmNewPasswordInputLayout.setError(getString(R.string.signup_passwords_not_matching));
            confirmNewPasswordInputLayout.setTag(InputValidity.ERROR);
            allValid = false;
        }
        else{
            confirmNewPasswordInputLayout.setError(null);
        }

        if(allValid){
            authenticator.signIn(user.getEmail(), currentPassword).addOnSuccessListener(authenticatorResult -> {
                account.updatePassword(newPassword);
                ((EditText)findViewById(R.id.currentPassword)).setText("");
                ((EditText)findViewById(R.id.newPassword)).setText("");
                ((EditText)findViewById(R.id.confirmNewPassword)).setText("");
                Toast.makeText(getApplicationContext(), R.string.password_updated, Toast.LENGTH_SHORT).show();
                currentPasswordInputLayout.setError(null);
            }).addOnFailureListener(e -> {
                currentPasswordInputLayout.setError(getString(R.string.invalid_current_password));
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
                makeDialog(this, R.string.no_created_listings);
                // we relaunch the SalesOverview activity with the list of favorites in the bundle
            } else {
                displaySavedListings(this, ownListingsIds, R.string.no_created_listings);
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
                makeDialog(this, R.string.no_favorites);
                // we relaunch the SalesOverview activity with the list of favorites in the bundle
            } else {
                displaySavedListings(this, favoritesIds, R.string.no_favorites);
            }
        });
    }

    public void signOutUser(View view) {
        authenticator.signOut();
        Intent notSignedIn = new Intent(getApplicationContext(), SalesOverview.class);
        startActivity(notSignedIn);
    }
}