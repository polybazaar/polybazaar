package ch.epfl.polybazaar.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;

public class AndroidInternetChecker implements InternetChecker {
    @Override
    public boolean checkInternetAvailability(Context context) {
        ConnectivityManager cm= (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        Network[] networks = cm.getAllNetworks();
        boolean hasInternet = false;
        if(networks.length>0){
            for(Network network :networks){
                NetworkCapabilities nc = cm.getNetworkCapabilities(network);
                if(nc.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) hasInternet = true;
            }
        }
        return hasInternet;
    }
}
