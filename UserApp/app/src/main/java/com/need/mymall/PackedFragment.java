package com.need.mymall;

import android.app.Dialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class PackedFragment extends Fragment {
    private RecyclerView myOrderRecycleView;
    private MyOrderAdapter packAdapter;

    private Dialog loadingDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ordered, container, false);

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

        packAdapter = new MyOrderAdapter(DBQueries.packed);
        myOrderRecycleView.setAdapter(packAdapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        loadingDialog.show();
        DBQueries.loadPackedOrders(getContext(),loadingDialog,packAdapter);

    }
}