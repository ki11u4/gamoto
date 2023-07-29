package com.example.gamotolastminute.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gamotolastminute.R;
import com.example.gamotolastminute.adapters.CategoryAdapter;
import com.example.gamotolastminute.adapters.ProductAdapter;
import com.example.gamotolastminute.model.CategoryModel;
import com.example.gamotolastminute.model.ProductModel;
import com.example.gamotolastminute.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HomeFragment extends Fragment implements CategoryAdapter.OnCategoryClickListener {

    // Properties
    private RecyclerView categoryRecyclerView, productRecyclerView;
    private CategoryAdapter categoryAdapter;
    private ProductAdapter productAdapter;
    private List<ProductModel> productModelList;
    private List<CategoryModel> categoryModelList;
    private UserModel userModel;
    private TextView firstName;
    private FirebaseFirestore db;
    private ProgressDialog progressDialog;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        // Initialize ProgressDialog
        progressDialog = new ProgressDialog(getActivity());

        // Find views
        productRecyclerView = root.findViewById(R.id.rec_product);
        categoryRecyclerView = root.findViewById(R.id.rec_category);
        firstName = root.findViewById(R.id.firstName);

        // Fetch and display the user's first name if logged in
        displayUserFirstName();

        progressDialog.setTitle("Welcome to Gamoto");
        progressDialog.setMessage("Please wait for a while...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        // Setup RecyclerViews and Adapters
        setupCategoryRecyclerView();
        fetchCategoriesFromFirestore();

        setupProductRecyclerView();
        fetchRandomProductsFromFirestore();

        return root;
    }

    private void displayUserFirstName() {
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        userModel = snapshot.getValue(UserModel.class);
                        if (userModel != null) {
                            String firstNameText = userModel.getFirstName();
                            firstName.setText(firstNameText);
                        }
                    } else {
                        Toast.makeText(getActivity(), "User data does not exist", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getActivity(), "Failed to retrieve user data", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // ... (Other methods omitted for brevity)

    private void setupCategoryRecyclerView() {
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
        categoryModelList = new ArrayList<>();
        categoryAdapter = new CategoryAdapter(getContext(), categoryModelList, this); // Passing 'this' as the click listener
        categoryRecyclerView.setAdapter(categoryAdapter);
    }

    private void fetchCategoriesFromFirestore() {
        db.collection("category").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    categoryModelList.clear();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        CategoryModel categoryModel = document.toObject(CategoryModel.class);
                        categoryModel.setId(document.getId()); // Set the category ID from Firestore document ID
                        categoryModelList.add(categoryModel);
                    }
                    categoryAdapter.notifyDataSetChanged();
                    progressDialog.dismiss();
                } else {
                    Toast.makeText(getActivity(), "Error loading categories", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setupProductRecyclerView() {
        productRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
        productModelList = new ArrayList<>();
        productAdapter = new ProductAdapter(requireContext(), productModelList, R.layout.products_lists);
        productRecyclerView.setAdapter(productAdapter);
    }

    private void fetchRandomProductsFromFirestore() {
        db.collectionGroup("product").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                productModelList.clear();
                List<ProductModel> allProducts = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    ProductModel productModel = document.toObject(ProductModel.class);
                    allProducts.add(productModel);
                }

                // Display random products (e.g., 10 products)
                productModelList.addAll(getRandomProducts(allProducts, 10));
                productAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(getActivity(), "Error loading products", Toast.LENGTH_SHORT).show();
            }
            progressDialog.dismiss();
        });
    }

    // Helper method to get random products from the product list
    private List<ProductModel> getRandomProducts(List<ProductModel> products, int count) {
        List<ProductModel> randomProducts = new ArrayList<>();
        int totalProducts = products.size();

        if (totalProducts <= count) {
            return products; // If the number of products is less than or equal to the required count, return all products
        }

        // Use a random number generator to select random products
        Random random = new Random();
        for (int i = 0; i < count; i++) {
            int randomIndex = random.nextInt(totalProducts);
            randomProducts.add(products.get(randomIndex));
        }

        return randomProducts;
    }

    @Override
    public void onCategoryClick(CategoryModel categoryModel) {
        if (categoryModel != null) {
            String categoryId = categoryModel.getId();
            Log.d("HomeFragment", "CategoryModel ID: " + categoryId);
            if (categoryId != null && !categoryId.isEmpty()) {
                fetchProductsFromFirestore(categoryId);
            } else {
                Toast.makeText(getActivity(), "Invalid category ID", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getActivity(), "Invalid category", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchProductsFromFirestore(String categoryId) {
        if (categoryId != null && !categoryId.isEmpty()) {
            CollectionReference productCollectionRef = db.collection("category").document(categoryId).collection("product");
            productCollectionRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        productModelList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            ProductModel productModel = document.toObject(ProductModel.class);
                            productModelList.add(productModel);
                        }
                        productAdapter.notifyDataSetChanged();
                        progressDialog.dismiss();
                    } else {
                        Toast.makeText(getActivity(), "Error loading products", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Log.d("HomeFragment", "CategoryModel ID: " + categoryId);
            Toast.makeText(getActivity(), "Invalid category ID", Toast.LENGTH_SHORT).show();
        }
    }
}
