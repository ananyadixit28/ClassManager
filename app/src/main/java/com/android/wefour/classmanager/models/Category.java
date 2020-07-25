package com.android.wefour.classmanager.models;

import java.util.HashMap;
// Class for category
public class Category {

    private String type;

    public Category() {
    }

    public Category(String type)
    {
        this.type  = type;
    }

    public void setCategory(String category) {
        this.type = category;
    }

    public String getCategory()
    {
        return  this.type;
    }
}
