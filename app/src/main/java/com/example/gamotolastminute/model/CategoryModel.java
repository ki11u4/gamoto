package com.example.gamotolastminute.model;

import java.io.Serializable;

public class CategoryModel implements Serializable {

    private String id;
    private String categoryImage;
    private String categoryName;
    private String categoryType;

    public CategoryModel() {
        // Required empty constructor for Firebase Firestore
    }
    public CategoryModel(String categoryImage, String categoryName, String categoryType) {
        this.categoryImage = categoryImage;
        this.categoryName = categoryName;
        this.categoryType = categoryType;
    }

    // Getter and Setter methods for properties

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategoryImage() {
        return categoryImage;
    }

    public void setCategoryImage(String categoryImage) {
        this.categoryImage = categoryImage;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryType() {
        return categoryType;
    }

    public void setCategoryType(String categoryType) {
        this.categoryType = categoryType;
    }
}
