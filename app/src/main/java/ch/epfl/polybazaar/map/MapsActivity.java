package ch.epfl.polybazaar.map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;

import java.util.Objects;

import ch.epfl.polybazaar.R;
import ch.epfl.polybazaar.database.callback.SuccessCallback;
import ch.epfl.polybazaar.widgets.permissions.PermissionRequest;

import static ch.epfl.polybazaar.map.Constants.*;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener {

    private GoogleMap mMap;

    private final String TAG = "MapsActivity";

    private FusedLocationProviderClient mFusedLocationProviderClient;
    private PermissionRequest perm;
    private boolean mLocationPermissionGranted = false;
    private boolean MPSet = false;

    // in showMode, the user cannot select a meeting point
    private boolean showMode = true;
    private Bundle extras;

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.getBoolean(GIVE_LatLng)) {
                Objects.requireNonNull(getSupportActionBar()).setTitle("Meeting Point");
                showMode = true;
            } else {
                Objects.requireNonNull(getSupportActionBar()).setTitle("Define Meeting Point");
                showMode = false;
            }
        }
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        checkLocationPermissions(result -> mapInit());
    }

    private void checkLocationPermissions(SuccessCallback successCallback) {
        SuccessCallback callback = result -> {
            if (result) {
                mLocationPermissionGranted = true;
            } else {
                mLocationPermissionGranted = false;
            }
            successCallback.onCallback(result);
        };
        perm = new PermissionRequest(this,"ACCESS_FINE_LOCATION",
                "Location is required to show your position on the map",
                "Location will be unavailable",
                callback);
        perm.assertPermission();
    }

    private void mapInit() {
        if (mMap != null) {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            }
            mMap.setBuildingsEnabled(true);
            mMap.setIndoorEnabled(true);
            if (!showMode) {
                // We are not in Show Mode
                mMap.setOnMapClickListener(latLng -> {
                    if (MPSet) {
                        mMap.clear();
                        extras.putBoolean(VALID, false);
                        MPSet = false;
                        Toast toast = Toast.makeText(this, "Meeting Point removed", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });
                mMap.setOnMapLongClickListener(latLng -> {
                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(latLng).title("Meeting Point"));
                    extras.putDouble(LAT, latLng.latitude);
                    extras.putDouble(LNG, latLng.longitude);
                    extras.putBoolean(VALID, true);
                    MPSet = true;
                    Toast toast = Toast.makeText(this, "Meeting Point defined", Toast.LENGTH_SHORT);
                    toast.show();
                });
                goToEPFL();
            } else {
                // We are in Show Mode
                mMap.clear();
                if (extras != null) {
                    LatLng meetingPoint = new LatLng(extras.getDouble(LAT), extras.getDouble(LNG));
                    mMap.addMarker(new MarkerOptions().position(meetingPoint).title("Meeting Point"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(meetingPoint, HOUSE_ZOOM));
                }
            }
            mMap.getUiSettings().setZoomControlsEnabled(false);
        }
    }

    private void goToEPFL(){
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(EPFL_LOCATION, VILLAGE_ZOOM));
    }

    @Override
    public boolean onMyLocationButtonClick() {
        getDeviceLocation();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            Location mLastKnownLocation = task.getResult();
                            assert mLastKnownLocation != null;
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()), STREET_ZOOM));
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            goToEPFL();
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                });
            }
        } catch(SecurityException e)  {
            Log.e(TAG, Objects.requireNonNull(e.getMessage()));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        perm.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}
