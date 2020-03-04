package ch.epfl.polybazaar;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasData;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withTagValue;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.AllOf.allOf;

@RunWith(AndroidJUnit4.class)
public class FillListingActivityTest {
    static Uri imageUri;
    static Intent galleryIntent;
    Instrumentation.ActivityResult result;
    static Matcher<Intent> expectedIntent;

    @Rule
    public final ActivityTestRule<FillListingActivity> fillSaleActivityTestRule = new ActivityTestRule<>(FillListingActivity.class);

    @BeforeClass
    public static void setupStubIntent(){
        Resources resources = InstrumentationRegistry.getInstrumentation().getTargetContext().getResources();
        imageUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                resources.getResourcePackageName(R.mipmap.ic_launcher) + '/' +
                resources.getResourceTypeName(R.mipmap.ic_launcher) + '/' +
                resources.getResourceEntryName(R.mipmap.ic_launcher));

        galleryIntent = new Intent();
        galleryIntent.setData(imageUri);

        expectedIntent = allOf(hasAction(Intent.ACTION_PICK),
                hasData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI));
    }

    @Test
    public void testFreeSwitchFreezesPriceSelector() {
        onView(withId(R.id.freeSwitch)).perform(scrollTo(), click());
        onView(withId(R.id.priceSelector)).perform(scrollTo(), typeText("123"));
        onView(withId(R.id.priceSelector)).check(matches(withText("0.0")));
    }
    @Test
    public void testPriceSelectorRemembersPriceAfterFreeSwitchDisabled() {
        onView(withId(R.id.priceSelector)).perform(scrollTo(), typeText("123.45"));
        onView(withId(R.id.freeSwitch)).perform(click());
        onView(withId(R.id.priceSelector)).check(matches(withText("0.0")));
        onView(withId(R.id.freeSwitch)).perform(click());
        onView(withId(R.id.priceSelector)).check(matches(withText("123.45")));
    }

    @Test
    public void testUploadPictureCorrectly(){
        result = new Instrumentation.ActivityResult( Activity.RESULT_OK, galleryIntent);
        uploadImage();
        onView(withId(R.id.picturePreview)).check(matches(withTagValue(CoreMatchers.<Object>equalTo(imageUri.hashCode()))));
    }

    @Test
    public void testUploadPictureFailsWhenUserCancels(){
        result = new Instrumentation.ActivityResult( Activity.RESULT_CANCELED, galleryIntent);
        uploadImage();
        onView(withId(R.id.picturePreview)).check(matches(withTagValue(CoreMatchers.<Object>equalTo(-1))));
    }

    @Test
    public void testUploadPictureFailsWhenDataIsNull(){
        result = new Instrumentation.ActivityResult( Activity.RESULT_OK, null);
        uploadImage();
        onView(withId(R.id.picturePreview)).check(matches(withTagValue(CoreMatchers.<Object>equalTo(-1))));
    }

    @Test
    public void testUploadPictureFailsWhenDataIsNullAndUserCancels(){
        result = new Instrumentation.ActivityResult( Activity.RESULT_CANCELED, null);
        uploadImage();
        onView(withId(R.id.picturePreview)).check(matches(withTagValue(CoreMatchers.<Object>equalTo(-1))));
    }

    @Test
    public void toastAppearsWhenTitleIsEmpty(){
        onView(withId(R.id.priceSelector)).perform(scrollTo(), typeText("123"));
        onView(withId(R.id.submitListing)).perform(scrollTo(), click());
        onView(withText(FillListingActivity.INCORRECT_FIELDS_TEXT)).inRoot(withDecorView(not(is(fillSaleActivityTestRule.getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));
    }

    @Test
    public void toastAppearsWhenPriceIsText(){
        onView(withId(R.id.titleSelector)).perform(scrollTo(), typeText("A book"));
        onView(withId(R.id.submitListing)).perform(scrollTo(), click());
        onView(withText(FillListingActivity.INCORRECT_FIELDS_TEXT)).inRoot(withDecorView(not(is(fillSaleActivityTestRule.getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));
    }

    @Test
    public void toastAppearsWhenPriceIsNegative(){
        onView(withId(R.id.priceSelector)).perform(scrollTo(), typeText("-0.10"));
        onView(withId(R.id.submitListing)).perform(scrollTo(), click());
        onView(withText(FillListingActivity.INCORRECT_FIELDS_TEXT)).inRoot(withDecorView(not(is(fillSaleActivityTestRule.getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));
    }

    private void uploadImage(){
        Intents.init();
        intending(expectedIntent).respondWith(result);

        onView(withId(R.id.uploadImage)).perform(click());


        intended(expectedIntent);
        Intents.release();
    }



}
