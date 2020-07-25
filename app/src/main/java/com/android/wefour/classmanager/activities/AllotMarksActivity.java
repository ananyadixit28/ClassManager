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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.wefour.classmanager.R;
import com.android.wefour.classmanager.models.Student;
import com.android.wefour.classmanager.adapters.StudentMarksAdapter;
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

public class AllotMarksActivity extends AppCompatActivity {


    private static final String TAG = AllotMarksActivity.class.getSimpleName();

    private DatabaseReference marksListReference, courseReference;
    private ValueEventListener detailsListener;
    private ChildEventListener marksListener;
    private FirebaseDatabase firebaseDatabase;
    private ArrayList<Student> listOfStudents;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private StudentMarksAdapter studentMarksAdapter;
    private String courseId, globalDetailsId, courseTitleString;
    private TextView courseTitle;
    private Query allStudents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allot_marks);
        Intent intent = getIntent();
        courseId = intent.getStringExtra("courseId");
        courseTitleString = intent.getStringExtra("courseTitle");
        //courseId = "CS101";
        listOfStudents = new ArrayList<>();
        firebaseDatabase = FirebaseDatabase.getInstance();
        courseTitle = (TextView)findViewById(R.id.cousreTitleMarksTextView);

        courseTitle.setText(courseTitleString);
        Log.i("allothua", courseId);
        populateList(courseId);
    }

    // populating the list using detailsID of the course
    private void populateList(String detailsId)
    {
        globalDetailsId = detailsId;

        listOfStudents = new ArrayList<>();
        recyclerView = findViewById(R.id.allotMarksRecyclerView);
        linearLayoutManager = new LinearLayoutManager(AllotMarksActivity.this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        firebaseDatabase = FirebaseDatabase.getInstance();
        marksListReference = firebaseDatabase.getReference("/details/"+detailsId+"/marks");
        studentMarksAdapter = new StudentMarksAdapter(listOfStudents, courseId);
        recyclerView.setAdapter(studentMarksAdapter);

        allStudents = marksListReference.orderByKey();

        //add listener to fetch marks of students
        marksListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable final String s) {
                String studentId = dataSnapshot.getKey();
                DatabaseReference studentReference = firebaseDatabase.getReference("/students/"+studentId);
                studentReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Student student = dataSnapshot.getValue(Student.class);
                        Log.i("student: ", student.getName());
                        listOfStudents.add(student);
                        studentMarksAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                Log.i(TAG, "onChildAdded: ");
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        allStudents.addChildEventListener(marksListener);
    }

    //to allot new marks to students
    public void allotMarksClicked(View view) {
        String studentId;
        int newMarks;
        View rowView;
        int size = listOfStudents.size();
        DatabaseReference updateMarksReference;
        Log.i(TAG, "allotMarksClicked: size: " + size);
        EditText inputMarks;
        Map<String , Object> updateMarks = new HashMap<>();
        for(int i=0;i<size;i++)
        {
            rowView = recyclerView.getChildAt(i);
            studentId = listOfStudents.get(i).getStudentId();
            inputMarks = (EditText)rowView.findViewById(R.id.studentMarksListEditText);
            //update marks of students if new marks are allotted
            if(!inputMarks.getText().toString().equals(""))
            {
                newMarks = Integer.parseInt(inputMarks.getText().toString());
                updateMarks.put("/details/"+globalDetailsId+"/marks/"+studentId, newMarks);
                updateMarks.put("/students/"+studentId+"/coursesRecords/"+courseId+"/coursesMarks", newMarks);
            }
        }
        try{
            updateMarksReference = firebaseDatabase.getReference();

            //atomic updates to update marks
            updateMarksReference.updateChildren(updateMarks).addOnSuccessListener(
                    new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(AllotMarksActivity.this, "Marks updated successfuly.", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
            ).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AllotMarksActivity.this, "A problem occurred. Please try again.", Toast.LENGTH_LONG).show();
                }
            });
        }
        catch (Exception e){
            Log.e(TAG, "allotMarksClicked: ",e );
        }
    }

    @Override
    public  void onDestroy(){
        super.onDestroy();
        allStudents.removeEventListener(marksListener);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
