package ch.epfl.polybazaar.utilities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import ch.epfl.polybazaar.R;
import ch.epfl.polybazaar.widgets.permissions.PermissionRequest;

import static ch.epfl.polybazaar.utilities.ImageUtilities.convertBitmapToStringWithQuality;


/**
 * TO TAKE OR IMPORT AN IMAGE:
 * startActivityForResult with this activity as target and requestCode:
 * LOAD_IMAGE to import picture from library
 * TAKE_PICTURE to open the camera and take a picture
 * to retrieve the image:
 * upon onActivity result, if IMAGE_AVAILABLE is true, call:
 * String stringImage = this.getSharedPreferences(PICTURE_PREFS, MODE_PRIVATE).getString(STRING_IMAGE, null);
 */
public class ImageTaker extends AppCompatActivity {

    public static final int QUALITY = 25;
    public static final String STRING_IMAGE = "bitmap_image";
    public static final String PICTURE_PREFS = "bitmap_prefs";
    public static final String CODE = "request_code";
    public static final int LOAD_IMAGE = 3;
    public static final int TAKE_IMAGE = 4;
    private static final int RESULT_LOAD_IMAGE = 1;
    private static final int RESULT_TAKE_PICTURE = 2;
    public static final String IMAGE_AVAILABLE = "image_present";
    private File photoFile;
    private Bitmap image;
    private PermissionRequest cameraPermissionRequest;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK) {
            if (requestCode == RESULT_LOAD_IMAGE) {
                if (data == null) {
                    failure();
                }
                if (data != null) {
                    Uri selectedImage = data.getData();
                    Bitmap bitmap;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                        image = bitmap;
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(this, R.string.unable_load_image, Toast.LENGTH_SHORT).show();
                        failure();
                    }
                    success();
                } else {
                    failure();
                }
            } else if (requestCode == RESULT_TAKE_PICTURE) {
                ByteArrayOutputStream bin = new ByteArrayOutputStream();
                image = BitmapFactory.decodeFile(getPhotoFile().getAbsolutePath());
                image.compress(Bitmap.CompressFormat.JPEG, QUALITY, bin);
                image = BitmapFactory.decodeStream(new ByteArrayInputStream(bin.toByteArray()));
                success();
            } else {
                failure();
            }
        } else {
            failure();
        }
    }

    private void success() {
        Intent returnIntent = new Intent();
        SharedPreferences myPrefs = this.getSharedPreferences(PICTURE_PREFS, MODE_PRIVATE);
        myPrefs.edit().putString(STRING_IMAGE, convertBitmapToStringWithQuality(image, QUALITY)).apply();
        returnIntent.putExtra(IMAGE_AVAILABLE, true);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    private void failure() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(IMAGE_AVAILABLE, false);
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        int requestCode = getIntent().getIntExtra(CODE, -1);
        if (requestCode == TAKE_IMAGE) {
            checkCameraPermission();
        } else if (requestCode == LOAD_IMAGE) {
            takeLibraryPicture();
        } else {
            failure();
        }
    }

    //Function taken from https://developer.android.com/training/camera/photobasics
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "PNG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(
                imageFileName,  /* prefix */
                ".png",         /* suffix */
                storageDir      /* directory */
        );
    }

    //Function taken from https://developer.android.com/training/camera/photobasics
    private void takeCameraPicture() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ignored) {
                Toast.makeText(this, R.string.take_picture_fail, Toast.LENGTH_SHORT).show();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                try{
                    Uri photoURI = FileProvider.getUriForFile(this,"ch.epfl.polybazaar.fileprovider", photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                } catch (IllegalArgumentException ignored) {
                    //Picture has been canceled by the user
                }
                this.startActivityForResult(takePictureIntent, RESULT_TAKE_PICTURE);
            }
        }
    }

    private void takeLibraryPicture() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        this.startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
    }

    private File getPhotoFile() {
        return photoFile;
    }

    private void checkCameraPermission(){
        cameraPermissionRequest = new PermissionRequest(this, "CAMERA", "Camera access is required to take pictures", null, result -> {
            if (result) {
                takeCameraPicture();
            } else {
                finish();
            }
        });
        cameraPermissionRequest.assertPermission();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        cameraPermissionRequest.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}
