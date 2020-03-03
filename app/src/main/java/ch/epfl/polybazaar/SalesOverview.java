package ch.epfl.polybazaar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class SalesOverview extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_overview);

        // TODO: fetch listing attributes from database and dynamically build the overview
    }

    public void onClick(View v) {
        Intent intent = new Intent(SalesOverview.this, SaleDetails.class);
        intent.putExtra("title", "Algebre Linéaire by David C. Lay" );
        intent.putExtra("description", "Never used");
        intent.putExtra("price", "18 CHF");
        startActivity(intent);
    }


}
