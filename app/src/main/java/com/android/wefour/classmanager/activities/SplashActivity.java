package com.android.wefour.classmanager.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.android.wefour.classmanager.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SplashActivity extends AppCompatActivity {


    private FirebaseDatabase firebaseDatabase;
    private FirebaseDatabase mFirebaseDatabaseInstance;
    private FirebaseAuth mAuth;
    private String uId;
    private ValueEventListener detailsListener;
    private DatabaseReference typeReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mFirebaseDatabaseInstance= FirebaseDatabase.getInstance();
        mAuth= FirebaseAuth.getInstance();
        mFirebaseDatabaseInstance= FirebaseDatabase.getInstance();

        //check if user is logged in or not
        if(mAuth.getCurrentUser()!=null)
        {
            // TODO implementation
            uId = mAuth.getCurrentUser().getUid();
            Log.i("userId ", uId + " " + mAuth.getCurrentUser().getEmail());
            firebaseDatabase = FirebaseDatabase.getInstance();
            typeReference = firebaseDatabase.getReference("/category/"+uId+"/type");

            //send to main page of user depending on teacher or student
            detailsListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String type = dataSnapshot.getValue(String.class);
                    if(type.equals("Teacher")) {
                        startActivity(new Intent(SplashActivity.this, TeacherMainActivity.class));
                        finish();
                    }
                    if(type.equals("Student")) {
                        startActivity(new Intent(SplashActivity.this, StudentMainActivity.class));
                        finish();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };

            typeReference.addListenerForSingleValueEvent(detailsListener);
        }
        else
        {
            startActivity(new Intent(SplashActivity.this, RegisterActivity.class));
            finish();
        }
    }

    //called when app is resumed
    @Override
    protected void onResume()
    {
        super.onResume();
        if(mAuth.getCurrentUser()!=null)
        {
            // TODO implementation
            uId = mAuth.getCurrentUser().getUid();
            firebaseDatabase = FirebaseDatabase.getInstance();
            typeReference = firebaseDatabase.getReference("/category/"+uId+"/type");
            detailsListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String type = dataSnapshot.getValue(String.class);
                    if(type.equals("Teacher")) {
                        startActivity(new Intent(SplashActivity.this,TeacherMainActivity.class));
                        finish();
                    }
                    if(type.equals("Student")) {
                        startActivity(new Intent(SplashActivity.this,StudentMainActivity.class));
                        finish();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };

            typeReference.addListenerForSingleValueEvent(detailsListener);
        }
        else
        {
            startActivity(new Intent(SplashActivity.this,RegisterActivity.class));
            finish();
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();

    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}
