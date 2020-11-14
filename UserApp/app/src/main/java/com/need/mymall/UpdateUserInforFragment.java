package com.need.mymall;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PathPermission;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.internal.$Gson$Preconditions;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class UpdateUserInforFragment extends Fragment {

    public UpdateUserInforFragment() {
        // Required empty public constructor
    }

    private CircleImageView circleImageView;
    private Button changePhotoBtn,removePhotoBtn,updateBtn;
    private EditText nameFeild,emailFeild;

    private Dialog loadingDialog;

    private String name,email,photo;

    private Uri imageUri;

    private boolean updatePhoto = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_update_user_infor, container, false);

        loadingDialog = new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.getWindow().setBackgroundDrawable(getActivity().getDrawable(R.drawable.slider_background));

        circleImageView = view.findViewById(R.id.update_user_profile_image);
        changePhotoBtn = view.findViewById(R.id.change_photo_btn);
        removePhotoBtn = view.findViewById(R.id.remove_photo_btn);
        nameFeild = view.findViewById(R.id.update_user_name);
        emailFeild = view.findViewById(R.id.update_user_email);
        updateBtn = view.findViewById(R.id.update_user_btn);

        name = getArguments().getString("Name");
        email = getArguments().getString("Email");
        photo = getArguments().getString("Photo");

        Glide.with(getContext()).load(photo).apply(new RequestOptions().placeholder(R.drawable.zhangtian4)).into(circleImageView);
        nameFeild.setText(name);
        emailFeild.setText(email);

        changePhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (getContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        Intent galeryIntent = new Intent(Intent.ACTION_PICK);
                        galeryIntent.setType("image/*");
                        startActivityForResult(galeryIntent,1);
                    } else {
                        getActivity().requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},2);
                    }
                } else {
                    Intent galeryIntent = new Intent(Intent.ACTION_PICK);
                    galeryIntent.setType("image/*");
                    startActivityForResult(galeryIntent,1);
                }
            }
        });

        removePhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageUri = null;
                updatePhoto = true;
                Glide.with(getContext()).load(R.drawable.profile_placeholder).apply(new RequestOptions().placeholder(R.drawable.zhangtian4)).into(circleImageView);
            }
        });

        nameFeild.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkInput();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog.show();
                if (updatePhoto) {
                    final StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("user/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + ".jpg");
                    if (imageUri == null) {
                        storageReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    DBQueries.profile = "";

                                    Map<String,Object> updateData = new HashMap<>();
                                    updateData.put("fullname",nameFeild.getText().toString());
                                    updateData.put("profile","");

                                    updateUser(updateData);
                                } else {
                                    loadingDialog.dismiss();
                                    String error = task.getException().getMessage();
                                    Toast.makeText(getContext(),error,Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    } else {
                        Glide.with(getContext()).asBitmap().load(imageUri).circleCrop().into(new ImageViewTarget<Bitmap>(circleImageView) {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {

                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                resource.compress(Bitmap.CompressFormat.JPEG,100,baos);
                                byte[] data = baos.toByteArray();

                                final UploadTask uploadTask = storageReference.putBytes(data);
                                uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Uri> task) {
                                                    if (task.isSuccessful()) {
                                                        imageUri = task.getResult();
                                                        DBQueries.profile = task.getResult().toString();
                                                        Glide.with(getContext()).load(DBQueries.profile).apply(new RequestOptions().placeholder(R.drawable.zhangtian4)).into(circleImageView);

                                                        Map<String,Object> updateData = new HashMap<>();
                                                        updateData.put("fullname",nameFeild.getText().toString());
                                                        updateData.put("profile",DBQueries.profile);

                                                        updateUser(updateData);

                                                    } else {
                                                        loadingDialog.dismiss();
                                                        DBQueries.profile = "";
                                                        String error = task.getException().getMessage();
                                                        Toast.makeText(getContext(),error,Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            });
                                        } else {
                                            loadingDialog.dismiss();
                                            String error = task.getException().getMessage();
                                            Toast.makeText(getContext(),error,Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                                return;
                            }

                            @Override
                            protected void setResource(@Nullable Bitmap resource) {
                                circleImageView.setImageResource(R.drawable.zhangtian4);
                            }
                        });
                    }
                }
                else {
                    Map<String,Object> updateData = new HashMap<>();
                    updateData.put("fullname",nameFeild.getText().toString());

                    updateUser(updateData);
                }
            }
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    imageUri = data.getData();
                    updatePhoto = true;
                    Glide.with(getContext()).load(imageUri).apply(new RequestOptions().placeholder(R.drawable.zhangtian4)).into(circleImageView);
                } else {
                    Toast.makeText(getContext(),"Không tìm thấy ảnh",Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 2) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent galeryIntent = new Intent(Intent.ACTION_PICK);
                galeryIntent.setType("image/*");
                startActivityForResult(galeryIntent,1);
            } else {
                Toast.makeText(getContext(),"Quyền truy cập bị từ chối",Toast.LENGTH_LONG).show();
            }
        }
    }

    private void checkInput () {
        if (!TextUtils.isEmpty(nameFeild.getText())) {
            updateBtn.setEnabled(true);
            updateBtn.setTextColor(Color.rgb(255,255,255));
        } else {
            updateBtn.setEnabled(false);
            updateBtn.setTextColor(Color.argb(50,255,255,255));
        }
    }

    private void updateUser (final Map<String,Object> updateData) {
        FirebaseFirestore.getInstance().collection("USERS").document(FirebaseAuth.getInstance().getUid())
                .update(updateData).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    DBQueries.fullname = nameFeild.getText().toString().trim();
                    getActivity().finish();
                    Toast.makeText(getContext(),"Thay đổi thông tin thành công",Toast.LENGTH_LONG).show();
                } else {
                    String error = task.getException().getMessage();
                    Toast.makeText(getContext(),error,Toast.LENGTH_LONG).show();
                }
                loadingDialog.dismiss();
            }
        });
    }
}