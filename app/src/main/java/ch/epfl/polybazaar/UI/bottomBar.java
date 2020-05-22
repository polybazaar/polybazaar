package ch.epfl.polybazaar.UI;

import android.app.Activity;
import android.content.Intent;

import ch.epfl.polybazaar.R;
import ch.epfl.polybazaar.conversationOverview.ConversationOverviewActivity;
import ch.epfl.polybazaar.login.Account;
import ch.epfl.polybazaar.login.Authenticator;
import ch.epfl.polybazaar.login.AuthenticatorFactory;

public abstract class bottomBar {

    /** method called when an item on the bar is selected
     *
     * @param i id of the button
     * @param activity activity where the bar is
     * @return
     */

    public static boolean updateActivity(Integer i, Activity activity){

        switch (i){
            case R.id.action_home:
                //no login needed to go home
                Intent intent = new Intent(activity,SalesOverview.class);
                activity.startActivity(intent);
                activity.overridePendingTransition(0, 0);
                break;

            case R.id.action_add_item:
                    toActivity(activity,FillListing.class);
                break;

            case R.id.action_messages:
                toActivity(activity, ConversationOverviewActivity.class);
                break;
            case R.id.action_profile:
                toActivity(activity, UserProfile.class);
                break;
        }
        return true;
    }

    /**
     * get to the desired activity if user is logged, get to NotSignedInActivity otherwise
     * @param currentActivity activity where the method is called
     * @param c the class to go to
     */
    private static void toActivity(Activity currentActivity, Class c){
        Authenticator authenticator = AuthenticatorFactory.getDependency();
        Account user = authenticator.getCurrentUser();
        Intent intent;
        if(user == null){
            intent = new Intent(currentActivity, NotSignedIn.class);
        }else{
            intent = new Intent(currentActivity,c);
        }
        currentActivity.startActivity(intent);
        currentActivity.overridePendingTransition(0, 0);
    }
}
