package com.android.wefour.classmanager.models;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;
//class for details
public class Details {

    private String detailsId;
    private Map<String, Integer> attendances = new HashMap<>();
    private Map<String, Integer> marks = new HashMap<>();

    public Details() {
    }

    public Details(String detailsId)
    {
        this.detailsId  = detailsId;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("detailsId",detailsId);
        result.put("attendances", attendances);
        result.put("marks", marks);
        return result;
    }

    public String getDetailsId() {
        return detailsId;
    }

    public void setDetailsId(String detailsId) {
        this.detailsId = detailsId;
    }

    public Map<String, Integer> getAttendances() {
        return attendances;
    }

    public void setAttendances(Map<String, Integer> attendances) {
        this.attendances = attendances;
    }

    public Map<String, Integer> getMarks() {
        return marks;
    }

    public void setMarks(Map<String, Integer> marks) {
        this.marks = marks;
    }
}
