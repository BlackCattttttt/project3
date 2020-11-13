package com.need.mymall;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class ProductSpecificationFragment extends Fragment {

    private RecyclerView productSpecificationRecycleView;
    private TextView des;

    public static ArrayList<ProductSpecificationModel> productSpecificationModelArrayList = new ArrayList<>();
    public static String descrip;

    public ProductSpecificationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_specification, container, false);

        des = view.findViewById(R.id.product_description);
        productSpecificationRecycleView = view.findViewById(R.id.product_specitication_recycleview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        productSpecificationRecycleView.setLayoutManager(layoutManager);

        if (descrip.equals("")) {
            des.setVisibility(View.GONE);
        } else {
            des.setVisibility(View.VISIBLE);
            des.setText(descrip);
        }
        if (productSpecificationModelArrayList.size() == 0) {
            productSpecificationRecycleView.setVisibility(View.GONE);
        } else {
            productSpecificationRecycleView.setVisibility(View.VISIBLE);
            ProductSpecificationAdapter adapter = new ProductSpecificationAdapter(productSpecificationModelArrayList);
            productSpecificationRecycleView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }

        return view;
    }
}