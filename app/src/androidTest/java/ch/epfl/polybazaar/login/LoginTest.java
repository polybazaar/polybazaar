package ch.epfl.polybazaar.login;

import android.view.View;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.polybazaar.R;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNot.not;

@RunWith(AndroidJUnit4.class)
public class LoginTest {
    String EMAIL = "otheruser.test@epfl.ch";
    String NICKNAME = "otheruser";
    String PASSWORD = "abcdef";

    @Rule
    public final ActivityTestRule<SignInActivity> mActivityRule =
            new ActivityTestRule<SignInActivity>(SignInActivity.class){
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

    /*@Test
    public void loginWithUnregisteredUserFails() {
        fillAndSubmitSignIn(EMAIL, PASSWORD);
        onView(withText(R.string.verify_credentials)).check(matches(isDisplayed()));
    }*/

    /*@Test
    public void signUpProcessWorks() {
        createAccountAndBackToLogin(EMAIL, NICKNAME, PASSWORD);
        fillAndSubmitSignIn(EMAIL, "aaaaaaaa");
        onView(withText(R.string.verify_credentials)).check(matches(isDisplayed()));

        clickButton(withText(R.string.alert_close));

        emptyInput(withId(R.id.emailInput));
        emptyInput(withId(R.id.passwordInput));

        fillAndSubmitSignIn(EMAIL, PASSWORD);
        onView(withText(R.string.authentication_successful)).check(matches(isDisplayed()));
    }*/

    /*@Test
    public void signUpWithExistingEmailFails() {
        createAccountAndBackToLogin(EMAIL, NICKNAME, PASSWORD);
        clickButton(withId(R.id.signUpButton));
        fillAndSubmitSignUp(EMAIL, NICKNAME, PASSWORD, PASSWORD);

        onView(withText(R.string.signup_error)).check(matches(isDisplayed()));
    }*/

    @Test
    public void signUpWithNonEpflPasswordFails() {
        String email = "test@gmail.com";
        clickButton(withId(R.id.signUpButton));
        fillAndSubmitSignUp(email, NICKNAME, PASSWORD, PASSWORD);

        onView(withText(R.string.signup_email_invalid)).check(matches(isDisplayed()));
    }

    /*@Test
    public void signUpWithNonMatchingPasswordFails() {
        clickButton(withId(R.id.signUpButton));
        fillAndSubmitSignUp(EMAIL, NICKNAME, PASSWORD, "random");

        onView(withText(R.string.signup_passwords_not_matching)).check(matches(isDisplayed()));
    }*/

    /*@Test
    public void signUpWithBadPassword() {
        clickButton(withId(R.id.signUpButton));
        fillAndSubmitSignUp(EMAIL, NICKNAME,"a", "a");

        onView(withText(R.string.signup_passwords_weak)).check(matches(isDisplayed()));
    }*/

    /*@Test
    public void signInWithoutVerificationBlocked() {
        clickButton(withId(R.id.signUpButton));

        fillAndSubmitSignUp(EMAIL, NICKNAME, PASSWORD, PASSWORD);
        clickButton(withId(R.id.signOutButton));

        fillAndSubmitSignIn(EMAIL, PASSWORD);

        onView(withText(R.string.email_not_verified)).check(matches(isDisplayed()));
    }*/

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

    public void createAccountAndBackToLogin(String email, String nickname, String password) {
        clickButton(withId(R.id.signUpButton));

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

    private void emptyInput(Matcher<View> object) {
        onView(object).perform(replaceText(""));
    }

    private void clickButton(Matcher<View> object) {
        onView(object).perform(click());
    }
}
