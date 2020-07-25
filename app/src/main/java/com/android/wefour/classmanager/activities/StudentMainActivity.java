package com.android.wefour.classmanager.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.solver.widgets.Snapshot;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.wefour.classmanager.JoinClassDialogStudent;
import com.android.wefour.classmanager.R;
import com.android.wefour.classmanager.adapters.StudentCourseAdapter;
import com.android.wefour.classmanager.models.Course;
import com.android.wefour.classmanager.models.Record;
import com.android.wefour.classmanager.models.Student;
import com.android.wefour.classmanager.models.Teacher;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StudentMainActivity extends AppCompatActivity implements JoinClassDialogStudent.ExampleDialogListener{

    private static final String TAG = StudentMainActivity.class.getSimpleName();

    TextView nameStudent;
    Teacher loggedInTeacher;

    private DatabaseReference courseRef,findCourseRef,findCourseKeyRef;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference nameRef,studentReference;
    private ValueEventListener nameListener,findCourseKeyListener;
    private ChildEventListener courseListener;
    private  ValueEventListener findCourseListener;
    private Query allCourses;
    private Map<String,Boolean> removeDuplicacy;

    private ValueEventListener findCourseListenerKey;
    private DatabaseReference findCourseRefKey;

    private ArrayList<Course> listOfCourses;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private StudentCourseAdapter studentCourseAdapter;
    private String inputUserId;
    private String getCourseKey;
    private ValueEventListener studentListener;
    private ValueEventListener checkCourseRecord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_main);

        removeDuplicacy = new HashMap<>();

        final FirebaseUser currentStudentLoggedIn = FirebaseAuth.getInstance().getCurrentUser();

        firebaseDatabase = FirebaseDatabase.getInstance();
        inputUserId = currentStudentLoggedIn.getUid();
        nameRef = firebaseDatabase.getReference("/students/"+inputUserId);

        //show student name on top
        nameListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                nameStudent = (TextView)findViewById(R.id.nameStudentMainActivityEditText);
                nameStudent.setText(dataSnapshot.getValue(Student.class).getName());
                //}
            }
            @Override
            public void onCancelled(DatabaseError d){}
        };

        nameRef.addListenerForSingleValueEvent(nameListener);




        listOfCourses = new ArrayList<Course>();
        recyclerView = (RecyclerView)findViewById(R.id.courseStudentMainRecycleView);
        linearLayoutManager = new LinearLayoutManager(StudentMainActivity.this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        studentCourseAdapter = new StudentCourseAdapter(listOfCourses);
        recyclerView.setAdapter(studentCourseAdapter);





        courseRef = firebaseDatabase.getReference("/students/" + inputUserId +"/coursesRecords");

        allCourses = courseRef.orderByKey();

        //add listener to show all the courses in which logged in student is enrolled
        courseListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                Log.i("Course ", dataSnapshot.getKey());
                DatabaseReference cRef = firebaseDatabase.getReference("/courses/"+dataSnapshot.getKey());
                cRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        Course myCourse = dataSnapshot.getValue(Course.class);

                        String check = myCourse.getCourseId();
                        if(!removeDuplicacy.containsKey(check))
                        {
                            listOfCourses.add(myCourse);
                            removeDuplicacy.put(myCourse.getCourseId(),Boolean.TRUE);
                        }
                        Log.i("answer",myCourse.getTitle());
                        recyclerView.setAdapter(new StudentCourseAdapter(listOfCourses, new StudentCourseAdapter.OnItemClickListener() {
                            @Override public void onItemClick(Course course) {
                                showOptions(course.getCourseId());
                            }
                        }));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                Log.i("Course ", dataSnapshot.getKey());
                DatabaseReference cRef = firebaseDatabase.getReference("/courses/"+dataSnapshot.getKey());
                cRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Log.i("course ", dataSnapshot.child("title").getValue(String.class));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
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

        allCourses.addChildEventListener(courseListener);

    }

    //dialog box on click result of each courses
    private void showOptions(String id)
    {

        final String idd = id;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.choose_option)
                .setItems(R.array.studentDialog, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        switch(which){
                            case 0:
                                Intent intents = new Intent(StudentMainActivity.this,GroupChatActivity.class);
                                intents.putExtra("courseId",idd);
                                String name = nameStudent.getText().toString();
                                intents.putExtra("name",name);
                                startActivity(intents);

                                Toast.makeText(StudentMainActivity.this,"join Coversation pressed",Toast.LENGTH_SHORT).show();
                                return;
                        }
                    }
                });
        builder.show();
    }

    //creating menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_student, menu);
        return true;
    }

    //menu on click option ans result
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.logoutStudent:
                FirebaseAuth.getInstance().signOut();

                startActivity(new Intent(StudentMainActivity.this, RegisterActivity.class));
                finish();
                return true;
            case R.id.joinClass:
                /*startActivity(new Intent(TeacherMainActivity.this, CreateClassActivity.class));
                return true;*/
                openDialog();
                return true;
            case R.id.updateStudentProfile:
                startActivity(new Intent(StudentMainActivity.this,StudentProfileActivity.class));
                return true;
            case R.id.viewRecord:
                startActivity(new Intent(StudentMainActivity.this,StudentViewRecordActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //opening dialog to enroll in new course
    public void openDialog()
    {
        JoinClassDialogStudent exampleDialog=new JoinClassDialogStudent();
        exampleDialog.show(getSupportFragmentManager(),"dialog");

    }


    //check if there is any course in the database
    public void checkCourse(final String key)
    {
        firebaseDatabase = FirebaseDatabase.getInstance();
        findCourseRefKey=firebaseDatabase.getReference("/courses/");
        findCourseListenerKey=new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()==null)
                {
                    Toast.makeText(getApplicationContext(),"Course Not Found!",Toast.LENGTH_SHORT).show();
                    return;
                }
                else
                {
                    checkToJoinNext(key);
                    return;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        findCourseRefKey.addListenerForSingleValueEvent(findCourseListenerKey);
    }

    //call method to check if key exist or not

    @Override
    public void applyTexts(final String key) {

        //Toast.makeText(getApplicationContext(), "Key Entered!", Toast.LENGTH_LONG).show();
        checkCourse(key);
    }

    //chack if student is enrolled in same course previously or not
    public void checkToJoinNext(final String key)
    {
        findCourseRef = firebaseDatabase.getReference("/students/"+inputUserId);

        checkCourseRecord = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Student student = dataSnapshot.getValue(Student.class);
                Map<String, Record> map = student.getCoursesRecords();
                if(map==null || map.containsKey(key)==false)
                {
                    Log.i("Join","qwerty");
                    JoinClass(key);
                    return;
                }
                else
                {
                    Log.i("Check","qwerty");
                    return;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {
                Toast.makeText(getApplicationContext(),"Error!",Toast.LENGTH_SHORT).show();
            }
        };
        findCourseRef.addListenerForSingleValueEvent(checkCourseRecord);
    }

    //enroll student in given class key
    void JoinClass(final String key)
    {
        final int[] flag = {0};
        firebaseDatabase.getReference("/courses/"+key)
                .addListenerForSingleValueEvent(studentListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot.getValue()==null)
                        {
                            Toast.makeText(getApplicationContext(), "Course Not Available", Toast.LENGTH_LONG).show();
                            return;
                        }
                        else
                        {
                            Course course = dataSnapshot.getValue(Course.class);
                            Toast.makeText(getApplicationContext(), " Course Found", Toast.LENGTH_SHORT).show();
                            Map<String, Object> studentCourse = new HashMap<>();
                            Record record = new Record(course.getTitle());
                            studentCourse.put("/students/" + inputUserId + "/coursesRecords/" + course.getCourseId(), record);
                            studentCourse.put("/details/" + course.getCourseId() + "/attendances/" + inputUserId, 0);
                            studentCourse.put("/details/" + course.getCourseId() + "/marks/" + inputUserId, 0);
//                                Toast.makeText(getApplicationContext(),"Record Updated Successfully!",Toast.LENGTH_SHORT).show();
                            studentReference = firebaseDatabase.getReference();
                            studentReference.updateChildren(studentCourse).addOnSuccessListener(
                                    new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            //Toast.makeText(getApplicationContext(), "Data Updated Successfully.", Toast.LENGTH_SHORT).show();
                                            //finish();
                                        }
                                    }
                            ).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(StudentMainActivity.this, "Failure, Please try again.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    //destroy attached listener
    @Override
    public void onDestroy(){
        super.onDestroy();
        if(nameListener!=null)
            nameRef.removeEventListener(nameListener);
        if(courseListener!=null)
            allCourses.removeEventListener(courseListener);
        if(studentListener!=null)
            studentReference.removeEventListener(studentListener);
        if(findCourseListener!=null)
            findCourseRef.removeEventListener(findCourseListener);
        if(findCourseKeyListener!=null)
            findCourseKeyRef.removeEventListener(findCourseListener);
        if(findCourseListenerKey!=null)
            findCourseRefKey.removeEventListener(findCourseListenerKey);
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}