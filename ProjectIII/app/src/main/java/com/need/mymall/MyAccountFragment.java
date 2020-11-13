package com.need.mymall;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyAccountFragment extends Fragment {

    private Button viewAllAdressBtn;
    private Button signOutBtn;

    private CircleImageView profile_image;
    private TextView name,email;

    private TextView yourRecentOrdeTitle;
    private LinearLayout recentOrderContainer;

    private TextView fullname,phone,address;

    private FloatingActionButton settingBtn;

    public static final int MANAGER_ADDRESS = 1;

    private Dialog loadingDialog;

    public MyAccountFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_account, container, false);

        loadingDialog = new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.getWindow().setBackgroundDrawable(getActivity().getDrawable(R.drawable.slider_background));
        loadingDialog.show();

        viewAllAdressBtn = view.findViewById(R.id.viewall_address_btn);
        signOutBtn = view.findViewById(R.id.sign_out_btn);
        profile_image = view.findViewById(R.id.profile_image);
        name = view.findViewById(R.id.profile_usename);
        email = view.findViewById(R.id.profile_email);

        yourRecentOrdeTitle = view.findViewById(R.id.your_recent_order_title);
        recentOrderContainer = view.findViewById(R.id.recent_order_container);

        fullname = view.findViewById(R.id.my_acc_fullname);
        phone = view.findViewById(R.id.my_acc_phonenumber);
        address = view.findViewById(R.id.my_acc_address);

        settingBtn = view.findViewById(R.id.setting_btn);

        name.setText(DBQueries.fullname);
        email.setText(DBQueries.email);
        if (!DBQueries.profile.equals("")) {
            Glide.with(getContext()).load(DBQueries.profile).apply(new RequestOptions().placeholder(R.drawable.zhangtian4)).into(profile_image);
        }

        loadingDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                int i=0;
                for (MyOrderItemModel myOrderItemModel : DBQueries.myOrderItemModelArrayList) {
                    if (i<4) {
                        if (myOrderItemModel.getOrderStatus().equals("Đã giao hàng")) {
                            Glide.with(getContext()).load(myOrderItemModel.getProductOrderArrayList().get(0).getProductImg()).apply(new RequestOptions().placeholder(R.drawable.no_image)).into((CircleImageView) recentOrderContainer.getChildAt(i));
                            i++;
                        }
                    } else {
                        break;
                    }
                }
                if (i==0) {
                    yourRecentOrdeTitle.setText("Bạn chưa có đơn hàng nào");
                }
                if (i<3) {
                    for (int x = i;x<4;x++) {
                        recentOrderContainer.getChildAt(x).setVisibility(View.GONE);
                    }
                }
                loadingDialog.show();
                loadingDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        loadingDialog.setOnDismissListener(null);
                        if (DBQueries.addressesModelArrayList.size() == 0) {
                            fullname.setText("Không có địa chỉ");
                            phone.setText("-");
                            address.setText("-");
                        } else {
                            fullname.setText(DBQueries.addressesModelArrayList.get(DBQueries.selectedAddress).getFullname());
                            phone.setText(DBQueries.addressesModelArrayList.get(DBQueries.selectedAddress).getPhonenumber());
                            address.setText(DBQueries.addressesModelArrayList.get(DBQueries.selectedAddress).getAddressFull());
                        }
                    }
                });
                DBQueries.loadAddresses(getContext(),loadingDialog,false);
            }
        });
        DBQueries.loadOrders(getContext(),loadingDialog,null);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        name.setText(DBQueries.fullname);
        email.setText(DBQueries.email);
        if (!DBQueries.profile.equals("")) {
            Glide.with(getContext()).load(DBQueries.profile).apply(new RequestOptions().placeholder(R.drawable.zhangtian4)).into(profile_image);
        }
        if(!loadingDialog.isShowing()) {
            if (DBQueries.addressesModelArrayList.size() == 0) {
                fullname.setText("Không có địa chỉ");
                phone.setText("-");
                address.setText("-");
            } else {
                fullname.setText(DBQueries.addressesModelArrayList.get(DBQueries.selectedAddress).getFullname());
                phone.setText(DBQueries.addressesModelArrayList.get(DBQueries.selectedAddress).getPhonenumber());
                address.setText(DBQueries.addressesModelArrayList.get(DBQueries.selectedAddress).getAddressFull());
            }
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewAllAdressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myAddressIntent = new Intent(getActivity(),MyAddressActivity.class);
                myAddressIntent.putExtra("MODE",MANAGER_ADDRESS);
                startActivity(myAddressIntent);
            }
        });
        signOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                DBQueries.clearData();
                Intent registerIntent = new Intent(getContext(),RegisterActivity.class);
                startActivity(registerIntent);
                getActivity().finish();
            }
        });
        settingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent updateUserInfor = new Intent(getContext(),UpdateUserInforActivity.class);
                updateUserInfor.putExtra("Name",name.getText().toString());
                updateUserInfor.putExtra("Email",email.getText().toString());
                updateUserInfor.putExtra("Photo",DBQueries.profile);
                startActivity(updateUserInfor);
            }
        });
    }
}