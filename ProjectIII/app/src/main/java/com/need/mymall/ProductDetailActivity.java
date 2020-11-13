package com.need.mymall;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.ViewPager;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.need.mymall.DBQueries.firebaseFirestore;
import static com.need.mymall.MainActivity.showCart;
import static com.need.mymall.ProductDescriptionFragment.productShortDescription;
import static com.need.mymall.RegisterActivity.setSignUpFragment;

public class ProductDetailActivity extends AppCompatActivity {

    public static boolean ALREADY_ADDED_TO_CART = false;

    public static boolean running_cart_query = false;

    public static MenuItem cartItem;

    public static String productID;

    private ViewPager productImagesViewPager;
    private TabLayout viewPagerIndicator;

    private ViewPager productDetailsViewPager;
    private TabLayout productDetailsTabLayout;

    private TextView product_title;
    private TextView product_price;
    private TextView product_average_rating;
    private TextView product_total_rating;

    private TextView badge_count;

    private Button buyNowButn;
    private LinearLayout addToCartbtn;

    private Dialog loadingDialog;

    ProductDatabase productDatabase;
    int quantity;
    String des=null;

    private Dialog signInDialog;
    private FirebaseUser curentUser;

    ArrayList<CategoryDatabase> categoryDatabaseArrayList;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_product_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Product Detail");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        productImagesViewPager = findViewById(R.id.product_images_viewpager);
        viewPagerIndicator = findViewById(R.id.viewpager_indicator);
        productDetailsViewPager = findViewById(R.id.product_detail_viewpager);
        productDetailsTabLayout = findViewById(R.id.product_description_tablayout);

        product_title = findViewById(R.id.product_title);
        product_price = findViewById(R.id.product_price);
        product_average_rating = findViewById(R.id.tv_product_average_star);
        product_total_rating = findViewById(R.id.total_rating);

        buyNowButn = findViewById(R.id.buy_now_btn);
        addToCartbtn = findViewById(R.id.add_to_cart_btn);

