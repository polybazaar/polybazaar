package ch.epfl.polybazaar.filllisting;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.viewpager2.widget.ViewPager2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ch.epfl.polybazaar.R;
import ch.epfl.polybazaar.UI.SalesOverview;
import ch.epfl.polybazaar.category.Category;
import ch.epfl.polybazaar.category.CategoryRepository;
import ch.epfl.polybazaar.category.StringCategory;
import ch.epfl.polybazaar.listing.Listing;
import ch.epfl.polybazaar.map.MapsActivity;
import ch.epfl.polybazaar.widgets.NoConnectionForListingDialog;
import ch.epfl.polybazaar.widgets.NoticeDialogListener;
import ch.epfl.polybazaar.widgets.permissions.PermissionRequest;

import static ch.epfl.polybazaar.map.MapsActivity.GIVE_LAT_LNG;
import static ch.epfl.polybazaar.map.MapsActivity.LAT;
import static ch.epfl.polybazaar.map.MapsActivity.LNG;
import static ch.epfl.polybazaar.map.MapsActivity.NOLAT;
import static ch.epfl.polybazaar.map.MapsActivity.NOLNG;
import static ch.epfl.polybazaar.map.MapsActivity.VALID;
import static ch.epfl.polybazaar.utilities.ImageUtilities.convertBitmapToStringWithQuality;
import static ch.epfl.polybazaar.utilities.ImageUtilities.convertFileToStringWithQuality;

public class FillListingActivity extends AppCompatActivity implements NoticeDialogListener {

    public static final int RESULT_LOAD_IMAGE = 1;
    public static final int RESULT_TAKE_PICTURE = 2;
    public static final int RESULT_ADD_MP = 3;
    private final int QUALITY = 10;

    private Button setImageFirst;
    private Button rotateImageLeft;
    private Button deleteImage;
    private Button modifyImage;
    private ImageManager imageManager;
    private ListingManager listingManager;
    private CategoryManager categoryManager;
    private Button uploadImage;
    private Button camera;
    private Button submitListing;
    private Button addMP;
    private ImageView pictureView;
    private Switch freeSwitch;
    private TextView titleSelector;
    private TextView meetingPointStatus;
    private EditText descriptionSelector;
    private EditText priceSelector;
    private Spinner categorySelector;
    private List<Spinner> spinnerList;
    private String oldPrice;
    private List<String> listStringImage;
    //only used for edit to delete all images
    private List<String> listImageID;
    private String stringImage = "";
    private Category traversingCategory;
    private String stringThumbnail = "";
    private double lat = NOLAT;
    private double lng = NOLNG;

