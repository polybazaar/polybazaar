package ch.epfl.polybazaar.endToEnd;

import androidx.test.rule.ActivityTestRule;

import com.google.android.gms.tasks.Tasks;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import ch.epfl.polybazaar.MainActivity;
import ch.epfl.polybazaar.R;
import ch.epfl.polybazaar.listing.Listing;
import ch.epfl.polybazaar.litelisting.LiteListing;
import ch.epfl.polybazaar.login.AuthenticatorFactory;
import ch.epfl.polybazaar.login.MockAuthenticator;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static ch.epfl.polybazaar.database.datastore.DataStoreFactory.useMockDataStore;
import static ch.epfl.polybazaar.listing.ListingDatabase.queryListingStringEquality;
import static ch.epfl.polybazaar.listing.ListingDatabase.storeListing;
import static ch.epfl.polybazaar.litelisting.LiteListingDatabase.addLiteListing;
import static ch.epfl.polybazaar.litelisting.LiteListingDatabase.queryLiteListingStringEquality;
import static java.util.UUID.randomUUID;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNot.not;

public class EditAndDeleteTest {


    @Rule
    public final ActivityTestRule<MainActivity> activityRule =
            new ActivityTestRule<MainActivity>(MainActivity.class){
                @Override
                protected void beforeActivityLaunched() {
                    AuthenticatorFactory.setDependency(MockAuthenticator.getInstance());
                    useMockDataStore();
                }
                @Override
                protected void afterActivityFinished() {
                    MockAuthenticator.getInstance().reset();
                }
    };

    @Test
    public void testDeleteWorksForPreviouslyUploadedListing() throws InterruptedException {
        String title = "My delete test";
        String email = MockAuthenticator.TEST_USER_EMAIL;
        storeNewListing(title, email);
        assertDatabaseHasAtLeastOneEntryWithTitle(title);
        signInWithFromMainActivity(email, MockAuthenticator.TEST_USER_PASSWORD);

        onView(withId(R.id.saleOverview)).perform(click());
        onView(withText(title)).perform(click());
        onView(withId(R.id.deleteButton)).perform(scrollTo(), click());
        onView(withText("Yes")).perform(click());

        assertDatabaseHasNoListingWithTitle(title);
    }

    @Test
    public void testEditWorksForPreviouslyUploadedListing() throws Throwable {
        String oldTitle = "My test";
        String newTitle = "My edited test";
        String email = MockAuthenticator.TEST_USER_EMAIL;
        storeNewListing(oldTitle, email);
        assertDatabaseHasAtLeastOneEntryWithTitle(oldTitle);
        signInWithFromMainActivity(email, MockAuthenticator.TEST_USER_PASSWORD);

        onView(withId(R.id.saleOverview)).perform(click());

        onView(withText(oldTitle)).perform(click());
        onView(withId(R.id.editButton)).perform(scrollTo(), click());
        onView(withId(R.id.titleSelector)).perform(scrollTo(), clearText(), typeText(newTitle));
        closeSoftKeyboard();
        onView(withId(R.id.categorySelector)).perform(scrollTo(), click());
        onView(withText("Furniture")).perform(scrollTo(), click());
        onView(withId(R.id.submitListing)).perform(scrollTo(), click());

        assertDatabaseHasAtLeastOneEntryWithTitle(newTitle);
        assertDatabaseHasNoListingWithTitle(oldTitle);

    }

    private void assertDatabaseHasNoListingWithTitle(String title){
        queryLiteListingStringEquality("title", title, result -> assertThat(result, is(empty())));
        queryListingStringEquality("title", title, result -> assertThat(result, is(empty())));
    }

    private void assertDatabaseHasAtLeastOneEntryWithTitle(String title){
        queryLiteListingStringEquality("title", title, result -> assertThat(result, is(not(empty()))));
        queryListingStringEquality("title", title, result -> assertThat(result, is(not(empty()))));
    }

    private void storeNewListing(String title, String userEmail){
        final String newListingID = randomUUID().toString();
        Listing testListing = new Listing(title,"testDescription","22",userEmail, "Games");
        LiteListing testLiteListing = new LiteListing(newListingID, title, "22", "Games");
        testListing.setId(newListingID);
        testLiteListing.setId(newListingID);

        Tasks.whenAll(testListing.save(), testLiteListing.save()).addOnFailureListener((v) -> {
                    throw new AssertionError();
        });
    }

    private void signInWithFromMainActivity(String email, String password){
        onView(withId(R.id.authenticationButton)).perform(click());
        onView(withId(R.id.emailInput)).perform(typeText(email));
        closeSoftKeyboard();
        onView(withId(R.id.passwordInput)).perform(typeText(password));
        closeSoftKeyboard();
        onView(withId(R.id.loginButton)).perform(click());
        onView(withId(R.id.toMainButton)).perform(click());
}
}
