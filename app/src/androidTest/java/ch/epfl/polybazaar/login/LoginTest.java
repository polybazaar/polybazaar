package ch.epfl.polybazaar.login;

import android.view.View;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.polybazaar.R;
import ch.epfl.polybazaar.utilities.InputValidity;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withTagValue;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static ch.epfl.polybazaar.database.datastore.DataStoreFactory.useMockDataStore;
import static ch.epfl.polybazaar.testingUtilities.SignInUtilities.createAccountAndBackToLoginFromLoginActivity;
import static ch.epfl.polybazaar.testingUtilities.SignInUtilities.fillAndSubmitSignUp;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;

public class LoginTest {
    public static final String EMAIL = "otheruser.test@epfl.ch";
    public static final String NICKNAME = "otheruser";
    public static final String PASSWORD = "AnotherStrongPassword69";

    @Rule
    public final ActivityTestRule<SignInActivity> mActivityRule =
            new ActivityTestRule<SignInActivity>(SignInActivity.class){
                @Override
                protected void beforeActivityLaunched() {
                    useMockDataStore();
                    AuthenticatorFactory.setDependency(MockAuthenticator.getInstance());
                    MockAuthenticator.getInstance().reset();
                }

                @Override
                protected void afterActivityFinished() {
                    MockAuthenticator.getInstance().reset();
                    MockPhoneSettings.getInstance().setAirPlaneMode(false);
                }
            };

    @Test
    public void loginWithUnregisteredUserFails() {
        fillAndSubmitSignIn(EMAIL, PASSWORD);
        onView(withText(R.string.verify_credentials)).check(matches(isDisplayed()));
    }

    @Test
    public void signUpProcessWorks() {
        createAccountAndBackToLoginFromLoginActivity(EMAIL, NICKNAME, PASSWORD);
        onView(withId(R.id.signInButton)).perform(click());
        fillAndSubmitSignIn(EMAIL, "aaaaaaaa");
        onView(withText(R.string.verify_credentials)).check(matches(isDisplayed()));

        clickButton(withText(R.string.alert_close));

        emptyInput(withId(R.id.emailInput));
        emptyInput(withId(R.id.passwordInput));

        fillAndSubmitSignIn(EMAIL, PASSWORD);
        assertThat(MockAuthenticator.getInstance().getCurrentUser().getEmail(), is(EMAIL));

    }


    @Test
    public void signUpWithExistingEmailFails() {
        createAccountAndBackToLoginFromLoginActivity(EMAIL, NICKNAME, PASSWORD);
        clickButton(withId(R.id.signUpButton));
        fillAndSubmitSignUp(EMAIL, NICKNAME, PASSWORD, PASSWORD);

        onView(withText(R.string.signup_error)).check(matches(isDisplayed()));
    }

    @Test
    public void signUpWithNonEpflPasswordFails() {
        String email = "test@gmail.com";
        clickButton(withId(R.id.signUpButton));
        fillAndSubmitSignUp(email, NICKNAME, PASSWORD, PASSWORD);

        assertThat(onView(allOf(withId(R.id.emailInputLayout), withTagValue(is(InputValidity.ERROR)))), is(not(nullValue())));
    }

    @Test
    public void signUpWithNonMatchingPasswordFails() {
        clickButton(withId(R.id.signUpButton));
        fillAndSubmitSignUp(EMAIL, NICKNAME, PASSWORD, "random");

        assertThat(onView(allOf(withId(R.id.confirmPasswordInputLayout), withTagValue(is(InputValidity.ERROR)))), is(not(nullValue())));
    }

    @Test
    public void signUpWithBadPassword() {
        clickButton(withId(R.id.signUpButton));
        fillAndSubmitSignUp(EMAIL, NICKNAME,"a", "a");

        assertThat(onView(allOf(withId(R.id.passwordInputLayout), withTagValue(is(InputValidity.ERROR)))), is(not(nullValue())));
    }

    @Test
    public void signInWithoutVerificationBlocked() {
        clickButton(withId(R.id.signUpButton));

        fillAndSubmitSignUp(EMAIL, NICKNAME, PASSWORD, PASSWORD);
        onView(withId(R.id.signOutButton)).perform(scrollTo(), click());
        fillAndSubmitSignIn(EMAIL, PASSWORD);

        onView(withText(R.string.email_not_verified)).check(matches(isDisplayed()));
    }

    @Test
    public void networkInterruptionFailsProperly() {
        clickButton(withId(R.id.signUpButton));
        fillAndSubmitSignUp(EMAIL, NICKNAME, PASSWORD, PASSWORD);

        MockPhoneSettings.getInstance().setAirPlaneMode(true);

        clickButton(withId(R.id.sendLinkButton));
        onView(withText(R.string.verification_email_fail))
                .inRoot(withDecorView(not(is(mActivityRule.getActivity().getWindow().getDecorView()))))
                .check(matches(isDisplayed()));

        clickButton(withText(R.string.alert_close));

        clickButton(withId(R.id.reloadButton));

        onView(withText(R.string.reload_fail))
                .inRoot(withDecorView(not(is(mActivityRule.getActivity().getWindow().getDecorView()))))
                .check(matches(isDisplayed()));

        clickButton(withText(R.string.alert_close));
    }

    private void fillAndSubmitSignIn(String email, String password) {
        typeInput(withId(R.id.emailInput), email);
        typeInput(withId(R.id.passwordInput), password);
        closeSoftKeyboard();
        clickButton(withId(R.id.loginButton));
    }

    private void typeInput(Matcher<View> object, String text) {
        onView(object).perform(typeText(text)).perform(closeSoftKeyboard());
    }

    private void emptyInput(Matcher<View> object) {
        onView(object).perform(replaceText(""));
    }

    private void clickButton(Matcher<View> object) {
        onView(object).perform(click());
    }
}
