package com.need.mymall;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.need.mymall.R;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private LinearLayout homeLinearLayout;

    ImageView noInternetConection;
    ImageButton resetBtn;

    EbayAPI ebayAPI;

    private Dialog loadingDialog;

    //Banner Slider
    private ViewPager viewPager;
    private ArrayList<SliderModer> sliderModerArrayList;
    private int currentPage = 0;
    private Timer timer;

    //RequestQuote
    private EditText editTextName,editTextCount,editTextDescription;
    private Button request_quote_btn;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        ebayAPI = new EbayAPI();
        noInternetConection = view.findViewById(R.id.no_internet_conection);
        recyclerView = view.findViewById(R.id.category_recyclerView);
        viewPager = view.findViewById(R.id.banner_slider_viewpager);
        homeLinearLayout = view.findViewById(R.id.home_linear_layout);

        loadingDialog = new Dialog(getActivity());
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.getWindow().setBackgroundDrawable(getActivity().getDrawable(R.drawable.slider_background));

        //Requestquote
        editTextName = view.findViewById(R.id.et_request_quote_name);
        editTextCount = view.findViewById(R.id.et_request_quote_count);
        editTextDescription = view.findViewById(R.id.et_request_quote_description);
        request_quote_btn = view.findViewById(R.id.request_quote_btn);
        resetBtn = view.findViewById(R.id.reset_btn);

        reloadPage();

        noInternetConection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reloadPage();
            }
        });
        return view;
    }

    private void reloadPage() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        DBQueries.clearData();
        if (networkInfo != null && networkInfo.isConnected() == true) {

            MainActivity.drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            homeLinearLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
            noInternetConection.setVisibility(View.INVISIBLE);

            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            recyclerView.setLayoutManager(layoutManager);

            ArrayList<CategoryModel> categoryModelArrayList = new ArrayList<CategoryModel>();
            categoryModelArrayList.add(new CategoryModel(CategoryModel.HOMEPAGE,R.mipmap.home_icon,"Trang chủ"));
            categoryModelArrayList.add(new CategoryModel(CategoryModel.SEARCH,R.mipmap.search_black,"Tìm kiếm"));
            categoryModelArrayList.add(new CategoryModel(CategoryModel.PRICE_LIST,R.mipmap.pricelist_icon,"Bảng giá cước"));
            categoryModelArrayList.add(new CategoryModel(CategoryModel.CONTACT,R.mipmap.contact_icon,"Liên hệ"));

            CategoryAdapter categoryAdapter = new CategoryAdapter(categoryModelArrayList);
            recyclerView.setAdapter(categoryAdapter);
            categoryAdapter.notifyDataSetChanged();

            //Banner Slider

            sliderModerArrayList = new ArrayList<SliderModer>();

            sliderModerArrayList.add(new SliderModer(R.drawable.ebay_store_banner));
            sliderModerArrayList.add(new SliderModer(R.drawable.banner_slide1));
            sliderModerArrayList.add(new SliderModer(R.drawable.banner_slide2));

            if (timer != null) {
                timer.cancel();
            }
            SliderAdapter adapter = new SliderAdapter(sliderModerArrayList);
            viewPager.setAdapter(adapter);
            viewPager.setClipToPadding(false);
            viewPager.setPageMargin(20);

            ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    currentPage = position;
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                    if (state == ViewPager.SCROLL_STATE_IDLE) {
                        pageLooper();
                    }
                }
            };
            viewPager.addOnPageChangeListener(onPageChangeListener);

            startBannerSlideShow();

            viewPager.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    pageLooper();
                    stopBannerSlideShow();
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        startBannerSlideShow();
                    }
                    return false;
                }
            });

            request_quote_btn.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onClick(View v) {
                    loadingDialog.show();
                    productDetail();
                }
            });

            resetBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editTextName.setText("");
                }
            });

            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                DBQueries.loadCartList(getActivity(),loadingDialog,false);
            } else {
                loadingDialog.dismiss();
            }
        } else {
            MainActivity.drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            Toast.makeText(getActivity(),"Không có kết nối mạng",Toast.LENGTH_LONG).show();
            recyclerView.setVisibility(View.GONE);
            homeLinearLayout.setVisibility(View.GONE);
            noInternetConection.setVisibility(View.VISIBLE);
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void productDetail () {
        String productLink = editTextName.getText().toString();
        if (!productLink.isEmpty() && !TextUtils.isEmpty(editTextCount.getText())) {
            Float quantity = Float.parseFloat(editTextCount.getText().toString());
            String description = editTextDescription.getText().toString();
            if (description == null) description = "";
            if (quantity == quantity.intValue() && quantity>0) {
                if (productLink.startsWith("https://www.ebay.com/p/")) {
                    int index = productLink.lastIndexOf("/p/") + 2;

                    int lastIndex = index;
                    for (int i = index + 1; i < productLink.length(); i++) {
                        if (productLink.charAt(i) >= '0' && productLink.charAt(i) <= '9') {
                            lastIndex = i;
                            continue;
                        }
                        break;
                    }
                    String epid = productLink.substring(index + 1, lastIndex + 1);
                    Toast.makeText(getActivity(), epid, Toast.LENGTH_LONG).show();

                    ProductDatabase productDatabase = new ProductDatabase();
                    productDatabase = ebayAPI.getItemByEpid(epid);
                    Intent productDetailIntent = new Intent(getActivity(), ProductDetailActivity.class);
                    productDetailIntent.putExtra("product", productDatabase);
                    productDetailIntent.putExtra("quantity", quantity.intValue());
                    productDetailIntent.putExtra("description",description);
                    loadingDialog.dismiss();
                    getActivity().startActivity(productDetailIntent);

                } else if (productLink.startsWith("https://www.ebay.com/itm/")) {
                    int index = productLink.lastIndexOf("/itm/") + 4;

                    int lastIndex = index;
                    for (int i = index + 1; i < productLink.length(); i++) {
                        if (productLink.charAt(i) != '/') {
                            lastIndex = i;
                            continue;
                        }
                        break;
                    }
                    String name = productLink.substring(index + 1, lastIndex + 1);
                    Toast.makeText(getActivity(), name, Toast.LENGTH_LONG).show();

                    ProductDatabase productDatabase = new ProductDatabase();
                    productDatabase = ebayAPI.getItemByName(name,productLink);
                    if (productDatabase!=null) {
                        Intent productDetailIntent = new Intent(getActivity(), ProductDetailActivity.class);
                        productDetailIntent.putExtra("product", productDatabase);
                        productDetailIntent.putExtra("quantity", quantity.intValue());
                        productDetailIntent.putExtra("description",description);
                        loadingDialog.dismiss();
                        getActivity().startActivity(productDetailIntent);
                    } else {
                        Toast.makeText(getActivity(),"Link không chính xác hoặc sản phẩm không tồn tại",Toast.LENGTH_LONG).show();
                        loadingDialog.dismiss();
                    }
                }
                else {
                    Toast.makeText(getActivity(),"Nhập link không hợp lệ",Toast.LENGTH_LONG).show();
                    loadingDialog.dismiss();
                }
            } else {
                Toast.makeText(getActivity(),"Số lượng sản phẩm không hợp lệ",Toast.LENGTH_LONG).show();
                loadingDialog.dismiss();
            }
        } else {
            Toast.makeText(getActivity(),"Bạn chưa điền đủ các trường",Toast.LENGTH_LONG).show();
            loadingDialog.dismiss();
        }
    }
    private void pageLooper() {
//        if (currentPage == sliderModerArrayList.size() - 1) {
//            currentPage = 0;
//            viewPager.setCurrentItem(currentPage,false);
//        }
//        if (currentPage == 0) {
//            currentPage = sliderModerArrayList.size() - 1;
//            viewPager.setCurrentItem(currentPage,false);
//        }
    }

    private void startBannerSlideShow() {
        final Handler handler = new Handler();
        final Runnable update = new Runnable() {
            @Override
            public void run() {
                if (currentPage>=sliderModerArrayList.size()) {
                    currentPage = 0;
                }
                viewPager.setCurrentItem(currentPage++,true);
            }
        };
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(update);
            }
        },3000,3000);
    }
    private void stopBannerSlideShow() {
        timer.cancel();
    }

}