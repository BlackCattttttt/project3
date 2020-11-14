package com.need.mymall;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class OrderDetailActivity extends AppCompatActivity {

    private int position;

    private TextView fullname,phone,address;
    private ImageView orderedIndicator,payedIndicator,packedIndicator,shipedUsaIndicator,shipedVnIndicator,deliveriedIndicator;
    private ProgressBar O_Pay_progress,Pay_Pack_progress,Pack_SU_progress,SU_SV_progress,SV_D_progress;
    private TextView orderTitle,payTitle,packTitle,shipUsaTitle,shipVnTitle,deliveryTitle;
    private TextView orderDate,payDate,packDate,shipUsaDate,shipVnDate,deliveryDate;
    private RecyclerView recyclerView;
    private TextView orderId,orderedDate;
    private TextView totalAmount,deposit,amount;
    private TextView orderStatus;
    private Button cancleOrderBtn;

    private Dialog cancleDialog,loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Order detail");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_background));
        //loadingDialog.show();

        cancleDialog = new Dialog(OrderDetailActivity.this);
        cancleDialog.setContentView(R.layout.order_cancle_dialog);
        cancleDialog.setCancelable(true);
        cancleDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_background));

        fullname = findViewById(R.id.my_orderdetail_fullname);
        phone = findViewById(R.id.my_orderdetail_phonenumber);
        address = findViewById(R.id.my_orderdetail_address);
        recyclerView = findViewById(R.id.detail_order_item_recycleview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        orderedIndicator = findViewById(R.id.ordered_indicator);
        payedIndicator = findViewById(R.id.payed_indicator);
        packedIndicator = findViewById(R.id.packed_indicator);
        shipedUsaIndicator = findViewById(R.id.shiped_usa_indicator);
        shipedVnIndicator = findViewById(R.id.shiped_vn_indicator);
        deliveriedIndicator = findViewById(R.id.delivered_indicator);

        O_Pay_progress = findViewById(R.id.ordered_progressbar);
        Pay_Pack_progress = findViewById(R.id.payed_progressbar);
        Pack_SU_progress = findViewById(R.id.packed_progressbar);
        SU_SV_progress = findViewById(R.id.shiped_usa_progressbar);
        SV_D_progress = findViewById(R.id.shiped_vn_progressbar);

        orderTitle = findViewById(R.id.ordered_title);
        payTitle = findViewById(R.id.payed_title);
        packTitle = findViewById(R.id.packed_title);
        shipUsaTitle = findViewById(R.id.shiped_usa_title);
        shipVnTitle = findViewById(R.id.shiped_vn_title);
        deliveryTitle = findViewById(R.id.delivered_title);

        orderDate = findViewById(R.id.ordered_date);
        payDate = findViewById(R.id.payed_date);
        packDate = findViewById(R.id.packed_date);
        shipUsaDate = findViewById(R.id.shiped_usa_date);
        shipVnDate = findViewById(R.id.shiped_vn_date);
        deliveryDate = findViewById(R.id.delivered_date);

        orderedDate = findViewById(R.id.my_orderdetail_time);
        orderId = findViewById(R.id.my_oreritem_id);

        totalAmount = findViewById(R.id.my_orderdetail_totalamount);
        deposit = findViewById(R.id.my_orderdetail_deposit);
        amount = findViewById(R.id.my_orderdetail_amount);

        orderStatus = findViewById(R.id.my_orderdetail_status);
        cancleOrderBtn = findViewById(R.id.cancel_order_btn);

        position = getIntent().getIntExtra("position",-1);
        final MyOrderItemModel model = DBQueries.myOrderItemModelArrayList.get(position);

        ProductOrderAdater productOrderAdater = new ProductOrderAdater(model.getProductOrderArrayList());
        recyclerView.setAdapter(productOrderAdater);
        productOrderAdater.notifyDataSetChanged();

        fullname.setText(model.getFullName());
        phone.setText(model.getPhoneNum());
        address.setText(model.getAddress());

        orderedDate.setText(model.getOrderedDate().toString());
        orderId.setText(model.getOrderId());

        totalAmount.setText(String.format("%,.2f",Double.parseDouble(model.getOrderTotalPrice()) * Double.parseDouble(MainActivity.exchange)) + "đ");
        deposit.setText(String.format("%,.2f",Double.parseDouble(model.getDeposit()) * Double.parseDouble(MainActivity.exchange)) + "đ");
        double price = Double.parseDouble(model.getOrderTotalPrice()) - Double.parseDouble(model.getDeposit());
        amount.setText(String.format("%,.2f",price * Double.parseDouble(MainActivity.exchange)) + "đ");

        String orderstatus = model.getOrderStatus();
        if (orderstatus.equals("Đã tạo")) {
           orderStatus.setBackgroundColor(Color.rgb(75,181,67));
           orderStatus.setTextColor(Color.WHITE);
           orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.sucessGreen)));
           orderDate.setText(String.valueOf(model.getOrderedDate()));

           O_Pay_progress.setVisibility(View.GONE);
           Pay_Pack_progress.setVisibility(View.GONE);
           Pack_SU_progress.setVisibility(View.GONE);
           SU_SV_progress.setVisibility(View.GONE);
           SV_D_progress.setVisibility(View.GONE);

           payedIndicator.setVisibility(View.GONE);
           payTitle.setVisibility(View.GONE);
           payDate.setVisibility(View.GONE);

           packedIndicator.setVisibility(View.GONE);
           packTitle.setVisibility(View.GONE);
           packDate.setVisibility(View.GONE);

           shipedUsaIndicator.setVisibility(View.GONE);
           shipUsaTitle.setVisibility(View.GONE);
           shipUsaDate.setVisibility(View.GONE);

           shipedVnIndicator.setVisibility(View.GONE);
           shipVnTitle.setVisibility(View.GONE);
           shipVnDate.setVisibility(View.GONE);

           deliveriedIndicator.setVisibility(View.GONE);
           deliveryTitle.setVisibility(View.GONE);
           deliveryDate.setVisibility(View.GONE);

        } else if (orderstatus.equals("Đã thanh toán")) {
            orderStatus.setBackgroundColor(Color.rgb(0,194,199));
            orderStatus.setTextColor(Color.WHITE);
            orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.sucessGreen)));
            orderDate.setText(String.valueOf(model.getOrderedDate()));

            payedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.sucessGreen)));
            payDate.setText(String.valueOf(model.getPayedDate()));

            O_Pay_progress.setProgress(100);
            Pay_Pack_progress.setVisibility(View.GONE);
            Pack_SU_progress.setVisibility(View.GONE);
            SU_SV_progress.setVisibility(View.GONE);
            SV_D_progress.setVisibility(View.GONE);

            packedIndicator.setVisibility(View.GONE);
            packTitle.setVisibility(View.GONE);
            packDate.setVisibility(View.GONE);

            shipedUsaIndicator.setVisibility(View.GONE);
            shipUsaTitle.setVisibility(View.GONE);
            shipUsaDate.setVisibility(View.GONE);

            shipedVnIndicator.setVisibility(View.GONE);
            shipVnTitle.setVisibility(View.GONE);
            shipVnDate.setVisibility(View.GONE);

            deliveriedIndicator.setVisibility(View.GONE);
            deliveryTitle.setVisibility(View.GONE);
            deliveryDate.setVisibility(View.GONE);

        } else if (orderstatus.equals("[USA]Đã lấy hàng/Đã nhập kho")) {
            orderStatus.setBackgroundColor(Color.rgb(130,152,59));
            orderStatus.setTextColor(Color.WHITE);
            orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.sucessGreen)));
            orderDate.setText(String.valueOf(model.getOrderedDate()));

            payedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.sucessGreen)));
            payDate.setText(String.valueOf(model.getPayedDate()));

            packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.sucessGreen)));
            packDate.setText(String.valueOf(model.getPackedDate()));

            O_Pay_progress.setProgress(100);
            Pay_Pack_progress.setProgress(100);
            Pack_SU_progress.setVisibility(View.GONE);
            SU_SV_progress.setVisibility(View.GONE);
            SV_D_progress.setVisibility(View.GONE);

            shipedUsaIndicator.setVisibility(View.GONE);
            shipUsaTitle.setVisibility(View.GONE);
            shipUsaDate.setVisibility(View.GONE);

            shipedVnIndicator.setVisibility(View.GONE);
            shipVnTitle.setVisibility(View.GONE);
            shipVnDate.setVisibility(View.GONE);

            deliveriedIndicator.setVisibility(View.GONE);
            deliveryTitle.setVisibility(View.GONE);
            deliveryDate.setVisibility(View.GONE);
        } else if (orderstatus.equals("[VN]Đã lấy hàng/Đã nhập kho")) {
            orderStatus.setBackgroundColor(Color.rgb(98,244,110));
            orderStatus.setTextColor(Color.WHITE);
            orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.sucessGreen)));
            orderDate.setText(String.valueOf(model.getOrderedDate()));

            payedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.sucessGreen)));
            payDate.setText(String.valueOf(model.getPayedDate()));

            packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.sucessGreen)));
            packDate.setText(String.valueOf(model.getPackedDate()));

            shipedUsaIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.sucessGreen)));
            shipUsaDate.setText(String.valueOf(model.getShipedUsaDate()));

            O_Pay_progress.setProgress(100);
            Pay_Pack_progress.setProgress(100);
            Pack_SU_progress.setProgress(100);
            SU_SV_progress.setVisibility(View.GONE);
            SV_D_progress.setVisibility(View.GONE);

            shipedVnIndicator.setVisibility(View.GONE);
            shipVnTitle.setVisibility(View.GONE);
            shipVnDate.setVisibility(View.GONE);

            deliveriedIndicator.setVisibility(View.GONE);
            deliveryTitle.setVisibility(View.GONE);
            deliveryDate.setVisibility(View.GONE);
        } else if (orderstatus.equals("[VN]Đã điều phối giao hàng/Đang giao hàng")) {
            orderStatus.setBackgroundColor(Color.rgb(68,170,77));
            orderStatus.setTextColor(Color.WHITE);
            orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.sucessGreen)));
            orderDate.setText(String.valueOf(model.getOrderedDate()));

            payedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.sucessGreen)));
            payDate.setText(String.valueOf(model.getPayedDate()));

            packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.sucessGreen)));
            packDate.setText(String.valueOf(model.getPackedDate()));

            shipedUsaIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.sucessGreen)));
            shipUsaDate.setText(String.valueOf(model.getShipedUsaDate()));

            shipedVnIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.sucessGreen)));
            shipVnDate.setText(String.valueOf(model.getShipedVnDate()));

            O_Pay_progress.setProgress(100);
            Pay_Pack_progress.setProgress(100);
            Pack_SU_progress.setProgress(100);
            SU_SV_progress.setProgress(100);
            SV_D_progress.setVisibility(View.GONE);

            deliveriedIndicator.setVisibility(View.GONE);
            deliveryTitle.setVisibility(View.GONE);
            deliveryDate.setVisibility(View.GONE);
        } else if (orderstatus.equals("Đã giao hàng")) {
            orderStatus.setBackgroundColor(Color.rgb(40,102,46));
            orderStatus.setTextColor(Color.WHITE);
            orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.sucessGreen)));
            orderDate.setText(String.valueOf(model.getOrderedDate()));

            payedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.sucessGreen)));
            payDate.setText(String.valueOf(model.getPayedDate()));

            packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.sucessGreen)));
            packDate.setText(String.valueOf(model.getPackedDate()));

            shipedUsaIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.sucessGreen)));
            shipUsaDate.setText(String.valueOf(model.getShipedUsaDate()));

            shipedVnIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.sucessGreen)));
            shipVnDate.setText(String.valueOf(model.getShipedVnDate()));

            deliveriedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.sucessGreen)));
            deliveryDate.setText(String.valueOf(model.getDeliveriedDate()));


            O_Pay_progress.setProgress(100);
            Pay_Pack_progress.setProgress(100);
            Pack_SU_progress.setProgress(100);
            SU_SV_progress.setProgress(100);
            SV_D_progress.setProgress(100);
        } else if (orderstatus.equals("Đã hủy")) {
            orderStatus.setBackgroundColor(Color.rgb(217,9,46));
            orderStatus.setTextColor(Color.WHITE);
            if (model.getPayedDate().after(model.getOrderedDate())) {
                if (model.getPackedDate().after(model.getPayedDate())) {
                    orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.sucessGreen)));
                    orderDate.setText(String.valueOf(model.getOrderedDate()));

                    payedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.sucessGreen)));
                    payDate.setText(String.valueOf(model.getPayedDate()));

                    packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.sucessGreen)));
                    packDate.setText(String.valueOf(model.getPackedDate()));

                    shipedUsaIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.sucessGreen)));
                    shipUsaDate.setText(String.valueOf(model.getShipedUsaDate()));

                    shipedVnIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.sucessGreen)));
                    shipVnDate.setText(String.valueOf(model.getShipedVnDate()));

                    deliveriedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
                    deliveryDate.setText(String.valueOf(model.getCancelledDate()));
                    deliveryTitle.setText("Đã hủy");
                } else {
                    orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.sucessGreen)));
                    orderDate.setText(String.valueOf(model.getOrderedDate()));

                    payedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.sucessGreen)));
                    payDate.setText(String.valueOf(model.getPayedDate()));

                    packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
                    packDate.setText(String.valueOf(model.getCancelledDate()));
                    packTitle.setText("Đã hủy");

                    O_Pay_progress.setProgress(100);
                    Pay_Pack_progress.setProgress(100);
                    Pack_SU_progress.setVisibility(View.GONE);
                    SU_SV_progress.setVisibility(View.GONE);
                    SV_D_progress.setVisibility(View.GONE);

                    shipedUsaIndicator.setVisibility(View.GONE);
                    shipUsaTitle.setVisibility(View.GONE);
                    shipUsaDate.setVisibility(View.GONE);

                    shipedVnIndicator.setVisibility(View.GONE);
                    shipVnTitle.setVisibility(View.GONE);
                    shipVnDate.setVisibility(View.GONE);

                    deliveriedIndicator.setVisibility(View.GONE);
                    deliveryTitle.setVisibility(View.GONE);
                    deliveryDate.setVisibility(View.GONE);
                }

            } else {
                orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.sucessGreen)));
                orderDate.setText(String.valueOf(model.getOrderedDate()));

                payedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
                payDate.setText(String.valueOf(model.getCancelledDate()));
                payTitle.setText("Đã hủy");

                O_Pay_progress.setProgress(100);
                Pay_Pack_progress.setVisibility(View.GONE);
                Pack_SU_progress.setVisibility(View.GONE);
                SU_SV_progress.setVisibility(View.GONE);
                SV_D_progress.setVisibility(View.GONE);

                packedIndicator.setVisibility(View.GONE);
                packTitle.setVisibility(View.GONE);
                packDate.setVisibility(View.GONE);

                shipedUsaIndicator.setVisibility(View.GONE);
                shipUsaTitle.setVisibility(View.GONE);
                shipUsaDate.setVisibility(View.GONE);

                shipedVnIndicator.setVisibility(View.GONE);
                shipVnTitle.setVisibility(View.GONE);
                shipVnDate.setVisibility(View.GONE);

                deliveriedIndicator.setVisibility(View.GONE);
                deliveryTitle.setVisibility(View.GONE);
                deliveryDate.setVisibility(View.GONE);
            }
        }
        orderStatus.setText(orderstatus);

        if(orderstatus.equals("Đã hủy")) {
            cancleOrderBtn.setEnabled(false);
            cancleOrderBtn.setText("Đang xử lý hủy đơn hàng");
            cancleOrderBtn.setTextColor(getResources().getColor(R.color.colorPrimary));
            cancleOrderBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ffffff")));
        } else {
            cancleOrderBtn.setEnabled(true);
            cancleOrderBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cancleDialog.show();
                    cancleDialog.findViewById(R.id.no_btn).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            cancleDialog.dismiss();
                        }
                    });
                    cancleDialog.findViewById(R.id.yes_btn).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            cancleDialog.dismiss();
                            loadingDialog.show();

                            Map<String,Object> updateOrder = new HashMap<>();
                            updateOrder.put("order_status","Đã hủy");
                            updateOrder.put("cancelled_date", FieldValue.serverTimestamp());
                            FirebaseFirestore.getInstance().collection("ORDERS").document(model.getOrderId())
                                .update(updateOrder).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        cancleOrderBtn.setEnabled(false);
                                        cancleOrderBtn.setText("Đang xử lý hủy đơn hàng");
                                        cancleOrderBtn.setTextColor(getResources().getColor(R.color.colorPrimary));
                                        cancleOrderBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ffffff")));
                                        Toast.makeText(OrderDetailActivity.this,"Hủy đơn hàng thành công",Toast.LENGTH_LONG).show();
                                    } else {
                                        String error = task.getException().getMessage();
                                        Toast.makeText(OrderDetailActivity.this,error,Toast.LENGTH_LONG).show();
                                    }
                                    loadingDialog.dismiss();
                                }
                            });
                        }
                    });
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}