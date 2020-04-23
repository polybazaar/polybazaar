package ch.epfl.polybazaar.widgets;

import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;

import ch.epfl.polybazaar.R;

import static androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread;

public class NotSignedInActivityTest {

    @Rule
    public final ActivityTestRule<NotSignedInActivity> activityRule =
            new ActivityTestRule<>(
                    NotSignedInActivity.class,
                    true,
                    true);

    @Test
    public void signInClick() throws Throwable {
        runOnUiThread(() -> activityRule.getActivity().findViewById(R.id.signInButton).performClick());
    }

    @Test
    public void signUpClick() throws Throwable {
        runOnUiThread(() -> activityRule.getActivity().findViewById(R.id.signUpButton).performClick());
    }
}
