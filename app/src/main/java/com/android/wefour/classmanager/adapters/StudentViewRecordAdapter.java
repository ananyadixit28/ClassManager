package com.android.wefour.classmanager.adapters;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.wefour.classmanager.R;
import com.android.wefour.classmanager.models.Record;

import java.util.ArrayList;
//Adapter to show and bind the data in a recycler view of the records
// of marks and attendance of courses
public class StudentViewRecordAdapter extends RecyclerView.Adapter<StudentViewRecordAdapter.StudentViewRecordViewHolder> {

    private ArrayList<Record> listOfCourses;

    public static class StudentViewRecordViewHolder extends RecyclerView.ViewHolder {

        public TextView text1,text2,text3;

        public StudentViewRecordViewHolder(View itemView){
            super(itemView);
            text1 = (TextView) itemView.findViewById(R.id.studentCourseName);
            text2 = (TextView) itemView.findViewById(R.id.attendanceStudentTextView);
            text3 = (TextView) itemView.findViewById(R.id.marksStudentTextView);
        }
    }

    // parameterized constructor
    public StudentViewRecordAdapter(ArrayList<Record> list) {
        listOfCourses = list;
    }

    @Override
    public StudentViewRecordAdapter.StudentViewRecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Log.i("rekard","oncreate");
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.student_record_list, parent, false);
        StudentViewRecordViewHolder viewHolder = new StudentViewRecordViewHolder(v);
        return viewHolder;
    }

    //BindViewHolder for the adapter
    @Override
    public void onBindViewHolder(@NonNull StudentViewRecordViewHolder holder, int position) {
        Log.i("rekard","onbind");
        Record r = listOfCourses.get(position);
        holder.text1.setText("Subject Title : " + r.getTitle());
        int a = r.getCoursesAttendances();
        int b = r.getTotalAttendance();
        double per = ((double)a/(double)b)*100;
        holder.text2.setText("Attendance : "+r.getCoursesAttendances() + " out of "+ r.getTotalAttendance()+" ("+String.format("%.2f",per)+"%)");
        holder.text3.setText("Marks : "+r.getCoursesMarks());
    }

    // returns the count of the ArrayList
    @Override
    public int getItemCount() {
        return listOfCourses.size();
    }
}
