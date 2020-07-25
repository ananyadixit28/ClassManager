package com.android.wefour.classmanager.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.android.wefour.classmanager.models.Course;
import com.android.wefour.classmanager.R;
import com.android.wefour.classmanager.models.Student;
import com.android.wefour.classmanager.adapters.StudentAttendanceAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TakeAttendanceActivity extends AppCompatActivity {

    private static final String TAG = TakeAttendanceActivity.class.getSimpleName();

    private DatabaseReference attendanceListReference, courseReference, studentReference;
    private FirebaseDatabase firebaseDatabase;
    private ArrayList<Student> listOfStudents;
    private ValueEventListener detailsListener, studentListener;
    private ChildEventListener attendanceListener;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private StudentAttendanceAdapter studentAttendanceAdapter;
    private String courseId;
    private String globalDetailsId;
    private int prevTotalAttendance;
    private Query allStudents;

    private TextView courseTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("details", "onCreate: ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_attendance);
        Intent intent = getIntent();
        courseId = intent.getStringExtra("courseId");
        //courseId = "CS101";
        courseTitle = (TextView)findViewById(R.id.cousreTitleAttendanceTextView);
        firebaseDatabase = FirebaseDatabase.getInstance();
        courseReference = firebaseDatabase.getReference("/courses/"+courseId);
        //fetch meta data of course
        detailsListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Course course = dataSnapshot.getValue(Course.class);
                String detailsId = course.getCourseId();
                int attendance = course.getTotalAttendance();
                courseTitle.setText(course.getTitle());
                populateList(detailsId, attendance);
                Log.i("detail", detailsId);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        courseReference.addListenerForSingleValueEvent(detailsListener);
    }

    //populate list with list of students
    private void populateList(String detailsId, int attendance)
    {
        Log.i("detail", detailsId);
        globalDetailsId = detailsId;
        prevTotalAttendance = attendance;
        listOfStudents = new ArrayList<Student>();

        recyclerView = findViewById(R.id.recyclerViewTakeAttendance);
        linearLayoutManager = new LinearLayoutManager(TakeAttendanceActivity.this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        firebaseDatabase = FirebaseDatabase.getInstance();
        attendanceListReference = firebaseDatabase.getReference("/details/"+detailsId+"/attendances");
        studentAttendanceAdapter = new StudentAttendanceAdapter(listOfStudents, courseId);

        recyclerView.setAdapter(studentAttendanceAdapter);
        allStudents = attendanceListReference.orderByKey();

        //listener to fetch list of students and their attendances
        attendanceListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String studentId = dataSnapshot.getKey();
                DatabaseReference studentRef = firebaseDatabase.getReference("/students/"+studentId);
                Log.i("student: ", studentId);
                studentRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Student student = dataSnapshot.getValue(Student.class);
                        Log.i("student", student.toString());
                        listOfStudents.add(student);
                        studentAttendanceAdapter.notifyDataSetChanged();
                        Log.i("studentlistsize ", String.valueOf(listOfStudents.size()));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e(TAG, "onCancelled: ", databaseError.toException());
                    }
                });
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.i("student name,attendance", dataSnapshot.getKey() + dataSnapshot.getValue(Integer.class));
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "onChildRemoved: "+dataSnapshot.getKey() );
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d(TAG, "onChildMoved: "+dataSnapshot.getKey() );
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "onCancelled: ", databaseError.toException());
            }
        };

        allStudents.addChildEventListener(attendanceListener);
    }

    //submit attendance of students and update database
    public void submitAttendanceClicked(View view) {
        Log.i("detail", globalDetailsId);
        String studentId;
        View rowView;
        CheckBox checkBox;
        TextView previous;
        int previousAttendance;
        int size = listOfStudents.size();
        Log.i(TAG, "submitAttendanceClicked: size: " + size);
        DatabaseReference studentAttendanceReference;
        Map<String, Object> newAttendance = new HashMap<>();
        for(int i=0;i<size;i++)
        {
            Log.i("Student ", listOfStudents.get(i).getStudentId());
            rowView = recyclerView.getChildAt(i);
            studentId = listOfStudents.get(i).getStudentId();
            checkBox = (CheckBox)rowView.findViewById(R.id.studentAttendanceCheckBox);
            previous = (TextView)rowView.findViewById(R.id.studentPreviousAttendanceTextView);
            previousAttendance = Integer.parseInt(previous.getText().toString());
            newAttendance.put("/students/"+studentId+"/coursesRecords/"+courseId+"/totalAttendance", prevTotalAttendance+1);
            //to update attendance of student if he/she is present
            if(checkBox.isChecked())
            {
                newAttendance.put("/students/"+studentId+"/coursesRecords/"+courseId+"/coursesAttendances", previousAttendance+1 );
                newAttendance.put("/details/"+globalDetailsId+"/attendances/"+studentId, previousAttendance+1);
                Log.i(TAG, "submitAttendanceClicked: " +studentId);
            }
        }
        newAttendance.put("/courses/"+courseId+"/totalAttendance", prevTotalAttendance+1);
        try {
            studentAttendanceReference = firebaseDatabase.getReference();
            //atomic updates
            studentAttendanceReference.updateChildren(newAttendance).addOnSuccessListener(
                    new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(TakeAttendanceActivity.this, "Attendance Updated Successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
            ).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(TakeAttendanceActivity.this, "Failure. Please Try Again.", Toast.LENGTH_LONG).show();
                }
            });
        }
        catch (Exception e) {
            Log.e(TAG, "submitAttendanceClicked: ", e);
        }
    }

    @Override
    public  void onDestroy(){
        super.onDestroy();
        courseReference.removeEventListener(detailsListener);
        allStudents.removeEventListener(attendanceListener);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
