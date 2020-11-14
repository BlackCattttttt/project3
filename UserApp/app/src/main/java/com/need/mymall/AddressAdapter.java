package com.need.mymall;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.need.mymall.DeliveryActivity.SELECT_ADDRESS;
import static com.need.mymall.MyAccountFragment.MANAGER_ADDRESS;
import static com.need.mymall.MyAddressActivity.refeshItem;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ViewHolder> {

    private ArrayList<AddressesModel> addressesModelArrayList;
    private int MODE;
    private int preSelectedPosition;
    private boolean refresh = false;
    private Dialog loadingDialog;

    public AddressAdapter(ArrayList<AddressesModel> addressesModelArrayList, int mode, @Nullable Dialog loadingDialog) {
        this.addressesModelArrayList = addressesModelArrayList;
        this.MODE = mode;
        preSelectedPosition = DBQueries.selectedAddress;
        if (loadingDialog != null) {
            this.loadingDialog = loadingDialog;
        }
    }

    @NonNull
    @Override
    public AddressAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.address_item_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressAdapter.ViewHolder holder, int position) {
        String name = addressesModelArrayList.get(position).getFullname();
        String phone = addressesModelArrayList.get(position).getPhonenumber();
        String add = addressesModelArrayList.get(position).getAddressFull();
        Boolean selected = addressesModelArrayList.get(position).getSelected();

        holder.setData(name,phone,add,selected,position);
    }

    @Override
    public int getItemCount() {
        return addressesModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView fullname;
        private TextView phone;
        private TextView address;
        private ImageView icon;
        private LinearLayout optionContainer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            fullname = itemView.findViewById(R.id.address_item_fullname);
            phone = itemView.findViewById(R.id.address_item_phonenumber);
            address = itemView.findViewById(R.id.address_item_address);
            icon = itemView.findViewById(R.id.icon_view);
            optionContainer = itemView.findViewById(R.id.option_container);
        }

        private void setData (String name, String phone, String address, boolean selected, final int position) {
            fullname.setText(name);
            this.phone.setText(phone);
            this.address.setText(address);

            if (MODE == SELECT_ADDRESS) {
                icon.setImageResource(R.mipmap.check);
                if (selected) {
                    icon.setVisibility(View.VISIBLE);
                    preSelectedPosition = position;
                } else {
                    icon.setVisibility(View.GONE);
                }

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (preSelectedPosition != position) {
                            addressesModelArrayList.get(position).setSelected(true);
                            addressesModelArrayList.get(preSelectedPosition).setSelected(false);
                            refeshItem(preSelectedPosition,position);
                            preSelectedPosition = position;
                            DBQueries.selectedAddress = position;
                        }
                    }
                });
            } else if (MODE == MANAGER_ADDRESS) {
                icon.setImageResource(R.mipmap.vertical_dots);
                optionContainer.setVisibility(View.GONE);

                optionContainer.getChildAt(0).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent addAddressIntent = new Intent(itemView.getContext(),AddAddressActivity.class);
                        addAddressIntent.putExtra("INTENT","edit_address");
                        addAddressIntent.putExtra("index",position);
                        itemView.getContext().startActivity(addAddressIntent);
                        refresh = false;
                    }
                });
                optionContainer.getChildAt(1).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        final AlertDialog.Builder deleteDialog = new AlertDialog.Builder(itemView.getContext(),R.style.MyDialogTheme);
                        deleteDialog.setTitle("Xóa địa chỉ");
                        deleteDialog.setMessage("Bạn có chắc muốn xóa địa chỉ này không?");

                        deleteDialog.setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                loadingDialog.show();

                                Map<String,Object> addresses = new HashMap<>();
                                int x = 1;
                                for (int i = 0;i< addressesModelArrayList.size();i++) {
                                    if (i!=position) {
                                        addresses.put("fullname_" +String.valueOf(x),addressesModelArrayList.get(i).getFullname());
                                        addresses.put("phonenumber_" +String.valueOf(x),addressesModelArrayList.get(i).getPhonenumber());
                                        addresses.put("address_full_" +String.valueOf(x),addressesModelArrayList.get(i).getAddressFull());
                                        addresses.put("address_" +String.valueOf(x),addressesModelArrayList.get(i).getAddress());
                                        addresses.put("province_" +String.valueOf(x),addressesModelArrayList.get(i).getProvinces());
                                        addresses.put("distric_" +String.valueOf(x),addressesModelArrayList.get(i).getDistric());
                                        addresses.put("description_" +String.valueOf(x),addressesModelArrayList.get(i).getDescription());
                                        addresses.put("selected_" +String.valueOf(x),addressesModelArrayList.get(i).getSelected().toString());
                                        x++;
                                    }
                                }
                                addresses.put("list_size",x-1);
                                FirebaseFirestore.getInstance().collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_ADDRESSES")
                                        .set(addresses).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            if (DBQueries.addressesModelArrayList.get(position).getSelected()) {
                                                DBQueries.selectedAddress = 0;
                                                DBQueries.addressesModelArrayList.get(0).setSelected(true);
                                            }
                                            DBQueries.addressesModelArrayList.remove(position);
                                            notifyDataSetChanged();
                                        } else {
                                            String error = task.getException().getMessage();
                                            Toast.makeText(itemView.getContext(),error,Toast.LENGTH_LONG).show();
                                        }
                                        loadingDialog.dismiss();
                                    }
                                });
                                refresh = false;
                            }
                        });

                        deleteDialog.setNegativeButton("Đóng", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });

                        deleteDialog.show();
                    }
                });
                icon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        optionContainer.setVisibility(View.VISIBLE);
                        if (refresh) {
                            refeshItem(preSelectedPosition, preSelectedPosition);
                        } else {
                            refresh = true;
                        }
                        preSelectedPosition = position;
                    }
                });
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        refeshItem(preSelectedPosition,preSelectedPosition);
                        preSelectedPosition = -1;
                    }
                });
            }
        }
    }
}
