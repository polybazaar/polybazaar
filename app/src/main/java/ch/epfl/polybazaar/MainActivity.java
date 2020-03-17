package ch.epfl.polybazaar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import ch.epfl.polybazaar.UI.SalesOverview;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button saleDet = findViewById(R.id.saleDet);
        saleDet.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), SaleDetails.class);
            intent.putExtra("listingID", "06d6f073-30ae-48ac-9530-f30e288b299a");
            startActivity(intent);
        });

        Button saleOverBut = findViewById(R.id.saleOverview);
        saleOverBut.setOnClickListener(view -> {
            Intent fillListingIntent = new Intent(MainActivity.this, SalesOverview.class);
            startActivity(fillListingIntent);
        });

        Button addListBut = findViewById(R.id.addListing);
        addListBut.setOnClickListener(view -> {
            Intent fillListingIntent = new Intent(MainActivity.this, FillListingActivity.class);
            startActivity(fillListingIntent);
        });

        Button signInBut = findViewById(R.id.signIn);
        signInBut.setOnClickListener(view -> {
            Toast toast = Toast.makeText(getApplicationContext(),"This functionality is not implemented yet",Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
            toast.show();
        });



    }

    @Override
    public void onStart() {
        super.onStart();

    }
}