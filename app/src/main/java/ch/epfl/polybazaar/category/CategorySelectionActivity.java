package ch.epfl.polybazaar.category;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ch.epfl.polybazaar.R;


public class CategorySelectionActivity extends AppCompatActivity {

    private CategoryRecyclerAdapter categoryRecyclerAdapter;
    private RecyclerView categoryRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_selector);
        //categoryRecyclerAdapter = new CategoryRecyclerAdapter(getApplicationContext(), cat);
        //categoryRecycler = findViewById(R.id.categoriesRecycler);
        //categoryRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        //categoryRecycler.setAdapter(categoryRecyclerAdapter);

    }
}