package com.example.gamotolastminute.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gamotolastminute.R;
import com.example.gamotolastminute.adapters.PaymentAdapter;
import com.example.gamotolastminute.model.CartItemsModel;
import com.example.gamotolastminute.model.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PaymentActivity extends AppCompatActivity {

    RecyclerView rec_payment;
    TextView firstName, phoneNumber, address, totalPriceTextView;
    Button buttonBuyNow;
    UserModel userModel;
    FirebaseUser currentUser;
    DatabaseReference userRef;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        rec_payment = findViewById(R.id.rec_payment);
        firstName = findViewById(R.id.firstName);
        phoneNumber = findViewById(R.id.phoneNumber);
        address = findViewById(R.id.address);
        totalPriceTextView = findViewById(R.id.totalPrice);
        buttonBuyNow = findViewById(R.id.buttonBuyNow);

        if (currentUser != null) {
            String userId = currentUser.getUid();
            userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        userModel = snapshot.getValue(UserModel.class);
                        if (userModel != null) {
                            firstName.setText(userModel.getFirstName());
                            phoneNumber.setText(userModel.getPhoneNumber());
                            address.setText(userModel.getAddress());
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "User data does not exist", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getApplicationContext(), "Failed to retrieve user data", Toast.LENGTH_SHORT).show();
                }
            });
        }

        List<CartItemsModel> selectedProducts = (List<CartItemsModel>) getIntent().getSerializableExtra("SelectedProducts");
        double totalPrice = getIntent().getDoubleExtra("TotalPrice", 0.0);

        // Check if there are selected products
        if (selectedProducts != null && !selectedProducts.isEmpty()) {
            // Set up the RecyclerView for selected products
            LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            rec_payment.setLayoutManager(layoutManager);
            PaymentAdapter recPaymentAdapter = new PaymentAdapter(this, selectedProducts);
            rec_payment.setAdapter(recPaymentAdapter);

            // Display the total price
            totalPriceTextView.setText(String.format("Total Price: %.2f", totalPrice));
        } else {
            // Show a message or handle the case where no products were selected
            Toast.makeText(this, "No products selected", Toast.LENGTH_SHORT).show();
        }

        // Set the total price in the TextView
        TextView totalPriceLabel = findViewById(R.id.totalPrice);
        totalPriceLabel.setText(String.format("%.2f", totalPrice));

        buttonBuyNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<CartItemsModel> selectedProducts = (List<CartItemsModel>) getIntent().getSerializableExtra("SelectedProducts");
                double totalPrice = getIntent().getDoubleExtra("TotalPrice", 0.0);

                // Check if there are selected products
                if (selectedProducts != null && !selectedProducts.isEmpty()) {
                    // Set up the RecyclerView for selected products
                    LinearLayoutManager layoutManager = new LinearLayoutManager(PaymentActivity.this, LinearLayoutManager.HORIZONTAL, false);
                    rec_payment.setLayoutManager(layoutManager);
                    PaymentAdapter recPaymentAdapter = new PaymentAdapter(PaymentActivity.this, selectedProducts);
                    rec_payment.setAdapter(recPaymentAdapter);

                    // Display the total price
                    totalPriceTextView.setText(String.format("Total Price: %.2f", totalPrice));

                    // Pass the purchased item IDs back to CartFragment
                    List<String> purchasedItemIds = new ArrayList<>();
                    for (CartItemsModel selectedItem : selectedProducts) {
                        purchasedItemIds.add(selectedItem.getCartItemId());
                    }
                    Intent intent = new Intent();
                    intent.putStringArrayListExtra("PurchasedItemIds", (ArrayList<String>) purchasedItemIds);

                    // Also pass the number of orders placed
                    intent.putExtra("NumberOfOrders", purchasedItemIds.size());
                    setResult(RESULT_OK, intent);
                } else {
                    // Show a message or handle the case where no products were selected
                    Toast.makeText(PaymentActivity.this, "No products selected", Toast.LENGTH_SHORT).show();
                }

                showSuccessDialog();
            }
        });
    }

    private void showSuccessDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Success")
                .setMessage("Your order is on process")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Handle the "OK" button click if needed
                        finish(); // Close the PaymentActivity after successful payment
                    }
                })
                .show();
    }
}
