package ch.epfl.polybazaar.login;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.polybazaar.R;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class LoginTest {
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
                }
            };

    @Test
    public void loginWithUnregisteredUserFails() {
        fillAndSubmitSignIn("user@test.com", "abcdef");
        onView(withText("Please verify your credentials")).check(matches(isDisplayed()));


    }

    @Test
    public void signUpProcessWorks() {
        onView(withId(R.id.signUpButton)).perform(click());

        fillAndSubmitSignUp("user2@test.com", "abcdef", "abcdef");

        onView(withId(R.id.sendLinkButton)).perform(click());
        onView(withId(R.id.reloadButton)).perform(click());
        onView(withId(R.id.signOutButton)).perform(click());

        fillAndSubmitSignIn("user2@test.com", "abcdef");

        onView(withText("Authentication was successful!")).check(matches(isDisplayed()));
    }

    private void fillAndSubmitSignIn(String email, String password) {
        onView(withId(R.id.emailInput)).perform(typeText(email));
        onView(withId(R.id.passwordInput)).perform(typeText(password))
                .perform(closeSoftKeyboard());
        onView(withId(R.id.loginButton)).perform(click());
    }

    private void fillAndSubmitSignUp(String email, String password, String confirm) {
        onView(withId(R.id.emailInput)).perform(typeText(email));
        onView(withId(R.id.passwordInput)).perform(typeText(password));
        onView(withId(R.id.confirmPasswordInput)).perform(typeText(confirm))
                .perform(closeSoftKeyboard());
        onView(withId(R.id.submitButton)).perform(click());
    }
}
