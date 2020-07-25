package com.android.wefour.classmanager.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.wefour.classmanager.R;
import com.android.wefour.classmanager.models.Student;
import com.android.wefour.classmanager.models.Teacher;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//Activity to register the user in the firebase database
public class RegisterActivity extends AppCompatActivity {
    private EditText inputEmail,inputPassword,inputName;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabaseInstance;
    private String userId;
    private String email;
    private Spinner spinnerAppointmentType;
    private String spin;
    private String uId;
    private ValueEventListener detailsListener;
    private DatabaseReference typeReference;
    private FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth=FirebaseAuth.getInstance();

        setContentView(R.layout.activity_register);
        inputEmail=(EditText)findViewById(R.id.emailRegisterEditText);
        inputPassword=(EditText)findViewById(R.id.passwordRegisterEditText);
        inputName=(EditText)findViewById(R.id.nameRegisterEditText);
        progressBar=(ProgressBar)findViewById(R.id.progressBar);
        spinnerAppointmentType = (Spinner) findViewById(R.id.spnTaskTypeRegisterEditText);

        mFirebaseDatabaseInstance= FirebaseDatabase.getInstance();
        List<String> catogeries=new ArrayList<>();
        catogeries.add(0,"Choose Designation");
        catogeries.add("Teacher");
        catogeries.add("Student");
        ArrayAdapter<String> dataAdapter;
        dataAdapter=new ArrayAdapter(this,android.R.layout.simple_spinner_item,catogeries);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAppointmentType.setAdapter(dataAdapter);
    }
   //onClick to implement the registration of the user in the firebase
    public void onRegisterClicked(View view)
    {
        String emailInput=inputEmail.getText().toString().trim();
        String password=inputPassword.getText().toString().trim();
        final String name=inputName.getText().toString().trim();
        spin=spinnerAppointmentType.getSelectedItem().toString();
        if(TextUtils.isEmpty(emailInput))
        {
            Toast.makeText(getApplicationContext(),"Enter email address!",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(password))
        {
            Toast.makeText(getApplicationContext(),"Enter Password!",Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(name))
        {
            Toast.makeText(getApplicationContext(),"Enter Username!",Toast.LENGTH_SHORT).show();
            return;
        }
        if(spin.equals("Choose Designation"))
        {
            Toast.makeText(getApplicationContext(),"Enter Designation",Toast.LENGTH_SHORT).show();
            return;
        }
        if(password.length()<6)
        {
            Toast.makeText(getApplicationContext(),"Password too short,enter minimum 6 characters!",Toast.LENGTH_SHORT).show();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(emailInput, password)
                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Toast.makeText(RegisterActivity.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.INVISIBLE  );
                        if (!task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "Authentication failed." + task.getException(),
                                    Toast.LENGTH_LONG).show();
                            Log.e("MyTag", task.getException().toString());
                        } else {
                            //For teacher
                            if(spin.equals("Teacher")) {
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                userId = user.getUid();
                                email = user.getEmail();
                                Map<String, Object> teacherUpdates = new HashMap<>();
                                teacherUpdates.put("/category/"+userId+"/type","Teacher");
                                Teacher newTeacher = new Teacher(name, email, userId, spin);
                                Map<String, Object> newTeacherValues = newTeacher.toMap();
                                teacherUpdates.put("/teachers/"+userId,newTeacherValues);

                                mFirebaseDatabaseInstance.getReference().updateChildren(teacherUpdates).addOnSuccessListener(
                                        new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(RegisterActivity.this, "Profile Created Successfully.", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                ).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(RegisterActivity.this, "Failure, Please try again.", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                startActivity(new Intent(RegisterActivity.this, TeacherProfileActivity.class));
                                finish();
                            }
                            // for student
                            else {
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                userId = user.getUid();
                                email = user.getEmail();

                                Map<String, Object> studentUpdates = new HashMap<>();
                                studentUpdates.put("/category/"+userId+"/type","Student");
                                Student newStudent = new Student(name, email, userId, spin);
                                Map<String, Object> newStudentValues = newStudent.toMap();
                                studentUpdates.put("/students/"+userId,newStudentValues);

                                mFirebaseDatabaseInstance.getReference().updateChildren(studentUpdates).addOnSuccessListener(
                                        new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(RegisterActivity.this, "Profile Created Successfully.", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                ).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(RegisterActivity.this, "Failure, Please try again.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                startActivity(new Intent(RegisterActivity.this, StudentProfileActivity.class));
                                finish();
                            }
                        }
                    }
                });
    }

    @Override
    public void onDestroy(){
        super.onDestroy();

    }
// on back Pressed
    @Override
    public void onBackPressed() {
        finishAffinity();
    }
// onclick to login if already registered
    public void onLoginClicked(View view) {
        startActivity(new Intent(this, LoginActivity.class));
    }
}
