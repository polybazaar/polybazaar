package ch.epfl.polybazaar.chat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.iid.FirebaseInstanceId;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import ch.epfl.polybazaar.R;
import ch.epfl.polybazaar.UI.bottomBar;
import ch.epfl.polybazaar.login.AuthenticatorFactory;
import ch.epfl.polybazaar.notifications.NotificationClient;
import ch.epfl.polybazaar.notifications.Data;
import ch.epfl.polybazaar.notifications.FCMServiceAPI;
import ch.epfl.polybazaar.notifications.NotificationSender;
import ch.epfl.polybazaar.notifications.NotificationUtilities;
import ch.epfl.polybazaar.user.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.util.UUID.randomUUID;

public class ChatActivity extends AppCompatActivity {

    private Button sendMessageButton;
    private EditText messageEditor;

    private RecyclerView messageRecycler;
    private ChatMessageRecyclerAdapter chatMessageRecyclerAdapter;

    private String senderEmail;
    private String receiverEmail;
    private String receiverToken;
    private String listingID;

    private List<ChatMessage> conversation = new ArrayList<>();

    private FCMServiceAPI fcmServiceAPI;
    private BroadcastReceiver broadcastReceiver;

    public static final String BUNDLE_LISTING_ID = "listingID";
    public static final String BUNDLE_RECEIVER_EMAIL = "receiverEmail";
    public static final String BROADCAST_UPDATE_MESSAGE = "broadcastUpdateMessage";
    public static final String CURRENT_CHAT_ID = "currentChatID";
    public static final String NO_CURRENT_ID = "noCurrentID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                loadConversation();
            }
        };

        BottomNavigationView bottomNavigationView = findViewById(R.id.activity_main_bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.action_messages);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> bottomBar.updateActivity(item.getItemId(), ChatActivity.this));

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(instanceIdResult -> User.updateField(User.TOKEN, senderEmail, instanceIdResult.getToken()));
        //TODO: What if the bundle is null ?
        Bundle bundle = getIntent().getExtras();
        this.listingID = bundle.getString(BUNDLE_LISTING_ID);
        this.receiverEmail = bundle.getString(BUNDLE_RECEIVER_EMAIL);
        this.senderEmail = AuthenticatorFactory.getDependency().getCurrentUser().getEmail();
        writeCurrentChatIDToPreferences(receiverEmail+listingID);
        sendMessageButton = findViewById(R.id.sendMessageButton);
        messageEditor = findViewById(R.id.messageEditor);
        messageRecycler = findViewById(R.id.messageDisplay);
        messageRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        fcmServiceAPI = NotificationClient.getClient(getString(R.string.fcm_url)).create(FCMServiceAPI.class);

        sendMessageButton.setOnClickListener(v -> sendMessage());

        removeBottomBarWhenKeyboardUp(this);
        KeyboardVisibilityEvent.setEventListener(
                this,
                isOpen -> {
                    if (isOpen) {
                        messageRecycler.scrollToPosition(conversation.size() - 1);
                    } else {
                    }
                });

        loadConversation();
    }

    /**
     * Self Explanatory, fixes bug0
     * @param activity the calling activity
     */
    public static void removeBottomBarWhenKeyboardUp(Activity activity) {
        BottomNavigationView bottomNavigationView = activity.findViewById(R.id.activity_main_bottom_navigation);
        KeyboardVisibilityEvent.setEventListener(
                activity,
                isOpen -> {
                    if (isOpen) {
                        bottomNavigationView.setVisibility(View.GONE);
                    } else {
                        bottomNavigationView.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void loadConversation() {
        conversation.clear();
        Task<List<ChatMessage>> fetchSenderMessages = ChatMessage.fetchConversation(senderEmail, receiverEmail, listingID).addOnSuccessListener(chatMessages -> conversation.addAll(chatMessages));
        Task<List<ChatMessage>> fetchReceiverMessages = ChatMessage.fetchConversation(receiverEmail, senderEmail, listingID).addOnSuccessListener(chatMessages -> conversation.addAll(chatMessages));

        Tasks.whenAll(fetchReceiverMessages, fetchSenderMessages).addOnSuccessListener(aVoid -> {
            //Sort the conversation by the time the messages were sent
            if (conversation != null) {
                Collections.sort(conversation, (o1, o2) -> o1.getTime().compareTo(o2.getTime()));
            }
            chatMessageRecyclerAdapter = new ChatMessageRecyclerAdapter(getApplicationContext(), conversation);
            messageRecycler.setAdapter(chatMessageRecyclerAdapter);
            messageRecycler.scrollToPosition(conversation.size() - 1);
        });
    }

    private void sendMessage() {
        String messageText = messageEditor.getText().toString();
        ChatMessage message = new ChatMessage(senderEmail, receiverEmail, listingID, messageText, new Timestamp(new Date(System.currentTimeMillis())));
        final String newMessageID = randomUUID().toString();
        message.setId(newMessageID);

        message.save();
        conversation.add(message);
        messageEditor.setText("");
        chatMessageRecyclerAdapter = new ChatMessageRecyclerAdapter(getApplicationContext(), conversation);
        messageRecycler.setAdapter(chatMessageRecyclerAdapter);
        messageRecycler.scrollToPosition(conversation.size() - 1);

        User.fetch(receiverEmail).addOnSuccessListener(user -> {
            if(user != null) {
                receiverToken = user.getToken();
                NotificationUtilities.sendNewChatNotification(getApplicationContext(), fcmServiceAPI, senderEmail, receiverEmail, listingID, AuthenticatorFactory.getDependency().getCurrentUser().getNickname(), message, receiverToken);
            }
        });
    }



    private void writeCurrentChatIDToPreferences(String id){
        SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
        editor.putString(CURRENT_CHAT_ID, id);
        editor.apply();
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver((broadcastReceiver),
                new IntentFilter(BROADCAST_UPDATE_MESSAGE)
        );
    }

    @Override
    protected void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        super.onStop();
    }

    @Override
    protected void onResume(){
        super.onResume();
        writeCurrentChatIDToPreferences(receiverEmail + listingID);
    }

    @Override
    protected void onPause(){
        super.onPause();
        writeCurrentChatIDToPreferences(NO_CURRENT_ID);
    }
}


