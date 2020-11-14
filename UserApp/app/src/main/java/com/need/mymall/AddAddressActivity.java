package com.need.mymall;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.SyncFailedException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.PropertyPermission;

public class AddAddressActivity extends AppCompatActivity {

    private ArrayList<String> provinces;
    private ArrayList<String> districts;
    private ArrayList<ProvinceModel> provinceModelArrayList;

    private TextView editTextProvinces;
    private TextView editTextDistricts;
    private CheckBox selectedAddress;

    private EditText editTextName;
    private EditText editTextPhone;
    private EditText editTextAddress;
    private EditText editTextDescription;

    Button saveBtn;

    private boolean updateAddress = false;
    private AddressesModel addressesModel;
    private int position;

    private Dialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Thêm địa chỉ");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_background));

        editTextName = findViewById(R.id.add_address_fullname);
        editTextPhone = findViewById(R.id.add_address_phonenumber);
        editTextDescription = findViewById(R.id.add_address_des);
        editTextAddress = findViewById(R.id.add_address_address);
        selectedAddress = findViewById(R.id.add_address_selected);

        editTextProvinces = findViewById(R.id.add_address_province);
        editTextDistricts = findViewById(R.id.add_address_district);
        saveBtn = findViewById(R.id.save_address_btn);

        provinceModelArrayList = new ArrayList<ProvinceModel>();
        provinces = new ArrayList<>();
        districts = new ArrayList<>();
        readJson();

        final AlertDialog.Builder builder = new AlertDialog.Builder(AddAddressActivity.this);
        builder.setTitle("Chọn quận/ huyện");

