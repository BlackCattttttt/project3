package com.need.mymall;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ProductSpecificationAdapter extends RecyclerView.Adapter<ProductSpecificationAdapter.ViewHolder> {

    private ArrayList<ProductSpecificationModel> productSpecificationModelArrayList;

    public ProductSpecificationAdapter(ArrayList<ProductSpecificationModel> productSpecificationModelArrayList) {
        this.productSpecificationModelArrayList = productSpecificationModelArrayList;
    }

    @NonNull
    @Override
    public ProductSpecificationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_specification_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductSpecificationAdapter.ViewHolder holder, int position) {
        String name = productSpecificationModelArrayList.get(position).getFeatureName();
        String value = productSpecificationModelArrayList.get(position).getFeatureValue();

        holder.setFeature(name,value);
    }

    @Override
    public int getItemCount() {
        return productSpecificationModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewFeatureName;
        private TextView textViewFeatureValue;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewFeatureName = itemView.findViewById(R.id.feature_name);
            textViewFeatureValue = itemView.findViewById(R.id.feature_value);
        }

        private void setFeature (String name,String value){
            textViewFeatureName.setText(name);
            textViewFeatureValue.setText(value);
        }
    }
}
