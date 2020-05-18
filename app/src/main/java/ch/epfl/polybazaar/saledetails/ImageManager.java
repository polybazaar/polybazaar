package ch.epfl.polybazaar.saledetails;

import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.polybazaar.R;
import ch.epfl.polybazaar.UI.SaleDetails;
import ch.epfl.polybazaar.UI.SliderAdapter;
import ch.epfl.polybazaar.UI.SliderItem;

import static ch.epfl.polybazaar.utilities.ImageUtilities.convertStringToBitmap;

public class ImageManager {

    SaleDetails activity;

    public ImageManager(SaleDetails activity) {
        this.activity = activity;
    }

    /**
     * Displays the images on the ViewPager
     * @param listStringImage list of StringImages to Display
     */
    public void drawImages(List<String> listStringImage) {
        ViewPager2 viewPager = activity.findViewById(R.id.viewPagerImageSlider);
        activity.runOnUiThread (()-> {
            List<SliderItem> sliderItems = new ArrayList<>();
            if (!listStringImage.isEmpty()) {
                viewPager.setVisibility(View.VISIBLE);
                activity.findViewById(R.id.loadingImage).setVisibility(View.GONE);
                activity.findViewById(R.id.pageNumber).setVisibility(View.VISIBLE);
                for (String strImg : listStringImage) {
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
                        TextView textPageNumber = activity.findViewById(R.id.pageNumber);
                        textPageNumber.setText(String.format("%s/%s", Integer.toString(viewPager.getCurrentItem() + 1), Integer.toString(listStringImage.size())));
                        textPageNumber.setGravity(Gravity.CENTER);
                    }
                });
            } else {
                activity.findViewById(R.id.imageDisplay).setVisibility(View.GONE);
            }

            viewPager.setOnClickListener(v -> {
                Toast.makeText(activity.getApplicationContext(), "UN BON CLICK SES MORTS", Toast.LENGTH_SHORT).show();
            });

        });
    }
}
