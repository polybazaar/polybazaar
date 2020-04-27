package ch.epfl.polybazaar.chat;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import com.google.firebase.Timestamp;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import ch.epfl.polybazaar.R;
import ch.epfl.polybazaar.listing.Listing;
import ch.epfl.polybazaar.login.AuthenticatorFactory;

import static java.util.UUID.randomUUID;

public class ChatActivity extends AppCompatActivity {

    private Button sendMessageButton;
    private EditText messageEditor;

    private RecyclerView messageRecycler;
    private ChatMessageRecyclerAdapter chatMessageRecyclerAdapter;

    private String senderEmail;
    private String receiverEmail;
    private String listingID;

    private List<ChatMessage> conversation = new ArrayList<>();

    public static final String bundleLisitngId = "listingID";
    public static final String bundleReceiverEmail = "receiverEmail";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //TODO: What if the bundle is null ?
        Bundle bundle = getIntent().getExtras();
        this.listingID = bundle.getString(bundleLisitngId);
        this.receiverEmail = bundle.getString(bundleReceiverEmail);
        this.senderEmail = AuthenticatorFactory.getDependency().getCurrentUser().getEmail();

        sendMessageButton = findViewById(R.id.sendMessageButton);
        messageEditor = findViewById(R.id.messageEditor);
        messageRecycler = findViewById(R.id.messageDisplay);
        messageRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        sendMessageButton.setOnClickListener(v -> sendMessage());

        KeyboardVisibilityEvent.setEventListener(
               this,
                new KeyboardVisibilityEventListener() {
                    @Override
                    public void onVisibilityChanged(boolean isOpen) {
                        if(isOpen) {
                            messageRecycler.scrollToPosition(conversation.size() - 1);
                        }
                    }
                });

        loadConversation();
    }

    private void loadConversation() {
        Task<List<ChatMessage>> fetchSenderMessages = ChatMessage.fetchConversation(senderEmail, receiverEmail, listingID).addOnSuccessListener(chatMessages -> conversation.addAll(chatMessages));
        Task<List<ChatMessage>> fetchReceiverMessages = ChatMessage.fetchConversation(receiverEmail, senderEmail, listingID).addOnSuccessListener(chatMessages -> conversation.addAll(chatMessages));


        Tasks.whenAll(fetchReceiverMessages, fetchSenderMessages).addOnSuccessListener(aVoid -> {
            //Sort the conversation by the time the messages were sent
            if(conversation != null){
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
    }
}
