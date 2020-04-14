package ch.epfl.polybazaar.UI;

import android.widget.Toast;

public final class SingletonToast {
    private static Toast toast = null;

    /**
     * replace older Toast with a new one, show() is directly called
     * @param newToast
     */
    public static Toast addNewToast(Toast newToast) {
        //first cancel older toast
        cancelToast();
        //set new Toast
        toast = newToast;
        return newToast;
    }

    public static void cancelToast() {
        if(toast != null) {
            toast.cancel();
            toast = null;
        }
    }
}