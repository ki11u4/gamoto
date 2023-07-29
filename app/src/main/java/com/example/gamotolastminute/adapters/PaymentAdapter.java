package com.example.gamotolastminute.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.gamotolastminute.R;
import com.example.gamotolastminute.model.CartItemsModel;

import java.util.ArrayList;
import java.util.List;

public class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.ViewHolder> {

    private final Context context;
    private final List<CartItemsModel> cartItemsList;

    public PaymentAdapter(Context context, List<CartItemsModel> cartItemsList) {
        this.context = context;
        this.cartItemsList = cartItemsList;
    }

    public List<CartItemsModel> getSelectedItems() {
        List<CartItemsModel> selectedItems = new ArrayList<>();
        for (CartItemsModel item : cartItemsList) {
            if (item.isSelected()) {
                selectedItems.add(item);
            }
        }
        return selectedItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.payment_list, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartItemsModel item = cartItemsList.get(position); // Get the current item from the cartItemsList

        Glide.with(context).load(item.getProductImage()).into(holder.productImage);
        holder.productName.setText(item.getProductName());
        String formattedPrice = String.format("%.2f", item.getProductPrice());
        holder.productPrice.setText(formattedPrice);

        holder.productQuantity.setText(String.valueOf(item.getProductQuantity()));
        holder.itemView.setSelected(item.isSelected()); // Set the item's selected status
    }

    @Override
    public int getItemCount() {
        return cartItemsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView productImage;
        TextView productName, productPrice, productQuantity;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            productImage = itemView.findViewById(R.id.productImage);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
            productQuantity = itemView.findViewById(R.id.productQuantity);
        }
    }
}
