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
import com.android.wefour.classmanager.models.Student;
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

public class StudentProfileActivity extends AppCompatActivity {

    private static final String TAG = StudentProfileActivity.class.getSimpleName();
    private EditText nameText, emailText, regNoText, instituteText, cityText, stateText, countryText;

    private DatabaseReference studentReference, databaseReference;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseUser firebaseUser;
    private ValueEventListener studentListener;
    private String inputUserId, inputEmail;private Button profilePhotoButton;
    private ImageView profilePhotoImage;
    // Uri indicates, where the image will be picked from
    private Uri filePath;

    // request code
    private final int PICK_IMAGE_REQUEST = 23;

    // instance for firebase storage and StorageReference
    FirebaseStorage storage;
    StorageReference storageReference, imageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_profile);

        nameText = (EditText)findViewById(R.id.nameStudentEditText);
        emailText = (EditText)findViewById(R.id.emailStudentEditText);
        regNoText = (EditText)findViewById(R.id.regStudentEditText);
        instituteText = (EditText)findViewById(R.id.instituteStudentEditText);
        cityText = (EditText)findViewById(R.id.cityStudentEditText);
        stateText = (EditText)findViewById(R.id.stateStudentEditText);
        countryText = (EditText)findViewById(R.id.countryStudentEditText);
        profilePhotoButton = findViewById(R.id.uploadImageStudentProfileButton);
        profilePhotoImage = findViewById(R.id.uploadImageStudentProfileImageView);
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

                uploadImage();
            }
        });

        firebaseDatabase = FirebaseDatabase.getInstance();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        inputUserId = firebaseUser.getUid();
        inputEmail = firebaseUser.getEmail();
        imageReference = storageReference.child("profilePhoto/").child(inputUserId);
        databaseReference = firebaseDatabase.getReference("/students/"+inputUserId);

        studentListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Student student = dataSnapshot.getValue(Student.class);
                nameText.setText(student.getName());
                emailText.setText(student.getEmail());
                regNoText.setText(student.getRegNo());
                instituteText.setText(student.getInstitute());
                cityText.setText(student.getCity());
                stateText.setText(student.getState());
                countryText.setText(student.getCountry());
                int f = student.getPhotoUploaded();
                loadImage(f);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        databaseReference.addListenerForSingleValueEvent(studentListener);
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
            FirebaseDatabase.getInstance().getReference("/students/"+inputUserId+"/photoUploaded").setValue(2);

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
                                            .makeText(StudentProfileActivity.this,
                                                    "Image Uploaded!!",
                                                    Toast.LENGTH_SHORT)
                                            .show();
                                    FirebaseDatabase.getInstance().getReference("/students/"+inputUserId+"/photoUploaded").setValue(1);
                                }
                            })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            // Error, Image not uploaded
                            progressDialog.dismiss();
                            Toast
                                    .makeText(StudentProfileActivity.this,
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

    public void updateStudentProfile(View view) {
        String name, email, regNo, institute, city, state, country;

        name = nameText.getText().toString();
        email = emailText.getText().toString();
        regNo = regNoText.getText().toString();
        institute = instituteText.getText().toString();
        city = cityText.getText().toString();
        state = stateText.getText().toString();
        country = countryText.getText().toString();

        studentReference = firebaseDatabase.getReference();

        Map<String, Object> studentUpdates = new HashMap<>();
        studentUpdates.put("/students/"+inputUserId+"/name",name);
        studentUpdates.put("/students/"+inputUserId+"/email",email);
        studentUpdates.put("/students/"+inputUserId+"/regNo",regNo);
        studentUpdates.put("/students/"+inputUserId+"/city",city);
        studentUpdates.put("/students/"+inputUserId+"/state",state);
        studentUpdates.put("/students/"+inputUserId+"/institute",institute);
        studentUpdates.put("/students/"+inputUserId+"/country",country);

        studentReference.updateChildren(studentUpdates).addOnSuccessListener(
                new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(StudentProfileActivity.this, "Profile Updated Successfully.", Toast.LENGTH_SHORT).show();
                        //TODO change activity
                    }
                }
        ).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(StudentProfileActivity.this, "Failure, Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
        startActivity(new Intent(StudentProfileActivity.this, StudentMainActivity.class));
        finish();
    }

    @Override
    public  void onDestroy(){
        super.onDestroy();
        databaseReference.removeEventListener(studentListener);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
