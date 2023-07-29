package com.example.gamotolastminute.model;

import java.io.Serializable;

public class CartItemsModel implements Serializable {

    // Properties
    private String cartItemId;
    private String productName;
    private double productPrice;
    private String productImage;
    private Long productQuantity;
    private boolean isSelected;

    public CartItemsModel() {
        // Required empty constructor for Firebase Firestore
    }

    // Constructor with parameters
    public CartItemsModel(String productImage, String productName, double productPrice, Long productQuantity) {
        this.productImage = productImage;
        this.productName = productName;
        this.productPrice = productPrice;
        this.productQuantity = productQuantity;
    }

    // Getter and Setter methods for properties

    public String getCartItemId() {
        return cartItemId;
    }

    public void setCartItemId(String cartItemId) {
        this.cartItemId = cartItemId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
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

    public Long getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(Long productQuantity) {
        this.productQuantity = productQuantity;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
