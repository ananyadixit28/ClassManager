package com.android.wefour.classmanager.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.wefour.classmanager.models.ChatMessage;
import com.android.wefour.classmanager.adapters.MessageViewHolder;
import com.android.wefour.classmanager.R;
import com.android.wefour.classmanager.SendImageFragment;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class GroupChatActivity extends AppCompatActivity {

    private EditText currentMessage;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference messageReference;
    private FirebaseAuth mAuth;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private FirebaseRecyclerAdapter messageAdapter;
    private String groupId;
    private Query latestMessages;
    private String userId, userName;

    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private Uri filePath;
    private ImageView sendImage;
    private EditText imageCaption;
    private EditText imageCaptionString;
    private static final int PICK_IMAGE_REQUEST = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);
        Intent intent = getIntent();
        groupId = intent.getStringExtra("courseId");
        userName = intent.getStringExtra("name");
        //groupId = "CS101";
        //userName = "Aditya Akash Singh";
        currentMessage = (EditText)findViewById(R.id.sendMessageGroupChatEditText);
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        firebaseDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getUid();
        recyclerView = findViewById(R.id.recyclerViewGroupChat);
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        try {
            messageReference = firebaseDatabase.getReference("/groupChats/" + groupId + "/messageMap");

            // fetch last 50 messages from database
            latestMessages = messageReference.orderByChild("timestamp").limitToLast(50);
            FirebaseRecyclerOptions<ChatMessage> options = new FirebaseRecyclerOptions.Builder<ChatMessage>()
                    .setQuery(latestMessages, ChatMessage.class)
                    .build();

            //set firebase adapter to show the messages
            messageAdapter = new FirebaseRecyclerAdapter<ChatMessage, MessageViewHolder>(options) {

                private boolean isReceived(int position) {
                    if (getItem(position).getFromId().equals(userId))
                        return false;
                    return true;
                }

                @Override
                public int getItemViewType(int pos) {
                    if (isReceived(pos)) {
                        if(getItem(pos).getType().equals("text"))
                            return 3;
                        else if(getItem(pos).getType().equals("image"))
                            return 4;
                    }
                    if(getItem(pos).getType().equals("text"))
                        return 1;
                    else if(getItem(pos).getType().equals("image"))
                        return 2;
                    return -1;
                }

                // view holder to display different type of messages
                @Override
                public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                    if (viewType == 1) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_out_chat, parent, false);
                        return new MessageViewHolder(view, 1);
                    }
                    else if(viewType==2) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_message_out_chat,parent,false);
                        return new MessageViewHolder(view,2);
                    }
                    else if(viewType == 3){
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_in_chat, parent, false);
                        Log.i("messageAaya","3");
                        return new MessageViewHolder(view, 3);
                    }
                    else if(viewType == 4) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_message_in_chat,parent,false);
                        return new MessageViewHolder(view,4);
                    }
                    return null;
                }

                @Override
                protected void onBindViewHolder(MessageViewHolder messageViewHolder, final int position, ChatMessage chatMessage) {
                    int viewType = getItemViewType(position);
                    if(viewType!=-1)messageViewHolder.setMessage(chatMessage,viewType,groupId);
                }
            };

            recyclerView.setAdapter(messageAdapter);
        }
        catch (Exception e) {
            Log.e("exception: ", e.toString());
        }
    }

    //send the text message from the user and update the database
    public void messageSendButtonClicked(View view) {

        String message = currentMessage.getText().toString();
        final String messageKey = firebaseDatabase.getReference("/groupChats/"+groupId+"/messageMap").push().getKey();
        ChatMessage chatMessage = new ChatMessage(message, messageKey, userId, userName, "text");
        Map<String, Object> newMessage = chatMessage.toMap();
        Map<String, Object> childUpdates = new HashMap<>();

        //atomic updates
        childUpdates.put("/groupChats/"+groupId+"/messageMap/"+messageKey,newMessage);
        firebaseDatabase.getReference().updateChildren(childUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                currentMessage.setText("");
                goToLast();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(GroupChatActivity.this, "Failure to send message. Please Try Again.", Toast.LENGTH_LONG).show();
            }
        });

    }

    public void goToLast() {
        recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount() - 1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_chat, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.sendImageChat:
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
                if(prev != null) {
                    fragmentTransaction.remove(prev);
                }
                fragmentTransaction.addToBackStack(null);
                DialogFragment dialogFragment = new SendImageFragment();
                Bundle b = new Bundle();
                b.putString("groupId",groupId);
                b.putString("userId",userId);
                b.putString("userName",userName);
                dialogFragment.setArguments(b);
                dialogFragment.show(fragmentTransaction, "dialog");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        messageAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        messageAdapter.stopListening();
    }

    @Override
    public  void onDestroy(){
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
