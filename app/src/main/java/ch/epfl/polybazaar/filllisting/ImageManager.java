package ch.epfl.polybazaar.filllisting;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.view.View;
import android.widget.TableRow;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.epfl.polybazaar.R;
import ch.epfl.polybazaar.UI.FillListing;
import ch.epfl.polybazaar.UI.SliderAdapter;
import ch.epfl.polybazaar.UI.SliderItem;
import ch.epfl.polybazaar.listing.Listing;

import static ch.epfl.polybazaar.widgets.MinimalAlertDialog.makeDialog;

public class ImageManager extends AppCompatActivity {
    //if you change this  variable, change R.string.max_num_images too
    public static final int MAXIMUM_IMAGES_NUMBER = 10;
    private ViewPager2 viewPager;
    private TableRow editButtons;
    private FillListing activity;

    public ImageManager(FillListing activity) {
        this.activity = activity;
        imageManagerFindViews();
    }

    private void imageManagerFindViews() {
        if (activity != null) {
            viewPager = activity.findViewById(R.id.viewPager);
            editButtons = activity.findViewById(R.id.editButtons);
        }
    }

    public void addImage(List<Bitmap> listImage, Bitmap image) {
        if(listImage.size() < MAXIMUM_IMAGES_NUMBER) {
            listImage.add(image);
            drawImages(listImage);
            viewPager.setCurrentItem(listImage.size() - 1, false);
        } else {
            makeDialog(activity, R.string.max_num_images);
        }
    }

    /**
     * recursive function to retrieve all images
     * @param listingID ID of the image
     */
    public void retrieveAllImages(String listingID, List<Bitmap> listImage) {
        Listing.fetch(listingID).addOnSuccessListener(listing -> {
            listing.fetchImages(activity.getApplicationContext()).addOnSuccessListener(bitmaps -> {
                if (bitmaps != null && !bitmaps.isEmpty()) {
                    drawImages(bitmaps);
                    updateViewPagerVisibility(bitmaps);
                    listImage.addAll(bitmaps);
                }
            });
        });
    }

    public void drawImages(List<Bitmap> listImage) {
        List<SliderItem> sliderItems = new ArrayList<>();
        for(Bitmap img: listImage) {
            sliderItems.add(new SliderItem(img));
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

    public void updateViewPagerVisibility(List<Bitmap> listImage) {
        if (listImage == null || listImage.isEmpty()) {
            viewPager.setVisibility(View.GONE);
            editButtons.setVisibility(View.GONE);
        } else {
            viewPager.setVisibility(View.VISIBLE);
            editButtons.setVisibility(View.VISIBLE);
        }
    }

    public void setFirst(List<Bitmap> listImage) {
        int index = viewPager.getCurrentItem();
        if(index == 0) {
            return;
        }
        Collections.swap(listImage, 0, index);
        drawImages(listImage);
    }

    public void rotateLeft(List<Bitmap> listImage) {
        if(listImage.isEmpty()) {
            return;
        }
        int index = viewPager.getCurrentItem();
        Bitmap bitmap = listImage.get(index);
        Matrix matrix = new Matrix();
        matrix.postRotate(-90);
        listImage.set(index, Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true));
        drawImages(listImage);
        viewPager.setCurrentItem(index, false);
    }

    public void deleteImage(List<Bitmap> listImage) {
        if(listImage.size() > 0)
            listImage.remove(viewPager.getCurrentItem());
        drawImages(listImage);
        updateViewPagerVisibility(listImage);
    }

}
