package ch.epfl.polybazaar.UI;

import android.app.Activity;
import android.content.Intent;

import ch.epfl.polybazaar.MainActivity;
import ch.epfl.polybazaar.R;
import ch.epfl.polybazaar.UserProfileActivity;
import ch.epfl.polybazaar.conversationOverview.ConversationOverview;
import ch.epfl.polybazaar.conversationOverview.ConversationOverviewActivity;
import ch.epfl.polybazaar.filllisting.FillListingActivity;
import ch.epfl.polybazaar.login.Account;
import ch.epfl.polybazaar.login.Authenticator;
import ch.epfl.polybazaar.login.AuthenticatorFactory;
import ch.epfl.polybazaar.widgets.NotSignedInActivity;

public abstract class bottomBar {

    public static boolean updateActivity(Integer i,Activity activity){

        switch (i){
            case R.id.action_home:
                //no login needed to go home
                Intent intent = new Intent(activity,MainActivity.class);
                activity.startActivity(intent);
                activity.overridePendingTransition(0, 0);
                break;

            case R.id.action_add_item:
                    toActivity(activity,FillListingActivity.class);
                break;

            case R.id.action_messages:
                toActivity(activity, ConversationOverviewActivity.class);
                break;
            case R.id.action_profile:
                toActivity(activity, UserProfileActivity.class);
                break;
        }
        return true;
    }

    private static void toActivity(Activity currentActivity,Class c){
        Authenticator authenticator = AuthenticatorFactory.getDependency();
        //authenticator.signOut();
        Account user = authenticator.getCurrentUser();

        //Toast toast = Toast.makeText(currentActivity, user.getEmail(), Toast.LENGTH_LONG);
        //toast.show();
        Intent intent;
        if(user == null){
            intent = new Intent(currentActivity, NotSignedInActivity.class);
        }else{
            intent = new Intent(currentActivity,c);
        }
        currentActivity.startActivity(intent);
        currentActivity.overridePendingTransition(0, 0);

    }
}
