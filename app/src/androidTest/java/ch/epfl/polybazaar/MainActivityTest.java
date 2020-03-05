package ch.epfl.polybazaar;

import android.content.Intent;

import androidx.test.espresso.intent.Intents;
import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread;
import static org.hamcrest.core.IsNot.not;

public class MainActivityTest{
    private Intent intent;

    @Rule
    public final ActivityTestRule<MainActivity> activityRule =
            new ActivityTestRule<>(
                    MainActivity.class,
                    true,
                    false);

    public void init() {
        Intents.init();
        intent = new Intent();
        activityRule.launchActivity(intent);
    }


    public void finalize() {
        Intents.release();;
    }

    @Test
    public void testStartSaleOverview() throws Throwable {
        init();
        //Use runOnUiThread because of the small screen on travis tests
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activityRule.getActivity().findViewById(R.id.sale_overview)
                        .performClick();
            }
        });
        intended(hasComponent(SalesOverview.class.getName()));
        finalize();
    }

    @Test
    public void testStartFillListingActivity() throws Throwable {
        init();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activityRule.getActivity().findViewById(R.id.add_listing)
                        .performClick();
            }
        });
        intended(hasComponent(FillListingActivity.class.getName()));
        finalize();
    }

    @Test
    public void testStartSignIn() throws Throwable {
        init();
        //TODO uncomment the code below
        /*runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activityRule.getActivity().findViewById(R.id.add_listing)
                         .performClick();
            }
        });
        // TODO complete
        intended(hasComponent(<yourClass>.class.getName()));*/

        //TODO remove code below
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activityRule.getActivity().findViewById(R.id.sign_in)
                        .performClick();
            }
        });

        onView(withText("This functionality is not implemented yet"))
                .inRoot(withDecorView(not(activityRule.getActivity().getWindow().getDecorView())))
                .check(matches(isDisplayed()));
        // Don't remove this line
        finalize();
    }
}