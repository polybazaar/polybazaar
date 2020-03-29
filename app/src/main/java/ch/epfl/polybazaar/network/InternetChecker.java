package ch.epfl.polybazaar.network;

import android.content.Context;

/**
 * interface for classes used to check internet connection
 */
public interface InternetChecker {
   boolean checkInternetAvailability(Context context);
}
