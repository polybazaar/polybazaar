package ch.epfl.polybazaar.chat;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
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
import ch.epfl.polybazaar.notifications.FCMServiceAPI;
import ch.epfl.polybazaar.notifications.Client;
import ch.epfl.polybazaar.notifications.Data;
import ch.epfl.polybazaar.notifications.NotificationResponse;
import ch.epfl.polybazaar.notifications.Sender;
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

    private FCMServiceAPI apiService;
    boolean notify = false;

    public static final String bundleListingId = "listingID";
    public static final String bundleReceiverEmail = "receiverEmail";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        BottomNavigationView bottomNavigationView = findViewById(R.id.activity_main_bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.action_messages);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> bottomBar.updateActivity(item.getItemId(), ChatActivity.this));

        //TODO: What if the bundle is null ?
        Bundle bundle = getIntent().getExtras();
        this.listingID = bundle.getString(bundleListingId);
        this.receiverEmail = bundle.getString(bundleReceiverEmail);
        this.senderEmail = AuthenticatorFactory.getDependency().getCurrentUser().getEmail();

        sendMessageButton = findViewById(R.id.sendMessageButton);
        messageEditor = findViewById(R.id.messageEditor);
        messageRecycler = findViewById(R.id.messageDisplay);
        messageRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        sendMessageButton.setOnClickListener(v -> sendMessage());

        KeyboardVisibilityEvent.setEventListener(
                this,
                isOpen -> {
                    if (isOpen) {
                        messageRecycler.scrollToPosition(conversation.size() - 1);
                        bottomNavigationView.setVisibility(View.GONE);
                    } else {
                        bottomNavigationView.setVisibility(View.VISIBLE);
                    }
                });

        loadConversation();
        User.updateField("token", senderEmail, FirebaseInstanceId.getInstance().getToken());
        apiService = Client.getClient("https://fcm.googleapis.com/").create(FCMServiceAPI.class);
    }

    private void loadConversation() {
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

        message.save().addOnSuccessListener(aVoid -> {
            conversation.add(message);
            messageEditor.setText("");
            chatMessageRecyclerAdapter = new ChatMessageRecyclerAdapter(getApplicationContext(), conversation);
            messageRecycler.setAdapter(chatMessageRecyclerAdapter);
            messageRecycler.scrollToPosition(conversation.size() - 1);
        });

        final String msg = messageText;
        User.fetch(senderEmail).addOnSuccessListener(user -> sendNotification(receiverEmail, user.getNickName(), msg));
    }

    private void sendNotification(String receiverEmail, String nickname, String message) {
        User.fetch(receiverEmail).addOnSuccessListener(new OnSuccessListener<User>() {
            @Override
            public void onSuccess(User user) {
                receiverToken = user.getToken();
                Data data = new Data(senderEmail, R.mipmap.ic_launcher_round, nickname + ": " + message, "New Message", receiverEmail, listingID);
                Sender sender = new Sender(data, receiverToken);
                apiService.sendNotification(sender).enqueue(new Callback<NotificationResponse>() {
                    @Override
                    public void onResponse(Call<NotificationResponse> call, Response<NotificationResponse> response) {
                        if (response.code() == 200) {
                            if (response.body().success != 1) {
                                Toast.makeText(ChatActivity.this, "FAIL", Toast.LENGTH_SHORT);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<NotificationResponse> call, Throwable t) {

                    }
                });
            }
        });
    }
}


