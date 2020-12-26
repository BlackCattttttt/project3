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

public class ProductOrderWeightAdapter extends RecyclerView.Adapter<ProductOrderWeightAdapter.ViewHolder> {

    private ArrayList<ProductOrder> productOrderArrayList;

    public ProductOrderWeightAdapter(ArrayList<ProductOrder> productOrderArrayList) {
        this.productOrderArrayList = productOrderArrayList;
    }

    @NonNull
    @Override
    public ProductOrderWeightAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item_weight_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductOrderWeightAdapter.ViewHolder holder, int position) {
        String img = productOrderArrayList.get(position).getProductImg();
        double weight = productOrderArrayList.get(position).getProductWeight();
        int quantity = productOrderArrayList.get(position).getProductQuantity();
        double transportfee = productOrderArrayList.get(position).getProductTransportfee();

        ((ViewHolder)holder).setItemDetail(img,quantity,weight,transportfee);
    }

    @Override
    public int getItemCount() {
        return productOrderArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView orderItemImage;
        private TextView orderItemWeight;
        private TextView orderItemQuantity;
        private TextView orderItemTransportFee;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            orderItemImage = itemView.findViewById(R.id.order_item_weight_image);
            orderItemWeight = itemView.findViewById(R.id.order_item_weight);
            orderItemQuantity = itemView.findViewById(R.id.order_item_weight_quantity);
            orderItemTransportFee = itemView.findViewById(R.id.order_item_weight_transportfee);
        }

        public void setItemDetail (String imageUrl, int quantity, double weight,double transportfee) {
            Glide.with(itemView.getContext()).load(imageUrl).apply(new RequestOptions().placeholder(R.mipmap.home_icon)).into(orderItemImage);
            orderItemQuantity.setText(quantity+"");
            orderItemWeight.setText(String.valueOf(weight));
            double tran = (transportfee * weight) * quantity;
            orderItemTransportFee.setText(String.format("%,.2f",tran * Double.parseDouble(MainActivity.exchange)) + "đ");
        }
    }
}
