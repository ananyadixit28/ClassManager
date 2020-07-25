package com.android.wefour.classmanager.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.ColorSpace;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.wefour.classmanager.R;
import com.android.wefour.classmanager.adapters.TeacherCourseAdapter;
import com.android.wefour.classmanager.models.Course;
import com.android.wefour.classmanager.models.Teacher;
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

public class TeacherMainActivity extends AppCompatActivity {


    TextView nameTeacher;

    private DatabaseReference courseRef;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference nameRef;
    private ValueEventListener nameListener;
    private ChildEventListener courseListener;
    private Query allCourses;
    private Map<String,Boolean> removeDuplicacy;

    private ArrayList<Course> listOfCourses;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private TeacherCourseAdapter teacherCourseAdapter;
    private String inputUserId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_main);


        final FirebaseUser currentTeacherLoggedIn = FirebaseAuth.getInstance().getCurrentUser();

        removeDuplicacy = new HashMap<>();

        firebaseDatabase = FirebaseDatabase.getInstance();
        inputUserId = currentTeacherLoggedIn.getUid();

        nameRef = firebaseDatabase.getReference("/teachers/"+inputUserId);

        //show teacher's name on the top
        nameListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                nameTeacher = (TextView)findViewById(R.id.nameTeacherMainActivityEditText);
                nameTeacher.setText(dataSnapshot.getValue(Teacher.class).getName());
            }
            @Override
            public void onCancelled(DatabaseError d){}
        };

        nameRef.addListenerForSingleValueEvent(nameListener);


        listOfCourses = new ArrayList<Course>();
        recyclerView = (RecyclerView)findViewById(R.id.courseTeacherMainRecycleView);
        linearLayoutManager = new LinearLayoutManager(TeacherMainActivity.this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        teacherCourseAdapter = new TeacherCourseAdapter(listOfCourses);
        recyclerView.setAdapter(teacherCourseAdapter);


        Log.i("User",inputUserId);

        courseRef = firebaseDatabase.getReference("/teachers/" + inputUserId +"/courses");
        allCourses = courseRef.orderByKey();

        //add listener to fetch list of courses added by teacher
        courseListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {

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

                        System.out.println(listOfCourses.size());
                        Log.i("answer",myCourse.getTitle());
                        recyclerView.setAdapter(new TeacherCourseAdapter(listOfCourses, new TeacherCourseAdapter.OnItemClickListener() {
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
                        Log.i("courses ", dataSnapshot.child("title").getValue(String.class));
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

    //add menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_teacher, menu);
        return true;
    }

    //menu option onclick opions
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();

                startActivity(new Intent(TeacherMainActivity.this, RegisterActivity.class));
                finish();
                return true;
            case R.id.createClass:
                startActivity(new Intent(TeacherMainActivity.this, CreateClassActivity.class));
                return true;
            case R.id.updateTeacherProfile:
                startActivity(new Intent(TeacherMainActivity.this,TeacherProfileActivity.class));
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //dialog box onClick result of each courses
    private void showOptions(String id)
    {

        final String idd = id;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.choose_option)
                .setItems(R.array.teacherDialog, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        switch(which){
                            case 0:
                                Intent intent = new Intent(TeacherMainActivity.this,TakeAttendanceActivity.class);
                                intent.putExtra("courseId",idd);
                                startActivity(intent);

                                return;
                            case 1:
                                Intent intents = new Intent(TeacherMainActivity.this,AllotMarksActivity.class);
                                intents.putExtra("courseId",idd);
                                startActivity(intents);
                                return;
                            case 2:
                                Intent intentss = new Intent(TeacherMainActivity.this,GroupChatActivity.class);
                                intentss.putExtra("courseId",idd);
                                String name = nameTeacher.getText().toString();
                                intentss.putExtra("name", name);
                                startActivity(intentss);

                                return;
                        }
                    }
                });
        builder.show();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        nameRef.removeEventListener(nameListener);
        allCourses.removeEventListener(courseListener);
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}
