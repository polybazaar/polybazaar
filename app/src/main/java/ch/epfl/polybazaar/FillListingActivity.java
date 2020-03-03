package ch.epfl.polybazaar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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

public class FillListingActivity extends AppCompatActivity {

    private static final int RESULT_LOAD_IMAGE = 1;
    private Button uploadImage;
    private ImageView pictureView;
    private Switch freeSwitch;
    private EditText priceSelector;
    private double oldPrice;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fill_listing);

        pictureView = findViewById(R.id.picturePreview);
        freeSwitch = findViewById(R.id.freeSwitch);
        priceSelector = findViewById(R.id.priceSelector);
        uploadImage = findViewById(R.id.uploadImage);

        freeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    if(priceSelector.getText().length() >0) {
                        oldPrice = Double.parseDouble(priceSelector.getText().toString());
                    }
                    priceSelector.setFocusable(false);
                    priceSelector.setText(Double.toString(0.00));
                }
                else{
                    priceSelector.setFocusableInTouchMode(true);
                    priceSelector.setText(Double.toString(oldPrice));
                }
            }
        });

        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });
    }

    public void uploadImage(){
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
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



}
