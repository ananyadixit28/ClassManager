package com.android.wefour.classmanager.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.android.wefour.classmanager.models.Course;
import com.android.wefour.classmanager.R;

import java.util.ArrayList;


public class TeacherCourseAdapter extends RecyclerView.Adapter<TeacherCourseAdapter.TeacherCourseViewHolder> {

    private ArrayList<Course> listOfCourses;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Course item);
    }

    public static class TeacherCourseViewHolder extends RecyclerView.ViewHolder {

        public TextView text1, text2;
        public String courseId;

        public TeacherCourseViewHolder(View itemView) {
            super(itemView);
            text1 = itemView.findViewById(R.id.teacherCourseName);
            text2 = itemView.findViewById(R.id.keyCourseTeacherTextView);
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

    public TeacherCourseAdapter(ArrayList<Course> list,OnItemClickListener listener) {
        listOfCourses = list;
        this.listener = listener;
    }
    public TeacherCourseAdapter(ArrayList<Course> list) {
        listOfCourses = list;
        this.listener = null;
    }

    @Override
    public TeacherCourseAdapter.TeacherCourseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.teacher_course_list, parent, false);
        TeacherCourseViewHolder viewHolder = new TeacherCourseViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(TeacherCourseViewHolder holder, int position) {
        holder.bind(listOfCourses.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return listOfCourses.size();
    }
}
