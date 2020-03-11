package ch.epfl.polybazaar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import ch.epfl.polybazaar.database.callback.SuccessCallback;
import ch.epfl.polybazaar.listing.Listing;

import static ch.epfl.polybazaar.listing.ListingDatabase.storeListing;
import static java.util.UUID.randomUUID;

public class FillListingActivity extends AppCompatActivity {

    public static final int RESULT_LOAD_IMAGE = 1;
    public static final int RESULT_TAKE_PICTURE = 2;
    public static final String INCORRECT_FIELDS_TEXT = "One or more required fields are incorrect or uncompleted";

    private Button uploadImage;
    private Button camera;
    private Button submitListing;
    private ImageView pictureView;
    private Switch freeSwitch;
    private TextView titleSelector;
    private EditText descriptionSelector;
    private EditText priceSelector;
    private String oldPrice;
    private String currentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fill_listing);

        camera = findViewById(R.id.camera);
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
        if(resultCode != RESULT_OK){
            pictureView.setTag(-1);
        }
        else if (requestCode == RESULT_LOAD_IMAGE){
            if(data == null){
                pictureView.setTag(-1);
                return;
            }
            Uri selectedImage = data.getData();
            pictureView.setImageURI(selectedImage);
            pictureView.setTag(selectedImage.hashCode());
        }
        else if (requestCode == RESULT_TAKE_PICTURE){
           pictureView.setImageURI(Uri.fromFile(new File(currentPhotoPath)));
        }
    }

    private void addListeners(){
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture();
            }
        });

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
        return !priceSelector.getText().toString().isEmpty();
    }

    private boolean checkTitle() {
        return !titleSelector.getText().toString().isEmpty();
    }

    private void submit() {
        Context context = getApplicationContext();
        if (!checkFields()) {
            Toast.makeText(context, INCORRECT_FIELDS_TEXT, Toast.LENGTH_SHORT).show();
        }
        else {
            final String newListingID = randomUUID().toString();
            SuccessCallback successCallback = new SuccessCallback() {
                @Override
                public void onCallback(boolean result) {
                    Intent SalesOverviewIntent = new Intent(FillListingActivity.this, SalesOverview.class);
                    startActivity(SalesOverviewIntent);
                }
            };
            Listing newListing = new Listing(titleSelector.getText().toString(), descriptionSelector.getText().toString(), priceSelector.getText().toString(), "test.user@epfl.ch");
            storeListing(newListing, newListingID, successCallback);
        }
    }

    //Function taken from https://developer.android.com/training/camera/photobasics
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    //Function taken from https://developer.android.com/training/camera/photobasics
    private  void takePicture(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                try{
                    Uri photoURI = FileProvider.getUriForFile(this,"ch.epfl.polybazaar.fileprovider", photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                }
                catch (IllegalArgumentException ex) {
                }
                startActivityForResult(takePictureIntent, RESULT_TAKE_PICTURE);
            }
        }
    }


}
