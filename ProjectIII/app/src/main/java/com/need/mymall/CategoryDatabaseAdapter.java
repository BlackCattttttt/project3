package com.need.mymall;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CategoryDatabaseAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private ArrayList<CategoryDatabase> categoryDatabaseArrayList;

    public CategoryDatabaseAdapter(Context context, int layout, ArrayList<CategoryDatabase> categoryDatabaseArrayList) {
        this.context = context;
        this.layout = layout;
        this.categoryDatabaseArrayList = categoryDatabaseArrayList;
    }

    @Override
    public int getCount() {
        return categoryDatabaseArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    private class ViewHoder {
        TextView textViewName,textViewPrice,textViewSur;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHoder hoder;
        if (convertView==null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(layout,null);

            hoder = new ViewHoder();
            hoder.textViewName = convertView.findViewById(R.id.categorry_database_dong_name);
            hoder.textViewPrice = convertView.findViewById(R.id.category_database_dong_price);
            hoder.textViewSur = convertView.findViewById(R.id.category_database_dong_surcharge);

            convertView.setTag(hoder);
        } else {
            hoder = (ViewHoder) convertView.getTag();
        }
        CategoryDatabase categoryDatabase = categoryDatabaseArrayList.get(position);
        hoder.textViewName.setText(categoryDatabase.getName());
        hoder.textViewPrice.setText("$" + categoryDatabase.getPrice() +"/kg");
        hoder.textViewSur.setText(categoryDatabase.getSurcharge());
        return convertView;
    }
}
