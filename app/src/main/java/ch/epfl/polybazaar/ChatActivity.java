package ch.epfl.polybazaar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnSuccessListener;

import ch.epfl.polybazaar.login.AuthenticatorFactory;
import ch.epfl.polybazaar.login.FirebaseAuthenticator;
import ch.epfl.polybazaar.message.ChatMessage;

import static java.util.UUID.randomUUID;

public class ChatActivity extends AppCompatActivity {

    private Button sendMessageButton;
    private EditText messageEditor;
    private RecyclerView messagesDiplay;

    private String senderEmail;
    private String receiverEmail;
    private String listingID;

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


        //TODO: Fill the recycler view with the messages belonging to that conversation (i.e. same listingID and sender (and receiver but probably not needed)
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
