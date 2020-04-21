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
import ch.epfl.polybazaar.category.NodeCategory;
import ch.epfl.polybazaar.category.RootCategoryFactory;
import ch.epfl.polybazaar.listing.Listing;
import ch.epfl.polybazaar.map.MapsActivity;
import ch.epfl.polybazaar.widgets.ImportImageDialog;
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

    private Button setMainImage;
    private Button rotateImage;
    private Button deleteImage;
    private ImageManager imageManager;
    private ListingManager listingManager;
    private CategoryManager categoryManager;
    private Button addImages;
    private Button selectCategory;
    private Button submitListing;
    private Button addMP;
    private ImageView pictureView;
    private TextView titleSelector;
    private EditText descriptionSelector;
    private EditText priceSelector;
    private Spinner categorySelector;
    private List<Spinner> spinnerList;
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
        setMainImage = findViewById(R.id.setMain);
        rotateImage = findViewById(R.id.rotate);
        deleteImage = findViewById(R.id.deleteImage);
        addImages = findViewById(R.id.addImage);
        submitListing = findViewById(R.id.submitListing);
        titleSelector = findViewById(R.id.titleSelector);
        descriptionSelector = findViewById(R.id.descriptionSelector);
        priceSelector = findViewById(R.id.priceSelector);
        selectCategory = findViewById(R.id.selectCategory);
        addMP = findViewById(R.id.addMP);
        pictureView = findViewById(R.id.picturePreview);

        /**
         * FOR TESTING PURPOSES ONLY:
         */
        categorySelector = findViewById(R.id.categorySelector);

        spinnerList = new ArrayList<>();
        spinnerList.add(categorySelector);
        RootCategoryFactory.useJSONCategory(this);
        categoryManager.setupSpinner(categorySelector, RootCategoryFactory.getDependency().subCategories() , spinnerList, traversingCategory);
        traversingCategory = categoryManager.getTraversingCategory();
        spinnerList = categoryManager.getSpinnerList();
        /**
         * ==========================
         */

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
            imageManager.updateViewPagerVisibility(listStringImage);
        }
        else if (requestCode == RESULT_TAKE_PICTURE){
           stringImage = convertFileToStringWithQuality(imageManager.getPhotoFile(), QUALITY);
           imageManager.addImage(listStringImage, stringImage);
           imageManager.updateViewPagerVisibility(listStringImage);
        }
        else if (requestCode == RESULT_ADD_MP) {
            if (data != null) {
                if (data.getBooleanExtra(VALID, false)) {
                    lng = data.getDoubleExtra(LNG, NOLNG);
                    lat = data.getDoubleExtra(LAT, NOLAT);
                    addMP.setText(R.string.change_MP);
                } else {
                    lng = NOLNG;
                    lat = NOLAT;
                    addMP.setText(R.string.add_MP);
                }
            }
        }
    }

    private void addListeners(boolean edit){
        addImages.setOnClickListener(v -> {
            ImportImageDialog dialog = new ImportImageDialog();
            dialog.show(getSupportFragmentManager(), "select image import");
        });
        selectCategory.setOnClickListener(v -> {
            // TODO : open category selection activity
            Toast.makeText(this, R.string.not_implemented, Toast.LENGTH_SHORT).show();
        });
        addMP.setOnClickListener(v -> {
            Intent defineMP = new Intent(this, MapsActivity.class);
            defineMP.putExtra(GIVE_LAT_LNG, false);
            defineMP.putExtra(LAT, lat);
            defineMP.putExtra(LNG, lng);
            startActivityForResult(defineMP, RESULT_ADD_MP);
        });
        setMainImage.setOnClickListener(v -> imageManager.setFirst(listStringImage));
        rotateImage.setOnClickListener(v -> imageManager.rotateLeft(listStringImage));
        deleteImage.setOnClickListener(v -> imageManager.deleteImage(listStringImage));
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
        if (dialog instanceof ImportImageDialog) {
            checkCameraPermission();
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        if(dialog instanceof NoConnectionForListingDialog){
            //do nothing
        }
        if (dialog instanceof ImportImageDialog) {
            imageManager.uploadImage();
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
        priceSelector.setText(listing.getPrice());
        lat = listing.getLatitude();
        lng = listing.getLongitude();
        if (lat != NOLAT && lng != NOLNG) {
            addMP.setText(R.string.change_MP);
        }

        /**
         * FOR TESTING PURPOSES ONLY:
         */
        Category editedCategory = new NodeCategory(listing.getCategory());
        Category root = RootCategoryFactory.getDependency();
        traversingCategory = root.getSubCategoryContaining(editedCategory);
        categorySelector.setSelection(root.indexOf(traversingCategory)+1);
        /**
         * ==========================
         */

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
        setMainImage.setVisibility(View.INVISIBLE);
        rotateImage.setVisibility(View.INVISIBLE);
        deleteImage.setVisibility(View.INVISIBLE);
    }

    private void showImagesButtons() {
        setMainImage.setVisibility(View.VISIBLE);
        rotateImage.setVisibility(View.VISIBLE);
        deleteImage.setVisibility(View.VISIBLE);
    }

    /**
     * FOR TESTING PURPOSES ONLY:
     */
    // @return the current StringImage displayed or null if there is no image
    public String getCurrentStringImage() {
        if(listStringImage.size() > 0) {
            return listStringImage.get(((ViewPager2)findViewById(R.id.viewPager)).getCurrentItem());
        } else {
            return null;
        }
    }
    /**
     * ==========================
     */

}
