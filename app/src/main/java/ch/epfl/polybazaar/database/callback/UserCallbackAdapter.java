package ch.epfl.polybazaar.database.callback;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentSnapshot;


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
    public void onCallback(DocumentSnapshot result) {
        if (result==null){
            usercallback.onCallback(null);
            return;
        }
        User user = new User(result.get("firstName").toString(),
                result.get("lastName").toString(),
                result.get("dateOfBirth").toString(),
                result.get("email").toString());
        usercallback.onCallback(user);
    }

}
