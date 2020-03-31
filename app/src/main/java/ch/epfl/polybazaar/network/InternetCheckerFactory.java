package ch.epfl.polybazaar.network;

import android.content.Context;

public abstract class InternetCheckerFactory {

    private static InternetChecker internetChecker = new AndroidInternetChecker();

    /**
     *
     * @return the actual dependency used to check internet connection
     */
    public static InternetChecker getDependency(){
        return internetChecker;
    }

    /**
     * set the dependency to the real android internet checker
     */
    public static void useRealNetwork(){
        internetChecker = new AndroidInternetChecker();
    }

    /**
     * set the dependency to a mock that returns 'networkState' when checkInternetAvailability is called
     * @param networkState the state returned by the mock dependency
     */
    public static void useMockNetworkState(boolean networkState){
         internetChecker = new MockInternetChecker(networkState);
    }

    /**
     * check internet availability using the actual dependency
     * @param context context in which the method is called
     * @return true if there is an available internet connection
     */
    public static boolean isInternetAvailable(Context context){
         return internetChecker.checkInternetAvailability(context);
    }

}