        final AlertDialog.Builder builderSingle = new AlertDialog.Builder(AddAddressActivity.this);
        builderSingle.setTitle("Chọn tỉnh/ thành phố");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(AddAddressActivity.this, android.R.layout.select_dialog_singlechoice,provinces);

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int position) {
                String strName = arrayAdapter.getItem(position);
                editTextProvinces.setText(strName);
                for (int i = 0;i<provinceModelArrayList.size();i++) {
                    if (strName.equals(provinceModelArrayList.get(i).getName())) {
                        districts = provinceModelArrayList.get(i).getDistricts();
                        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(AddAddressActivity.this, android.R.layout.select_dialog_singlechoice,districts);

                        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int position) {
                                String strName = adapter.getItem(position);
                                editTextDistricts.setText(strName);
                            }
                        });
                        break;
                    }
                }
            }
        });

        editTextProvinces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builderSingle.show();
            }
        });
        editTextDistricts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.show();
            }
        });

        if (getIntent().getStringExtra("INTENT").equals("edit_address")) {
            updateAddress = true;
            position = getIntent().getIntExtra("index",-1);
            addressesModel = DBQueries.addressesModelArrayList.get(position);

            editTextName.setText(addressesModel.getFullname());
            editTextPhone.setText(addressesModel.getPhonenumber());
            editTextAddress.setText(addressesModel.getAddress());
            editTextProvinces.setText(addressesModel.getProvinces());
            editTextDistricts.setText(addressesModel.getDistric());
            editTextDescription.setText(addressesModel.getDescription());
            selectedAddress.setChecked(addressesModel.getSelected());

        } else {
            position = (int) DBQueries.addressesModelArrayList.size();
        }
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!updateAddress) {
                    addAddress();
                } else {
                    editAddress();
                }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void readJson () {
        try {
            String jsonText = readText(this, R.raw.province);
            JSONObject jsonRoot = new JSONObject(jsonText);

            for (int i = 1;i<=63 ;i++) {
                JSONObject object = jsonRoot.getJSONObject(i+"");
                String name = object.getString("name");
                provinces.add(name);
                JSONArray arrayDistrict = object.getJSONArray("districts");
                ArrayList<String> districts = new ArrayList<>();
                for (int j=0;j<arrayDistrict.length();j++) {
                    districts.add(arrayDistrict.getString(j));
                }
                provinceModelArrayList.add(new ProvinceModel(i,name,districts));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private String readText(Context context, int resId) throws IOException {
        InputStream is = context.getResources().openRawResource(resId);
        BufferedReader br= new BufferedReader(new InputStreamReader(is));
        StringBuilder sb= new StringBuilder();
        String s= null;
        while((  s = br.readLine())!=null) {
            sb.append(s);
            sb.append("\n");
        }
        return sb.toString();
    }

    public void addAddress () {
        if (!TextUtils.isEmpty(editTextName.getText())) {
            if (!TextUtils.isEmpty(editTextPhone.getText()) && editTextPhone.getText().length() == 10) {
                if (!TextUtils.isEmpty(editTextAddress.getText())) {
                    if (!editTextProvinces.getText().equals("Chọn tỉnh / thành ...")) {
                        if (!editTextDistricts.getText().equals("Chọn quận /huyện ...")) {

                            loadingDialog.show();
                            if (TextUtils.isEmpty(editTextDescription.getText())) {
                                editTextDescription.setText("");
                            } else {
                                editTextDescription.setText(editTextDescription.getText().toString() + ", ");
                            }
                            final String fullAddress = editTextDescription.getText().toString() + editTextAddress.getText().toString()+", "+editTextDistricts.getText().toString()+", "+editTextProvinces.getText().toString();

                            final Map<String,Object> addAddress = new HashMap<>();
                            addAddress.put("list_size", (long) DBQueries.addressesModelArrayList.size() + 1);
                            addAddress.put("fullname_" +String.valueOf(position+1),editTextName.getText().toString());
                            addAddress.put("phonenumber_" +String.valueOf(position+1),editTextPhone.getText().toString());
                            addAddress.put("address_full_" +String.valueOf(position+1),fullAddress);
                            addAddress.put("address_" +String.valueOf(position+1),editTextAddress.getText().toString());
                            addAddress.put("province_" +String.valueOf(position+1),editTextProvinces.getText().toString());
                            addAddress.put("distric_" +String.valueOf(position+1),editTextDistricts.getText().toString());
                            addAddress.put("description_" +String.valueOf(position+1),editTextDescription.getText().toString());
                            if (selectedAddress.isChecked()) {
                                addAddress.put("selected_" +String.valueOf(position+1),String.valueOf(selectedAddress.isChecked()));
                                if (DBQueries.addressesModelArrayList.size() > 0) {
                                    addAddress.put("selected_" +(DBQueries.selectedAddress+1),false);
                                }
                            } else {
                                addAddress.put("selected_" +String.valueOf((long) DBQueries.addressesModelArrayList.size()+1),String.valueOf(selectedAddress.isChecked()));
                            }

                            FirebaseFirestore.getInstance().collection("USERS").document(FirebaseAuth.getInstance().getUid())
                                    .collection("USER_DATA").document("MY_ADDRESSES")
                                    .update(addAddress).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        AddressesModel addressesModel = new AddressesModel(editTextName.getText().toString()
                                                ,editTextPhone.getText().toString()
                                                ,fullAddress,selectedAddress.isChecked());
                                        addressesModel.setProvinces(editTextProvinces.getText().toString());
                                        addressesModel.setDistric(editTextDistricts.getText().toString());
                                        addressesModel.setDescription(editTextDescription.getText().toString());
                                        addressesModel.setAddress(editTextAddress.getText().toString());
                                        DBQueries.addressesModelArrayList.add(addressesModel);
                                        if (selectedAddress.isChecked() && DBQueries.addressesModelArrayList.size()>0) {
                                            DBQueries.addressesModelArrayList.get(DBQueries.selectedAddress).setSelected(false);
                                            DBQueries.selectedAddress = DBQueries.addressesModelArrayList.size()-1;
                                        }
                                        if (getIntent().getStringExtra("INTENT").equals("deliveryIntent")) {
                                            Intent delyveryIntent = new Intent(AddAddressActivity.this,DeliveryActivity.class);
                                            startActivity(delyveryIntent);
                                        } else {
                                            MyAddressActivity.refeshItem(DBQueries.selectedAddress,DBQueries.addressesModelArrayList.size()-1);
                                        }
                                        finish();
                                    } else {
                                        String error = task.getException().getMessage();
                                        Toast.makeText(AddAddressActivity.this,error,Toast.LENGTH_LONG).show();
                                    }
                                    loadingDialog.dismiss();
                                }
                            });
                        } else {
                            editTextDistricts.requestFocus();
                        }
                    } else {
                        editTextProvinces.requestFocus();
                    }
                } else {
                    editTextAddress.requestFocus();
                }
            } else {
                editTextPhone.requestFocus();
                Toast.makeText(AddAddressActivity.this,"Vui lòng nhập số điện thoại hợp lệ",Toast.LENGTH_LONG).show();
            }
        } else {
            editTextName.requestFocus();
        }
    }

    public void editAddress () {
        if (!TextUtils.isEmpty(editTextName.getText())) {
            if (!TextUtils.isEmpty(editTextPhone.getText()) && editTextPhone.getText().length() == 10) {
                if (!TextUtils.isEmpty(editTextAddress.getText())) {
                    if (!editTextProvinces.getText().equals("Chọn tỉnh / thành ...")) {
                        if (!editTextDistricts.getText().equals("Chọn quận /huyện ...")) {

                            loadingDialog.show();
                            String descrip;
                            if (TextUtils.isEmpty(editTextDescription.getText())) {
                                descrip = "";
                            } else {
                                descrip = editTextDescription.getText().toString() +", ";
                            }
                            final String fullAddress = descrip + editTextAddress.getText().toString()+", "+editTextDistricts.getText().toString()+", "+editTextProvinces.getText().toString();

                            final Map<String,Object> addAddress = new HashMap<>();
                            addAddress.put("fullname_" +String.valueOf(position+1),editTextName.getText().toString());
                            addAddress.put("phonenumber_" +String.valueOf(position+1),editTextPhone.getText().toString());
                            addAddress.put("address_full_" +String.valueOf(position+1),fullAddress);
                            addAddress.put("address_" +String.valueOf(position+1),editTextAddress.getText().toString());
                            addAddress.put("province_" +String.valueOf(position+1),editTextProvinces.getText().toString());
                            addAddress.put("distric_" +String.valueOf(position+1),editTextDistricts.getText().toString());
                            addAddress.put("description_" +String.valueOf(position+1),editTextDescription.getText().toString());
                            if (selectedAddress.isChecked()) {
                                addAddress.put("selected_" +String.valueOf(position+1),String.valueOf(selectedAddress.isChecked()));
                                if (DBQueries.addressesModelArrayList.size() > 0) {
                                    addAddress.put("selected_" +(DBQueries.selectedAddress+1),false);
                                }
                            } else {
                                addAddress.put("selected_" +String.valueOf((long) DBQueries.addressesModelArrayList.size()+1),String.valueOf(selectedAddress.isChecked()));
                            }

                            FirebaseFirestore.getInstance().collection("USERS").document(FirebaseAuth.getInstance().getUid())
                                    .collection("USER_DATA").document("MY_ADDRESSES")
                                    .update(addAddress).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        if (selectedAddress.isChecked() && DBQueries.addressesModelArrayList.size()>0) {
                                            DBQueries.addressesModelArrayList.get(DBQueries.selectedAddress).setSelected(false);
                                            DBQueries.selectedAddress = position;
                                        }
                                        AddressesModel addressesModel = new AddressesModel(editTextName.getText().toString()
                                                ,editTextPhone.getText().toString()
                                                ,fullAddress,selectedAddress.isChecked());
                                        addressesModel.setProvinces(editTextProvinces.getText().toString());
                                        addressesModel.setDistric(editTextDistricts.getText().toString());
                                        addressesModel.setDescription(editTextDescription.getText().toString());
                                        addressesModel.setAddress(editTextAddress.getText().toString());
                                        DBQueries.addressesModelArrayList.set(position,addressesModel);

                                        if (getIntent().getStringExtra("INTENT").equals("deliveryIntent")) {
                                            Intent delyveryIntent = new Intent(AddAddressActivity.this,DeliveryActivity.class);
                                            startActivity(delyveryIntent);
                                        } else {
                                            MyAddressActivity.refeshItem(DBQueries.selectedAddress,position);
                                        }
                                        finish();
                                    } else {
                                        String error = task.getException().getMessage();
                                        Toast.makeText(AddAddressActivity.this,error,Toast.LENGTH_LONG).show();
                                    }
                                    loadingDialog.dismiss();
                                }
                            });
                        } else {
                            editTextDistricts.requestFocus();
                        }
                    } else {
                        editTextProvinces.requestFocus();
                    }
                } else {
                    editTextAddress.requestFocus();
                }
            } else {
                editTextPhone.requestFocus();
                Toast.makeText(AddAddressActivity.this,"Vui lòng nhập số điện thoại hợp lệ",Toast.LENGTH_LONG).show();
            }
        } else {
            editTextName.requestFocus();
        }
    }
}