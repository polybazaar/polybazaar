package ch.epfl.polybazaar;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import ch.epfl.polybazaar.login.AuthenticatorFactory;
import ch.epfl.polybazaar.message.ChatMessage;
import ch.epfl.polybazaar.message.ChatMessageRecyclerAdapter;

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

        loadConversation();
    }

    private void loadConversation() {
        Task<List<ChatMessage>> fetchSenderMessages = ChatMessage.fetchMessagesOfConversation(senderEmail, receiverEmail, listingID).addOnSuccessListener(chatMessages -> conversation.addAll(chatMessages));
        Task<List<ChatMessage>> fetchReceiverMessages = ChatMessage.fetchMessagesOfConversation(receiverEmail, senderEmail, listingID).addOnSuccessListener(chatMessages -> conversation.addAll(chatMessages));


        Tasks.whenAll(fetchReceiverMessages, fetchSenderMessages).addOnSuccessListener(aVoid -> {
            //Sort the conversation by the time the messages were sent
            if(conversation != null){
                Collections.sort(conversation, (o1, o2) -> {
                    if(o1.getTime().getTime() < o2.getTime().getTime()){
                        return -1;
                    } else{
                        return 1;
                    }
                });
            }
            chatMessageRecyclerAdapter = new ChatMessageRecyclerAdapter(getApplicationContext(), conversation);
            messageRecycler.setAdapter(chatMessageRecyclerAdapter);
        });
    }

    private void sendMessage() {

        String messageText = messageEditor.getText().toString();
        ChatMessage message = new ChatMessage(senderEmail, receiverEmail, listingID, messageText, new Date(System.currentTimeMillis()));
        final String newMessageID = randomUUID().toString();
        message.setId(newMessageID);

        message.save().addOnSuccessListener(aVoid -> {
            //TODO: It would be nice to have the keyboard close when the message is sent
           conversation.add(message);
           messageEditor.setText("");
           chatMessageRecyclerAdapter = new ChatMessageRecyclerAdapter(getApplicationContext(), conversation);
           messageRecycler.setAdapter(chatMessageRecyclerAdapter);
        });
    }
}
