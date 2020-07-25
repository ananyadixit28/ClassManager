package com.android.wefour.classmanager.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.wefour.classmanager.GlideApp;
import com.android.wefour.classmanager.R;
import com.android.wefour.classmanager.models.Teacher;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TeacherProfileActivity extends AppCompatActivity {

    private static final String TAG = TeacherProfileActivity.class.getSimpleName();
    private EditText nameText, emailText, regNoText, instituteText, cityText, stateText, countryText;
    private Button profilePhotoButton;
    private ImageView profilePhotoImage;
    // Uri indicates, where the image will be picked from
    private Uri filePath;

    // request code
    private final int PICK_IMAGE_REQUEST = 22;

    // instance for firebase storage and StorageReference
    FirebaseStorage storage;
    StorageReference storageReference, imageReference;

    private DatabaseReference databaseReference, teacherReference;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseUser firebaseUser;
    private ValueEventListener teacherListener;

    private String inputUserId, inputEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_profile);

        nameText = (EditText) findViewById(R.id.nameTeacherEditText);
        emailText = (EditText) findViewById(R.id.emailTeacherEditText);
        regNoText = (EditText) findViewById(R.id.regTeacherEditText);
        instituteText = (EditText) findViewById(R.id.instituteTeacherEditText);
        cityText = (EditText) findViewById(R.id.cityTeacherEditText);
        stateText = (EditText) findViewById(R.id.stateTeacherEditText);
        countryText = (EditText) findViewById(R.id.countryTeacherEditText);
        profilePhotoButton = findViewById(R.id.uploadImageTeacherProfileButton);
        profilePhotoImage = findViewById(R.id.uploadImageTeacherProfileImageView);
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();


        profilePhotoImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectImage();
            }
        });

        // on pressing btnUpload uploadImage() is called
        profilePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Kaam","Nhi");
                uploadImage();
            }
        });

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        inputUserId = firebaseUser.getUid();
        inputEmail = firebaseUser.getEmail();
        imageReference = storageReference.child("profilePhoto/").child(inputUserId);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("/teachers/" + inputUserId);

        teacherListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Teacher teacher = dataSnapshot.getValue(Teacher.class);
                nameText.setText(teacher.getName());
                emailText.setText(teacher.getEmail());
                regNoText.setText(teacher.getRegNo());
                instituteText.setText(teacher.getInstitute());
                cityText.setText(teacher.getCity());
                stateText.setText(teacher.getState());
                countryText.setText(teacher.getCountry());
                Log.i("Photoo",Integer.toString(teacher.getPhotoUploaded()));
                int f = teacher.getPhotoUploaded();
                loadImage(f);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        databaseReference.addListenerForSingleValueEvent(teacherListener);
    }

    private void loadImage(int f) {
        if(f==1)
        {

            Log.i("fffff","1");
            GlideApp.with(this)
                    .load(imageReference)
                    .apply(RequestOptions.skipMemoryCacheOf(true))
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                    .into(profilePhotoImage);
            FirebaseDatabase.getInstance().getReference("/teachers/"+inputUserId+"/photoUploaded").setValue(2);

        }
        else if(f==2) {
            Log.i("fffff","2");
            GlideApp.with(this).load(imageReference)
                    .into(profilePhotoImage);
        }
    }

    private void SelectImage() {

        // Defining Implicit Intent to mobile gallery
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(
                        intent,
                        "Select Image from here..."),
                PICK_IMAGE_REQUEST);
    }

    // Override onActivityResult method
    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent data) {

        super.onActivityResult(requestCode,
                resultCode,
                data);

        // checking request code and result code
        // if request code is PICK_IMAGE_REQUEST and
        // resultCode is RESULT_OK
        // then set image in the image view
        if (requestCode == PICK_IMAGE_REQUEST
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {

            // Get the Uri of data
            filePath = data.getData();
            try {

                // Setting image on image view using Bitmap
                Bitmap bitmap = MediaStore
                        .Images
                        .Media
                        .getBitmap(
                                getContentResolver(),
                                filePath);
                profilePhotoImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                // Log the exception
                e.printStackTrace();
            }
        }
    }

    // UploadImage method
    private void uploadImage() {
        if (filePath != null) {

            Log.i("Kaam","Kiya1");

            // Code for showing progressDialog while uploading
            final ProgressDialog progressDialog
                    = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            // Defining the child of storageReference
            StorageReference ref
                    = storageReference
                    .child(
                            "profilePhoto/"
                                    + inputUserId);

            // adding listeners on upload

            //Log.i("Kaam",filePath.toString());
            ref.putFile(filePath)
                    .addOnSuccessListener(
                            new OnSuccessListener<UploadTask.TaskSnapshot>() {

                                @Override
                                public void onSuccess(
                                        UploadTask.TaskSnapshot taskSnapshot) {

                                    // Image uploaded successfully
                                    // Dismiss dialog
                                    progressDialog.dismiss();
                                    Toast
                                            .makeText(TeacherProfileActivity.this,
                                                    "Image Uploaded!!",
                                                    Toast.LENGTH_SHORT)
                                            .show();
                                    FirebaseDatabase.getInstance().getReference("/teachers/"+inputUserId+"/photoUploaded").setValue(1);
                                }
                            })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            // Error, Image not uploaded
                            progressDialog.dismiss();
                            Toast
                                    .makeText(TeacherProfileActivity.this,
                                            "Failed " + e.getMessage(),
                                            Toast.LENGTH_SHORT)
                                    .show();
                        }
                    })
                    .addOnProgressListener(
                            new OnProgressListener<UploadTask.TaskSnapshot>() {

                                // Progress Listener for loading
                                // percentage on the dialog box
                                @Override
                                public void onProgress(
                                        UploadTask.TaskSnapshot taskSnapshot) {
                                    double progress
                                            = (100.0
                                            * taskSnapshot.getBytesTransferred()
                                            / taskSnapshot.getTotalByteCount());
                                    progressDialog.setMessage(
                                            "Uploaded "
                                                    + (int) progress + "%");
                                }
                            });
        }
    }





    public void updateTeacherProfile(View view) {
        String name, email, regNo, institute, city, state, country;

        name = nameText.getText().toString();
        email = emailText.getText().toString();
        regNo = regNoText.getText().toString();
        institute = instituteText.getText().toString();
        city = cityText.getText().toString();
        state = stateText.getText().toString();
        country = countryText.getText().toString();



        Map<String, Object> teacherUpdates = new HashMap<>();
        teacherUpdates.put("/teachers/"+inputUserId+"/name",name);
        teacherUpdates.put("/teachers/"+inputUserId+"/email",email);
        teacherUpdates.put("/teachers/"+inputUserId+"/regNo",regNo);
        teacherUpdates.put("/teachers/"+inputUserId+"/city",city);
        teacherUpdates.put("/teachers/"+inputUserId+"/state",state);
        teacherUpdates.put("/teachers/"+inputUserId+"/institute",institute);
        teacherUpdates.put("/teachers/"+inputUserId+"/country",country);
        //teacherUpdates.put("/teachers/"+inputUserId+"/photoUploaded",0);

        teacherReference = firebaseDatabase.getReference();
        teacherReference.updateChildren(teacherUpdates).addOnSuccessListener(
                new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(TeacherProfileActivity.this, "Profile Updated Successfully.", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(TeacherProfileActivity.this, TeacherMainActivity.class));
                        finish();
                    }
                }
        ).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(TeacherProfileActivity.this, "Failure, Please try again.", Toast.LENGTH_SHORT).show();
            }
        });

        // TODO change activity
    }

    @Override
    public  void onDestroy(){
        super.onDestroy();
        databaseReference.removeEventListener(teacherListener);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
