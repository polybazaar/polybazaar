package ch.epfl.polybazaar;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import ch.epfl.polybazaar.UI.UserProfile;
import ch.epfl.polybazaar.UI.SalesOverview;
import ch.epfl.polybazaar.UI.bottomBar;
import ch.epfl.polybazaar.conversationOverview.ConversationOverviewActivity;
import ch.epfl.polybazaar.login.Account;
import ch.epfl.polybazaar.UI.FillListing;
import ch.epfl.polybazaar.login.Authenticator;
import ch.epfl.polybazaar.login.AuthenticatorFactory;
import ch.epfl.polybazaar.login.SignInActivity;

public class MainActivity extends AppCompatActivity {

    private Authenticator authenticator;
    private Account user;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        authenticator = AuthenticatorFactory.getDependency();

        BottomNavigationView bottomNavigationView = findViewById(R.id.activity_main_bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.action_home);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> bottomBar.updateActivity(item.getItemId(),MainActivity.this));
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
            Intent intent = new Intent(MainActivity.this, FillListing.class);
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

            updateUserButtons();
        }
    }

    public void toProfile(View view){
        Intent intent = new Intent(MainActivity.this, UserProfile.class);
        startActivity(intent);
    }

    public void toConversationOverview(View viw){
        Intent intent = new Intent(MainActivity.this, ConversationOverviewActivity.class);
        startActivity(intent);
    }

    @Override
    public void onStart() {
        super.onStart();

        user = authenticator.getCurrentUser();
        updateUserButtons();
    }

    private void updateUserButtons() {
        Button signInBut = findViewById(R.id.authenticationButton);
        Button profileBut = findViewById(R.id.profileButton);
        Button conversationOverviewButton = findViewById(R.id.conversationOverviewButton);

        if (user != null) {
            signInBut.setText(R.string.sign_out);
            profileBut.setClickable(true);
            profileBut.setVisibility(View.VISIBLE);
            conversationOverviewButton.setVisibility(View.VISIBLE);
        } else {
            signInBut.setText(R.string.sign_in);
            profileBut.setClickable(false);
            profileBut.setVisibility(View.INVISIBLE);
            conversationOverviewButton.setVisibility(View.INVISIBLE);
        }
    }
}