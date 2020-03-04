package ch.epfl.polybazaar;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SaleDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) throws NullPointerException{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale_details);

        Bundle bundle = getIntent().getExtras();
        if(bundle == null)
            bundle = new Bundle();

        //set image
        ImageView image = findViewById(R.id.imageView2);
        int pic = bundle.getInt("image", -1);
        if(pic != -1) {
            image.setImageResource(pic);
        }

        //Set the title
        TextView title_txt = (TextView)findViewById(R.id.title);
        title_txt.setText(bundle.getString("title", "No Title"));

        //Set the description
        TextView description_txt = (TextView)findViewById(R.id.description);
        description_txt.setText(bundle.getString("description", "No description"));

        //Set the price
        TextView price_txt = (TextView)findViewById(R.id.price);
        price_txt.setTextSize(20);
        price_txt.setText(bundle.getString("price", "No price"));


        Button get_seller = findViewById(R.id.contact_sel);

        //Get seller action
        get_seller.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Toast toast = Toast.makeText(getApplicationContext(),"This functionality is not implemented yet",Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
                toast.show();
            }
        });

    }
}
