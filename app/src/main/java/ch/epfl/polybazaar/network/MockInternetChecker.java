package ch.epfl.polybazaar.network;

import android.content.Context;
/**
 * class to mock AndroidInternetChecker
 */
public class MockInternetChecker implements InternetChecker {

    private boolean networkState;

    public MockInternetChecker(boolean networkState){
        this.networkState = networkState;
    }
    @Override
    public boolean checkInternetAvailability(Context context) {
        return networkState;
    }


}
