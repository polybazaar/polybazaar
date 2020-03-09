package ch.epfl.polybazaar;

import android.Manifest;
import android.app.Activity;
import android.app.Instrumentation;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;

import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasData;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withTagValue;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.AllOf.allOf;

@RunWith(AndroidJUnit4.class)
public class FillListingActivityTest {
    static Uri imageUri;
    static Bitmap imageBitmap;
    static Intent galleryIntent;
    static Intent cameraIntent;

    Instrumentation.ActivityResult galleryResult;
    Instrumentation.ActivityResult cameraResult;
    static Matcher<Intent> expectedGalleryIntent;
    static Matcher<Intent> expectedCameraIntent;

    @Rule
    public final ActivityTestRule<FillListingActivity> fillSaleActivityTestRule = new ActivityTestRule<>(FillListingActivity.class);

    @Rule public GrantPermissionRule permissionRule = GrantPermissionRule.grant(Manifest.permission.CAMERA);
    
    @BeforeClass
    public static void setupStubIntent(){
        Resources resources = InstrumentationRegistry.getInstrumentation().getTargetContext().getResources();
        imageUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                resources.getResourcePackageName(R.mipmap.ic_launcher) + '/' +
                resources.getResourceTypeName(R.mipmap.ic_launcher) + '/' +
                resources.getResourceEntryName(R.mipmap.ic_launcher));

        imageBitmap = BitmapFactory.decodeResource(
                InstrumentationRegistry.getInstrumentation().getTargetContext().getResources(),
                R.mipmap.ic_launcher);

        galleryIntent = new Intent();
        galleryIntent.setData(imageUri);
        
        cameraIntent = new Intent();
        cameraIntent.putExtra("data", imageBitmap);

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
        galleryResult = new Instrumentation.ActivityResult( Activity.RESULT_OK, galleryIntent);
        uploadImage();
        onView(withId(R.id.picturePreview)).check(matches(withTagValue(CoreMatchers.<Object>equalTo(imageUri.hashCode()))));
    }

    @Test
    public void testUploadPictureFailsWhenUserCancels(){
        galleryResult = new Instrumentation.ActivityResult( Activity.RESULT_CANCELED, galleryIntent);
        uploadImage();
        checkNoImageUploaded();
    }

    @Test
    public void testUploadPictureFailsWhenDataIsNull(){
        galleryResult = new Instrumentation.ActivityResult( Activity.RESULT_OK, null);
        uploadImage();
        checkNoImageUploaded();
    }

    @Test
    public void testUploadPictureFailsWhenDataIsNullAndUserCancels(){
        galleryResult = new Instrumentation.ActivityResult( Activity.RESULT_CANCELED, null);
        uploadImage();
        onView(withId(R.id.picturePreview)).check(matches(withTagValue(CoreMatchers.<Object>equalTo(-1))));
    }

    @Test
    public void toastAppearsWhenTitleIsEmpty() {
        onView(withId(R.id.priceSelector)).perform(scrollTo(), typeText("123"));
        submitListingAndCheckIncorrectToast();
        }

    @Test
    public void toastAppearsWhenPriceIsText() {
        onView(withId(R.id.titleSelector)).perform(scrollTo(), typeText("A book"));
        submitListingAndCheckIncorrectToast();
    }

    @Test
    public void toastAppearsWhenPriceIsNegative() {
        onView(withId(R.id.priceSelector)).perform(scrollTo(), typeText("-0.10"));
        submitListingAndCheckIncorrectToast();
    }

    @Test
    public void testNoPictureIsDisplayedWhenNoPictureIsTaken(){
        cancelTakingPicture();
        checkNoImageUploaded();
    }


    public void cancelTakingPicture() {
        cameraResult = new Instrumentation.ActivityResult(Activity.RESULT_CANCELED, cameraIntent);
        closeSoftKeyboard();
        Intents.init();
        expectedCameraIntent = hasAction(MediaStore.ACTION_IMAGE_CAPTURE);
        intending(expectedCameraIntent).respondWith(cameraResult);
        onView(withId(R.id.camera)).perform(scrollTo(), click());
        intended(expectedCameraIntent);
        Intents.release();
    }


    private void uploadImage(){
        closeSoftKeyboard();
        expectedGalleryIntent = allOf(hasAction(Intent.ACTION_PICK), hasData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI));
        Intents.init();
        intending(expectedGalleryIntent).respondWith(galleryResult);
        onView(withId(R.id.uploadImage)).perform(scrollTo(), click());
        intended(expectedGalleryIntent);
        Intents.release();
    }

    private void submitListingAndCheckIncorrectToast(){
        onView(withId(R.id.submitListing)).perform(scrollTo(), click());
        onView(withText(FillListingActivity.INCORRECT_FIELDS_TEXT)).inRoot(withDecorView(not(is(fillSaleActivityTestRule.getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));
    }

    private void checkNoImageUploaded(){
        onView(withId(R.id.picturePreview)).check(matches(withTagValue(CoreMatchers.<Object>equalTo(-1))));
    }
}
