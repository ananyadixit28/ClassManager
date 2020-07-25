package com.android.wefour.classmanager.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.android.wefour.classmanager.R;
import com.android.wefour.classmanager.models.Record;
import com.android.wefour.classmanager.models.Student;

import java.util.ArrayList;
import java.util.Map;

import androidx.recyclerview.widget.RecyclerView;

public class StudentAttendanceAdapter extends RecyclerView.Adapter<StudentAttendanceAdapter.StudentAttendanceViewHolder> {

    private ArrayList<Student> listOfStudents;
    public String courseId;

    public static class StudentAttendanceViewHolder extends RecyclerView.ViewHolder {

        public TextView text1, text2;
        public CheckBox checkBox;

        public StudentAttendanceViewHolder(View itemView) {
            super(itemView);
            text1 = itemView.findViewById(R.id.studentNameAttendanceTextView);
            text2 = itemView.findViewById(R.id.studentPreviousAttendanceTextView);
            checkBox = itemView.findViewById(R.id.studentAttendanceCheckBox);

        }
    }

    public StudentAttendanceAdapter(ArrayList<Student> list, String courseId) {
        listOfStudents = list;
        this.courseId = courseId;
    }

    @Override
    public StudentAttendanceAdapter.StudentAttendanceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.student_attendance_list, parent, false);
        StudentAttendanceViewHolder viewHolder = new StudentAttendanceViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(StudentAttendanceViewHolder holder, int position) {
        holder.text1.setText(listOfStudents.get(position).getName());
        Map<String, Record> mapRecord = listOfStudents.get(position).getCoursesRecords();
        Record record = mapRecord.get(courseId);
        holder.text2.setText(String.valueOf(record.getCoursesAttendances()));
    }

    @Override
    public int getItemCount() {
        return listOfStudents.size();
    }
}