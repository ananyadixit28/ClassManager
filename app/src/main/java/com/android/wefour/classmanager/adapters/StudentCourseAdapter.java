package com.android.wefour.classmanager.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.android.wefour.classmanager.models.Course;
import com.android.wefour.classmanager.R;

import java.util.ArrayList;


public class StudentCourseAdapter extends RecyclerView.Adapter<StudentCourseAdapter.StudentCourseViewHolder> {

    private ArrayList<Course> listOfCourses;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Course item);
    }


    public static class StudentCourseViewHolder extends RecyclerView.ViewHolder {

        public TextView text1, text2;
        public String courseId;

        public StudentCourseViewHolder(View itemView) {
            super(itemView);
            text1 = itemView.findViewById(R.id.studentCourseName);
            text2 = itemView.findViewById(R.id.keyCourseStudentTextView);
        }

        public void bind(final Course listOfCourses, final OnItemClickListener listener) {
            text1.setText(listOfCourses.getTitle());
            text2.setText("Key : "+String.valueOf(listOfCourses.getCourseId()));
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(listOfCourses);
                }
            });
        }
    }

    public StudentCourseAdapter(ArrayList<Course> list,OnItemClickListener listener) {
        listOfCourses = list;
        this.listener = listener;
    }
    public StudentCourseAdapter(ArrayList<Course> list) {
        listOfCourses = list;
        this.listener = null;
    }

    @Override
    public StudentCourseAdapter.StudentCourseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.student_course_list, parent, false);
        StudentCourseViewHolder viewHolder = new StudentCourseViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(StudentCourseViewHolder holder, int position) {
        holder.bind(listOfCourses.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return listOfCourses.size();
    }
}
