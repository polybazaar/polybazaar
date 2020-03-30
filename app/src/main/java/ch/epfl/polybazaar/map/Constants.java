package ch.epfl.polybazaar.map;

import com.google.android.gms.maps.model.LatLng;

final class Constants {
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
    /**
     * the boolean associated to GIVE_LatLng is
     * true if an external activity gives the map a marker to display and
     * false if the map gives an external activity a user-chosen location
     */
    static final String GIVE_LatLng = "GIVE";
    /**
     * the valid boolean indicates if the passed location (from map to
     * external activity) is valid, i.e. a user-chosen location.
     */
    static final String VALID = "VALID";
    static final String LAT = "LAT";
    static final String LNG = "LNG";
}
