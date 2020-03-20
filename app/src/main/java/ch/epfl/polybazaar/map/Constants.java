package ch.epfl.polybazaar.map;

import com.google.android.gms.maps.model.LatLng;

class Constants {
    /**
     * Zoom values range from 1f (minimal/no zoom) to infinity
     */
    static final float LOWEST_ZOOM = 1f;
    static final float NORMAL_ZOOM = 14f;
    static final float CLOSE_ZOOM = 16f;
    static final float FAR_ZOOM = 11f;
    static final LatLng EPFL_LOCATION = new LatLng(46.5191, 6.5668);
}
