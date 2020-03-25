package ch.epfl.polybazaar.network;

import android.content.Context;

public abstract class InternetCheckerFactory {

    static InternetChecker internetChecker = new AndroidInternetChecker();

     public static InternetChecker getDependency(){
        return internetChecker;
    }
    public static void useRealNetwork(){
        internetChecker = new AndroidInternetChecker();
    }
    public static void useMockNetworkState(boolean networkState){
         internetChecker = new MockInternetChecker(networkState);
    }
    public static boolean isInternetAvailable(Context context){
         return internetChecker.checkInternetAvailability(context);
    }

}
