package com.android.wefour.classmanager.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.wefour.classmanager.models.Course;
import com.android.wefour.classmanager.models.Details;
import com.android.wefour.classmanager.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
//Activity to create a class of the given name
public class CreateClassActivity extends AppCompatActivity {

    private EditText titleEditText;
    private TextView keyTextView,titleTextView;

    private FirebaseDatabase firebaseDatabase;

    private String courseTitle;

    private FirebaseUser currentTeacherLoggedIn;
    String inputUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_class);


        currentTeacherLoggedIn = FirebaseAuth.getInstance().getCurrentUser();

        firebaseDatabase = FirebaseDatabase.getInstance();
        inputUserId = currentTeacherLoggedIn.getUid();

    }

    //create class button on click
    public void createNewClass(View view) {


        titleEditText = (EditText)findViewById(R.id.titleCreateClassEditText);
        courseTitle = titleEditText.getText().toString();
        keyTextView = (TextView)findViewById(R.id.keyCreateClassTextView);
        titleTextView=(TextView)findViewById(R.id.courseTitleTextView);

        if(courseTitle.isEmpty())
        {
            Toast.makeText(CreateClassActivity.this,"Enter valid title",Toast.LENGTH_SHORT).show();
            return;
        }


        final String courseKey = firebaseDatabase.getInstance().getReference().child("courses").push().getKey();

        Course courseNew = new Course(courseKey,inputUserId,courseTitle);
        Map<String, Object> courseNewValues = courseNew.toMap();

        Details detailsNew = new Details(courseKey);

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/details/"+courseKey,detailsNew);
        childUpdates.put("/courses/"+courseKey,courseNewValues);
        childUpdates.put("/teachers/"+inputUserId+"/courses/"+courseKey,courseTitle);
        firebaseDatabase.getInstance().getReference().updateChildren(childUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // Write was successful!
                // ...
                keyTextView.setText(courseKey);
                titleEditText.setText("");
                titleTextView.setText(courseTitle);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Write failed
                // ...
                keyTextView.setText("PROBLEM OCCURRED");
            }
        });
    }

    @Override
    public void onDestroy(){
        super.onDestroy();

    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
