package com.need.mymall;

import android.Manifest;
import android.app.ActionBar;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.need.mymall.RegisterActivity.setSignUpFragment;

public class MainActivity extends AppCompatActivity {

    private static final int HOME_FRAGMENT = 0;
    private static final int CART_FRAGMENT = 1;
    private static final int ORDER_FRAGMENT = 2;
    private static final int ACC_FRAGMENT = 3;
    public static boolean showCart = false;
    public static  DrawerLayout drawer;
    public static String exchange = null;

    private FrameLayout frameLayout;
    private ImageView actionbarLogo;
    private AppBarConfiguration mAppBarConfiguration;
    NavigationView navigationView;

    private int currentFragment = -1;
    private int scrollFlags;
    private AppBarLayout.LayoutParams params;

    private Dialog signInDialog;
    private FirebaseUser curentUser;
    private MenuItem menuItem;

    private CircleImageView profileView;
    private TextView fullname,email;
    private ImageView addProfileIcon;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        actionbarLogo = findViewById(R.id.actionbar_logo);
        setSupportActionBar(toolbar);

        FixerAPI fixerAPI = new FixerAPI();
        exchange = fixerAPI.getExchange();

        params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        scrollFlags = params.getScrollFlags();

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawerLayout.closeDrawer(GravityCompat.START);
                menuItem = item;

                if (curentUser  != null) {
                    drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
                        @Override
                        public void onDrawerClosed(View drawerView) {
                            super.onDrawerClosed(drawerView);
                            int id = menuItem.getItemId();

                            if (id == R.id.nav_my_mall) {
                                actionbarLogo.setVisibility(View.VISIBLE);
                                invalidateOptionsMenu();
                                setFragment(new HomeFragment(),HOME_FRAGMENT);
                            } else if (id == R.id.nav_my_orders) {
                                gotoFragment("My Order",new MyOrdersFragment(),ORDER_FRAGMENT);
                            } else if (id == R.id.nav_my_cart) {
                                gotoFragment("My Cart",new MyCartFragment(),CART_FRAGMENT);
                            } else if (id == R.id.nav_price_list) {
                                Intent priceListIntent = new Intent(MainActivity.this,PriceListActivity.class);
                                priceListIntent.putExtra("CategoryName","Bảng giá cước");
                                startActivity(priceListIntent);
                            } else if (id == R.id.nav_my_acc) {
                                gotoFragment("My Account",new MyAccountFragment(),ACC_FRAGMENT);
                            } else if (id == R.id.nav_sign_out) {
                                FirebaseAuth.getInstance().signOut();
                                DBQueries.clearData();
                                Intent registerIntent = new Intent(MainActivity.this,RegisterActivity.class);
                                startActivity(registerIntent);
                                finish();
                            }
                        }
                    });
                    return true;
                } else {
                    signInDialog.show();
                    return false;
                }
            }
        });
        navigationView.getMenu().getItem(0).setChecked(true);

        frameLayout = findViewById(R.id.main_framelayout);

        profileView = navigationView.getHeaderView(0).findViewById(R.id.main_profile);
        fullname = navigationView.getHeaderView(0).findViewById(R.id.main_fullname);
        email = navigationView.getHeaderView(0).findViewById(R.id.main_email);
        addProfileIcon = navigationView.getHeaderView(0).findViewById(R.id.add_profile_icon);

        if (showCart) {
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            gotoFragment("My Cart",new MyCartFragment(),-2);
        } else {
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawer,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
            drawer.addDrawerListener(toggle);
            toggle.syncState();
            setFragment(new HomeFragment(),HOME_FRAGMENT);
        }

        signInDialog = new Dialog(MainActivity.this);
        signInDialog.setContentView(R.layout.sign_in_dialog);
        signInDialog.setCancelable(true);

        signInDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        Button dialogSignInBtn = signInDialog.findViewById(R.id.sign_in_btn);
        Button dialogSignUpBtn = signInDialog.findViewById(R.id.sign_up_btn);

        final Intent registerIntent = new Intent(MainActivity.this,RegisterActivity.class);

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

