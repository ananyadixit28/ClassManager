package com.android.wefour.classmanager.models;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;
//class for record
public class Record {

    private String title;
    private Integer coursesAttendances;
    private Integer coursesMarks;
    private Integer totalAttendance;

    public Record(){}

    public Record(String title)
    {
        this.title = title;
        this.coursesAttendances = 0;
        this.coursesMarks = 0;
        this.totalAttendance = 0;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("title",title);
        result.put("coursesAttendances", coursesAttendances);
        result.put("coursesMarks", coursesMarks);
        result.put("totalAttendance", totalAttendance);
        return result;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCoursesMarks(Integer coursesMarks) {
        this.coursesMarks = coursesMarks;
    }

    public void setCoursesAttendances(Integer coursesAttendances) {
        this.coursesAttendances = coursesAttendances;
    }

    public Integer getTotalAttendance() {
        return totalAttendance;
    }

    public void setTotalAttendance(Integer totalAttendance) {
        this.totalAttendance = totalAttendance;
    }

    public Integer getCoursesAttendances() {
        return coursesAttendances;
    }

    public Integer getCoursesMarks() {
        return coursesMarks;
    }
}
