package com.example.gamotolastminute.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.gamotolastminute.R;
import com.example.gamotolastminute.model.CartItemsModel;

import java.util.ArrayList;
import java.util.List;

public class CartItemsAdapter extends RecyclerView.Adapter<CartItemsAdapter.ViewHolder> {

    private final Context context;
    private final List<CartItemsModel> list;
    private final List<CartItemsModel> selectedItemsList;
    private OnCartItemRemoveListener itemRemoveListener;

    public CartItemsAdapter(Context context, List<CartItemsModel> list) {
        this.context = context;
        this.list = list;
        this.selectedItemsList = new ArrayList<>();
    }

    public void setOnCartItemRemoveListener(OnCartItemRemoveListener listener) {
        this.itemRemoveListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.addtocart_list, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartItemsModel item = list.get(position);

        Glide.with(context).load(item.getProductImage()).into(holder.productImage);
        holder.productName.setText(item.getProductName());
        holder.productPrice.setText(String.valueOf(item.getProductPrice()));

        holder.deleteToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemRemoveListener != null) {
                    itemRemoveListener.onCartItemRemove(item);
                }
            }
        });
        holder.choiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleItemSelection(item);
                if (itemRemoveListener != null) {
                    itemRemoveListener.onCartItemSelectionChanged(); // Call the correct listener method here
                }
            }
        });

        // Update the UI based on the selected state
        holder.choiceButton.setChecked(selectedItemsList.contains(item));
    }

    public void toggleItemSelection(CartItemsModel item) {
        if (selectedItemsList.contains(item)) {
            selectedItemsList.remove(item);
        } else {
            selectedItemsList.add(item);
        }
        notifyDataSetChanged();

        // Update the UI and button visibility in the fragment
        if (itemRemoveListener != null) {
            itemRemoveListener.onCartItemSelectionChanged();
        }
    }


    public void clearSelectedItems() {
        selectedItemsList.clear();
        notifyDataSetChanged();
    }

    public List<CartItemsModel> getSelectedItems() {
        return selectedItemsList;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView productImage;
        TextView productName, productPrice;
        Button deleteToCartButton;
        RadioButton choiceButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            productImage = itemView.findViewById(R.id.productImage);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
            deleteToCartButton = itemView.findViewById(R.id.deleteToCartButton);
            choiceButton = itemView.findViewById(R.id.choiceButton);
        }
    }

    public interface OnCartItemRemoveListener {
        void onCartItemRemove(CartItemsModel item);

        void onCartItemSelectionChanged();
    }
}
