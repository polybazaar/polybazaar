package ch.epfl.polybazaar;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;

import static androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread;
import static org.junit.Assert.assertEquals;

// TODO: how to test addListingView? method returns void and has side effects on objects whose reference can't be fetched?

public class SalesOverviewTest {

    @Rule
    public final ActivityTestRule<SalesOverview> activityRule =
            new ActivityTestRule<>(
                    SalesOverview.class,
                    true,
                    false);


    @Test
    public void testSetScrollView() {
        Intent intent = new Intent();
        activityRule.launchActivity(intent);

        ViewGroup.LayoutParams layoutParams = activityRule.getActivity().scroll.getLayoutParams();
        assertEquals(layoutParams.width, ViewGroup.LayoutParams.MATCH_PARENT);
        assertEquals(layoutParams.height, ViewGroup.LayoutParams.MATCH_PARENT);
    }


    @Test
    public void testSetLinearLayout() {
        Intent intent = new Intent();
        activityRule.launchActivity(intent);

        ViewGroup.LayoutParams layoutParams = activityRule.getActivity().linearLayout.getLayoutParams();
        assertEquals(layoutParams.width, ViewGroup.LayoutParams.MATCH_PARENT);
        assertEquals(layoutParams.height, ViewGroup.LayoutParams.WRAP_CONTENT);
        int layoutOrientation = activityRule.getActivity().linearLayout.getOrientation();
        assertEquals(layoutOrientation, LinearLayout.VERTICAL);
    }


    @Test
    public void testLinearLayoutAddedToScrollView() {
        Intent intent = new Intent();
        activityRule.launchActivity(intent);

        View testLayout = activityRule.getActivity().scroll.getChildAt(0);
        assertEquals(activityRule.getActivity().linearLayout, testLayout);
    }


    @Test
    public void testScrollViewAddedToConstraintLayout() {
        Intent intent = new Intent();
        activityRule.launchActivity(intent);

        ConstraintLayout constraintLayout = activityRule.getActivity().findViewById(R.id.rootContainer);
        assertEquals(activityRule.getActivity().scroll, constraintLayout.getChildAt(0));
    }


    @Test
    public void testCreateView() throws Throwable {
        Intent intent = new Intent();
        activityRule.launchActivity(intent);

        final int width = 30;
        final int height = 10;
        final int boxWeight = 1;
        final float textSize = (float)8.0;
        final boolean ellipsize = true;
        final boolean clickable = true;
        final String content = "Nintendo Switch";

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView textView = activityRule.getActivity().createView(width, height, boxWeight, textSize, ellipsize, clickable, content);
                assertEquals(30, textView.getLayoutParams().width);
                assertEquals(10, textView.getLayoutParams().height);
                assertEquals("Nintendo Switch", textView.getText());
                assertEquals(TextUtils.TruncateAt.END, textView.getEllipsize());
                assertEquals(true, textView.isClickable());
                assertEquals(true, textView.hasOnClickListeners());
                assertEquals(1, textView.getMaxLines());
            }
        });
    }
}
