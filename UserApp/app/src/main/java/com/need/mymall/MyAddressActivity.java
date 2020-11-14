package com.need.mymall;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.need.mymall.DeliveryActivity.SELECT_ADDRESS;

public class MyAddressActivity extends AppCompatActivity {

    private int previousAddress;
    private TextView addNewAddressBtn;
    private TextView addressSaved;

    private RecyclerView myAddressRecycleView;
    private Button deliveryHere;
    private static AddressAdapter addressAdapter;

    private int mode;

    private Dialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_address);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Sổ địa chỉ");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_background));

        previousAddress = DBQueries.selectedAddress;

        addNewAddressBtn = findViewById(R.id.add_new_address_btn);
        addressSaved = findViewById(R.id.address_saved);

        deliveryHere = findViewById(R.id.delivery_here_btn);
        myAddressRecycleView = findViewById(R.id.my_address_recycleview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        myAddressRecycleView.setLayoutManager(layoutManager);

        mode = getIntent().getIntExtra("MODE",-1);
        if (mode == SELECT_ADDRESS)  {
            deliveryHere.setVisibility(View.VISIBLE);
        } else {
            deliveryHere.setVisibility(View.GONE);
        }

//        if (DBQueries.addressesModelArrayList.size()==0) {
//            loadingDialog.show();
//            DBQueries.loadAddresses(this,loadingDialog);
//        }
        deliveryHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DBQueries.selectedAddress != previousAddress) {
                    final int previousAddressIndex = previousAddress;
                    loadingDialog.show();
                    Map<String,Object> updateSelection = new HashMap<>();
                    updateSelection.put("selected_"+String.valueOf(previousAddress+1),false);
                    updateSelection.put("selected_"+String.valueOf(DBQueries.selectedAddress+1),true);

                    previousAddress = DBQueries.selectedAddress;

                    FirebaseFirestore.getInstance().collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_ADDRESSES")
                            .update(updateSelection).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                finish();
                            } else {
                                previousAddress = previousAddressIndex;
                                String error = task.getException().getMessage();
                                Toast.makeText(MyAddressActivity.this,error,Toast.LENGTH_LONG).show();
                            }
                            loadingDialog.dismiss();
                        }
                    });
                } else {
                    finish();
                }
            }
        });
        addressAdapter = new AddressAdapter(DBQueries.addressesModelArrayList,mode,loadingDialog);
        myAddressRecycleView.setAdapter(addressAdapter);
        ((SimpleItemAnimator)myAddressRecycleView.getItemAnimator()).setSupportsChangeAnimations(false);
        addressAdapter.notifyDataSetChanged();

        addNewAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addAddressIntent = new Intent(MyAddressActivity.this,AddAddressActivity.class);
                addAddressIntent.putExtra("INTENT","null");
                startActivity(addAddressIntent);
            }
        });
        loadingDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                addressSaved.setText(String.valueOf(DBQueries.addressesModelArrayList.size())+" địa chỉ");
            }
        });
        addressSaved.setText(String.valueOf(DBQueries.addressesModelArrayList.size())+" địa chỉ");
    }

    @Override
    protected void onStart() {
        super.onStart();
        addressSaved.setText(String.valueOf(DBQueries.addressesModelArrayList.size())+" địa chỉ");
    }

    public static void refeshItem (int deselect, int select) {
        addressAdapter.notifyItemChanged(deselect);
        addressAdapter.notifyItemChanged(select);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (mode == SELECT_ADDRESS) {
                if (DBQueries.selectedAddress != previousAddress) {
                    DBQueries.addressesModelArrayList.get(DBQueries.selectedAddress).setSelected(false);
                    DBQueries.addressesModelArrayList.get(previousAddress).setSelected(true);
                    DBQueries.selectedAddress = previousAddress;
                }
            }
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mode == SELECT_ADDRESS) {
            if (DBQueries.selectedAddress != previousAddress) {
                DBQueries.addressesModelArrayList.get(DBQueries.selectedAddress).setSelected(false);
                DBQueries.addressesModelArrayList.get(previousAddress).setSelected(true);
                DBQueries.selectedAddress = previousAddress;
            }
        }
        super.onBackPressed();
    }
}