package com.need.mymall;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class DeliveryActivity extends AppCompatActivity {

    private RecyclerView orderItemRecyclerView;
    private Button addOrChangeAddressBtn;

    private TextView totalAmountOriginal;
    private TextView totalAmountTransportFee;
    private TextView totalAmount;

    private TextView fullname;
    private TextView phone;
    private TextView address;

    private Button completeOrderBtn;

    private Dialog loadingDialog;
    private Dialog completeOrderDialog;

    private TextView completeOrderPrice;
    private TextView continueShoppingBtn;
    private TextView orderId;

    private String id;
    private int totalitem;
    private String totalamount;

    public static final int SELECT_ADDRESS = 0;
    public static boolean fromCart;

    ArrayList<CartItemModel> cartItemModelArrayList;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery);

        addOrChangeAddressBtn = findViewById(R.id.add_or_change_address_btn);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Thanh toán");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_background));

        completeOrderDialog = new Dialog(this);
        completeOrderDialog.setContentView(R.layout.complete_order_dialog);
        completeOrderDialog.setCancelable(false);
        completeOrderDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        completeOrderDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_background));

        orderItemRecyclerView = findViewById(R.id.delyvery_order_item_recycleview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        orderItemRecyclerView.setLayoutManager(layoutManager);

        totalAmountOriginal = findViewById(R.id.amount_delivery_originalprice);
        totalAmountTransportFee = findViewById(R.id.amount_delivery_surchargeprice);
        totalAmount = findViewById(R.id.amount_delivery_totalprice);

        fullname = findViewById(R.id.delivery_infor_fullname);
        phone = findViewById(R.id.delivery_infor_phonenumber);
        address = findViewById(R.id.delivery_infor_address);

        completeOrderBtn = findViewById(R.id.complete_order_btn);
        continueShoppingBtn = completeOrderDialog.findViewById(R.id.continue_shopping_btn);
        orderId = completeOrderDialog.findViewById(R.id.order_id);

        double totalprice = 0;
        double totalTransportfee = 0;
        totalitem = 0;

        cartItemModelArrayList = new ArrayList<>();

        for (int i = 0;i<DBQueries.cartItemModelArrayList.size();i++) {
            if (DBQueries.cartItemModelArrayList.get(i).getType()==CartItemModel.CART_ITEM) {
                CartItemModel cartItemModel = DBQueries.cartItemModelArrayList.get(i);
                cartItemModel.setType(CartItemModel.ORDER_ITEN);
                cartItemModelArrayList.add(cartItemModel);
                totalitem += cartItemModel.getProductQuantity();
                totalprice += Double.parseDouble(cartItemModelArrayList.get(i).getProductPrice()) * cartItemModelArrayList.get(i).getProductQuantity();
                totalTransportfee += Double.parseDouble(cartItemModelArrayList.get(i).getProductTransportFee()) * cartItemModelArrayList.get(i).getProductQuantity();
            }
        }
        CartAdapter adapter = new CartAdapter(cartItemModelArrayList,totalAmountOriginal,totalAmountTransportFee);
        orderItemRecyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        totalamount = String.valueOf(totalprice+totalTransportfee);
        totalAmountOriginal.setText(String.format("%,.2f",totalprice * Double.parseDouble(MainActivity.exchange))+"đ");
        totalAmountTransportFee.setText(String.format("%,.2f",totalTransportfee * Double.parseDouble(MainActivity.exchange))+"đ");
        totalAmount.setText(String.format("%,.2f",(totalprice+totalTransportfee) * Double.parseDouble(MainActivity.exchange))+ "đ");

        if (DBQueries.addressesModelArrayList.size()>0) {
            fullname.setText(DBQueries.addressesModelArrayList.get(DBQueries.selectedAddress).getFullname());
            phone.setText(DBQueries.addressesModelArrayList.get(DBQueries.selectedAddress).getPhonenumber());
            address.setText(DBQueries.addressesModelArrayList.get(DBQueries.selectedAddress).getAddressFull());
        }

        addOrChangeAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myAddressIntent = new Intent(DeliveryActivity.this,MyAddressActivity.class);
                myAddressIntent.putExtra("MODE",SELECT_ADDRESS);
                startActivity(myAddressIntent);
            }
        });

        final double finalTotalprice = totalprice;
        completeOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                completeOrderDialog.show();
                completeOrderPrice = completeOrderDialog.findViewById(R.id.order_price_complete);
                double price = finalTotalprice * 70 / 100;
                completeOrderPrice.setText(String.format("%,.2f",price * Double.parseDouble(MainActivity.exchange)) + "đ - " + String.format("%,.2f",finalTotalprice* Double.parseDouble(MainActivity.exchange)) + "đ");

                Random random = new Random();
                int temp = random.nextInt(999999999-111111111)+111111111;
                id = String.valueOf(temp);
                orderId.setText("Order ID: " + id);

                if (fromCart) {
                    Map<String,Object> updatecartList = new HashMap<>();

                    updatecartList.put("list_size",(long) 0);

                    DBQueries.firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_CART")
                            .set(updatecartList).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                DBQueries.cartList.clear();
                                DBQueries.cartItemModelArrayList.clear();

                            } else {
                                String error = task.getException().getMessage();
                                Toast.makeText(DeliveryActivity.this,error,Toast.LENGTH_LONG).show();
                            }
                            loadingDialog.dismiss();
                        }
                    });
                }
                placeOrderDetails();
            }
        });

        continueShoppingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                completeOrderDialog.dismiss();
                Intent mainIntent = new Intent(DeliveryActivity.this,MainActivity.class);
                MainActivity.showCart = false;
                startActivity(mainIntent);
                finish();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (DBQueries.addressesModelArrayList.size()>0) {
            fullname.setText(DBQueries.addressesModelArrayList.get(DBQueries.selectedAddress).getFullname());
            phone.setText(DBQueries.addressesModelArrayList.get(DBQueries.selectedAddress).getPhonenumber());
            address.setText(DBQueries.addressesModelArrayList.get(DBQueries.selectedAddress).getAddressFull());
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            DBQueries.cartItemModelArrayList.clear();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        DBQueries.cartItemModelArrayList.clear();
    }

    private void placeOrderDetails() {

        String userId = FirebaseAuth.getInstance().getUid();
        int index = 0;
        loadingDialog.show();
        Map<String, Object> orderDetails = new HashMap<>();
        orderDetails.put("order_ID", id);
        orderDetails.put("user_ID", userId);
        orderDetails.put("total_item",(long) totalitem);
        orderDetails.put("total_amount",totalamount);
        orderDetails.put("deposit","0");
        orderDetails.put("ordered_date", FieldValue.serverTimestamp());
        orderDetails.put("payed_date", FieldValue.serverTimestamp());
        orderDetails.put("packed_date", FieldValue.serverTimestamp());
        orderDetails.put("shiped_usa_date", FieldValue.serverTimestamp());
        orderDetails.put("shiped_vn_date", FieldValue.serverTimestamp());
        orderDetails.put("delivery_date", FieldValue.serverTimestamp());
        orderDetails.put("cancelled_date", FieldValue.serverTimestamp());
        orderDetails.put("fullname", fullname.getText().toString());
        orderDetails.put("address", address.getText().toString());
        orderDetails.put("phone_number", phone.getText().toString());
        orderDetails.put("order_status", "Đã tạo");
        for (CartItemModel cartItemModel : cartItemModelArrayList) {
            if (cartItemModel.getType() == CartItemModel.ORDER_ITEN) {
                orderDetails.put("product_ID_"+String.valueOf(index), cartItemModel.getProductId());
                orderDetails.put("product_quantity_"+String.valueOf(index), cartItemModel.getProductQuantity());
                orderDetails.put("product_price_"+String.valueOf(index), cartItemModel.getProductPrice());
                orderDetails.put("product_des_"+String.valueOf(index), cartItemModel.getProductDes());
                orderDetails.put("category_ID_"+String.valueOf(index), cartItemModel.getCategoryId());
                orderDetails.put("product_weight_"+String.valueOf(index), 0.5);
                orderDetails.put("transportfee_"+String.valueOf(index), (Double.parseDouble(cartItemModel.getProductTransportFee())/0.5));
                index++;
            }
        }
        orderDetails.put("list_size",(long)index);

        DBQueries.firebaseFirestore.collection("ORDERS").document(id).set(orderDetails)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Map<String,Object> updateUser = new HashMap<>();
                            updateUser.put("order_id",id);
                            FirebaseFirestore.getInstance().collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_ORDER")
                                    .add(updateUser).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                    if (task.isSuccessful()) {

                                    } else {
                                        String error = task.getException().getMessage();
                                        Toast.makeText(DeliveryActivity.this,error,Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(DeliveryActivity.this,error,Toast.LENGTH_LONG).show();
                        }
                        loadingDialog.dismiss();
                    }
                });
    }
}