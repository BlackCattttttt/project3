package com.need.mymall;

import android.app.Dialog;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;

public class MyOrdersFragment extends Fragment {

    private RecyclerView myOrderRecycleView;

    private Dialog loadingDialog;

    private MyOrderAdapter orderAdapter;

    public MyOrdersFragment() {
        // Required empty public constructor
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_orders, container, false);

        loadingDialog = new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.getWindow().setBackgroundDrawable(getActivity().getDrawable(R.drawable.slider_background));
        loadingDialog.show();

        myOrderRecycleView = view.findViewById(R.id.my_orders_recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        myOrderRecycleView.setLayoutManager(layoutManager);

        orderAdapter = new MyOrderAdapter(DBQueries.myOrderItemModelArrayList);
        myOrderRecycleView.setAdapter(orderAdapter);

        //DBQueries.loadOrders(getContext(),loadingDialog,orderAdapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        loadingDialog.show();
        DBQueries.loadOrders(getContext(),loadingDialog,orderAdapter);
    }
}