package com.android.wefour.classmanager.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.wefour.classmanager.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

//Activity to let the user login in
public class LoginActivity extends AppCompatActivity {

    final private static String TAG = LoginActivity.class.getSimpleName();

    private EditText inputEmail, inputPassword;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private Spinner spinnerAppointmentType;
    private String spin;
    private String uId;
    private ValueEventListener detailsListener;
    private DatabaseReference typeReference;
    private FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        inputEmail = (EditText) findViewById(R.id.emailLoginActivityEditText);
        inputPassword = (EditText) findViewById(R.id.passwordLoginActivityEditText);
        progressBar = (ProgressBar) findViewById(R.id.progressBarLoginActivityProgressBar);

        mAuth=FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser()!=null)
        {
            // TODO implementation


        }

    }

    @Override
    protected void onResume() {
        super.onResume();



        progressBar.setVisibility(View.INVISIBLE);
    }

  //onClick to implement login activity
    public void loginButtonClicked(View view) {
        String email = inputEmail.getText().toString();
        final String password = inputPassword.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);


        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        progressBar.setVisibility(View.INVISIBLE);
                        if (!task.isSuccessful()) {

                            if (password.length() < 6) {
                                inputPassword.setError(getString(R.string.minimum_password));
                            } else {
                                Toast.makeText(LoginActivity.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                            }
                        } else {
                            //getting the id
                            uId = mAuth.getCurrentUser().getUid();
                            firebaseDatabase = FirebaseDatabase.getInstance();
                            typeReference = firebaseDatabase.getReference("/category/" + uId + "/type");
                            detailsListener = new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    String type = dataSnapshot.getValue(String.class);
                                    if (type.equals("Teacher")) {
                                        startActivity(new Intent(LoginActivity.this, TeacherMainActivity.class));
                                        finish();
                                    }
                                    if (type.equals("Student")) {
                                        startActivity(new Intent(LoginActivity.this, StudentMainActivity.class));
                                        finish();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            };

                            typeReference.addListenerForSingleValueEvent(detailsListener);
                        }
                    }
                });
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if(detailsListener!=null)
            typeReference.removeEventListener(detailsListener);
    }

    // on pressing the back button
    @Override
    public void onBackPressed() {
        finish();
    }

    //onClick to register if not registered
    public void onRegisterClicked(View view) {
        startActivity(new Intent(this, RegisterActivity.class));
    }
}
