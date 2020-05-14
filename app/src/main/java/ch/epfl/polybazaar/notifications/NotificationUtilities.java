package ch.epfl.polybazaar.notifications;

import android.content.Context;

import ch.epfl.polybazaar.R;
import ch.epfl.polybazaar.chat.ChatMessage;
import ch.epfl.polybazaar.chat.ChatMessageRecyclerAdapter;
import ch.epfl.polybazaar.login.AuthenticatorFactory;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationUtilities {
    public static void sendNewChatNotification(Context context, FCMServiceAPI fcmServiceAPI, String senderEmail, String receiverEmail, String listingID, String nickname, ChatMessage message, String receiverToken) {
        String body = nickname;
        String title;
        if(message.getMessage().startsWith(ChatMessage.OFFER_MADE)){
            body += " "+context.getString(R.string.user_sent_offer);
            title = context.getString(R.string.new_offer);
        }
        else if(message.getMessage().startsWith(ChatMessage.OFFER_PROCESSED)){
            body += " "+context.getString(R.string.user_answered_offer);
            title = context.getString(R.string.offer_answered);
        }
        else{
            body+= ": "+message.getMessage();
            title = context.getString(R.string.new_message);
        }

        Data data = new Data(senderEmail, body, title, receiverEmail, listingID);
        NotificationSender notificationSender = new NotificationSender(data, receiverToken);
        fcmServiceAPI.sendNotification(notificationSender).enqueue(new Callback<Void>() {
            //These functions have to be overridden but in our implementation we do not want anything to happen if we fail to send a notification
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {}
            @Override
            public void onFailure(Call<Void> call, Throwable t) {}
        });
    }
}
