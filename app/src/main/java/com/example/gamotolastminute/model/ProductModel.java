package com.example.gamotolastminute.model;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.Exclude;
import java.io.Serializable;

public class ProductModel implements Serializable {

    // Properties
    @DocumentId
    String productId;
    String productDescription;
    String productName;
    String productGenericName;
    String productReviews;
    double productPrice;
    String productImage;
    String productIngredients;


    public ProductModel() {
    }

    public ProductModel(String productIngredients, String productDescription, String productImage,
                        String productGenericName, String productName, double productPrice) {

        this.productIngredients = productIngredients;
        this.productDescription = productDescription;
        this.productImage = productImage;
        this.productGenericName = productGenericName;
        this.productName = productName;
        this.productPrice = productPrice;
    }

    // Getter and Setter methods for properties

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductGenericName() {
        return productGenericName;
    }

    public void setProductGenericName(String productGenericName) {
        this.productGenericName = productGenericName;
    }

    public String getProductReviews() {
        return productReviews;
    }

    public void setProductReviews(String productReviews) {
        this.productReviews = productReviews;
    }

    public double getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(double productPrice) {
        this.productPrice = productPrice;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public String getProductIngredients() {
        return productIngredients;
    }

    public void setProductIngredients(String productIngredients) {
        this.productIngredients = productIngredients;
    }
}
