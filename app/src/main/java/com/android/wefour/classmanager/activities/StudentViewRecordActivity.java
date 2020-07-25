package com.android.wefour.classmanager.activities;

import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.wefour.classmanager.R;
import com.android.wefour.classmanager.models.Record;
import com.android.wefour.classmanager.adapters.StudentViewRecordAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import java.util.ArrayList;

//Activity to implement student record of a particular course
public class StudentViewRecordActivity extends AppCompatActivity {

    private ChildEventListener courseListener;
    private Query allCourses;
    private RecyclerView recyclerView;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference courseRef;
    private String uId;
    private ArrayList<Record> listOfCourses;
    private StudentViewRecordAdapter studentViewRecordAdapter;
    private LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_view_record);

        final FirebaseUser currentStudentLoggedIn = FirebaseAuth.getInstance().getCurrentUser();
        uId = currentStudentLoggedIn.getUid();
        firebaseDatabase = FirebaseDatabase.getInstance();


        listOfCourses = new ArrayList<Record>();
        recyclerView = (RecyclerView)findViewById(R.id.recordStudentRecycleView);
        linearLayoutManager = new LinearLayoutManager(StudentViewRecordActivity.this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        studentViewRecordAdapter = new StudentViewRecordAdapter(listOfCourses);
        recyclerView.setAdapter(studentViewRecordAdapter);

        courseRef = firebaseDatabase.getReference("/students/" + uId + "/coursesRecords");
        allCourses = courseRef.orderByKey();

        //child event listener to show the record and get the attendance
        // and marks of that particular course from the firebase
        courseListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                Log.i("Key :- ", dataSnapshot.getKey());
                Record record = dataSnapshot.getValue(Record.class);

                Log.i("Get Course Title - ", record.getTitle());
                Log.i("Get course Attendance ", "Attendance : " + record.getCoursesAttendances());
                Log.i("Get course marks ", "Marks : " + record.getCoursesMarks());

                listOfCourses.add(record);
                studentViewRecordAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        allCourses.addChildEventListener(courseListener);
    }

    // on Destroy
    public  void onDestroy(){
        super.onDestroy();
        allCourses.removeEventListener(courseListener);
    }

    // on pressing the back button
    @Override
    public void onBackPressed() {
        finish();
    }
}
