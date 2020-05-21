package ch.epfl.polybazaar.UI;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;

import ch.epfl.polybazaar.R;
import ch.epfl.polybazaar.utilities.SatCompassShakeDetector;

public class SatCompass extends AppCompatActivity implements SensorEventListener, LocationListener {
    public static final double SAT_LATITUDE = 46.520484;
    public static final double SAT_LONGITUDE = 6.567829;
    public static final float PIVOT_X_VALUE = 0.5f;
    public static final float PIVOT_Y_VALUE = 0.5f;
    // record the compass picture angle turned
    private float currentDegreeNorth = 0f;
    private float currentDegreeSat = 0f;

    // device sensor manager
    private SensorManager mSensorManager;

    ImageView image;
    Location SatLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sat_compass);

        Glide.with(this).load(R.drawable.loading).into((ImageView)findViewById(R.id.loadingImage));

        image = findViewById(R.id.satArrow);

        // initialize your android device sensor capabilities
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        SatLocation = new Location("");
        SatLocation.setLatitude(SAT_LATITUDE);
        SatLocation.setLongitude(SAT_LONGITUDE);

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            startActivity(new Intent(SatCompass.this, SalesOverview.class));
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // for the system's orientation sensor registered listeners
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // to stop the listener and save battery
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        // get the angle around the z-axis rotated
        float degree = Math.round(event.values[0]);

        // create a rotation animation (reverse turn degree degrees)
        RotateAnimation ra = new RotateAnimation(
                currentDegreeNorth + currentDegreeSat,
                -degree,
                Animation.RELATIVE_TO_SELF, PIVOT_X_VALUE,
                Animation.RELATIVE_TO_SELF,
                PIVOT_Y_VALUE);

        // how long the animation will take place
        ra.setDuration(210);

        // set the animation after the end of the reservation status
        ra.setFillAfter(true);

        // Start the animation
        image.startAnimation(ra);
        currentDegreeNorth = -degree;
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onLocationChanged(Location location) {
        currentDegreeSat = location.bearingTo(SatLocation);
        findViewById(R.id.loadingImage).setVisibility(View.GONE);
        findViewById(R.id.satArrow).setVisibility(View.VISIBLE);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    protected void onStop() {
        SatCompassShakeDetector.enable();
        super.onStop();
    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
