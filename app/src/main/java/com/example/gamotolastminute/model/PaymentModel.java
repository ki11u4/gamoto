package com.example.gamotolastminute.model;

import java.io.Serializable;

public class PaymentModel implements Serializable {

    // Properties
    private String cartItemId;
    private String productName;
    private String productPrice;
    private String productImage;
    private int productQuantity;
    private boolean isSelected;

    // Constructors
    public PaymentModel() {
    }

    public PaymentModel(String productName, String productPrice, String productImage, int productQuantity) {
        this.productName = productName;
        this.productPrice = productPrice;
        this.productImage = productImage;
        this.productQuantity = productQuantity;
        this.isSelected = false;
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

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public int getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(int productQuantity) {
        this.productQuantity = productQuantity;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
