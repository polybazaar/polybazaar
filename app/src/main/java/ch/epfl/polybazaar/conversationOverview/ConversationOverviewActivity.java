package ch.epfl.polybazaar.conversationOverview;

import android.content.Intent;
import android.os.Bundle;
import android.util.ArraySet;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ch.epfl.polybazaar.R;
import ch.epfl.polybazaar.chat.ChatActivity;
import ch.epfl.polybazaar.chat.ChatMessage;
import ch.epfl.polybazaar.login.AuthenticatorFactory;

public class ConversationOverviewActivity extends AppCompatActivity {

    private List<ChatMessage> messagesFrom;
    private List<ChatMessage> messagesTo;
    private Set<ConversationOverview> conversationOverviews;
    private ConversationOverviewRecyclerAdapter conversationOverviewRecyclerAdapter;
    private RecyclerView conversationOverviewRecycler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversations);

        messagesFrom = new ArrayList<>();
        messagesTo = new ArrayList<>();
        conversationOverviews = new HashSet<>();

        loadConversationOverview();
    }

    private void loadConversationOverview() {
        String userEmail = AuthenticatorFactory.getDependency().getCurrentUser().getEmail();
        Task<List<ChatMessage>> messagesFromTask = ChatMessage.fetchMessagesFrom(userEmail).addOnSuccessListener(chatMessages -> messagesFrom.addAll(chatMessages));
        Task<List<ChatMessage>> messagesToTask = ChatMessage.fetchMessagesTo(userEmail).addOnSuccessListener(chatMessages -> messagesTo.addAll(chatMessages));
        Tasks.whenAll(messagesFromTask, messagesToTask).addOnSuccessListener(aVoid -> {
            for(ChatMessage message : messagesFrom){
                conversationOverviews.add(new ConversationOverview(message.getReceiver(), message.getListingID()));
            }

            for(ChatMessage message : messagesTo){
                conversationOverviews.add(new ConversationOverview(message.getSender(), message.getListingID()));
            }

            ArrayList<ConversationOverview> conversationOverviewsList = new ArrayList<>(conversationOverviews);
            conversationOverviewRecyclerAdapter = new ConversationOverviewRecyclerAdapter(getApplicationContext(), conversationOverviewsList);
            conversationOverviewRecyclerAdapter.setOnItemClickListener(view -> {
                int viewPosition = (int)view.getTag();
                Intent intent = new Intent(ConversationOverviewActivity.this, ChatActivity.class);
                intent.putExtra(ChatActivity.bundleLisitngId, conversationOverviewsList.get(viewPosition).getListingID());
                intent.putExtra(ChatActivity.bundleReceiverEmail, conversationOverviewsList.get(viewPosition).getOtherUser());
                startActivity(intent);
            });

            conversationOverviewRecycler = findViewById(R.id.conversationsOverview);
            conversationOverviewRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            conversationOverviewRecycler.setAdapter(conversationOverviewRecyclerAdapter);
        });
    }
}
