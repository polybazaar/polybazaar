package ch.epfl.polybazaar.conversationOverview;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ch.epfl.polybazaar.R;
import ch.epfl.polybazaar.UI.SalesOverview;
import ch.epfl.polybazaar.UI.bottomBar;
import ch.epfl.polybazaar.chat.ChatActivity;
import ch.epfl.polybazaar.chat.ChatMessage;
import ch.epfl.polybazaar.login.AuthenticatorFactory;
import ch.epfl.polybazaar.user.User;

public class ConversationOverviewActivity extends AppCompatActivity {

    private List<ChatMessage> messagesFrom;
    private List<ChatMessage> messagesTo;
    private Set<ConversationOverview> conversationOverviews;
    private ConversationOverviewRecyclerAdapter conversationOverviewRecyclerAdapter;
    private RecyclerView conversationOverviewRecycler;

    /*
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent SalesOverviewIntent = new Intent(ConversationOverviewActivity.this, SalesOverview.class);
        startActivity(SalesOverviewIntent);
    }
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversations);

        messagesFrom = new ArrayList<>();
        messagesTo = new ArrayList<>();
        conversationOverviews = new HashSet<>();

        loadConversationOverview();

        BottomNavigationView bottomNavigationView = findViewById(R.id.activity_main_bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.action_messages);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> bottomBar.updateActivity(item.getItemId(), ConversationOverviewActivity.this));
    }

    private void loadConversationOverview() {
        String userEmail = AuthenticatorFactory.getDependency().getCurrentUser().getEmail();
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(instanceIdResult -> User.updateField(User.TOKEN, userEmail, instanceIdResult.getToken()));
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
                intent.putExtra(ChatActivity.BUNDLE_LISTING_ID, conversationOverviewsList.get(viewPosition).getListingID());
                intent.putExtra(ChatActivity.BUNDLE_RECEIVER_EMAIL, conversationOverviewsList.get(viewPosition).getOtherUser());
                startActivity(intent);
            });

            conversationOverviewRecycler = findViewById(R.id.conversationsOverview);
            conversationOverviewRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            conversationOverviewRecycler.setAdapter(conversationOverviewRecyclerAdapter);
        });
    }
}