    private PermissionRequest cameraPermissionRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fill_listing);
        imageManager = new ImageManager(this);
        listingManager = new ListingManager(this);
        categoryManager = new CategoryManager(this);
        setImageFirst = findViewById(R.id.setFirst);
        rotateImageLeft = findViewById(R.id.rotateLeft);
        deleteImage = findViewById(R.id.deleteImage);
        modifyImage = findViewById(R.id.modifyImage);
        camera = findViewById(R.id.camera);
        freeSwitch = findViewById(R.id.freeSwitch);
        uploadImage = findViewById(R.id.uploadImage);
        submitListing = findViewById(R.id.submitListing);
        titleSelector = findViewById(R.id.titleSelector);
        descriptionSelector = findViewById(R.id.descriptionSelector);
        priceSelector = findViewById(R.id.priceSelector);
        addMP = findViewById(R.id.addMP);
        meetingPointStatus = findViewById(R.id.meetingPointStatus);
        pictureView = findViewById(R.id.picturePreview);
        categorySelector = findViewById(R.id.categorySelector);
        spinnerList = new ArrayList<>();
        spinnerList.add(categorySelector);
        categoryManager.setupSpinner(categorySelector, CategoryRepository.categories, spinnerList, traversingCategory);
        traversingCategory = categoryManager.getTraversingCategory();
        spinnerList = categoryManager.getSpinnerList();
        listStringImage = new ArrayList<>();
        listImageID = new ArrayList<>();
        boolean edit = fillFieldsIfEdit();
        addListeners(edit);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Tags are set only for testing purposes using Espresso
        if(resultCode != Activity.RESULT_OK){
            pictureView.setTag(-1);
        }
        else if (requestCode == RESULT_LOAD_IMAGE){
            if(data == null){
                pictureView.setTag(-1);
                return;
            }
            Uri selectedImage = data.getData();
            Bitmap bitmap;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, R.string.unable_load_image, Toast.LENGTH_SHORT).show();
                return;
            }

            stringImage = convertBitmapToStringWithQuality(bitmap, QUALITY);
            imageManager.addImage(listStringImage, stringImage);
        }
        else if (requestCode == RESULT_TAKE_PICTURE){
           stringImage = convertFileToStringWithQuality(imageManager.getPhotoFile(), QUALITY);
           imageManager.addImage(listStringImage, stringImage);
        }
        else if (requestCode == RESULT_ADD_MP) {
            if (data != null) {
                if (data.getBooleanExtra(VALID, false)) {
                    lng = data.getDoubleExtra(LNG, NOLNG);
                    lat = data.getDoubleExtra(LAT, NOLAT);
                    addMP.setText(R.string.change_MP);
                    meetingPointStatus.setText(R.string.MP_ok);
                } else {
                    lng = NOLNG;
                    lat = NOLAT;
                    addMP.setText(R.string.add_MP);
                    meetingPointStatus.setText(R.string.MP_nok);
                }
            }
        }
    }

    private void addListeners(boolean edit){
        camera.setOnClickListener(v -> checkCameraPermission());
        freeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> freezePriceSelector(isChecked));
        uploadImage.setOnClickListener(v -> imageManager.uploadImage());
        addMP.setOnClickListener(v -> {
            Intent defineMP = new Intent(this, MapsActivity.class);
            defineMP.putExtra(GIVE_LAT_LNG, false);
            defineMP.putExtra(LAT, lat);
            defineMP.putExtra(LNG, lng);
            startActivityForResult(defineMP, RESULT_ADD_MP);
        });
        setImageFirst.setOnClickListener(v -> imageManager.setFirst(listStringImage));
        rotateImageLeft.setOnClickListener(v -> imageManager.rotateLeft(listStringImage));
        deleteImage.setOnClickListener(v -> imageManager.deleteImage(listStringImage));
        modifyImage.setOnClickListener(v -> {
            if (setImageFirst.getVisibility() == View.INVISIBLE) {
                showImagesButtons();
            } else {
                hideImagesButtons();
            }
        });
        if(!edit){
            submitListing.setOnClickListener(v -> {
                if (!listingManager.submit(spinnerList, listStringImage, stringThumbnail, lat, lng)) {
                    NoConnectionForListingDialog dialog = new NoConnectionForListingDialog();
                    dialog.show(getSupportFragmentManager(), "noConnectionDialog");
                }
            });
        }
        else{
            submitListing.setText(R.string.edit);
            submitListing.setOnClickListener(v ->
                    listingManager.deleteOldListingAndSubmitNewOne(spinnerList, listStringImage, stringThumbnail, lat, lng, listImageID));
        }
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

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        if(dialog instanceof NoConnectionForListingDialog){
            Listing newListing = listingManager.makeListing(lat, lng, spinnerList);
            if (newListing != null) {
                listingManager.createAndSendListing(newListing, listStringImage, stringThumbnail);
                Intent SalesOverviewIntent = new Intent(FillListingActivity.this, SalesOverview.class);
                startActivity(SalesOverviewIntent);
            }
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        if(dialog instanceof NoConnectionForListingDialog){
            //do nothing
        }
    }

    private boolean fillFieldsIfEdit() {
        Bundle bundle = getIntent().getExtras();
        if(bundle == null){
            return false;
        }
        String listingID = bundle.getString("listingID", "-1");
        if (listingID.equals("-1")){
            return false;
        }
        Listing listing = (Listing)bundle.get("listing");
        if(listing == null) {
            return false;
        }
        imageManager.retrieveAllImages(listStringImage, listImageID, listingID);
        titleSelector.setText(listing.getTitle());
        descriptionSelector.setText(listing.getDescription());
        freeSwitch.setChecked(listing.getPrice().equals("0.0"));
        priceSelector.setText(listing.getPrice());
        Category editedCategory = new StringCategory(listing.getCategory());
        traversingCategory = CategoryRepository.getCategoryContaining(editedCategory);
        categorySelector.setSelection(CategoryRepository.indexOf(traversingCategory)+1);
        lat = listing.getLatitude();
        lng = listing.getLongitude();
        if (lat != NOLAT && lng != NOLNG) {
            addMP.setText(R.string.change_MP);
            meetingPointStatus.setText(R.string.MP_ok);
        }
        return true;
    }

    private void checkCameraPermission(){
        cameraPermissionRequest = new PermissionRequest(this, "CAMERA", "Camera access is required to take pictures", null, result -> {
            if (result) imageManager.takePicture();
        });
        cameraPermissionRequest.assertPermission();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        cameraPermissionRequest.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void hideImagesButtons() {
        setImageFirst.setVisibility(View.INVISIBLE);
        rotateImageLeft.setVisibility(View.INVISIBLE);
        deleteImage.setVisibility(View.INVISIBLE);
    }

    private void showImagesButtons() {
        setImageFirst.setVisibility(View.VISIBLE);
        rotateImageLeft.setVisibility(View.VISIBLE);
        deleteImage.setVisibility(View.VISIBLE);
    }

    /**
     * return the current StringImage displayed or null if there is no image
     * @return
     */
    public String getCurrentStringImage() {
        if(listStringImage.size() > 0) {
            return listStringImage.get(((ViewPager2)findViewById(R.id.viewPager)).getCurrentItem());
        } else {
            return null;
        }
    }

}
