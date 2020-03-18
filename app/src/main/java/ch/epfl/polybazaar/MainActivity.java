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