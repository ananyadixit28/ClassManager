package com.android.wefour.classmanager.models;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;
//class for Teacher
public class Teacher {

    private String teacherId;
    private String name;
    private String email;
    private String regNo;
    private String type;
    private String institute;
    private String city;
    private String state;
    private String country;
    private int photoUploaded;

    private Map<String,String> courses;// = new HashMap<String, Integer>();

    public Teacher() {

    }

    public Teacher(String name, String email, String teacherId, String type) {
        this.name = name;
        this.email = email;
        this.teacherId = teacherId;
        this.type = type;
        this.regNo = "";
        this.institute = "";
        this.city = "";
        this.state = "";
        this.country = "";
        this.courses = new HashMap<String, String>();
        this.photoUploaded = 0;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("teacherId",teacherId);
        result.put("name",name);
        result.put("email",email);
        result.put("type",type);
        result.put("reg_no", regNo);
        result.put("institute", institute);
        result.put("city", city);
        result.put("state", state);
        result.put("country", country);
        result.put("courses", courses);
        result.put("photoUploaded",photoUploaded);
        return result;
    }

    public String getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
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

    public Map<String, String> getCourses() {
        return courses;
    }

    public void setCourses(Map<String, String> courses) {
        this.courses = courses;
    }

    public int getPhotoUploaded() {
        return photoUploaded;
    }

    public void setPhotoUploaded(int photoUploaded) {
        this.photoUploaded = photoUploaded;
    }
}
