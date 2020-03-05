package ch.epfl.polybazaar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();

        findViewById(R.id.sale_overview).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent FillListingIntent = new Intent(MainActivity.this, SalesOverview.class);
                startActivity(FillListingIntent);
            }
        });

        findViewById(R.id.add_listing).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent FillListingIntent = new Intent(MainActivity.this, FillListingActivity.class);
                startActivity(FillListingIntent);
            }
        });

        findViewById(R.id.sign_in).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Toast toast = Toast.makeText(getApplicationContext(),"This functionality is not implemented yet",Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
                toast.show();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}
