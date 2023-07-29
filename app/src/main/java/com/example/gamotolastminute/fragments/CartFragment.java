package com.example.gamotolastminute.fragments;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gamotolastminute.R;
import com.example.gamotolastminute.activities.PaymentActivity;
import com.example.gamotolastminute.adapters.CartItemsAdapter;
import com.example.gamotolastminute.model.CartItemsModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CartFragment extends Fragment implements CartItemsAdapter.OnCartItemRemoveListener {

    RecyclerView recyclerView;
    CartItemsAdapter cartItemsAdapter;
    List<CartItemsModel> cartItemsList;
    FirebaseAuth auth;
    Button buyNowButton;
    OnCartUpdateListener cartUpdateListener;
    OnOrderPlacedListener orderPlacedListener; // Listener for notifying the ProfileFragment

    private static final int PAYMENT_REQUEST_CODE = 100; // Choose any appropriate request code

    public CartFragment() {
        // Required empty public constructor
    }

    public void setOnOrderPlacedListener(OnOrderPlacedListener listener) {
        this.orderPlacedListener = listener;
    }

    public CartFragment(OnCartUpdateListener listener) {
        cartUpdateListener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_cart, container, false);

        recyclerView = rootView.findViewById(R.id.rec_cart);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        cartItemsList = new ArrayList<>();
        cartItemsAdapter = new CartItemsAdapter(getContext(), cartItemsList);
        cartItemsAdapter.setOnCartItemRemoveListener(this);
        recyclerView.setAdapter(cartItemsAdapter);
        buyNowButton = rootView.findViewById(R.id.buyNowButton);

        auth = FirebaseAuth.getInstance();

        loadCartItems();
        updateBuyNowButtonVisibility();

        buyNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the selected items and calculate the totalPrice
                List<CartItemsModel> selectedItems = cartItemsAdapter.getSelectedItems();
                double totalPrice = calculateTotalPrice(selectedItems);

                // Pass the selected items and total price to PaymentActivity
                Intent intent = new Intent(getActivity(), PaymentActivity.class);
                intent.putExtra("SelectedProducts", (Serializable) selectedItems);
                intent.putExtra("TotalPrice", totalPrice);
                startActivityForResult(intent, PAYMENT_REQUEST_CODE);
            }
        });

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PAYMENT_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            List<String> purchasedItemIds = data.getStringArrayListExtra("PurchasedItemIds");
            int numberOfOrders = data.getIntExtra("NumberOfOrders", 0); // Get the number of orders placed

            if (purchasedItemIds != null) {
                for (String cartItemId : purchasedItemIds) {
                    deleteCartItem(cartItemId);
                }

                // Notify the listener (ProfileFragment) about the number of orders placed
                if (orderPlacedListener != null) {
                    orderPlacedListener.onOrderPlaced(numberOfOrders);
                }
            }
        }
    }

    @Override
    public void onCartItemRemove(CartItemsModel item) {
        String cartItemId = item.getCartItemId();
        deleteCartItem(cartItemId);

        // Notify the CartFragment about the change in savedItemsCount
        if (cartUpdateListener != null) {
            cartUpdateListener.onCartItemRemoved();
        }
    }

    @Override
    public void onCartItemSelectionChanged() {
        updateBuyNowButtonVisibility();
    }

    private void deleteCartItem(String cartItemId) {
        final String userId = auth.getCurrentUser().getUid();
        DatabaseReference userCartRef = FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(userId)
                .child("cart")
                .child(cartItemId);

        userCartRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                CartItemsModel cartItem = snapshot.getValue(CartItemsModel.class);
                if (cartItem != null) {
                    int updatedQuantity = (int) (cartItem.getProductQuantity() - 1);
                    if (updatedQuantity <= 0) {
                        // If the updated quantity is less than or equal to 0, remove the item completely from the cart
                        userCartRef.removeValue()
                                .addOnSuccessListener(unused -> {
                                    // Item deleted successfully, reload cart items and update button visibility
                                    loadCartItems();
                                    updateBuyNowButtonVisibility();
                                })
                                .addOnFailureListener(e -> {
                                    // Handle the error
                                });
                    } else {
                        // If the updated quantity is greater than 0, just update the quantity value in the database
                        userCartRef.child("productQuantity").setValue(updatedQuantity)
                                .addOnSuccessListener(unused -> {
                                    // Quantity updated successfully, update cartItemsList with the new quantity
                                    for (CartItemsModel item : cartItemsList) {
                                        if (item.getCartItemId().equals(cartItemId)) {
                                            item.setProductQuantity((long) updatedQuantity);
                                            break;
                                        }
                                    }
                                    cartItemsAdapter.notifyDataSetChanged();

                                    // Update the UI and button visibility
                                    updateBuyNowButtonVisibility();
                                })
                                .addOnFailureListener(e -> {
                                    // Handle the error
                                });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle the error
            }
        });
    }



    private void updateBuyNowButtonVisibility() {
        boolean isAnyItemSelected = !cartItemsAdapter.getSelectedItems().isEmpty();
        buyNowButton.setVisibility(isAnyItemSelected ? View.VISIBLE : View.GONE);
    }

    private void loadCartItems() {
        final String userId = auth.getCurrentUser().getUid();
        DatabaseReference userCartRef = FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(userId)
                .child("cart");

        userCartRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cartItemsList.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    CartItemsModel cartItem = dataSnapshot.getValue(CartItemsModel.class);

                    if (cartItem != null) {
                        // Set the cartItemId
                        String cartItemId = dataSnapshot.getKey();
                        cartItem.setCartItemId(cartItemId);

                        cartItemsList.add(cartItem);
                    }
                }

                cartItemsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle the error
            }
        });
    }

    private double calculateTotalPrice(List<CartItemsModel> items) {
        double totalPrice = 0;
        for (CartItemsModel item : items) {
            totalPrice += item.getProductPrice() * item.getProductQuantity();
        }
        return totalPrice;
    }

    public interface OnCartUpdateListener {
        void onCartItemRemoved();
    }

    public interface OnOrderPlacedListener {
        void onOrderPlaced(int numberOfOrders);
    }
}
