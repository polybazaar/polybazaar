package ch.epfl.polybazaar.search;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import ch.epfl.polybazaar.R;

public class NoSearchResults extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_search_results);
    }
}
