package ch.epfl.polybazaar.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import ch.epfl.polybazaar.R;
import ch.epfl.polybazaar.chat.ChatActivity;
import ch.epfl.polybazaar.login.Account;
import ch.epfl.polybazaar.login.AuthenticatorFactory;
import ch.epfl.polybazaar.user.User;

public class NotificationService extends FirebaseMessagingService {

    LocalBroadcastManager broadcastManager;

    @Override
    public void onCreate(){
        broadcastManager = LocalBroadcastManager.getInstance(this);
    }

    public void onCreate(Context context){
        broadcastManager = LocalBroadcastManager.getInstance(context);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        broadcastManager.sendBroadcast(new Intent(ChatActivity.BROADCAST_UPDATE_MESSAGE));
        SharedPreferences preferences = getSharedPreferences("PREFS", MODE_PRIVATE);
        String currentChatID = preferences.getString(ChatActivity.CURRENT_CHAT_ID, ChatActivity.NO_CURRENT_ID);
        AuthenticatorFactory.getDependency().getCurrentUser().getUserData().addOnSuccessListener(user -> {
            if(user != null && remoteMessage.getData().get(Data.TO).equals(user.getId()) && !currentChatID.equals(remoteMessage.getData().get(Data.SENDER) + remoteMessage.getData().get(Data.LISTINGID))){
                showNotification(remoteMessage);
            }
        });
    }

    @Override
    public void onNewToken(String token) {
        Account user = AuthenticatorFactory.getDependency().getCurrentUser();
        if(user != null){
            Tasks.whenAll(user.getUserData().addOnSuccessListener(user1 -> {
                Tasks.whenAll(User.updateField(User.TOKEN, user1.getId(), token));
            }));
        }
    }

    private void showNotification(RemoteMessage remoteMessage) {
        Intent notificationIntent = toChatActivityIntent(remoteMessage);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, getString(R.string.notification_channel_id))
                .setContentTitle(remoteMessage.getData().get(Data.TITLE))
                .setContentText(remoteMessage.getData().get(Data.BODY))
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(contentIntent);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(getString(R.string.notification_channel_id), getString(R.string.notification_channel_name), NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channel);
        }
        manager.notify(0, builder.build());
    }

    private final Intent toChatActivityIntent(RemoteMessage remoteMessage){
        return new Intent(this, ChatActivity.class)
                .putExtra(ChatActivity.BUNDLE_RECEIVER_EMAIL, remoteMessage.getData().get(Data.SENDER))
                .putExtra(ChatActivity.BUNDLE_LISTING_ID, remoteMessage.getData().get(Data.LISTINGID))
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
    }
}


