package ch.epfl.polybazaar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class FillListingActivity extends AppCompatActivity {

    public static final int RESULT_LOAD_IMAGE = 1;
    public static final String INCORRECT_FIELDS_TEXT = "One or more required fields are incorrect or uncompleted";

    private Button uploadImage;
    private Button submitListing;
    private ImageView pictureView;
    private Switch freeSwitch;
    private TextView titleSelector;
    private EditText descriptionSelector;
    private EditText priceSelector;
    private String oldPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fill_listing);

        pictureView = findViewById(R.id.picturePreview);
        freeSwitch = findViewById(R.id.freeSwitch);
        uploadImage = findViewById(R.id.uploadImage);
        submitListing = findViewById(R.id.submitListing);
        titleSelector = findViewById(R.id.titleSelector);
        descriptionSelector = findViewById(R.id.descriptionSelector);
        priceSelector = findViewById(R.id.priceSelector);

        addListeners();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Tags are set only for testing purposes using Espresso
        if(requestCode != RESULT_LOAD_IMAGE || resultCode != RESULT_OK || data==null){
            pictureView.setTag(-1);
        }
        else{
            Uri selectedImage = data.getData();
            pictureView.setImageURI(selectedImage);
            pictureView.setTag(selectedImage.hashCode());
        }
    }

    private void addListeners(){
        freeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                freezePriceSelector(isChecked);
            }
        });

        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });

        submitListing.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                submit();
            }
        });
    }

    private void uploadImage(){
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
    }

    private void freezePriceSelector(boolean isChecked){
        if(isChecked){
            if(priceSelector.getText().length() > 0) {
                oldPrice = priceSelector.getText().toString();
            }
            priceSelector.setFocusable(false);
            priceSelector.setText(Double.toString(0.00));
        }
        else{
            priceSelector.setFocusableInTouchMode(true);
            priceSelector.setText(oldPrice);
        }
    }

    private boolean checkFields(){
        return checkTitle() && checkPrice();
    }

    private boolean checkPrice() {
        try {
            return Double.parseDouble(priceSelector.getText().toString()) >= 0.0;
        }
        catch(Exception e){
            return false;
        }
    }

    private boolean checkTitle() {
        return !titleSelector.getText().toString().isEmpty();
    }

    private void submit(){
        Context context = getApplicationContext();
        if(!checkFields()) {
            Toast.makeText(context, INCORRECT_FIELDS_TEXT, Toast.LENGTH_SHORT).show();
            incorrectFieldsToast.show();
        }
        Listing newListing = new Listing(titleSelector.getText().toString(), descriptionSelector.getText().toString(), priceSelector.getText().toString());

    }


}
