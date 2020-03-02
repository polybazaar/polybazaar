package ch.epfl.polybazaar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(MainActivity.this, SaleDetails.class);
        intent.putExtra("image_link",R.drawable.algebre_lin);
        intent.putExtra("title", "A title");
        intent.putExtra("description", "A description");
        intent.putExtra("price", "CHF 100.-");
        startActivity(intent);
    }
}
