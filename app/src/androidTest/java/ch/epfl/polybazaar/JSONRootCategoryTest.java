package ch.epfl.polybazaar;


import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.polybazaar.category.Category;
import ch.epfl.polybazaar.category.NodeCategory;
import ch.epfl.polybazaar.category.RootCategoryFactory;
import ch.epfl.polybazaar.filllisting.FillListingActivity;
import ch.epfl.polybazaar.login.AuthenticatorFactory;
import ch.epfl.polybazaar.login.MockAuthenticator;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;

@RunWith(AndroidJUnit4.class)
public class JSONRootCategoryTest {

    @Rule
    public final ActivityTestRule<FillListingActivity> activityRule =
            new ActivityTestRule<FillListingActivity>(FillListingActivity.class) {
                @Override
                protected void beforeActivityLaunched() {
                    RootCategoryFactory.clear();
                }
                @Override
                protected void afterActivityFinished() {
                    MockAuthenticator.getInstance().reset();
                }
            };


    @Test
    public void JSONIsLoadedCorrectly(){
        assertThat(RootCategoryFactory.getDependency().maxDepth(), greaterThan(1));
    }

    @Test
    public void CategoryOthersIsAlwaysContained(){
        assertThat(RootCategoryFactory.getDependency().contains(new NodeCategory("Others")), is(true));
    }
}