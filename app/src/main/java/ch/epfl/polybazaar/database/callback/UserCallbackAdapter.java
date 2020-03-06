package ch.epfl.polybazaar.database.callback;

import com.google.firebase.firestore.DocumentSnapshot;

import ch.epfl.polybazaar.database.generic.GenericCallback;
import ch.epfl.polybazaar.user.User;

public class UserCallbackAdapter implements GenericCallback {

    private UserCallback userCallback;

    /**
     * The resulting Callback behaves like a GenericCallback
     * @param userCallback the UserCallback to be adapted
     */
    public UserCallbackAdapter(UserCallback userCallback){
        this.userCallback = userCallback;
    }

    @Override
    public void onCallback(DocumentSnapshot result) {
        if (result==null){
            userCallback.onCallback(null);
            return;
        }
        User user = new User(result.get("nickName").toString(),
                result.get("email").toString());
        userCallback.onCallback(user);
    }

}
