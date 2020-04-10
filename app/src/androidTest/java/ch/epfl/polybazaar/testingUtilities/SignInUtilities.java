package ch.epfl.polybazaar.testingUtilities;

import ch.epfl.polybazaar.R;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

public abstract class SignInUtilities {

    public static void signInWithFromMainActivity(String email, String password){
        onView(withId(R.id.authenticationButton)).perform(click());
        onView(withId(R.id.emailInput)).perform(typeText(email));
        closeSoftKeyboard();
        onView(withId(R.id.passwordInput)).perform(typeText(password));
        closeSoftKeyboard();
        onView(withId(R.id.loginButton)).perform(click());
        onView(withId(R.id.toMainButton)).perform(click());
    }
}
