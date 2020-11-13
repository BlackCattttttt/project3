package com.need.mymall;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class ProductItemRecycleViewAdapter extends RecyclerView.Adapter<ProductItemRecycleViewAdapter.ProductItemViewHolder> {

    private ArrayList<ProductDatabase> productDatabaseArrayList;

    public ProductItemRecycleViewAdapter(ArrayList<ProductDatabase> productDatabaseArrayList) {
        this.productDatabaseArrayList = productDatabaseArrayList;
    }

    @NonNull
    @Override
    public ProductItemRecycleViewAdapter.ProductItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View productItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item_recycleview_layout,parent,false);
        return new ProductItemViewHolder(productItemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductItemRecycleViewAdapter.ProductItemViewHolder holder, int position) {
        String imageUrl = "";
        if (productDatabaseArrayList.get(position).getImagesUrl().size()>0) {
             imageUrl = productDatabaseArrayList.get(position).getImagesUrl().get(0);
        }
        String title = productDatabaseArrayList.get(position).getTitle();
        String price = productDatabaseArrayList.get(position).getPrice();
        String ave = productDatabaseArrayList.get(position).getAverageRating();
        int total = productDatabaseArrayList.get(position).getReviewCount();
        ((ProductItemViewHolder)holder).setData(imageUrl,title,price,ave,total,position);
    }

    @Override
    public int getItemCount() {
        return productDatabaseArrayList.size();
    }

    public class ProductItemViewHolder extends RecyclerView.ViewHolder {

        private ImageView productImage;
        private TextView productTitle;
        private TextView productPrice;
        private TextView productMore;
        private TextView productAveRating;
        private TextView productTotalReview;

        public ProductItemViewHolder(@NonNull View itemView) {
            super(itemView);

            productImage = itemView.findViewById(R.id.productitem_recycleview_img);
            productTitle = itemView.findViewById(R.id.productitem_recycleview_title);
            productPrice = itemView.findViewById(R.id.productitem_recycleview_price);
            productMore = itemView.findViewById(R.id.productitem_recycleview_more);
            productAveRating = itemView.findViewById(R.id.productitem_recycleview_average_star);
            productTotalReview = itemView.findViewById(R.id.productitem_recycleview_totalrating);
        }

        public void setData (String imageUrl, String title, String price, String ave, int total, final int position) {
            Glide.with(itemView.getContext()).load(imageUrl).apply(new RequestOptions().placeholder(R.drawable.no_image)).into(productImage);

            productTitle.setText(title);
            productPrice.setText(String.format("%,.2f",Double.parseDouble(price) * Double.parseDouble(MainActivity.exchange)) + "đ");
            productAveRating.setText(ave);
            productTotalReview.setText(total + " rating");

            productMore.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onClick(View v) {
                    Intent productDetailIntent = new Intent(itemView.getContext(),ProductDetailActivity.class);
                    productDetailIntent.putExtra("product",productDatabaseArrayList.get(position) );
                    productDetailIntent.putExtra("quantity", 1);
                    productDetailIntent.putExtra("description","");
                    itemView.getContext().startActivity(productDetailIntent);
                }
            });
        }
    }
}
