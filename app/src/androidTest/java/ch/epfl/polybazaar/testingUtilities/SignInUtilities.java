package ch.epfl.polybazaar.testingUtilities;

import ch.epfl.polybazaar.R;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

public abstract class SignInUtilities {

    /**
     * signs the user in and brings you back to the home page
     */
    public static void signInWithFromMainActivity(String email, String password){
        onView(withId(R.id.action_profile)).perform(click());
        onView(withId(R.id.signInButton)).perform(click());
        onView(withId(R.id.emailInput)).perform(typeText(email));
        closeSoftKeyboard();
        onView(withId(R.id.passwordInput)).perform(typeText(password));
        closeSoftKeyboard();
        onView(withId(R.id.loginButton)).perform(click());
        onView(withId(R.id.toMainButton)).perform(click());
    }

    public static void createAccountAndBackToLoginFromLoginActivity(String email, String nickname, String password) {
        onView(withId(R.id.signUpButton)).perform(click());
        fillAndSubmitSignUp(email, nickname, password, password);
        onView(withId(R.id.sendLinkButton)).perform(click());
        onView(withId(R.id.reloadButton)).perform(click());
        onView(withId(R.id.signOutButton)).perform(click());
    }

    public static void fillAndSubmitSignUp(String email, String nickname, String password, String confirm) {
        onView(withId(R.id.emailInput)).perform(typeText(email));
        onView(withId(R.id.nicknameInput)).perform(typeText(nickname));
        onView(withId(R.id.passwordInput)).perform(typeText(password));
        onView(withId(R.id.confirmPasswordInput)).perform(typeText(confirm));
        closeSoftKeyboard();
        onView(withId(R.id.submitSignUpButton)).perform(click());
    }
}
