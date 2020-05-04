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
import ch.epfl.polybazaar.UI.UserProfile;
import ch.epfl.polybazaar.chat.ChatActivity;
import ch.epfl.polybazaar.conversationOverview.ConversationOverview;
import ch.epfl.polybazaar.login.Account;
import ch.epfl.polybazaar.login.AuthenticatorFactory;
import ch.epfl.polybazaar.user.User;

public class NotificationService extends FirebaseMessagingService {
    public NotificationService() {
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        String sented = remoteMessage.getData().get("sented");

        Account account = AuthenticatorFactory.getDependency().getCurrentUser();
        account.getUserData().addOnSuccessListener(user -> {
            if(user != null && sented.equals(user.getId())){
                sendNotification(remoteMessage);
            }
        });
    }

    @Override
    public void onNewToken(String token) {
        Account user = AuthenticatorFactory.getDependency().getCurrentUser();
        if(user != null){
            Tasks.whenAll(user.getUserData().addOnSuccessListener(user1 -> {
                Tasks.whenAll(User.updateField("token", user1.getId(), token));
            }));
        }
    }

    private void sendNotification(RemoteMessage remoteMessage) {
        String channel_id = "notification_channel_id";
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channel_id)
                .setContentTitle(remoteMessage.getData().get("title"))
                .setContentText(remoteMessage.getData().get("body"))
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        Intent notificationIntent = new Intent(this, ChatActivity.class);
        System.out.println(remoteMessage.getData());
        notificationIntent.putExtra(ChatActivity.bundleReceiverEmail, remoteMessage.getData().get("user"));
        notificationIntent.putExtra(ChatActivity.bundleListingId, remoteMessage.getData().get("listingID"));
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_ONE_SHOT);
        builder.setContentIntent(contentIntent);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channel_id, "chat", NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channel);
        }
        manager.notify(0, builder.build());
    }
}
