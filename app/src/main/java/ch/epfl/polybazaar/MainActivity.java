package ch.epfl.polybazaar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

import ch.epfl.polybazaar.login.AppUser;
import ch.epfl.polybazaar.login.Authenticator;
import ch.epfl.polybazaar.login.AuthenticatorFactory;

public class MainActivity extends AppCompatActivity {

    private Authenticator authenticator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        authenticator = AuthenticatorFactory.getDependency();

        Button saleOverBut = findViewById(R.id.sale_overview);
        saleOverBut.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent FillListingIntent = new Intent(MainActivity.this, SalesOverview.class);
                startActivity(FillListingIntent);
            }
        });

        Button addListBut = findViewById(R.id.add_listing);
        addListBut.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent FillListingIntent = new Intent(MainActivity.this, FillListingActivity.class);
                startActivity(FillListingIntent);
            }
        });

        Button signInBut = findViewById(R.id.sign_in);
        signInBut.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Toast toast = Toast.makeText(getApplicationContext(),"This functionality is not implemented yet",Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
                toast.show();
            }
        });



    }

    @Override
    public void onStart() {
        super.onStart();

        AppUser user = authenticator.getCurrentUser();

        if (user != null) {
            Button signInBut = findViewById(R.id.sign_in);
            signInBut.setVisibility(View.GONE);
        }
    }
}