package ch.epfl.polybazaar.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import ch.epfl.polybazaar.R;
import ch.epfl.polybazaar.chat.ChatActivity;
import ch.epfl.polybazaar.login.Account;
import ch.epfl.polybazaar.login.AuthenticatorFactory;
import ch.epfl.polybazaar.user.User;

public class NotificationService extends FirebaseMessagingService {
    public NotificationService() {
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        AuthenticatorFactory.getDependency().getCurrentUser().getUserData().addOnSuccessListener(user -> {
            if(user != null && remoteMessage.getData().get(Data.TO).equals(user.getId())){
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
        String channel_id = "notification_channel_id";
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, getString(R.string.notification_channel_id))
                .setContentTitle(remoteMessage.getData().get(Data.TITLE))
                .setContentText(remoteMessage.getData().get(Data.BODY))
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        Intent notificationIntent = new Intent(this, ChatActivity.class);
        notificationIntent.putExtra(ChatActivity.bundleReceiverEmail, remoteMessage.getData().get(Data.SENDER));
        notificationIntent.putExtra(ChatActivity.bundleListingId, remoteMessage.getData().get(Data.LISTINGID));
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_ONE_SHOT);
        builder.setContentIntent(contentIntent);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(getString(R.string.notification_channel_id), getString(R.string.notification_channel_name), NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channel);
        }
        manager.notify(0, builder.build());
    }
}