//        ArrayList<CategoryDatabase> categoryDatabase = new ArrayList<>();
//
//        categoryDatabase.add(new CategoryDatabase("11450","May mặc, quần áo, giày dép",10,CategoryDatabase.SURCHARGE_BY_PRICE,3,300));
//        categoryDatabase.add(new CategoryDatabase("1","Collectibles",10,CategoryDatabase.NO_SURCHARGE,0,0));
//        categoryDatabase.add(new CategoryDatabase("20081","Đồ cổ",11,CategoryDatabase.SURCHARGE,5,0));
//        categoryDatabase.add(new CategoryDatabase("550","Nghệ thuật",11,CategoryDatabase.SURCHARGE,5,0));
//        categoryDatabase.add(new CategoryDatabase("2984","Trẻ em",10,CategoryDatabase.NO_SURCHARGE,0,0));
//        categoryDatabase.add(new CategoryDatabase("267","Sách",10,CategoryDatabase.NO_SURCHARGE,0,0));
//        categoryDatabase.add(new CategoryDatabase("12576","Mặt hàng điện tử",1,CategoryDatabase.SURCHARGE,3,0));
//        categoryDatabase.add(new CategoryDatabase("26395","Chăm sóc sức khỏe và làm đẹp",12,CategoryDatabase.SURCHARGE_BY_PRICE,3,300));
//        categoryDatabase.add(new CategoryDatabase("625","Cameras & Photo",11,CategoryDatabase.NO_SURCHARGE,0,0));
//        categoryDatabase.add(new CategoryDatabase("178893","Đồng hổ thông minh",10,CategoryDatabase.SURCHARGE_BY_QUANTITY,0,10));
//        categoryDatabase.add(new CategoryDatabase("182064","Phụ kiện đồng hồ thông minh",10,CategoryDatabase.NO_SURCHARGE,0,0));
//        categoryDatabase.add(new CategoryDatabase("9355","Điện thoại",10,CategoryDatabase.SURCHARGE_BY_QUANTITY,0,10));
//        categoryDatabase.add(new CategoryDatabase("9394","Phụ kiện điện thoại",11,CategoryDatabase.NO_SURCHARGE,0,0));
//        categoryDatabase.add(new CategoryDatabase("171485","Máy tính bảng",11,CategoryDatabase.SURCHARGE_BY_QUANTITY,0,40));
//        categoryDatabase.add(new CategoryDatabase("176970","Phụ kiện máy tính bảng",11,CategoryDatabase.NO_SURCHARGE,0,0));
//        categoryDatabase.add(new CategoryDatabase("175672","Máy tính xách tay",12,CategoryDatabase.SURCHARGE_BY_QUANTITY,0,30));
//        categoryDatabase.add(new CategoryDatabase("171957","Máy tính để bàn",12,CategoryDatabase.SURCHARGE_BY_QUANTITY,0,40));
//        categoryDatabase.add(new CategoryDatabase("31530","Phụ kiện máy tính",10,CategoryDatabase.SURCHARGE,3,0));
//        categoryDatabase.add(new CategoryDatabase("293","Điện tử tiêu dùng",11,CategoryDatabase.SURCHARGE,3,0));
//        categoryDatabase.add(new CategoryDatabase("14339","Đổ thủ công",10,CategoryDatabase.SURCHARGE_BY_QUANTITY,0,20));
//        categoryDatabase.add(new CategoryDatabase("11700","Home & Garden",10,CategoryDatabase.SURCHARGE_BY_QUANTITY,0,10));
//        categoryDatabase.add(new CategoryDatabase("281","Trang sức",15,CategoryDatabase.SURCHARGE,10,0));
//        categoryDatabase.add(new CategoryDatabase("870","Đồ gốm và thủy tinh",12,CategoryDatabase.SURCHARGE,5,0));
//        categoryDatabase.add(new CategoryDatabase("888","Dụng cụ thể thao",12,CategoryDatabase.NO_SURCHARGE,0,0));
//        categoryDatabase.add(new CategoryDatabase("220","Đồ chơi",12,CategoryDatabase.NO_SURCHARGE,0,0));
//        categoryDatabase.add(new CategoryDatabase("99","Các loại khác",15,CategoryDatabase.NO_SURCHARGE,0,0));
//
////        for (int i=0;i<categoryDatabase.size();i++) {
////            mData
////           .child("category").push().setValue(categoryDatabase.get(i));
////        }
//        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
//
//        for (int i=0;i<categoryDatabase.size();i++) {
//            firebaseFirestore.collection("Category").add(categoryDatabase.get(i));
//        }



    }

    @Override
    protected void onStart() {
        super.onStart();
        curentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (curentUser == null) {
            navigationView.getMenu().getItem(navigationView.getMenu().size()-1).setEnabled(false);
        } else {

            DBQueries.checkNotification(false);

            FirebaseFirestore.getInstance().collection("USERS").document(curentUser.getUid())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DBQueries.fullname = task.getResult().getString("fullname");
                        DBQueries.email = task.getResult().getString("email");
                        DBQueries.profile = task.getResult().getString("profile");

                        fullname.setText(DBQueries.fullname);
                        email.setText(DBQueries.email);
                        if (DBQueries.profile.equals("")) {
                            addProfileIcon.setVisibility(View.VISIBLE);
                        } else {
                            addProfileIcon.setVisibility(View.INVISIBLE);
                            Glide.with(MainActivity.this).load(DBQueries.profile).apply(new RequestOptions().placeholder(R.drawable.zhangtian4)).into(profileView);
                        }
                    } else {
                        String error = task.getException().toString();
                        Toast.makeText(MainActivity.this,  error, Toast.LENGTH_SHORT).show();
                    }
                }
            });
            navigationView.getMenu().getItem(navigationView.getMenu().size()-1).setEnabled(true);
        }
        invalidateOptionsMenu();
    }

    @Override
    protected void onPause() {
        super.onPause();
        DBQueries.checkNotification(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (currentFragment == HOME_FRAGMENT) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getMenuInflater().inflate(R.menu.main, menu);

            MenuItem cartItem = menu.findItem(R.id.main_cart_icon);

            if (curentUser != null) {
                if (DBQueries.cartList.size() == 0) {
                    DBQueries.loadCartList(MainActivity.this,new Dialog(MainActivity.this),false);
                }
            } else {
                cartItem.setActionView(null);
            }

            if (DBQueries.cartList.size() > 0) {
                cartItem.setActionView(R.layout.badge_layout);
                ImageView badge_icon = cartItem.getActionView().findViewById(R.id.badge_icon);
                badge_icon.setImageResource(R.mipmap.cart_white);
                TextView badge_count = cartItem.getActionView().findViewById(R.id.badge_count);

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
                            gotoFragment("My Cart",new MyCartFragment(),CART_FRAGMENT);
                        }
                    }
                });
            } else {
                cartItem.setActionView(null);
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.main_search_icon) {
            Intent searchIntent = new Intent(MainActivity.this, SearchActivity.class);
            startActivity(searchIntent);
            return true;
        } else if (id == R.id.main_notification_icon) {
            if (curentUser==null) {
                signInDialog.show();
            } else {
                Intent notificationIntent = new Intent(MainActivity.this, NotificationActivity.class);
                startActivity(notificationIntent);
            }
            return true;
        } else if (id == R.id.main_cart_icon) {
            if (curentUser==null) {
                signInDialog.show();
            } else {
                gotoFragment("My Cart",new MyCartFragment(),CART_FRAGMENT);
            }
            return true;
        } else if (id == android.R.id.home) {
            if (showCart) {
                showCart = false;
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            if (currentFragment == HOME_FRAGMENT) {
                currentFragment = -1;
                super.onBackPressed();
            } else {
                if (showCart) {
                    showCart = false;
                    finish();
                } else {
                    actionbarLogo.setVisibility(View.VISIBLE);
                    invalidateOptionsMenu();
                    setFragment(new HomeFragment(),HOME_FRAGMENT);
                    navigationView.getMenu().getItem(0).setChecked(true);
                }
            }
        }
    }
    private void gotoFragment (String title, Fragment fragment, int fragmentNo) {
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(title);
        actionbarLogo.setVisibility(View.GONE);
        invalidateOptionsMenu();
        setFragment(fragment,fragmentNo);
        if (fragmentNo == CART_FRAGMENT || showCart) {
            navigationView.getMenu().getItem(2).setChecked(true);
            params.setScrollFlags(0);
        } else {
            params.setScrollFlags(scrollFlags);
        }

    }

    private void setFragment(Fragment fragment,int fragmentNo) {
        if (currentFragment != fragmentNo) {
            currentFragment = fragmentNo;
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.fate_in, R.anim.fate_out);
            fragmentTransaction.replace(frameLayout.getId(), fragment);
            fragmentTransaction.commit();
        }
    }
}