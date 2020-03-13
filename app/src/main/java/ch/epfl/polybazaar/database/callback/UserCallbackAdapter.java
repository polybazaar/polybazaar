package ch.epfl.polybazaar.database.callback;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Objects;

import ch.epfl.polybazaar.database.DataSnapshot;
import ch.epfl.polybazaar.database.DataSnapshotCallback;
import ch.epfl.polybazaar.database.generic.DocumentSnapshotCallback;
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

    @Override
    public void onCallback(DataSnapshot result) {
        if (result==null){
            userCallback.onCallback(null);
            return;
        }
        User user = new User(Objects.requireNonNull(result.get("nickName")).toString(),
                Objects.requireNonNull(result.get("email")).toString());
        userCallback.onCallback(user);
    }

}
