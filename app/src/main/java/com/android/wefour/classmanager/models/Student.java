package com.android.wefour.classmanager.models;

import com.android.wefour.classmanager.models.Record;
import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;
// class for Student
public class Student {

    private String studentId;
    private String name;
    private String email;
    private String regNo;
    private String type;
    private String institute;
    private String city;
    private String state;
    private String country;
    private int photoUploaded;

    private Map<String, Record> coursesRecords;

    public Student() {

    }

    public Student(String name, String email, String teacherId, String type) {
        this.name = name;
        this.email = email;
        this.studentId = teacherId;
        this.type = type;
        this.regNo = "";
        this.institute = "";
        this.city = "";
        this.state = "";
        this.country = "";
        this.coursesRecords = new HashMap<String, Record>();
        this.photoUploaded = 0;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("studentId",studentId);
        result.put("name",name);
        result.put("email",email);
        result.put("type",type);
        result.put("regNo", regNo);
        result.put("institute", institute);
        result.put("city", city);
        result.put("state", state);
        result.put("country", country);
        result.put("coursesRecords", coursesRecords);
        this.photoUploaded = 0;
        return result;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRegNo() {
        return regNo;
    }

    public void setRegNo(String regNo) {
        this.regNo = regNo;
    }

    public String getInstitute() {
        return institute;
    }

    public void setInstitute(String institute) {
        this.institute = institute;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Map<String, Record> getCoursesRecords() {
        return coursesRecords;
    }

    public void setCoursesRecords(Map<String, Record> coursesRecords) {
        this.coursesRecords = coursesRecords;
    }

    public int getPhotoUploaded() {
        return photoUploaded;
    }

    public void setPhotoUploaded(int photoUploaded) {
        this.photoUploaded = photoUploaded;
    }


}