        loadingDialog = new Dialog(ProductDetailActivity.this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_background));
        loadingDialog.show();

        Intent intent = getIntent();
        productDatabase = new ProductDatabase();
        productDatabase = (ProductDatabase) intent.getSerializableExtra("product");
        quantity = intent.getIntExtra("quantity",1);
        des = intent.getStringExtra("description");
        productID = productDatabase.getItemId();

        buyNowButn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (curentUser==null) {
                    signInDialog.show();
                } else {
                    loadingDialog.show();
                    DeliveryActivity.fromCart = false;
                    DBQueries.cartItemModelArrayList.add(0,new CartItemModel(CartItemModel.CART_ITEM,productID,productDatabase.getImagesUrl().get(0),productDatabase.getTitle(),productDatabase.getTotalPrice(),productDatabase.getTransportFeePrice(),quantity,des,productDatabase.getCategoryDatabase().getId()));
                    DBQueries.loadAddresses(ProductDetailActivity.this,loadingDialog,true);
                }
            }
        });

        addToCartbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (curentUser==null) {
                    signInDialog.show();
                } else {
                    if (!running_cart_query) {
                        running_cart_query = true;
                        if (!ALREADY_ADDED_TO_CART) {
                            Map<String,Object> addProduct = new HashMap<>();
                            addProduct.put("product_ID_"+String.valueOf(DBQueries.cartList.size()),productDatabase.getItemId());
                            addProduct.put("quantity_"+String.valueOf(DBQueries.cartList.size()),quantity);
                            addProduct.put("description_"+String.valueOf(DBQueries.cartList.size()),des);
                            addProduct.put("category_ID_"+String.valueOf(DBQueries.cartList.size()),productDatabase.getCategoryDatabase().getId());
                            addProduct.put("list_size",(long) (DBQueries.cartList.size()+1));
                            firebaseFirestore.collection("USERS").document(curentUser.getUid()).collection("USER_DATA").document("MY_CART")
                                .update(addProduct).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        if (DBQueries.cartItemModelArrayList.size() != 0) {
                                            DBQueries.cartItemModelArrayList.add(0,new CartItemModel(CartItemModel.CART_ITEM,productID,productDatabase.getImagesUrl().get(0),productDatabase.getTitle(),productDatabase.getTotalPrice(),productDatabase.getTransportFeePrice(),quantity,des,productDatabase.getCategoryDatabase().getId()));
                                        }
                                        ALREADY_ADDED_TO_CART = true;
                                        DBQueries.cartList.add(productID);
                                        Toast.makeText(ProductDetailActivity.this,"Added to cast successfully",Toast.LENGTH_LONG).show();
                                        invalidateOptionsMenu();
                                        running_cart_query = false;
                                    } else {
                                        running_cart_query = false;
                                        String error = task.getException().getMessage();
                                        Toast.makeText(ProductDetailActivity.this,error,Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(ProductDetailActivity.this,"Already added to cart",Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
        });

        signInDialog = new Dialog(ProductDetailActivity.this);
        signInDialog.setContentView(R.layout.sign_in_dialog);
        signInDialog.setCancelable(true);
        signInDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);

        Button dialogSignInBtn = signInDialog.findViewById(R.id.sign_in_btn);
        Button dialogSignUpBtn = signInDialog.findViewById(R.id.sign_up_btn);

        final Intent registerIntent = new Intent(ProductDetailActivity.this,RegisterActivity.class);

        dialogSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignInFragment.disableCloseBtn = true;
                SignUpFragment.disableCloseBtn = true;
                signInDialog.dismiss();
                setSignUpFragment = false;
                startActivity(registerIntent);
            }
        });

        dialogSignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignUpFragment.disableCloseBtn = true;
                SignInFragment.disableCloseBtn = true;
                signInDialog.dismiss();
                setSignUpFragment = true;
                startActivity(registerIntent);
            }
        });
    }

    public void loadProduct() {

        product_title.setText(productDatabase.getTitle());
        double amount = Double.parseDouble(productDatabase.getPrice()) * Double.parseDouble(MainActivity.exchange);
        product_price.setText(String.format("%,.2f",amount) + "đ");
        product_average_rating.setText(productDatabase.getAverageRating());
        product_total_rating.setText(productDatabase.getReviewCount() + " ratings");

        ProductDescriptionFragment.productShortDescription = productDatabase.getShortDescription();
        ProductDescriptionFragment.productDescription = productDatabase.getDescription();

        productDatabase.caculatePrice(1);

        double transportFee = Double.parseDouble(productDatabase.getTransportFeePrice());
        double totalPrice = (Double.parseDouble(productDatabase.getTotalPrice())+transportFee) * quantity;

        ProductQuoteFragment.orginalPrice = productDatabase.getPrice();
        ProductQuoteFragment.transportPrice = String.valueOf(transportFee);
        ProductQuoteFragment.surchagePrice = productDatabase.getSurchagePrice();
        ProductQuoteFragment.quantity = quantity;
        ProductQuoteFragment.totalPrice = String.valueOf(totalPrice);

        ProductSpecificationFragment.productSpecificationModelArrayList = productDatabase.getLocalizedAspects();
        ProductSpecificationFragment.descrip = des;

        ProductImagesAdapter productImagesAdapter = new ProductImagesAdapter(productDatabase.getImagesUrl());
        productImagesViewPager.setAdapter(productImagesAdapter);

        viewPagerIndicator.setupWithViewPager(productImagesViewPager,true);

        productDetailsViewPager.setAdapter(new ProductDetailAdapter(getSupportFragmentManager(),productDetailsTabLayout.getTabCount()));
        productDetailsViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(productDetailsTabLayout));
        productDetailsTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                productDetailsViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        if (DBQueries.cartList.contains(productID)) {
            ALREADY_ADDED_TO_CART = true;
        } else {
            ALREADY_ADDED_TO_CART = false;
        }
        loadingDialog.dismiss();

    }

    @Override
    protected void onStart() {
        super.onStart();
        categoryDatabaseArrayList = new ArrayList<>();
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        firebaseFirestore.collection("Category").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        CategoryDatabase categoryDatabase = document.toObject(CategoryDatabase.class);
                        categoryDatabaseArrayList.add(categoryDatabase);
                    }
                    boolean flag = false;
                    for (int i=0;i<productDatabase.getCategories().size();i++) {
                        for (int j=0;j<categoryDatabaseArrayList.size();j++) {
                            if (productDatabase.getCategories().get(i).equals(categoryDatabaseArrayList.get(j).getId())) {
                                productDatabase.setCategoryDatabase(categoryDatabaseArrayList.get(j));
                                flag = true;
                                break;
                            }
                        }
                    }

                    if (!flag) {
                        productDatabase.setCategoryDatabase(categoryDatabaseArrayList.get(categoryDatabaseArrayList.size()-1));
                    }
                    loadProduct();
                } else {
                    String error = task.getException().getMessage();
                    Toast.makeText(ProductDetailActivity.this,error,Toast.LENGTH_LONG).show();
                }
            }
        });

        curentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (curentUser!= null) {
//            if (DBQueries.cartList.size()==0) {
//                DBQueries.cartList.clear();
//                DBQueries.loadCartList(ProductDetailActivity.this,loadingDialog,false);
//            } else {
//                loadingDialog.dismiss();
//            }
        } else {
            loadingDialog.dismiss();
        }

        if (DBQueries.cartList.contains(productID)) {
            ALREADY_ADDED_TO_CART = true;
        } else {
            ALREADY_ADDED_TO_CART = false;
        }
        invalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search_and_cart_icon, menu);

        cartItem = menu.findItem(R.id.main_cart_icon);

        if (curentUser != null) {
            if (DBQueries.cartList.size() == 0) {
                DBQueries.loadCartList(ProductDetailActivity.this,loadingDialog,false);
            } else {
               int length = DBQueries.cartList.size();
//               int index = length/2;
//               for (int i = index;i<length;i++) {
//                   DBQueries.cartList.remove(index);
//               }
            }
        } else {
            cartItem.setActionView(null);
        }
        if (DBQueries.cartList.size() > 0) {
            cartItem.setActionView(R.layout.badge_layout);
            ImageView badge_icon = cartItem.getActionView().findViewById(R.id.badge_icon);
            badge_icon.setImageResource(R.mipmap.cart_white);
            TextView badge_count = cartItem.getActionView().findViewById(R.id.badge_count);
            badge_count.setText(DBQueries.cartList.size()+"");

            if (DBQueries.cartList.size()<99) {
                badge_count.setText(String.valueOf(DBQueries.cartList.size()));
            } else {
                badge_count.setText("99");
            }

            cartItem.getActionView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (curentUser==null) {
                        signInDialog.show();
                    } else {
                        Intent cartIntent = new Intent(ProductDetailActivity.this,MainActivity.class);
                        showCart = true;
                        startActivity(cartIntent);
                    }
                }
            });
        } else {
            cartItem.setActionView(null);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.main_search_icon) {
            Intent searchIntent = new Intent(ProductDetailActivity.this, SearchActivity.class);
            startActivity(searchIntent);
            return true;
        } else if (id == R.id.main_cart_icon) {
            if (curentUser==null) {
                signInDialog.show();
            } else {
                Intent cartIntent = new Intent(ProductDetailActivity.this,MainActivity.class);
                showCart = true;
                startActivity(cartIntent);
            }
            return true;
        } else if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}