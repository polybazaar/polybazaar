package ch.epfl.polybazaar;

import android.content.Intent;

import androidx.test.espresso.intent.Intents;
import androidx.test.rule.ActivityTestRule;

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

    @Rule
    public final ActivityTestRule<MainActivity> activityRule =
            new ActivityTestRule<>(
                    MainActivity.class,
                    true,
                    false);

    @Test
    public void testStartSaleOverview() throws Throwable {
        Intents.init();
        Intent intent = new Intent();

        activityRule.launchActivity(intent);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activityRule.getActivity().findViewById(R.id.saleOverview)
                        .performClick();
            }
        });
        intended(hasComponent(SalesOverview.class.getName()));
        Intents.release();
    }

    @Test
    public void testStartFillListingActivity() throws Throwable {
        Intents.init();
        Intent intent = new Intent();

        activityRule.launchActivity(intent);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activityRule.getActivity().findViewById(R.id.addListing)
                        .performClick();
            }
        });
        intended(hasComponent(FillListingActivity.class.getName()));
        Intents.release();
    }

    @Test
    public void testStartSignIn() throws Throwable {
        //TODO uncomment the code below
        /*Intents.init();
        Intent intent = new Intent();
        activityRule.launchActivity(intent);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activityRule.getActivity().findViewById(R.id.addListing)
                         .performClick();
            }
        });
        // TODO complete
        intended(hasComponent(<yourClass>.class.getName()));
        Intents.release();*/

        //TODO remove code below
        Intent intent = new Intent();

        activityRule.launchActivity(intent);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activityRule.getActivity().findViewById(R.id.signIn)
                        .performClick();
            }
        });

        onView(withText("This functionality is not implemented yet"))
                .inRoot(withDecorView(not(activityRule.getActivity().getWindow().getDecorView())))
                .check(matches(isDisplayed()));
    }
}