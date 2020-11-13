package com.need.mymall;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

public class ProductOrderAdater extends RecyclerView.Adapter<ProductOrderAdater.ViewHolder> {

    private ArrayList<ProductOrder> productOrderArrayList;

    public ProductOrderAdater(ArrayList<ProductOrder> productOrderArrayList) {
        this.productOrderArrayList = productOrderArrayList;
    }

    @NonNull
    @Override
    public ProductOrderAdater.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductOrderAdater.ViewHolder holder, int position) {
        String img = productOrderArrayList.get(position).getProductImg();
        String title = productOrderArrayList.get(position).getProductTitle();
        int quantity = productOrderArrayList.get(position).getProductQuantity();
        String price = productOrderArrayList.get(position).getProductPrice();

        ((ViewHolder)holder).setItemDetail(img,title,price,quantity);
    }

    @Override
    public int getItemCount() {
        return productOrderArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView orderItemImage;
        private TextView orderItemTitle;
        private TextView orderItemPrice;
        private TextView orderItemQuantity;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            orderItemImage = itemView.findViewById(R.id.orderditem_img);
            orderItemTitle = itemView.findViewById(R.id.orderitem_title);
            orderItemPrice = itemView.findViewById(R.id.orderitem_price);
            orderItemQuantity = itemView.findViewById(R.id.orderitem_quantity);
        }

        public void setItemDetail (String imageUrl, String title,String price,int quantity) {
            Glide.with(itemView.getContext()).load(imageUrl).apply(new RequestOptions().placeholder(R.mipmap.home_icon)).into(orderItemImage);
            orderItemTitle.setText(title);
            orderItemPrice.setText(String.format("%,.2f",Double.parseDouble(price) * Double.parseDouble(MainActivity.exchange)) + "đ");
            orderItemQuantity.setText(("x" + quantity));
        }
    }
}
