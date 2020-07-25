package com.android.wefour.classmanager.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

import androidx.recyclerview.widget.RecyclerView;

import com.android.wefour.classmanager.R;
import com.android.wefour.classmanager.models.Record;
import com.android.wefour.classmanager.models.Student;

public class StudentMarksAdapter extends RecyclerView.Adapter<StudentMarksAdapter.StudentMarksViewHolder> {

    private ArrayList<Student> listOfStudents;
    public String courseId;

    public static class StudentMarksViewHolder extends RecyclerView.ViewHolder {

        public TextView text1, text2;
        public EditText editText;
        public StudentMarksViewHolder(View itemView) {
            super(itemView);
            text1 = itemView.findViewById(R.id.studentNameMarksTextView);
            text2 = itemView.findViewById(R.id.studentPreviousMarksTextView);
            editText = itemView.findViewById(R.id.studentMarksListEditText);

        }
    }

    public StudentMarksAdapter(ArrayList<Student> list, String courseId) {
        listOfStudents = list;
        this.courseId = courseId;
    }

    @Override
    public StudentMarksAdapter.StudentMarksViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.student_marks_list, parent, false);
        StudentMarksAdapter.StudentMarksViewHolder viewHolder = new StudentMarksAdapter.StudentMarksViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(StudentMarksAdapter.StudentMarksViewHolder holder, int position) {
        holder.text1.setText(listOfStudents.get(position).getName());
        Map<String, Record> mapRecord = listOfStudents.get(position).getCoursesRecords();
        Record record = mapRecord.get(courseId);
        holder.text2.setText(String.valueOf(record.getCoursesMarks()));
    }

    @Override
    public int getItemCount() {
        return listOfStudents.size();
    }
}
