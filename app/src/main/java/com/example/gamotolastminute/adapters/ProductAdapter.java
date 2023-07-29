package com.example.gamotolastminute.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.gamotolastminute.R;
import com.example.gamotolastminute.activities.ProductDetailsActivity;
import com.example.gamotolastminute.model.ProductModel;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    private Context context;
    private List<ProductModel> list;
    private int layoutResId;

    public ProductAdapter(Context context, List<ProductModel> list, int layoutResId) {
        this.context = context;
        this.list = list;
        this.layoutResId = layoutResId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(layoutResId, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductModel product = list.get(position);

        Glide.with(context).load(product.getProductImage()).error(R.drawable.pills).into(holder.productImage);
        holder.productName.setText(product.getProductName());
        holder.productGenericName.setText(product.getProductGenericName());
        holder.productPrice.setText(String.valueOf(product.getProductPrice()));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProductDetailsActivity.class);
                intent.putExtra("detailed", product);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setList(List<ProductModel> productModelList) {
        this.list = productModelList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productName, productPrice, productGenericName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.productImage);
            productName = itemView.findViewById(R.id.productName);
            productGenericName = itemView.findViewById(R.id.productGenericName);
            productPrice = itemView.findViewById(R.id.productPrice);
        }
    }
}
