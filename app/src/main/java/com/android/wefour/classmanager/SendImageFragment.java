package com.android.wefour.classmanager;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.wefour.classmanager.activities.GroupChatActivity;
import com.android.wefour.classmanager.models.ChatMessage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class SendImageFragment extends DialogFragment {

    public ImageView imageView;
    public EditText imageCaption;
    public Button sendButton;
    private static final int PICK_IMAGE_REQUEST = 1001;
    private Uri filePath;
    private String groupId, userId, userName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = getArguments();
        groupId = b.getString("groupId");
        userId = b.getString("userId");
        userName = b.getString("userName");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_send_image, container, false);
        imageView = (ImageView)v.findViewById(R.id.sendImageFragmentImageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage(v);
            }
        });
        imageCaption = (EditText)v.findViewById(R.id.sendImageCaptionFragmentEditText);
        sendButton = (Button)v.findViewById(R.id.sendImageFragmentButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendImage(v);
            }
        });
        return v;
    }

    // select image from gallery
    public void selectImage(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(
                        intent,"Select Image"),PICK_IMAGE_REQUEST);
    }

    //update image view to selected image from gallery
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK && data!=null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);
            }
            catch (IOException e) {
                Log.i("IOException", e.toString());
            }
        }
    }

    //upload image to firebase storage
    public void sendImage(View view) {
        if(filePath==null) return;
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("/groupChats/"+groupId+"/messageMap");
        final String messageKey = ref.push().getKey();
        String caption = imageCaption.getText().toString();
        ChatMessage chatMessage = new ChatMessage(caption,messageKey,userId, userName,"image");
        Map<String, Object> newMessage = chatMessage.toMap();
        final Map<String, Object> childUpdates = new HashMap<>();

        childUpdates.put("/groupChats/"+groupId+"/messageMap/"+messageKey,newMessage);
        StorageReference imageReference = FirebaseStorage.getInstance().getReference().child("chats/"+groupId+"/"+messageKey);
        // update databse as well as storage
        imageReference.putFile(filePath).addOnSuccessListener(
                new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(getContext(), "Image Sent.", Toast.LENGTH_SHORT).show();
                        databaseReference.updateChildren(childUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                //recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount() - 1);
                                Activity activity = getActivity();
                                if(activity instanceof GroupChatActivity) {
                                    ((GroupChatActivity)activity).goToLast();
                                }
                                dismiss();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), "Failure to send message. Please Try Again.", Toast.LENGTH_LONG).show();
                            }
                        });

                    }
                }
        ).addOnFailureListener(
                new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Failure !! Please Try Again", Toast.LENGTH_LONG).show();
                    }
                }
        ).addOnProgressListener(
                new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(getContext(), "In Progress.", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }
}
