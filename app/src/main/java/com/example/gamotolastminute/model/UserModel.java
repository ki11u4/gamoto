package com.example.gamotolastminute.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class UserModel implements Serializable {

    // Properties
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String email;
    private String address;
    private int savedItemsCount;
    private int ordersCount;
    private Map<String, CartItemsModel> cart;

    // Constructors
    public UserModel(String firstName, String lastName, String phoneNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
    }

    public UserModel() {
        // Empty constructor required by Firebase
    }

    // Getter and Setter methods for properties

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getSavedItemsCount() {
        return savedItemsCount;
    }

    public void setSavedItemsCount(int savedItemsCount) {
        this.savedItemsCount = savedItemsCount;
    }

    public int getOrdersCount() {
        return ordersCount;
    }

    public void setOrdersCount(int ordersCount) {
        this.ordersCount = ordersCount;
    }

    public Map<String, CartItemsModel> getCart() {
        return cart;
    }

    public void setCart(Map<String, CartItemsModel> cart) {
        this.cart = cart;
    }

}
