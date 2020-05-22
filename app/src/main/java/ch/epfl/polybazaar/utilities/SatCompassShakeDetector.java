package ch.epfl.polybazaar.utilities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import ch.epfl.polybazaar.R;
import ch.epfl.polybazaar.UI.SatCompass;
import safety.com.br.android_shake_detector.core.ShakeDetector;
import safety.com.br.android_shake_detector.core.ShakeOptions;

/**
 * Shake detection for secret feature ;)
 */
public final class SatCompassShakeDetector {
    // class is non-instantiable
    private SatCompassShakeDetector() {}

    private static final float SENSIBILITY = 2.0f;
    private static final ShakeDetector shakeDetector = new ShakeDetector(new ShakeOptions()
            .background(true)
            .interval(1000)
            .shakeCount(2)
            .sensibility(SENSIBILITY));

    private static boolean enabled = true;
    private static boolean launched = false;

    /**
     * Starts shake detection that launched the secret feature. Once a shake is detected, detection is
     * disabled until explicitly re-enabled.
     * @param ctx app context
     */
    public static void start(Context ctx) {
        if (launched) // make sure it is not launched twice to prevent unexpected behaviors
            return;
        launched = true;

        shakeDetector.start(ctx, () -> {
            if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(ctx, R.string.location_not_granted, Toast.LENGTH_SHORT).show();
                return;
            }
            LocationManager manager = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
            if (manager != null && manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                if (enabled) {
                    enabled = false;
                    ctx.startActivity(new Intent(ctx, SatCompass.class));
                }
            } else {
                Toast.makeText(ctx, R.string.location_not_enabled, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * (Re)enables shake detection
     */
    public static void enable() {
        enabled = true;
    }
}
