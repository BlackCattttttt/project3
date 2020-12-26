package com.need.mymall;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MyOrderAdapter extends RecyclerView.Adapter<MyOrderAdapter.ViewHolder> {

    private ArrayList<MyOrderItemModel> orderItemModelArrayList;

    public MyOrderAdapter(ArrayList<MyOrderItemModel> orderItemModelArrayList) {
        this.orderItemModelArrayList = orderItemModelArrayList;
    }

    @NonNull
    @Override
    public MyOrderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_order_item_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyOrderAdapter.ViewHolder holder, int position) {
        String imgUrl = orderItemModelArrayList.get(position).getProductOrderArrayList().get(0).productImg;
        String title = orderItemModelArrayList.get(position).getProductOrderArrayList().get(0).productTitle;
        int quantity = orderItemModelArrayList.get(position).getProductOrderArrayList().get(0).productQuantity;
        String price = orderItemModelArrayList.get(position).getProductOrderArrayList().get(0).getProductPrice();
        String orderstatus = orderItemModelArrayList.get(position).getOrderStatus();
        int ordertotalItem = orderItemModelArrayList.get(position).getOrderTotalItem();
        String ordertotalPrice = orderItemModelArrayList.get(position).getOrderTotalPrice();
        String Id = orderItemModelArrayList.get(position).getOrderId();

        ((ViewHolder)holder).setData(imgUrl,title,quantity,price,orderstatus,ordertotalItem,ordertotalPrice,Id,position);
    }

    @Override
    public int getItemCount() {
        return orderItemModelArrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView productImage;
        private TextView productTitle;
        private TextView productQuantity;
        private TextView productPrice;
        private TextView orderStatus;
        private TextView orderTotalItem;
        private TextView orderTotalPrice;
        private TextView orderId;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.my_orderitem_img);
            productTitle = itemView.findViewById(R.id.my_orderitem_title);
            productQuantity = itemView.findViewById(R.id.my_orderitem_quantity);
            productPrice = itemView.findViewById(R.id.my_orderitem_price);
            orderStatus = itemView.findViewById(R.id.my_orderitem_status);
            orderTotalItem = itemView.findViewById(R.id.my_oreritem_totalitem);
            orderTotalPrice = itemView.findViewById(R.id.my_oreritem_totalprice);
            orderId = itemView.findViewById(R.id.my_oreritem_id);
        }

        private void setData (String imgUrl, String title, int quantity, String price, final String orderstatus, int ordertotalItem, String ordertotalPrice, String Id, final int pos) {
            Glide.with(itemView.getContext()).load(imgUrl).apply(new RequestOptions().placeholder(R.mipmap.home_icon)).into(productImage);

            productTitle.setText(title);
            productQuantity.setText("x" + quantity);
            productPrice.setText(String.format("%,.2f",Double.parseDouble(price) * Double.parseDouble(MainActivity.exchange)) + "đ");
            if (orderstatus.equals("Đã tạo")) {
                orderStatus.setBackgroundColor(Color.rgb(75,181,67));
                orderStatus.setTextColor(Color.WHITE);
            } else if (orderstatus.equals("Đã thanh toán")) {
                orderStatus.setBackgroundColor(Color.rgb(0,194,199));
                orderStatus.setTextColor(Color.WHITE);
            } else if (orderstatus.equals("[USA]Đã lấy hàng/Đã nhập kho")) {
                orderStatus.setBackgroundColor(Color.rgb(130,152,59));
                orderStatus.setTextColor(Color.WHITE);
            } else if (orderstatus.equals("[VN]Đã lấy hàng/Đã nhập kho")) {
                orderStatus.setBackgroundColor(Color.rgb(98,244,110));
                orderStatus.setTextColor(Color.WHITE);
            } else if (orderstatus.equals("[VN]Đã điều phối giao hàng/Đang giao hàng")) {
                orderStatus.setBackgroundColor(Color.rgb(68,170,77));
                orderStatus.setTextColor(Color.WHITE);
            } else if (orderstatus.equals("Đã giao hàng")) {
                orderStatus.setBackgroundColor(Color.rgb(40,102,46));
                orderStatus.setTextColor(Color.WHITE);
            } else if (orderstatus.equals("Đã hủy")) {
                orderStatus.setBackgroundColor(Color.rgb(217,9,46));
                orderStatus.setTextColor(Color.WHITE);
            }
            orderStatus.setText(orderstatus);
            orderTotalItem.setText(ordertotalItem + " sản phẩm");
            orderTotalPrice.setText("Tổng thanh toán: " + String.format("%,.2f",Double.parseDouble(ordertotalPrice) * Double.parseDouble(MainActivity.exchange)) + "đ");
            orderId.setText(Id);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent orderDetailIntent = new Intent(itemView.getContext(),OrderDetailActivity.class);
                    orderDetailIntent.putExtra("position",pos);
                    orderDetailIntent.putExtra("status",orderstatus);
                    itemView.getContext().startActivity(orderDetailIntent);
                }
            });
        }
    }
}
