package com.need.mymall;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class SearchActivity extends AppCompatActivity {

    private SearchView searchView;
    private TextView noProduct;
    private RecyclerView resultSearch;

    private Dialog loadingDialog;

    private ArrayList<ProductDatabase> databaseArrayList;

    private ProductItemRecycleViewAdapter recycleViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        loadingDialog = new Dialog(SearchActivity.this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_background));

        searchView = findViewById(R.id.search_view);
        noProduct = findViewById(R.id.tv_no_product);
        resultSearch = findViewById(R.id.result_search);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        resultSearch.setLayoutManager(layoutManager);

        databaseArrayList = new ArrayList<>();

        recycleViewAdapter = new ProductItemRecycleViewAdapter(databaseArrayList);
        resultSearch.setAdapter(recycleViewAdapter);
        recycleViewAdapter.notifyDataSetChanged();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public boolean onQueryTextSubmit(String query) {
                databaseArrayList.clear();
                loadingDialog.show();
                query = query.replace(' ','-');
                EbayAPI ebayAPI = new EbayAPI();
                ArrayList<ProductDatabase> temp = new ArrayList<>();
                temp = ebayAPI.search(query,loadingDialog);
                if (temp == null) {
                    loadingDialog.dismiss();
                    noProduct.setVisibility(View.VISIBLE);
                    resultSearch.setVisibility(View.GONE);
                } else {
                    noProduct.setVisibility(View.GONE);
                    resultSearch.setVisibility(View.VISIBLE);
                    for (ProductDatabase productDatabase : temp) {
                        databaseArrayList.add(productDatabase);
                        recycleViewAdapter.notifyDataSetChanged();
                    }
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

    }

    public class loadProduct extends AsyncTask<String,Void,ArrayList<ProductDatabase>> {

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected ArrayList<ProductDatabase> doInBackground(String... strings) {
            EbayAPI ebayAPI = new EbayAPI();
            ArrayList<ProductDatabase> temp = new ArrayList<>();
            temp = ebayAPI.search(strings[0],loadingDialog);
            return temp;
        }
    }

}