package ch.epfl.polybazaar;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ch.epfl.polybazaar.UI.SalesOverview;
import ch.epfl.polybazaar.category.Category;
import ch.epfl.polybazaar.category.CategoryRepository;
import ch.epfl.polybazaar.category.StringCategory;
import ch.epfl.polybazaar.listingImage.ListingImage;
import ch.epfl.polybazaar.database.callback.SuccessCallback;
import ch.epfl.polybazaar.listing.Listing;
import ch.epfl.polybazaar.litelisting.LiteListing;

import static ch.epfl.polybazaar.Utilities.convertBitmapToString;
import static ch.epfl.polybazaar.Utilities.convertBitmapToStringWithQuality;
import static ch.epfl.polybazaar.Utilities.convertDrawableToBitmap;
import static ch.epfl.polybazaar.Utilities.convertFileToString;
import static ch.epfl.polybazaar.Utilities.convertFileToStringWithQuality;
import static ch.epfl.polybazaar.Utilities.resizeBitmap;
import static ch.epfl.polybazaar.listing.ListingDatabase.storeListing;
import static ch.epfl.polybazaar.listingImage.ListingImageDatabase.storeListingImage;
import static ch.epfl.polybazaar.litelisting.LiteListingDatabase.addLiteListing;
import static java.util.UUID.randomUUID;

public class FillListingActivity extends AppCompatActivity {

    public static final int RESULT_LOAD_IMAGE = 1;
    public static final int RESULT_TAKE_PICTURE = 2;
    public static final String INCORRECT_FIELDS_TEXT = "One or more required fields are incorrect or uncompleted";
    private final String DEFAULT_SPINNER_TEXT = "Select category...";

    private Button uploadImage;
    private Button camera;
    private Button submitListing;
    private ImageView pictureView;
    private Switch freeSwitch;
    private TextView titleSelector;
    private EditText descriptionSelector;
    private EditText priceSelector;
    private Spinner categorySelector;
    private List<Spinner> spinnerList;
    private LinearLayout linearLayout;
    private String oldPrice;
    private String currentPhotoPath;
    private File photoFile;
    private List<String> listStingImage;
    private String stringImage = "";
    private String stringThumbnail = "";

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
        linearLayout = findViewById(R.id.fillListingLinearLayout);

        categorySelector = findViewById(R.id.categorySelector);
        spinnerList = new ArrayList<>();
        spinnerList.add(categorySelector);
        setupSpinner(categorySelector, categoriesWithDefaultText(CategoryRepository.categories));
        addListeners();

        listStingImage = new ArrayList<>();
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

            Bitmap convertedBitmap = convertDrawableToBitmap(pictureView.getDrawable());
            stringImage = convertBitmapToString(convertedBitmap);
            Bitmap resizedBitmap = resizeBitmap(convertedBitmap, (float)0.5, (float)0.5);
            stringThumbnail = convertBitmapToStringWithQuality(resizedBitmap, 10);
        }
        else if (requestCode == RESULT_TAKE_PICTURE){
           pictureView.setImageURI(Uri.fromFile(new File(currentPhotoPath)));

           stringImage = convertFileToString(photoFile);
           stringThumbnail = convertFileToStringWithQuality(photoFile, 10);
        }
        listStingImage.add(stringImage);
    }

    private void addListeners(){
        camera.setOnClickListener(v -> takePicture());
        freeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> freezePriceSelector(isChecked));
        uploadImage.setOnClickListener(v -> uploadImage());
        submitListing.setOnClickListener(v -> submit());
    }

    private void setupSpinner(Spinner spinner, List<Category> categories){
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categories);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                removeSpinnersBelow(spinner);
                Category selectedCategory = (Category)parent.getItemAtPosition(position);
                popsUpSubCategorySpinner(selectedCategory);
                linearLayout.invalidate();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Do nothing
            }
        });
    }

    private void removeSpinnersBelow(Spinner spinner){
        if(!spinner.equals(spinnerList.get(spinnerList.size()-1))){
            int numbersOfSpinnersToRemove = spinnerList.size() - spinnerList.indexOf(spinner) -1;
            int indexOfFirstSpinnerToRemove = linearLayout.indexOfChild(categorySelector) + spinnerList.indexOf(spinner) + 1;
            linearLayout.removeViews(indexOfFirstSpinnerToRemove, numbersOfSpinnersToRemove);
            spinnerList = spinnerList.subList(0, spinnerList.indexOf(spinner)+1);
        }
    }

    private void popsUpSubCategorySpinner(Category selectedCategory){
        if (selectedCategory.hasSubCategories()){
            Spinner subSpinner = new Spinner(getApplicationContext());
            spinnerList.add(subSpinner);
            linearLayout.addView(subSpinner, linearLayout.indexOfChild(categorySelector)+spinnerList.size()-1);
            setupSpinner(subSpinner, categoriesWithDefaultText(selectedCategory.subCategories()));
        }
    }

    private List<Category> categoriesWithDefaultText(List<Category> categories){
        List<Category> categoriesWithDefText = new ArrayList<>(categories);
        categoriesWithDefText.add(0, new StringCategory(DEFAULT_SPINNER_TEXT));
        return categoriesWithDefText;
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
        return checkTitle() && checkPrice() && checkCategory();
    }

    private boolean checkCategory() {
        return !spinnerList.get(spinnerList.size()-1).getSelectedItem().toString().equals(DEFAULT_SPINNER_TEXT);
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
            SuccessCallback successCallback = result -> {
                if(result) {
                    Toast toast = Toast.makeText(getApplicationContext(),"Offer successfully sent!",Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
                    toast.show();
                }
            };
            String category = spinnerList.get(spinnerList.size()-1).getSelectedItem().toString();

            Listing newListing = new Listing(titleSelector.getText().toString(), descriptionSelector.getText().toString(), priceSelector.getText().toString(), "test.user@epfl.ch", "", category);
            LiteListing newLiteListing = new LiteListing(newListingID, titleSelector.getText().toString(), priceSelector.getText().toString(), category, stringThumbnail);

            storeListing(newListing, newListingID, successCallback);

            //store images (current has a ref to the next)
            if(listStingImage.size() > 0) {
                String currentId = newListingID;
                String nextId;
                for(int i = 0; i < (listStingImage.size() - 1); i++) {
                    nextId = randomUUID().toString();
                    ListingImage newListingImage = new ListingImage(listStingImage.get(i), nextId);
                    storeListingImage(newListingImage, currentId, result -> {
                        if(result) {
                            Log.d("FirebaseDataStore", "successfully stored data");
                        } else {
                            Toast.makeText(getApplicationContext(), "An error occurred.", Toast.LENGTH_LONG).show();
                        }
                    });
                    currentId = nextId;
                }
                //store the last without refNextImg
                ListingImage newListingImage = new ListingImage(listStingImage.get(listStingImage.size() - 1), "");
                storeListingImage(newListingImage, currentId, result -> {});
            }

            addLiteListing(newLiteListing, result -> {
                //TODO: Check the result to be true
            });
            Intent SalesOverviewIntent = new Intent(FillListingActivity.this, SalesOverview.class);
            startActivity(SalesOverviewIntent);
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
            photoFile = null;
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
