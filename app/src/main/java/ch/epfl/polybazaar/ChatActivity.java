package ch.epfl.polybazaar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

import ch.epfl.polybazaar.login.AuthenticatorFactory;
import ch.epfl.polybazaar.login.FirebaseAuthenticator;
import ch.epfl.polybazaar.message.ChatMessage;
import ch.epfl.polybazaar.message.ChatMessageDatabase;

import static java.util.UUID.randomUUID;

public class ChatActivity extends AppCompatActivity {

    private Button sendMessageButton;
    private EditText messageEditor;
    private RecyclerView messagesDiplay;

    private String senderEmail;
    private String receiverEmail;
    private String listingID;

    private List<ChatMessage> conversation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //TODO: What if the bundle is null ?
        Bundle bundle = getIntent().getExtras();
        this.listingID = bundle.getString("listingID");
        this.receiverEmail = bundle.getString("sellerEmail");
        this.senderEmail = AuthenticatorFactory.getDependency().getCurrentUser().getEmail();

        sendMessageButton = findViewById(R.id.sendMessageButton);
        messageEditor = findViewById(R.id.messageEditor);
        messagesDiplay = findViewById(R.id.messageDisplay);

        sendMessageButton.setOnClickListener(v -> sendMessage());

        loadConversation();
    }

    private void loadConversation() {
        ChatMessageDatabase.fetchMessagesOfConversation(senderEmail, receiverEmail, listingID).addOnSuccessListener(new OnSuccessListener<List<ChatMessage>>() {
            @Override
            public void onSuccess(List<ChatMessage> chatMessages) {
                conversation = chatMessages;
            }
        });
    }

    private void sendMessage() {

        String messageText = messageEditor.getText().toString();
        ChatMessage message = new ChatMessage(senderEmail, receiverEmail, listingID, messageText);
        final String newMessageID = randomUUID().toString();
        message.setId(newMessageID);

        message.save().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //TODO: probably reload the recycler view where the messages are displayed
            }
        });
    }
}
