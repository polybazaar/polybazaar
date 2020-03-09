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

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button saleDet = findViewById(R.id.sale_det);
        saleDet.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent fillListingIntent = new Intent(MainActivity.this, SaleDetails.class);
                fillListingIntent.putExtra("listingID", "35120421-4a81-4b78-b91d-23582698fcee");
                startActivity(fillListingIntent);
            }
        });

        Button saleOverBut = findViewById(R.id.sale_overview);
        saleOverBut.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent fillListingIntent = new Intent(MainActivity.this, SalesOverview.class);
                startActivity(fillListingIntent);
            }
        });

        Button addListBut = findViewById(R.id.add_listing);
        addListBut.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent fillListingIntent = new Intent(MainActivity.this, FillListingActivity.class);
                startActivity(fillListingIntent);
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

    }
}