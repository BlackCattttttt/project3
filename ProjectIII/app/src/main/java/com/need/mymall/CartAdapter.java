package com.need.mymall;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.InputQueue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.FloatRange;
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

public class CartAdapter extends RecyclerView.Adapter {

    private ArrayList<CartItemModel> cartItemModelArrayList;
    private TextView totalAmountOriginal;
    private TextView totalAmountTransportFee;

    private int lastPotision = -1;

    public CartAdapter(ArrayList<CartItemModel> cartItemModelArrayList,TextView totalAmountOriginal,TextView totalAmountTransportFee) {
        this.cartItemModelArrayList = cartItemModelArrayList;
        this.totalAmountOriginal = totalAmountOriginal;
        this.totalAmountTransportFee = totalAmountTransportFee;
    }

    @Override
    public int getItemViewType(int position) {
        switch (cartItemModelArrayList.get(position).getType()) {
            case 0:
                return CartItemModel.CART_ITEM;
            case 1:
                return CartItemModel.ORDER_ITEN;
            case 2:
                return CartItemModel.TOTAL_AMOUNT;
            default:
                return -1;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        switch (viewType) {
            case CartItemModel.CART_ITEM:
                View cartItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item_layout,parent,false);
                return new CartItemViewHolder(cartItemView);
            case CartItemModel.ORDER_ITEN:
                View orderItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item_layout,parent,false);
                return new OrderItemViewHolder(orderItemView);
            case CartItemModel.TOTAL_AMOUNT:
                View totalAmountItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_total_amount_layout,parent,false);
                return new TotalAmountViewHolder(totalAmountItemView);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (cartItemModelArrayList.get(position).getType()) {
            case CartItemModel.CART_ITEM:
                String productId = cartItemModelArrayList.get(position).getProductId();
                String imageUrl = cartItemModelArrayList.get(position).getProductImageUrl();
                String title = cartItemModelArrayList.get(position).getProductTitle();
                String price = cartItemModelArrayList.get(position).getProductPrice();
                int quantity = cartItemModelArrayList.get(position).getProductQuantity();
                ((CartItemViewHolder)holder).setItemDetail(imageUrl,title,price,position,quantity);
                break;
            case CartItemModel.ORDER_ITEN:
                String productId1 = cartItemModelArrayList.get(position).getProductId();
                String imageUrl1 = cartItemModelArrayList.get(position).getProductImageUrl();
                String title1 = cartItemModelArrayList.get(position).getProductTitle();
                String price1 = cartItemModelArrayList.get(position).getProductPrice();
                int quantity1 = cartItemModelArrayList.get(position).getProductQuantity();
                ((OrderItemViewHolder)holder).setItemDetail(imageUrl1,title1,price1,quantity1);
                break;
            case CartItemModel.TOTAL_AMOUNT:
                int totalitem = 0;
                double totalprice = 0;
                double totalTransportfee = 0;
                for (int x=0;x<cartItemModelArrayList.size();x++) {
                    if (cartItemModelArrayList.get(x).getType()==CartItemModel.CART_ITEM) {
                        totalitem += cartItemModelArrayList.get(x).getProductQuantity();
                        totalprice += Double.parseDouble(cartItemModelArrayList.get(x).getProductPrice()) * cartItemModelArrayList.get(x).getProductQuantity();
                        totalTransportfee += Double.parseDouble(cartItemModelArrayList.get(x).getProductTransportFee()) * cartItemModelArrayList.get(x).getProductQuantity();
                    }
                }
                cartItemModelArrayList.get(position).setTotalItem(totalitem);
                cartItemModelArrayList.get(position).setTotalAmount(String.valueOf(totalprice+totalTransportfee));
                ((TotalAmountViewHolder)holder).setData(String.valueOf(totalitem),String.valueOf(totalprice),String.valueOf(totalTransportfee));
                break;
            default:
                return ;
        }

