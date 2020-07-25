package com.android.wefour.classmanager.adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.wefour.classmanager.GlideApp;
import com.android.wefour.classmanager.R;
import com.android.wefour.classmanager.models.ChatMessage;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import androidx.recyclerview.widget.RecyclerView;

public class MessageViewHolder extends RecyclerView.ViewHolder {

    public TextView messageText;
    public TextView messageName;
    public ImageView messageImage;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    public MessageViewHolder(View itemView, int type) {
        super(itemView);
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        if(type==1) {
            messageText = itemView.findViewById(R.id.messageOutContentTextView);
        }
        else if(type==2) {
            messageImage = itemView.findViewById(R.id.messageOutContentImageView);
            messageText = itemView.findViewById(R.id.imageOutCaptionTextView);
        }
        else if(type==3) {
            messageText = itemView.findViewById(R.id.messageInContentTextView);
            messageName = itemView.findViewById(R.id.messageInNameTextView);
        }
        else if(type==4) {
            messageImage = itemView.findViewById(R.id.messageInContentImageView);
            messageName = itemView.findViewById(R.id.messageInNameTextView);
            messageText = itemView.findViewById(R.id.imageInCaptionTextView);
        }
        else {
            //do-nothing
        }
    }

    public void setMessage(ChatMessage chatMessage, int type, String groupId) {
        if(type==1) {
            messageText.setText(chatMessage.getMessage());
        }
        else if(type==2) {
            StorageReference ref = storageReference.child("chats/"+groupId+"/"+chatMessage.getMessageId());
            GlideApp.with(messageImage.getContext()).load(ref).into(messageImage);
            messageText.setText(chatMessage.getMessage());
        }
        else if(type==3) {
            messageText.setText(chatMessage.getMessage());
            messageName.setText(chatMessage.getFromName());
        }
        else if(type==4) {
            StorageReference ref = storageReference.child("chats/"+groupId+"/"+chatMessage.getMessageId());
            GlideApp.with(messageImage.getContext()).load(ref).into(messageImage);
            messageName.setText(chatMessage.getFromName());
            messageText.setText(chatMessage.getMessage());
        }
    }
}
