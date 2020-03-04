package ch.epfl.polybazaar.database.callback;

import ch.epfl.polybazaar.database.generic.GenericCallback;
import ch.epfl.polybazaar.user.User;

public class UserCallbackAdapter implements GenericCallback {

    private UserCallback usercallback;

    /**
     * The resulting Callback behaves like a GenericCallback
     * @param usercallback the UserCallback to be adapted
     */
    public UserCallbackAdapter(UserCallback usercallback){
        this.usercallback = usercallback;
    }

    @Override
    public void onCallback(Object result) {
        if (result==null){
            usercallback.onCallback(null);
        }
        usercallback.onCallback((User) result);
    }

}
