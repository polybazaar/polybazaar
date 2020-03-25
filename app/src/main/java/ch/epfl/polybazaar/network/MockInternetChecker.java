package ch.epfl.polybazaar.network;

import android.content.Context;

public class MockInternetChecker implements InternetChecker {

    boolean networkState;

    public MockInternetChecker(boolean networkState){
        this.networkState = networkState;
    }
    @Override
    public boolean checkInternetAvailability(Context context) {
        return networkState;
    }


}
