package com.example.gamotolastminute.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.gamotolastminute.R;
import com.example.gamotolastminute.model.CartItemsModel;
import com.example.gamotolastminute.model.ProductModel;
import com.example.gamotolastminute.model.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ProductDetailsActivity extends AppCompatActivity {

    ImageView productImage;
    TextView productName, productDescription, productPrice, productNumber, productIngredients;
    ImageView minusProduct, plusProduct;
    Button buttonAddToCart, buttonBuyNow;
    ProductModel productModel;
    List<CartItemsModel> selectedProducts;
    int quantity = 1;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        auth = FirebaseAuth.getInstance();
        selectedProducts = new ArrayList<>(); // Initialize the list here

        final Object obj = getIntent().getSerializableExtra("detailed");

        if (obj instanceof ProductModel) {
            productModel = (ProductModel) obj;
        }

        productImage = findViewById(R.id.productImage);
        productDescription = findViewById(R.id.productDescription);
        productIngredients = findViewById(R.id.productIngredients);
        productName = findViewById(R.id.productName);
        productPrice = findViewById(R.id.productPrice);
        productNumber = findViewById(R.id.productNumber);
        buttonAddToCart = findViewById(R.id.buttonAddtoCart);
        buttonBuyNow = findViewById(R.id.buttonBuyNow);
        minusProduct = findViewById(R.id.minusProduct);
        plusProduct = findViewById(R.id.plusProduct);

        if (productModel != null) {
            Glide.with(getApplicationContext()).load(productModel.getProductImage()).into(productImage);
            productName.setText(productModel.getProductName());
            productDescription.setText(productModel.getProductDescription());
            productIngredients.setText(productModel.getProductIngredients());
            productPrice.setText(String.valueOf(productModel.getProductPrice()));
        }

        productNumber.setText(String.valueOf(quantity));

        minusProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decreaseQuantity();
            }
        });

        plusProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                increaseQuantity();
            }
        });

        buttonAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToCart();
                Toast.makeText(getApplicationContext(), "Product added to cart", Toast.LENGTH_SHORT).show();
            }
        });

        buttonBuyNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add the current product to the selected products list
                CartItemsModel cartItem = new CartItemsModel(
                        productModel.getProductImage(),
                        productModel.getProductName(),
                        (long) productModel.getProductPrice(),
                        (long) quantity
                );
                selectedProducts.add(cartItem);

                double totalPrice = calculateTotalPrice();

                Intent intent = new Intent(ProductDetailsActivity.this, PaymentActivity.class);
                intent.putExtra("SelectedProducts", (Serializable) selectedProducts);
                intent.putExtra("TotalPrice", totalPrice);
                startActivity(intent);
            }
        });
    }

    private void increaseQuantity() {
        quantity++;
        updateQuantityText();
    }

    private void decreaseQuantity() {
        if (quantity > 1) {
            quantity--;
            updateQuantityText();
        } else {
            Toast.makeText(getApplicationContext(), "Minimum quantity is 1", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateQuantityText() {
        productNumber.setText(String.valueOf(quantity));
    }

    private double calculateTotalPrice() {
        return productModel.getProductPrice() * quantity;
    }

    private void addToCart() {
        if (quantity <= 0) {
            Toast.makeText(getApplicationContext(), "Please select at least 1 quantity", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = Objects.requireNonNull(auth.getCurrentUser()).getUid();
        DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(userId)
                .child("cart");

        String productName = productModel.getProductName();
        double productPriceDouble = productModel.getProductPrice();
        long productPrice = (long) productPriceDouble;

        String cartItemId = cartRef.push().getKey();
        CartItemsModel cartItem = new CartItemsModel(productModel.getProductImage(), productName, productPrice, (long) quantity);
        assert cartItemId != null;
        cartRef.child(cartItemId).setValue(cartItem);

        incrementSavedItemsCount();
    }

    private void incrementSavedItemsCount() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
            userRef.runTransaction(new Transaction.Handler() {
                @NonNull
                @Override
                public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                    UserModel userModel = currentData.getValue(UserModel.class);
                    if (userModel != null) {
                        userModel.setSavedItemsCount(userModel.getSavedItemsCount() + 1);
                        currentData.setValue(userModel);
                    }
                    return Transaction.success(currentData);
                }

                @Override
                public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                    // Handle the completion of the transaction if needed
                }
            });
        }
    }
}
