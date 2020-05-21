package ch.epfl.polybazaar.testingUtilities;

import ch.epfl.polybazaar.R;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
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
    }

    public static void createAccountAndBackToLoginFromLoginActivity(String email, String nickname, String password) {
        onView(withId(R.id.signUpButton)).perform(scrollTo(), click());
        fillAndSubmitSignUp(email, nickname, password, password);
        closeSoftKeyboard();
        onView(withId(R.id.sendLinkButton)).perform(scrollTo(), click());
        onView(withId(R.id.reloadButton)).perform(scrollTo(), click());
        onView(withId(R.id.action_profile)).perform(click());
        onView(withId(R.id.signOutButton)).perform(scrollTo(), click());
        onView(withId(R.id.action_profile)).perform(click());
    }

    public static void fillAndSubmitSignUp(String email, String nickname, String password, String confirm) {
        onView(withId(R.id.emailInput)).perform(scrollTo(), typeText(email));
        closeSoftKeyboard();
        onView(withId(R.id.nicknameInput)).perform(scrollTo(), typeText(nickname));
        closeSoftKeyboard();
        onView(withId(R.id.passwordInput)).perform(scrollTo(), typeText(password));
        closeSoftKeyboard();
        onView(withId(R.id.confirmPasswordInput)).perform(scrollTo(), typeText(confirm));
        closeSoftKeyboard();
        onView(withId(R.id.submitSignUpButton)).perform(scrollTo(), click());
    }
}
