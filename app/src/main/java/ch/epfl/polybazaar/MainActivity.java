package ch.epfl.polybazaar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

import ch.epfl.polybazaar.listing.Listing;
import ch.epfl.polybazaar.litelisting.LiteListing;
import ch.epfl.polybazaar.login.AppUser;
import ch.epfl.polybazaar.login.Authenticator;
import ch.epfl.polybazaar.login.AuthenticatorFactory;
import ch.epfl.polybazaar.login.SignInActivity;

import ch.epfl.polybazaar.UI.SalesOverview;

public class MainActivity extends AppCompatActivity {

    private Authenticator authenticator;
    private AppUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        authenticator = AuthenticatorFactory.getDependency();

        LiteListing.retrieveAll().addOnSuccessListener((liteListings -> {
            return;
        }));
    }

    public void toSalesOverview(View view) {
        Intent intent = new Intent(MainActivity.this, SalesOverview.class);
        startActivity(intent);
    }

    public void toNewListing(View view) {
        if (user == null) {
            Toast toast = Toast.makeText(MainActivity.this, R.string.sign_in_required, Toast.LENGTH_LONG);
            toast.show();
        } else {
            Intent intent = new Intent(MainActivity.this, FillListingActivity.class);
            startActivity(intent);
        }
    }

    public void clickAuthenticationButton(View view) {
        if (user == null) {
            Intent intent = new Intent(MainActivity.this, SignInActivity.class);
            startActivity(intent);
        } else {
            authenticator.signOut();
            user = authenticator.getCurrentUser();

            updateAuthenticationButton();
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        user = authenticator.getCurrentUser();
        updateAuthenticationButton();
    }

    private void updateAuthenticationButton() {
        Button signInBut = findViewById(R.id.authenticationButton);
        if (user != null) {
            signInBut.setText(R.string.sign_out);
        } else {
            signInBut.setText(R.string.sign_in);
        }
    }
}