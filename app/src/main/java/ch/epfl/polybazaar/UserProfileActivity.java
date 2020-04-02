package ch.epfl.polybazaar;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;

import ch.epfl.polybazaar.login.AppUser;
import ch.epfl.polybazaar.login.Authenticator;
import ch.epfl.polybazaar.login.AuthenticatorFactory;
import ch.epfl.polybazaar.user.User;

import static ch.epfl.polybazaar.widgets.MinimalAlertDialog.makeDialog;

public class UserProfileActivity extends AppCompatActivity {

    private Authenticator authenticator;
    private AppUser appUser;
    private User user;


    private EditText nicknameSelector;
    private EditText firstNameSelector;
    private EditText lastNameSelector;
    private EditText phoneNumberSelector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        nicknameSelector = findViewById(R.id.nicknameSelector);
        firstNameSelector = findViewById(R.id.firstNameSelector);
        lastNameSelector = findViewById(R.id.lastNameSelector);
        phoneNumberSelector = findViewById(R.id.phoneNumberSelector);

    }

    @Override
    public void onStart() {
        super.onStart();
        authenticator = AuthenticatorFactory.getDependency();
        appUser = authenticator.getCurrentUser();

        User.fetch(appUser.getEmail()).addOnSuccessListener( returnedUser -> {
                user = returnedUser;
                nicknameSelector.setText(user.getNickName());
                firstNameSelector.setText(user.getFirstName());
                lastNameSelector.setText(user.getLastName());
                phoneNumberSelector.setText(user.getPhoneNumber());
        });
    }

    public void editProfile(View view){
        String newNickname = nicknameSelector.getText().toString();
        String newFirstName = firstNameSelector.getText().toString();
        String newLastName = lastNameSelector.getText().toString();
        String newPhoneNumber = phoneNumberSelector.getText().toString();

        if(!Utilities.nickNameIsValid(newNickname)){
            makeDialog(UserProfileActivity.this, R.string.signup_nickname_invalid);
        }
        else if (!Utilities.nameIsValid(newFirstName)){
            makeDialog(UserProfileActivity.this, R.string.invalid_first_name);
        }
        else if (!Utilities.nameIsValid(newFirstName)){
            makeDialog(UserProfileActivity.this, R.string.invalid_last_name);
        }
        else{
            User editedUser = new User(newNickname, user.getEmail(), newFirstName, newLastName, newPhoneNumber);
            User.editUser(editedUser).addOnSuccessListener(aVoid -> {
                Toast.makeText(getApplicationContext(), R.string.profile_updated, Toast.LENGTH_SHORT).show();
            }).addOnSuccessListener(aVoid -> appUser.updateNickname(newNickname));
        }
    }

    public void editPassword(View view){
        String currentPassword= ((EditText)findViewById(R.id.currentPassword)).getText().toString();
        String newPassword = ((EditText)findViewById(R.id.newPassword)).getText().toString();
        String confirmNewPassword = ((EditText)findViewById(R.id.confirmNewPassword)).getText().toString();

        if (!newPassword.equals(confirmNewPassword)) {
            makeDialog(UserProfileActivity.this, R.string.signup_passwords_not_matching);
        }
        else if (!Utilities.passwordIsValid(newPassword)){
            makeDialog(UserProfileActivity.this, R.string.signup_passwords_weak);
        }
        else{
            authenticator.signIn(user.getEmail(), currentPassword).addOnSuccessListener(authenticatorResult -> {
                appUser.updatePassword(newPassword);
                ((EditText)findViewById(R.id.currentPassword)).setText("");
                ((EditText)findViewById(R.id.newPassword)).setText("");
                ((EditText)findViewById(R.id.confirmNewPassword)).setText("");
                Toast.makeText(getApplicationContext(), R.string.password_updated, Toast.LENGTH_SHORT).show();
            }).addOnFailureListener(e -> {
                Toast.makeText(getApplicationContext(), R.string.invalid_current_password, Toast.LENGTH_SHORT).show();
            });
        }
    }
}