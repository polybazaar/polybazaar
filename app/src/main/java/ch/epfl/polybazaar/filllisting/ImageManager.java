package ch.epfl.polybazaar.filllisting;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.TableRow;
import android.widget.Toast;

import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import ch.epfl.polybazaar.R;
import ch.epfl.polybazaar.UI.SliderAdapter;
import ch.epfl.polybazaar.UI.SliderItem;
import ch.epfl.polybazaar.listingImage.ListingImage;
import static ch.epfl.polybazaar.filllisting.FillListingActivity.RESULT_LOAD_IMAGE;
import static ch.epfl.polybazaar.filllisting.FillListingActivity.RESULT_TAKE_PICTURE;
import static ch.epfl.polybazaar.utilities.ImageUtilities.convertBitmapToStringWithQuality;
import static ch.epfl.polybazaar.utilities.ImageUtilities.convertStringToBitmap;

class ImageManager {

    private ViewPager2 viewPager;
    private TableRow editButtons;
    private Activity activity;
    private File photoFile;

    ImageManager(Activity activity) {
        this.activity = activity;
        imageManagerFindViews();
    }

    private void imageManagerFindViews() {
        if (activity != null) {
            viewPager = activity.findViewById(R.id.viewPager);
            editButtons = activity.findViewById(R.id.editButtons);
        }
    }

    void addImage(List<String> listStringImage, String stringImage) {
        listStringImage.add(stringImage);
        drawImages(listStringImage);
        viewPager.setCurrentItem(listStringImage.size() - 1, false);
    }

    //Function taken from https://developer.android.com/training/camera/photobasics
    File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
    }

    //Function taken from https://developer.android.com/training/camera/photobasics
    void takePicture() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
            photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ignored) {
                Toast.makeText(activity, R.string.take_picture_fail, Toast.LENGTH_SHORT).show();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                try{
                    Uri photoURI = FileProvider.getUriForFile(activity,"ch.epfl.polybazaar.fileprovider", photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                } catch (IllegalArgumentException ignored) {
                    //Picture has been canceled by the user
                }
                activity.startActivityForResult(takePictureIntent, RESULT_TAKE_PICTURE);
            }
        }
    }

    File getPhotoFile() {
        return photoFile;
    }

    void uploadImage() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        activity.startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
    }

    /**
     * recursive function to retrieve all images
     * @param listingID ID of the image
     */
    void retrieveAllImages(List<String> listStringImage, List<String> listImageID, String listingID) {
        listImageID.add(listingID);
        ListingImage.fetch(listingID).addOnSuccessListener(result -> {
            //check if Listing contains image
            if(result == null) {
                drawImages(listStringImage);
                updateViewPagerVisibility(listStringImage);
                return;
            }
            listStringImage.add(result.getImage());
            if(!result.getRefNextImg().equals("")) {
                retrieveAllImages(listStringImage, listImageID, result.getRefNextImg());
            } else {
                drawImages(listStringImage);
                updateViewPagerVisibility(listStringImage);
            }
        });
    }

    void drawImages(List<String> listStringImage) {
        List<SliderItem> sliderItems = new ArrayList<>();
        for(String strImg: listStringImage) {
            sliderItems.add(new SliderItem(convertStringToBitmap(strImg)));
        }
        viewPager.setAdapter(new SliderAdapter(sliderItems, viewPager));
        viewPager.setClipToPadding(false);
        viewPager.setClipChildren(false);
        viewPager.setOffscreenPageLimit(3);
        viewPager.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer((page, position) -> {
            float r = 1 - Math.abs(position);
            page.setScaleY(0.85f + r * 0.15f);
        });
        viewPager.setPageTransformer(compositePageTransformer);
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

            }
        });
    }

    void updateViewPagerVisibility(List<String> listStringImage) {
        if (listStringImage == null || listStringImage.isEmpty()) {
            viewPager.setVisibility(View.GONE);
            editButtons.setVisibility(View.GONE);
        } else {
            viewPager.setVisibility(View.VISIBLE);
            editButtons.setVisibility(View.VISIBLE);
        }
    }

    void setFirst(List<String> listStringImage) {
        int index = viewPager.getCurrentItem();
        if(index == 0) {
            return;
        }
        Collections.swap(listStringImage, 0, index);
        drawImages(listStringImage);
    }

    void rotateLeft(List<String> listStringImage) {
        if(listStringImage.isEmpty()) {
            return;
        }
        int index = viewPager.getCurrentItem();
        Bitmap bitmap = convertStringToBitmap(listStringImage.get(index));

        Matrix matrix = new Matrix();
        matrix.postRotate(-90);
        listStringImage.set(index, convertBitmapToStringWithQuality(Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true), 100));

        drawImages(listStringImage);
        viewPager.setCurrentItem(index);
    }

    void deleteImage(List<String> listStringImage) {
        if(listStringImage.size() > 0)
            listStringImage.remove(viewPager.getCurrentItem());
        drawImages(listStringImage);
        updateViewPagerVisibility(listStringImage);
    }

}
