package ch.epfl.polybazaar.map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ch.epfl.polybazaar.R;
import ch.epfl.polybazaar.database.callback.SuccessCallback;
import ch.epfl.polybazaar.widgets.permissions.PermissionRequest;

import static ch.epfl.polybazaar.UI.SingletonToast.addNewToast;
import static ch.epfl.polybazaar.widgets.MinimalAlertDialog.makeDialog;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback{

    /**
     * ===========================================================
     * CONSTANTS
     * ===========================================================
     */
        /**
         * Zoom values range from 1f (minimal/no zoom) to infinity
         */
        static final float HOUSE_ZOOM = 18f;
        static final float STREET_ZOOM = 16f;
        static final float VILLAGE_ZOOM = 14.5f;
        static final float CITY_ZOOM = 13f;
        static final float CANTON_ZOOM = 10.5f;
        static final float LOWEST_ZOOM = 1f;
        public static final LatLng EPFL_LOCATION = new LatLng(46.5191, 6.5668);
        public static final double NOLAT = EPFL_LOCATION.latitude;
        public static final double NOLNG = EPFL_LOCATION.longitude;
        /**
         * the boolean associated to GIVE_LatLng is
         * true if an external activity gives the map a marker to display and
         * false if the map gives an external activity a user-chosen location
         */
        public static final String GIVE_LatLng = "GIVE";
        /**
         * the valid boolean indicates if the passed location (from map to
         * external activity) is valid, i.e. a user-chosen location.
         */
        public static final String VALID = "VALID";
        public static final String LAT = "LAT";
        public static final String LNG = "LNG";
    /**
     * ===========================================================
     * ===========================================================
     */

    private GoogleMap mMap;

    private final String TAG = "MapsActivity";

    private FusedLocationProviderClient mFusedLocationProviderClient;
    private PermissionRequest perm;
    private boolean mLocationPermissionGranted = false;
    private boolean MPSet = false;
    private double showLat = NOLAT;
    private double showLng = NOLNG;
    private double chosenLng = NOLNG;
    private double chosenLat = NOLAT;
    private boolean locationAvailablility = false;

    // in showMode, the user cannot select a meeting point
    private boolean showMode = true;
    private ImageView imgMyLocation;
    private Button confirmMP;
    private Intent returnIntent;

    private List<Toast> toasts;

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
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
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
        Bundle extras = getIntent().getExtras();
        returnIntent = new Intent();
        if (extras != null) {
            returnIntent.putExtra(VALID, false);
            if (extras.getBoolean(GIVE_LatLng)) {
                // show mode
                setupShowMode();
            } else {
                // define mode
                setupDefineMode();
            }
        }
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
        toasts = new ArrayList<>();
        mapInit();
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
            imgMyLocation = (ImageView) findViewById(R.id.imgMyLocation);
            imgMyLocation.setOnClickListener(v -> checkLocationPermissions(result ->  {
                if (result) {
                    mMap.setMyLocationEnabled(true);
                    mMap.getUiSettings().setMyLocationButtonEnabled(false);
                    getDeviceLocation();
                }
            }));
            confirmMP = findViewById(R.id.confirmMP);
            confirmMP.setOnClickListener(v -> {
                sendResponse();
            });
            mMap.setBuildingsEnabled(true);
            mMap.setIndoorEnabled(true);
            if (!showMode) {
                DefineMode();
            } else {
                ShowMode();
            }
            mMap.getUiSettings().setZoomControlsEnabled(false);
        }
    }

    private void setupDefineMode() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Define Meeting Point");
        }
        chosenLat = getIntent().getDoubleExtra(LAT, NOLAT);
        chosenLng = getIntent().getDoubleExtra(LNG, NOLNG);
        MPSet = true;
        if (chosenLat != NOLAT && chosenLng != NOLNG) {
            LatLng meetingPoint = new LatLng(chosenLat, chosenLng);
            mMap.addMarker(new MarkerOptions().position(meetingPoint).title("Meeting Point"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(meetingPoint, VILLAGE_ZOOM));
            MPSet = true;
        } else {
            goToEPFL();
            MPSet = false;
        }
        showMode = false;
    }

    private void setupShowMode() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Meeting Point");
        }
        showLat = getIntent().getDoubleExtra(LAT, NOLAT);
        showLng = getIntent().getDoubleExtra(LNG, NOLNG);
        showMode = true;
    }

    private void ShowMode(){
        mMap.clear();
        findViewById(R.id.confirmMP).setVisibility(View.INVISIBLE);
        if (showLat != NOLAT && showLng != NOLNG) {
            LatLng meetingPoint = new LatLng(showLat, showLng);
            mMap.addMarker(new MarkerOptions().position(meetingPoint).title("Meeting Point"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(meetingPoint, STREET_ZOOM));
        } else {
            goToEPFL();
        }
    }

    private void DefineMode(){
        findViewById(R.id.confirmMP).setVisibility(View.VISIBLE);
        mMap.setOnMapClickListener(latLng -> {
            if (MPSet) {
                mMap.clear();
                chosenLat = NOLAT;
                chosenLng = NOLNG;
                MPSet = false;
                showToast(false);
            }
        });
        mMap.setOnMapLongClickListener(latLng -> {
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(latLng).title("Meeting Point"));
            chosenLat = latLng.latitude;
            chosenLng = latLng.longitude;
            MPSet = true;
            showToast(true);
        });
    }

    private void showToast(boolean isDefined) {
        for (Toast t : toasts) {
            t.cancel();
        }
        Toast toast;
        if (isDefined) {
            toast = Toast.makeText(this, R.string.MP_set, Toast.LENGTH_SHORT);
        } else {
            toast = Toast.makeText(this, R.string.MP_removed, Toast.LENGTH_SHORT);
        }
        toast.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM, 0, 200);
        addNewToast(toast);
        toasts.add(toast);
    }

    private void goToEPFL(){
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(EPFL_LOCATION, VILLAGE_ZOOM));
    }

    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Task<LocationAvailability> locationAvailable = mFusedLocationProviderClient.getLocationAvailability();
                locationAvailable.addOnCompleteListener(result -> {
                    if (!result.getResult().isLocationAvailable()) {
                        locationAvailablility = false;
                        makeDialog(this, R.string.location_unavailable);
                    } else {
                        locationAvailablility = true;
                    }
                });
                if (locationAvailablility) {
                    Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                    locationResult.addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            Location mLastKnownLocation = task.getResult();
                            if (mLastKnownLocation != null) {
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(mLastKnownLocation.getLatitude(),
                                                mLastKnownLocation.getLongitude()), STREET_ZOOM));
                            }
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                        }
                    });
                }
            }
        } catch(SecurityException e)  {
            Log.e(TAG, Objects.requireNonNull(e.getMessage()));
        }
    }

    private void sendResponse() {
        returnIntent.putExtra(LAT, chosenLat);
        returnIntent.putExtra(LNG, chosenLng);
        if (chosenLat != NOLAT && chosenLng != NOLNG && MPSet) {
            returnIntent.putExtra(VALID, true);
        } else {
            returnIntent.putExtra(VALID, false);
        }
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        perm.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}