        if (lastPotision<position) {
            Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(),R.anim.fate_in);
            holder.itemView.setAnimation(animation);
            lastPotision = position;
        }
    }

    @Override
    public int getItemCount() {
        return cartItemModelArrayList.size();
    }

    class CartItemViewHolder extends RecyclerView.ViewHolder {

        private ImageView productImage;
        private TextView productTitle;
        private TextView productPrice;
        private TextView productQuantity;
        private ImageView plusIcon;
        private ImageView minusIcon;
        private LinearLayout deleteBtn;

        public CartItemViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.product_image);
            productTitle = itemView.findViewById(R.id.product_title);
            productPrice = itemView.findViewById(R.id.product_price);
            productQuantity = itemView.findViewById(R.id.product_quantity);
            plusIcon = itemView.findViewById(R.id.product_plus_icon);
            minusIcon = itemView.findViewById(R.id.product_minus_icon);
            deleteBtn = itemView.findViewById(R.id.remove_item_btn);
        }

        public void setItemDetail (String imageUrl, String title, String price, final int pos,int quantity) {
            Glide.with(itemView.getContext()).load(imageUrl).apply(new RequestOptions().placeholder(R.drawable.no_image)).into(productImage);
            productTitle.setText(title);
            productPrice.setText(String.format("%,.2f",Double.parseDouble(price) * Double.parseDouble(MainActivity.exchange)) + "đ");
            productQuantity.setText(String.valueOf(quantity));
            productQuantity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Dialog quantityDialog = new Dialog(itemView.getContext());
                    quantityDialog.setContentView(R.layout.quantity_dialog);
                    quantityDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                    quantityDialog.setCancelable(false);

                    final EditText quantityNum = quantityDialog.findViewById(R.id.quantity_num);
                    Button cancle_btn = quantityDialog.findViewById(R.id.cancle_btn);
                    Button ok_btn = quantityDialog.findViewById(R.id.ok_btn);

                    cancle_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            quantityDialog.dismiss();
                        }
                    });
                    ok_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (Integer.parseInt(quantityNum.getText().toString()) <= 10 && Integer.parseInt(quantityNum.getText().toString()) > 0 && !TextUtils.isEmpty(quantityNum.getText())) {
                                DBQueries.cartItemModelArrayList.get(pos).setProductQuantity(Integer.parseInt(quantityNum.getText().toString()));
                                productQuantity.setText(quantityNum.getText());
                                MyCartFragment.cartAdapter.notifyDataSetChanged();
                                quantityDialog.dismiss();
                            } else {
                                Toast.makeText(itemView.getContext(),"Số lượng không hợp lệ",Toast.LENGTH_LONG).show();
                                quantityDialog.dismiss();
                            }

                        }
                    });

                    quantityDialog.show();
                }
            });
            plusIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int quantity = Integer.parseInt(productQuantity.getText().toString());
                    quantity++;
                    productQuantity.setText(quantity+"");
                    DBQueries.cartItemModelArrayList.get(pos).setProductQuantity(quantity);
                    MyCartFragment.cartAdapter.notifyDataSetChanged();
                }
            });
            minusIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int quantity = Integer.parseInt(productQuantity.getText().toString());
                    if (quantity>1) {
                        quantity--;
                    } else {
                        DeleteDialog(pos);
                    }
                    productQuantity.setText(quantity+"");
                    DBQueries.cartItemModelArrayList.get(pos).setProductQuantity(quantity);
                    MyCartFragment.cartAdapter.notifyDataSetChanged();
                }
            });

            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DeleteDialog(pos);
                }
            });
        }

        public void DeleteDialog (final int pos) {
            final AlertDialog.Builder deleteDialog = new AlertDialog.Builder(itemView.getContext(),R.style.MyDialogTheme);
            deleteDialog.setTitle("Xóa sản phẩm");
            deleteDialog.setMessage("Bạn có chắc muốn xóa sản phẩm này không?");

            deleteDialog.setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (!ProductDetailActivity.running_cart_query) {
                        ProductDetailActivity.running_cart_query = true;
                        DBQueries.removeFromCart(pos,itemView.getContext());
                    }
                }
            });

            deleteDialog.setNegativeButton("Đóng", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            deleteDialog.show();
        }
    }

    class OrderItemViewHolder extends RecyclerView.ViewHolder {

        private ImageView orderItemImage;
        private TextView orderItemTitle;
        private TextView orderItemPrice;
        private TextView orderItemQuantity;

        public OrderItemViewHolder(@NonNull View itemView) {
            super(itemView);
            orderItemImage = itemView.findViewById(R.id.orderditem_img);
            orderItemTitle = itemView.findViewById(R.id.orderitem_title);
            orderItemPrice = itemView.findViewById(R.id.orderitem_price);
            orderItemQuantity = itemView.findViewById(R.id.orderitem_quantity);
        }

        public void setItemDetail (String imageUrl, String title,String price,int quantity) {
            Glide.with(itemView.getContext()).load(imageUrl).apply(new RequestOptions().placeholder(R.mipmap.home_icon)).into(orderItemImage);
            orderItemTitle.setText(title);
            double amount = Double.parseDouble(price) * Double.parseDouble(MainActivity.exchange);
            orderItemPrice.setText(String.format("%,.2f",amount) + "đ");
            orderItemQuantity.setText(("x" + quantity));
        }
    }

    class TotalAmountViewHolder extends RecyclerView.ViewHolder {

        private TextView totalPrice;
        private TextView totalItem;

        public TotalAmountViewHolder(@NonNull View itemView) {
            super(itemView);
            totalPrice = itemView.findViewById(R.id.cart_totalamount_price);
            totalItem = itemView.findViewById(R.id.cart_totalamount_quantity);
        }

        public void setData(String item,String price,String transportfee) {
            totalItem.setText(item);
            totalPrice.setText(String.format("%,.2f",Double.parseDouble(price) * Double.parseDouble(MainActivity.exchange)) + "đ");
        }

    }
}
