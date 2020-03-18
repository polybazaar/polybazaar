package ch.epfl.polybazaar.database.callback;

import android.os.Build;

import androidx.annotation.RequiresApi;

import ch.epfl.polybazaar.database.datastore.DataSnapshot;
import ch.epfl.polybazaar.database.datastore.DataSnapshotCallback;
import ch.epfl.polybazaar.user.User;

public class UserCallbackAdapter implements DataSnapshotCallback {

    private UserCallback userCallback;

    /**
     * The resulting Callback behaves like a GenericCallback
     * @param userCallback the UserCallback to be adapted
     */
    public UserCallbackAdapter(UserCallback userCallback){
        this.userCallback = userCallback;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onCallback(DataSnapshot result) {
        if (result==null){
            userCallback.onCallback(null);
            return;
        }
        User user = new User(String.valueOf(result.get("nickName")),
                String.valueOf(result.get("email")));
        userCallback.onCallback(user);
    }

}
