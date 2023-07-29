package com.example.gamotolastminute.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.gamotolastminute.R;
import com.example.gamotolastminute.model.UserModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

public class SettingsAccount extends AppCompatActivity {

    TextView viewFirstName, viewLastName, viewEmail, viewPhoneNumber, viewAddress;
    EditText editFirstName, editLastName, editEmail, editPhoneNumber, editAddress;
    UserModel userModel;
    Button cancelButton, editButton, saveButton, deleteButton;
    FirebaseFirestore firestore;
    FirebaseAuth auth;
    DatabaseReference databaseReference;
    FirebaseUser currentUser;

    private boolean isEditing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_account);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        viewFirstName = findViewById(R.id.viewFirstName);
        viewLastName = findViewById(R.id.viewLastName);
        viewEmail = findViewById(R.id.viewEmail);
        viewPhoneNumber = findViewById(R.id.viewPhoneNumber);
        viewAddress = findViewById(R.id.viewAddress);

        editFirstName = findViewById(R.id.editFirstName);
        editLastName = findViewById(R.id.editLastName);
        editEmail = findViewById(R.id.editEmail);
        editPhoneNumber = findViewById(R.id.editPhoneNumber);
        editAddress = findViewById(R.id.editAddress);

        cancelButton = findViewById(R.id.cancelButton);
        editButton = findViewById(R.id.editButton);
        saveButton = findViewById(R.id.saveButton);
        deleteButton = findViewById(R.id.deleteButton);

        fetchUserData();

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelEditing();
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleEditingMode();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChanges();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() { // Add onClick listener for deleteButton
            @Override
            public void onClick(View v) {
                deleteUser();
            }
        });
    }

    private void fetchUserData() {
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        userModel = snapshot.getValue(UserModel.class);
                        if (userModel != null) {
                            String firstNameText = userModel.getFirstName();
                            String lastNameText = userModel.getLastName();
                            String addressText = userModel.getAddress();
                            String emailText = userModel.getEmail();
                            String phoneNumberText = userModel.getPhoneNumber();

                            if (isEditing) {
                                // Display data in editing mode
                                editFirstName.setText(firstNameText);
                                editLastName.setText(lastNameText);
                                editAddress.setText(addressText);
                                editEmail.setText(emailText);
                                editPhoneNumber.setText(phoneNumberText);
                            } else {
                                // Display data in viewing mode
                                viewFirstName.setText(firstNameText);
                                viewLastName.setText(lastNameText);
                                viewAddress.setText(addressText);
                                viewEmail.setText(emailText);
                                viewPhoneNumber.setText(phoneNumberText);
                            }
                        }
                    } else
                        Toast.makeText(getApplicationContext(), "User data does not exist", Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getApplicationContext(), "Failed to retrieve user data", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    private void displayData(String firstName, String lastName, String email, String phoneNumber, String address) {
        viewFirstName.setText(firstName);
        viewLastName.setText(lastName);
        viewEmail.setText(email);
        viewPhoneNumber.setText(phoneNumber);
        viewAddress.setText(address);
    }

    private void toggleEditingMode() {
        if (isEditing) {
            // Switch to viewing mode
            viewFirstName.setVisibility(View.VISIBLE);
            viewLastName.setVisibility(View.VISIBLE);
            viewEmail.setVisibility(View.VISIBLE);
            viewPhoneNumber.setVisibility(View.VISIBLE);
            viewAddress.setVisibility(View.VISIBLE);

            editFirstName.setVisibility(View.GONE);
            editLastName.setVisibility(View.GONE);
            editEmail.setVisibility(View.GONE);
            editPhoneNumber.setVisibility(View.GONE);
            editAddress.setVisibility(View.GONE);

            cancelButton.setVisibility(View.GONE);
            saveButton.setVisibility(View.GONE);

            // Update displayed data with edited values
            String editedFirstName = editFirstName.getText().toString();
            String editedLastName = editLastName.getText().toString();
            String editedEmail = editEmail.getText().toString();
            String editedPhoneNumber = editPhoneNumber.getText().toString();
            String editedAddress = editAddress.getText().toString();
            displayData(editedFirstName, editedLastName, editedEmail, editedPhoneNumber, editedAddress);
        } else {
            // Switch to editing mode
            viewFirstName.setVisibility(View.GONE); // Modified to GONE
            viewLastName.setVisibility(View.GONE); // Modified to GONE
            viewEmail.setVisibility(View.GONE); // Modified to GONE
            viewPhoneNumber.setVisibility(View.GONE); // Modified to GONE
            viewAddress.setVisibility(View.GONE); // Modified to GONE

            editFirstName.setVisibility(View.VISIBLE);
            editLastName.setVisibility(View.VISIBLE);
            editEmail.setVisibility(View.VISIBLE);
            editPhoneNumber.setVisibility(View.VISIBLE);
            editAddress.setVisibility(View.VISIBLE);

            cancelButton.setVisibility(View.VISIBLE);
            saveButton.setVisibility(View.VISIBLE);
        }

        isEditing = !isEditing;
    }

    private void cancelEditing() {
        saveChanges();
    }

    private void saveChanges() {
        String editedFirstName = editFirstName.getText().toString();
        String editedLastName = editLastName.getText().toString();
        String editedEmail = editEmail.getText().toString();
        String editedPhoneNumber = editPhoneNumber.getText().toString();
        String editedAddress = editAddress.getText().toString();

        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
            userRef.child("firstName").setValue(editedFirstName);
            userRef.child("lastName").setValue(editedLastName);
            userRef.child("email").setValue(editedEmail);
            userRef.child("phoneNumber").setValue(editedPhoneNumber);
            userRef.child("address").setValue(editedAddress)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Changes saved successfully
                            toggleEditingMode();
                            Toast.makeText(getApplicationContext(), "Changes saved successfully", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Handle the failure to save changes
                            Toast.makeText(getApplicationContext(), "Failed to save changes", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void deleteUser() {
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
            userRef.removeValue()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // User deleted successfully
                            Toast.makeText(getApplicationContext(), "User deleted successfully", Toast.LENGTH_SHORT).show();
                            finish(); // Finish the activity after deleting the user
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Handle the failure to delete user
                            Toast.makeText(getApplicationContext(), "Failed to delete user", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
