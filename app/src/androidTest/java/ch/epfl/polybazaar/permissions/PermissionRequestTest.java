package ch.epfl.polybazaar.permissions;

import android.Manifest;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.polybazaar.FillListingActivity;
import ch.epfl.polybazaar.widgets.permissions.PermissionRequest;

import static androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;


@RunWith(AndroidJUnit4.class)
public class PermissionRequestTest {

    @Rule
    public final ActivityTestRule<FillListingActivity> permissionActivityTestRule = new ActivityTestRule<>(FillListingActivity.class);

    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(Manifest.permission.CAMERA);

    @Test
    public void permissionAlreadyGivenReturnsTrue() throws Throwable {
        runOnUiThread(() -> {
            PermissionRequest permissionRequest = new PermissionRequest(permissionActivityTestRule.getActivity(), "CAMERA", null, null, result -> {
                assertThat(result, is(true));
            });
            permissionRequest.assertPermission();
        });
        Thread.sleep(1000);
    }

}
