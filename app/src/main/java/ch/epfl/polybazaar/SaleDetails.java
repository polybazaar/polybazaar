package ch.epfl.polybazaar;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SaleDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) throws NullPointerException{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale_details);

        Bundle b = getIntent().getExtras();
        if(b == null)
            throw new NullPointerException();

        //Set the title
        TextView title_txt = (TextView)findViewById(R.id.title);
        title_txt.setText(b.getString("title", "No Title"));

        //Set the description
        TextView description_txt = (TextView)findViewById(R.id.description);
        description_txt.setText(b.getString("description", "No description"));

        //Set the price
        TextView price_txt = (TextView)findViewById(R.id.price);
        price_txt.setTextSize(20);
        price_txt.setText(b.getString("price", "No Price"));



        Button get_seller = findViewById(R.id.contact_sel);

        //Get seller action
        get_seller.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"This functionality is not implemented yet",Toast.LENGTH_LONG).show();
            }
        });

    }
}
