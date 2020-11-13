package com.need.mymall;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MyCartFragment extends Fragment {

    private RecyclerView cartItemRecyclerView;
    private Button proceed_to_order_btn;

    private Dialog loadingDialog;

    public static CartAdapter cartAdapter;

    public MyCartFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_cart, container, false);

        loadingDialog = new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.getWindow().setBackgroundDrawable(getActivity().getDrawable(R.drawable.slider_background));
        loadingDialog.show();

        cartItemRecyclerView = view.findViewById(R.id.cart_item_recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        cartItemRecyclerView.setLayoutManager(layoutManager);

        cartAdapter = new CartAdapter(DBQueries.cartItemModelArrayList,new TextView(getContext()),new TextView(getContext()));
        cartItemRecyclerView.setAdapter(cartAdapter);
        cartAdapter.notifyDataSetChanged();

        proceed_to_order_btn = view.findViewById(R.id.proceed_to_order_btn);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        proceed_to_order_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DBQueries.cartItemModelArrayList.size()>0) {
                    DeliveryActivity.fromCart = true;
                    loadingDialog.show();
                    Map<String,Object> updateQuantity = new HashMap<>();
                    for (int x = 0;x<DBQueries.cartList.size();x++) {
                        for (int y = 0;y<DBQueries.cartItemModelArrayList.size();y++) {
                            if (DBQueries.cartItemModelArrayList.get(y).getType() == CartItemModel.CART_ITEM && DBQueries.cartList.get(x).equals(DBQueries.cartItemModelArrayList.get(y).getProductId())) {
                                updateQuantity.put("quantity_" + String.valueOf(x), (long) DBQueries.cartItemModelArrayList.get(y).getProductQuantity());
                            }
                        }
                    }
                    DBQueries.firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_CART")
                            .update(updateQuantity).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                            } else {
                                String error = task.getException().getMessage();
                                Toast.makeText(getContext(),error,Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    DBQueries.loadAddresses(getContext(),loadingDialog,true);
                } else {
                    Toast.makeText(getContext(),"Không có sản phẩm nào trong giỏ",Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        cartAdapter.notifyDataSetChanged();
        if (DBQueries.cartItemModelArrayList.size() == 0) {
            DBQueries.cartList.clear();
            DBQueries.loadCartList(getContext(),loadingDialog,true);
        } else {
            DBQueries.cartList.clear();
            DBQueries.loadCartList(getContext(),loadingDialog,false);
            loadingDialog.dismiss();
        }
    }
}