package ch.epfl.polybazaar.endToEnd;


import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.DataInteraction;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.polybazaar.MainActivity;
import ch.epfl.polybazaar.R;
import ch.epfl.polybazaar.login.AuthenticatorFactory;
import ch.epfl.polybazaar.login.MockAuthenticator;
import ch.epfl.polybazaar.login.MockPhoneSettings;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static ch.epfl.polybazaar.database.datastore.DataStoreFactory.useMockDataStore;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class MeetingPointTest {

    String EMAIL = "othereuser.test@epfl.ch";
    String NICKNAME = "otheruuser";
    String PASSWORD = "abcdef";

    @Rule
    public ActivityTestRule mActivityTestRule = new ActivityTestRule<MainActivity>(MainActivity.class) {
        @Override
        protected void beforeActivityLaunched() {
            AuthenticatorFactory.setDependency(MockAuthenticator.getInstance());
        }

        @Override
        protected void afterActivityFinished() {
            MockAuthenticator.getInstance().reset();
            MockPhoneSettings.getInstance().setAirPlaneMode(false);
        }
    };

    @Before
    public void init() {
        useMockDataStore();
        Activity activityUnderTest = mActivityTestRule.getActivity();
        activityUnderTest.sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
    }

    public void createAccountAndBackToLogin(String email, String nickname, String password) {
        clickButton(ViewMatchers.withId(R.id.signUpButton));

        fillAndSubmitSignUp(email, nickname, password, password);

        clickButton(withId(R.id.sendLinkButton));
        clickButton(withId(R.id.reloadButton));
        clickButton(withId(R.id.signOutButton));
    }

    private void fillAndSubmitSignIn(String email, String password) {
        typeInput(withId(R.id.emailInput), email);
        typeInput(withId(R.id.passwordInput), password);
        clickButton(withId(R.id.loginButton));
    }

    private void fillAndSubmitSignUp(String email, String nickname, String password, String confirm) {
        typeInput(withId(R.id.emailInput), email);
        typeInput(withId(R.id.nicknameInput), nickname);
        typeInput(withId(R.id.passwordInput), password);
        typeInput(withId(R.id.confirmPasswordInput), confirm);
        clickButton(withId(R.id.submitButton));
    }

    private void typeInput(Matcher<View> object, String text) {
        onView(object).perform(typeText(text)).perform(closeSoftKeyboard());
    }

    private void clickButton(Matcher<View> object) {
        onView(object).perform(click());
    }

    @Test
    public void meetingPointTest() {
        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.authenticationButton), withText("Sign in"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                2),
                        isDisplayed()));
        appCompatButton.perform(click());

        createAccountAndBackToLogin(EMAIL, NICKNAME, PASSWORD);
        fillAndSubmitSignIn(EMAIL, PASSWORD);

        ViewInteraction appCompatButton3 = onView(
                allOf(withId(R.id.toMainButton), withText("Go to main page"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                2),
                        isDisplayed()));
        appCompatButton3.perform(click());

        ViewInteraction appCompatButton4 = onView(
                allOf(withId(R.id.addListing), withText("Add new listing"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        appCompatButton4.perform(click());

        ViewInteraction appCompatEditText5 = onView(
                allOf(withId(R.id.titleSelector),
                        childAtPosition(
                                allOf(withId(R.id.fillListingLinearLayout),
                                        childAtPosition(
                                                withClassName(is("android.widget.ScrollView")),
                                                0)),
                                3)));
        appCompatEditText5.perform(scrollTo(), replaceText("t"), closeSoftKeyboard());

        ViewInteraction appCompatSpinner = onView(
                allOf(withId(R.id.categorySelector),
                        childAtPosition(
                                allOf(withId(R.id.fillListingLinearLayout),
                                        childAtPosition(
                                                withClassName(is("android.widget.ScrollView")),
                                                0)),
                                5)));
        appCompatSpinner.perform(scrollTo(), click());

        DataInteraction appCompatCheckedTextView = onData(anything())
                .inAdapterView(childAtPosition(
                        withClassName(is("android.widget.PopupWindow$PopupBackgroundView")),
                        0))
                .atPosition(1);
        appCompatCheckedTextView.perform(click());

        ViewInteraction appCompatEditText6 = onView(
                allOf(withId(R.id.descriptionSelector),
                        childAtPosition(
                                allOf(withId(R.id.fillListingLinearLayout),
                                        childAtPosition(
                                                withClassName(is("android.widget.ScrollView")),
                                                0)),
                                7)));
        appCompatEditText6.perform(scrollTo(), replaceText("u"), closeSoftKeyboard());

        ViewInteraction appCompatEditText7 = onView(
                allOf(withId(R.id.priceSelector),
                        childAtPosition(
                                allOf(withId(R.id.fillListingLinearLayout),
                                        childAtPosition(
                                                withClassName(is("android.widget.ScrollView")),
                                                0)),
                                9)));
        appCompatEditText7.perform(scrollTo(), replaceText("3"), closeSoftKeyboard());

        ViewInteraction appCompatButton5 = onView(
                allOf(withId(R.id.addMP), withText("Define Meeting Point"),
                        childAtPosition(
                                allOf(withId(R.id.fillListingLinearLayout),
                                        childAtPosition(
                                                withClassName(is("android.widget.ScrollView")),
                                                0)),
                                10)));
        appCompatButton5.perform(scrollTo(), click());

        ViewInteraction appCompatButton6 = onView(
                allOf(withId(R.id.confirmMP), withText("Validate"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.RelativeLayout")),
                                        1),
                                0),
                        isDisplayed()));
        appCompatButton6.perform(click());

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

        ViewInteraction appCompatButton7 = onView(
                allOf(withId(R.id.submitListing), withText("Submit Listing"),
                        childAtPosition(
                                allOf(withId(R.id.fillListingLinearLayout),
                                        childAtPosition(
                                                withClassName(is("android.widget.ScrollView")),
                                                0)),
                                12)));
        appCompatButton7.perform(scrollTo(), click());

        ViewInteraction appCompatImageView = onView(
                allOf(childAtPosition(
                        childAtPosition(
                                withId(R.id.rvLiteListings),
                                0),
                        0),
                        isDisplayed()));
        appCompatImageView.perform(click());

        ViewInteraction appCompatButton10 = onView(
                allOf(withId(R.id.contactSel), withText("Contact the seller"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                6)));
        appCompatButton10.perform(scrollTo(), click());

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
