package ch.epfl.polybazaar.map;

import com.google.android.gms.maps.model.LatLng;

class Constants {
    /**
     * Zoom values range from 1f (minimal/no zoom) to infinity
     */
    static final float HOUSE_ZOOM = 18f;
    static final float STREET_ZOOM = 16f;
    static final float VILLAGE_ZOOM = 14.5f;
    static final float CITY_ZOOM = 13f;
    static final float CANTON_ZOOM = 10.5f;
    static final float LOWEST_ZOOM = 1f;
    static final LatLng EPFL_LOCATION = new LatLng(46.5191, 6.5668);
}
