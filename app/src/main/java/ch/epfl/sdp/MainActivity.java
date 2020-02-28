package ch.epfl.sdp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button add_listing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        add_listing = findViewById(R.id.add_listing);
        add_listing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddListingActivity();
            }
        });
    }

    private void openAddListingActivity(){
        Intent intent = new Intent(this, FillListingActivity.class);
        startActivity(intent);
    }
}
