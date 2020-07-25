package com.android.wefour.classmanager.models;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;
//class for course
public class Course {

    private String title;
    private String courseId;
    private String teacherId;
    private int totalAttendance;

    public Course()
    {

    }

    public Course(String courseId,String teacherId,String title)
    {
        this.courseId = courseId;
        this.title = title;
        this.teacherId = teacherId;
        this.totalAttendance = 0;
    }

    public String getCourseId() {
        return this.courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public String getTeacherId(){return this.teacherId; };

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }

    public void setTotalAttendance(int totalAttendance)
    {
        this.totalAttendance = totalAttendance;
    }

    public int getTotalAttendance()
    {
        return this.totalAttendance;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("teacherId",teacherId);
        result.put("title",title);
        result.put("courseId",courseId);
        result.put("totalAttendance",totalAttendance);
        return result;
    }
}
