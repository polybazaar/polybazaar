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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale_details);

        ImageView image = (ImageView)findViewById(R.id.imageView2);
        image.setImageResource(R.drawable.algebre_lin);


        TextView title_txt = (TextView)findViewById(R.id.title);
        title_txt.setText("Algèbre Linéaire by David C. Lay");

        TextView description_txt = (TextView)findViewById(R.id.description);
        description_txt.setText("Never used");

        TextView price_txt = (TextView)findViewById(R.id.price);
        price_txt.setTextSize(20);
        price_txt.setText("18.-");

        Button ret = (Button) findViewById(R.id.ret_but);
        Button get_seller = (Button) findViewById(R.id.contact_sel);

        ret.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //Intent myIntent = new Intent(view.getContext(), SalesOverview.class);
                //startActivityForResult(myIntent, 0);
                

            }
        });

        get_seller.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                

            }
        });



        /*
        ret.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //Intent myIntent = new Intent(view.getContext(), ???.class); //create a class to display info? Or put a TextView at the place of the button?
                //startActivityForResult(myIntent, 0);
            }
        });*/
    }
}
