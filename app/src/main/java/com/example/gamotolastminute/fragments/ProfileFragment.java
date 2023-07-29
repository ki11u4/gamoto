package com.example.gamotolastminute.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.gamotolastminute.R;
import com.example.gamotolastminute.activities.LoginActivity;
import com.example.gamotolastminute.activities.SettingsAccount;
import com.example.gamotolastminute.model.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragment extends Fragment {

    // Properties
    TextView firstName, savedItemCount, ordersCount;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        // Get the current user
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        // Initialize views
        firstName = root.findViewById(R.id.firstName);
        savedItemCount = root.findViewById(R.id.savedItemCount);
        ordersCount = root.findViewById(R.id.ordersCount);

        if (currentUser != null) {
            // Retrieve user data from Firebase Realtime Database
            String userId = currentUser.getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        UserModel userModel = snapshot.getValue(UserModel.class);
                        if (userModel != null) {
                            // Set user data to the views
                            String firstNameText = userModel.getFirstName();
                            firstName.setText(firstNameText);

                            int savedItemsCount = userModel.getSavedItemsCount();
                            savedItemCount.setText(String.valueOf(savedItemsCount));
                        }
                    } else {
                        Toast.makeText(getActivity(), "User data does not exist", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getActivity(), "Failed to retrieve user data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Click listeners for buttons
        LinearLayout logoutButtonProfile = root.findViewById(R.id.logoutButtonProfile);
        logoutButtonProfile.setOnClickListener(v -> logoutUser());

        LinearLayout aboutUsButton = root.findViewById(R.id.aboutUsButton);
        aboutUsButton.setOnClickListener(v -> showAboutUsDialog());

        LinearLayout myAccountSettings = root.findViewById(R.id.myAccountSettings);
        myAccountSettings.setOnClickListener(v -> openSettingsActivity());

        return root;
    }

    private void logoutUser() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getContext(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    private void showAboutUsDialog() {
        String message = getString(R.string.aboutus);
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("About Us")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    private void openSettingsActivity() {
        Intent intent = new Intent(getActivity(), SettingsAccount.class);
        startActivity(intent);
    }
}
