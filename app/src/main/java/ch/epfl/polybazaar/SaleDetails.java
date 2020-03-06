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

        //get the uId of the object
        int uId = bundle.getInt("uId", -1);
        Listing listing = getObjectData(uId);

        //set image
        ImageView image = findViewById(R.id.imageView2);
        int pic = bundle.getInt("image", -1);
        if(pic != -1) {
            image.setImageResource(pic);
        }

        //Set the title
        TextView title_txt = (TextView)findViewById(R.id.title);
        title_txt.setText(listing.getTitle());

        //Set the description
        TextView description_txt = (TextView)findViewById(R.id.description);
        description_txt.setText(listing.getDescription());

        //Set the price
        TextView price_txt = (TextView)findViewById(R.id.price);
        price_txt.setTextSize(20);
        price_txt.setText(listing.getPrice());


        Button get_seller = findViewById(R.id.contact_sel);

        //Get seller action
        get_seller.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //TODO get the seller information through the listing
                Toast toast = Toast.makeText(getApplicationContext(),"This functionality is not implemented yet",Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
                toast.show();
            }
        });

    }

    private Listing getObjectData(int uId) {
        //get the information from the database
        Listing listing = new Listing("Title", "Description", "Price");
        return listing;
    }
}
