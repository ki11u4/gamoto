package com.example.gamotolastminute.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gamotolastminute.R;
import com.example.gamotolastminute.adapters.ProductAdapter;
import com.example.gamotolastminute.model.ProductModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    // Properties
    private ProductAdapter productAdapter;
    private CollectionReference collectionRef;
    private final List<ProductModel> list = new ArrayList<>();
    private final List<ProductModel> filteredList = new ArrayList<>();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final Handler searchHandler = new Handler();
    private Runnable searchRunnable;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        SearchView searchView = view.findViewById(R.id.searchView);
        RecyclerView recyclerViewSearch = view.findViewById(R.id.rec_search);

        recyclerViewSearch.setLayoutManager(new LinearLayoutManager(getActivity()));
        productAdapter = new ProductAdapter(requireContext(), list, R.layout.search_list);
        recyclerViewSearch.setAdapter(productAdapter);

        collectionRef = db.collection("category");

        loadInitialData();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                performSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (searchRunnable != null) {
                    searchHandler.removeCallbacks(searchRunnable);
                }

                searchRunnable = new Runnable() {
                    @Override
                    public void run() {
                        performSearch(newText);
                    }
                };

                searchHandler.postDelayed(searchRunnable, 300); // Delay the search execution by 300ms
                return true;
            }
        });

        return view;
    }

    private void loadInitialData() {
        collectionRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                list.clear(); // Clear the list before adding data to avoid duplicates
                for (QueryDocumentSnapshot categoryDoc : queryDocumentSnapshots) {
                    CollectionReference productCollectionRef = categoryDoc.getReference().collection("product");

                    productCollectionRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot productQueryDocumentSnapshots) {
                            for (QueryDocumentSnapshot productDoc : productQueryDocumentSnapshots) {
                                // Retrieve the product data from the document
                                String productImage = productDoc.getString("productImage");
                                String productName = productDoc.getString("productName");
                                String productDescription = productDoc.getString("productDescription");
                                String productIngredients = productDoc.getString("productIngredients");
                                String productGenericName = productDoc.getString("productGenericName");
                                Object productPriceObj = productDoc.get("productPrice");

                                // Check if 'productPrice' exists and is not null and is a numeric type before adding to the list
                                if (productPriceObj instanceof Number) {
                                    double productPrice = ((Number) productPriceObj).doubleValue();

                                    // Create a ProductModel object and add it to the list
                                    ProductModel product = new ProductModel(productIngredients, productDescription, productImage, productGenericName, productName, productPrice);
                                    list.add(product);
                                }
                            }
                            // Notify the adapter of the data change
                            productAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });
    }

    private void performSearch(String query) {
        filteredList.clear();

        if (query.isEmpty()) {
            filteredList.addAll(list);
        } else {
            String lowerCaseQuery = query.toLowerCase();
            for (ProductModel product : list) {
                String productName = product.getProductName().toLowerCase();
                String productGenericName = product.getProductGenericName();

                if (productGenericName != null && productGenericName.toLowerCase().contains(lowerCaseQuery)) {
                    filteredList.add(product);
                } else if (productName.contains(lowerCaseQuery)) {
                    filteredList.add(product);
                }
            }
        }

        productAdapter.setList(filteredList);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (searchRunnable != null) {
            searchHandler.removeCallbacks(searchRunnable);
        }
    }
}
