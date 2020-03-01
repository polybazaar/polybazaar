package ch.epfl.polybazaar;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class SaleDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) throws NullPointerException{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale_details);

        Bundle b = getIntent().getExtras();
        if(b == null)
            throw new NullPointerException();

        //Set the image
        ImageView image = (ImageView)findViewById(R.id.imageView2);
        image.setImageResource(R.drawable.algebre_lin); //need to change this

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


        //Button ret = (Button) findViewById(R.id.ret_but);
        //Button get_seller = (Button) findViewById(R.id.contact_sel);

        //Return button action
        /*ret.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //Intent myIntent = new Intent(view.getContext(), SalesOverview.class);
                //startActivityForResult(myIntent, 0);
            }
        });*/

        //Get seller action
        /*get_seller.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                

            }
        });*/

    }
}
