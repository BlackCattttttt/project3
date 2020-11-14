package com.need.mymall;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private ArrayList<CategoryModel> categoryModelArrayList;

    public CategoryAdapter(ArrayList<CategoryModel> categoryModelArrayList) {
        this.categoryModelArrayList = categoryModelArrayList;
    }

    @NonNull
    @Override
    public CategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.ViewHolder holder, int position) {
        int icon = categoryModelArrayList.get(position).getLink();
        String name = categoryModelArrayList.get(position).getName();
        int type = categoryModelArrayList.get(position).getType();
        holder.setCategoryName(name);
        holder.setCategory(name,type);
        holder.setCategoryIcon(icon);
    }

    @Override
    public int getItemCount() {
        return categoryModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView categoryIcon;
        private TextView categoryName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryIcon = itemView.findViewById(R.id.category_icon);
            categoryName = itemView.findViewById(R.id.categor_name);
        }

        private void setCategoryIcon (int res) {
            categoryIcon.setImageResource(res);
        }

        private void setCategoryName(String name) {
            categoryName.setText(name);
        }

        private void setCategory(final String name, final int type) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (type) {
                        case CategoryModel.HOMEPAGE:
                            break;
                        case CategoryModel.SEARCH:
                            Intent intent = new Intent(itemView.getContext(),SearchActivity.class);
                            itemView.getContext().startActivity(intent);
                            break;
                        case CategoryModel.PRICE_LIST:
                            Intent priceListIntent = new Intent(itemView.getContext(),PriceListActivity.class);
                            priceListIntent.putExtra("CategoryName",name);
                            itemView.getContext().startActivity(priceListIntent);
                            break;
                        case CategoryModel.CONTACT:
                            Intent contactIntent = new Intent(itemView.getContext(),ContactActivity.class);
                            contactIntent.putExtra("CategoryName",name);
                            itemView.getContext().startActivity(contactIntent);
                            break;
                    }
                }
            });
        }
    }
}
