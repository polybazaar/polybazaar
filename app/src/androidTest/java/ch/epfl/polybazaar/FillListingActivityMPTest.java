package ch.epfl.polybazaar;


import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class FillListingActivityMPTest {

    @Rule
    public ActivityTestRule<FillListingActivity> mActivityTestRule = new ActivityTestRule<>(FillListingActivity.class);

    @Test
    public void fillListingActivityMPTest() {
        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.addMP), withText("Define Meeting Point"),
                        childAtPosition(
                                allOf(withId(R.id.fillListingLinearLayout),
                                        childAtPosition(
                                                withClassName(is("android.widget.ScrollView")),
                                                0)),
                                10)));
        appCompatButton.perform(scrollTo(), click());

        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.confirmMP), withText("Validate"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.RelativeLayout")),
                                        1),
                                0),
                        isDisplayed()));
        appCompatButton2.perform(click());

        ViewInteraction textView = onView(
                allOf(withId(R.id.MPStatus), withText("Meeting Point not defined"),
                        childAtPosition(
                                allOf(withId(R.id.fillListingLinearLayout),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(android.widget.ScrollView.class),
                                                0)),
                                11),
                        isDisplayed()));
        textView.check(matches(withText("Meeting Point not defined")));
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
