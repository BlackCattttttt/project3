package com.need.mymall;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DBQueries {

    public static String email,fullname,profile;

    private static ArrayList<CategoryDatabase> categoryDatabaseArrayList;
    private static ProductDatabase productDatabase;

    public static FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    public static ArrayList<String> cartList = new ArrayList<>();
    public static ArrayList<CartItemModel> cartItemModelArrayList = new ArrayList<>();

    public static int selectedAddress = 0;
    public static ArrayList<AddressesModel> addressesModelArrayList = new ArrayList<>();

    public static ArrayList<MyOrderItemModel> myOrderItemModelArrayList = new ArrayList<>();

    public static ArrayList<NotificationModel> notificationModelArrayList = new ArrayList<>();
    private static ListenerRegistration registration;

    public static void loadCartList (final Context context, final Dialog dialog, final boolean loadProductData) {
        cartList.clear();
        cartItemModelArrayList.clear();

        firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_CART")
            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if ((long) task.getResult().get("list_size")==0) {
                        dialog.dismiss();
                    } else {
                        for (long x = 0;x<(long) task.getResult().get("list_size");x++) {
                            cartList.add(task.getResult().get("product_ID_"+x).toString());

                            if (DBQueries.cartList.contains(ProductDetailActivity.productID)) {
                                ProductDetailActivity.ALREADY_ADDED_TO_CART = true;
                            } else {
                                ProductDetailActivity.ALREADY_ADDED_TO_CART = false;
                            }

                            if (loadProductData) {
                                //cartItemModelArrayList.clear();
                                final String productId = task.getResult().get("product_ID_"+x).toString();
                                final long quantity = (long) task.getResult().get("quantity_"+x);
                                final String productDes = task.getResult().get("description_"+x).toString();
                                final String categoryId = task.getResult().get("category_ID_"+x).toString();

                                categoryDatabaseArrayList = new ArrayList<>();

                                firebaseFirestore.collection("Category").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            int index = 0;
                                            if (cartList.size() >= 2) {
                                                index = cartList.size() - 2;
                                            }
                                            EbayAPI ebayAPI = new EbayAPI();
                                            productDatabase = new ProductDatabase();
                                            productDatabase = ebayAPI.getItem(productId);

                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                CategoryDatabase categoryDatabase = document.toObject(CategoryDatabase.class);
                                                categoryDatabaseArrayList.add(categoryDatabase);
                                            }
                                            for (int j=0;j<categoryDatabaseArrayList.size();j++) {
                                                if (categoryId.equals(categoryDatabaseArrayList.get(j).getId())) {
                                                    productDatabase.setCategoryDatabase(categoryDatabaseArrayList.get(j));
                                                    break;
                                                }
                                            }
                                            productDatabase.caculatePrice(1);
                                            cartItemModelArrayList.add(0,new CartItemModel(CartItemModel.CART_ITEM,productId,productDatabase.getImagesUrl().get(0),productDatabase.getTitle(),productDatabase.getTotalPrice(),productDatabase.getTransportFeePrice(),(int)quantity,productDes,categoryId));

                                            if (cartItemModelArrayList.size()==1) {
                                                cartItemModelArrayList.add(new CartItemModel(CartItemModel.TOTAL_AMOUNT));
                                            }

                                            //Toast.makeText(context,cartItemModelArrayList.size()+"",Toast.LENGTH_LONG).show();
                                            if (cartList.size() == 0) {
                                                cartItemModelArrayList.clear();
                                            }
                                            MyCartFragment.cartAdapter.notifyDataSetChanged();
                                        } else {
                                            String error = task.getException().getMessage();
                                            Toast.makeText(context,error,Toast.LENGTH_LONG).show();
                                        }
                                        dialog.dismiss();
                                    }
                                });
                            }
                        }
                    }
                } else {
                    String error = task.getException().getMessage();
                    Toast.makeText(context,error,Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public static void removeFromCart (final int index, final Context context) {
        final String removeProductId = cartItemModelArrayList.get(index).getProductId();

        for (int x = 0;x < cartList.size();x++) {
            if (cartList.get(x).equals(removeProductId)) {
                cartList.remove(x);
                break;
            }
        }

        Map<String,Object> updatecartList = new HashMap<>();

        for (int x = 0;x<DBQueries.cartList.size();x++) {
            for (int y = 0;y<DBQueries.cartItemModelArrayList.size();y++) {
                if (DBQueries.cartItemModelArrayList.get(y).getType() == CartItemModel.CART_ITEM && DBQueries.cartList.get(x).equals(DBQueries.cartItemModelArrayList.get(y).getProductId())) {
                    updatecartList.put("quantity_" + String.valueOf(x), (long) DBQueries.cartItemModelArrayList.get(y).getProductQuantity());
                    updatecartList.put("product_ID_"+x,cartList.get(x));
                    updatecartList.put("description_"+x,DBQueries.cartItemModelArrayList.get(y).getProductDes());
                }
            }
        }

        updatecartList.put("list_size",(long) cartList.size());
        firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_CART")
            .set(updatecartList).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    if (cartItemModelArrayList.size()!=0) {
                        for (int x = 0;x<cartItemModelArrayList.size();x++) {
                            if (cartItemModelArrayList.get(x).getType() == CartItemModel.CART_ITEM && cartItemModelArrayList.get(x).getProductId().equals(removeProductId)) {
                                cartItemModelArrayList.remove(x);
                                break;
                            }
                        }
                        MyCartFragment.cartAdapter.notifyDataSetChanged();
                    }
                    if (cartList.size() == 0) {
                        cartItemModelArrayList.clear();
                    }
                    Toast.makeText(context,"Xóa thành công",Toast.LENGTH_LONG).show();
                } else {
                    cartList.add(index,removeProductId);
                    String error = task.getException().getMessage();
                    Toast.makeText(context,error,Toast.LENGTH_LONG).show();
                }
                ProductDetailActivity.running_cart_query = false;
            }
        });
    }

    public static void loadAddresses(final Context context, final  Dialog dialog, final boolean gotoDelivery) {

        addressesModelArrayList.clear();

        firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_ADDRESSES")
            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    Intent deliveryIntent = null;
                    if ((long)task.getResult().get("list_size") == 0) {
                        deliveryIntent = new Intent(context,AddAddressActivity.class);
                        deliveryIntent.putExtra("INTENT","deliveryIntent");
                    } else {
                        for (long x = 1;x <= (long) task.getResult().get("list_size");x++) {
                            boolean selected;
                            if (task.getResult().get("selected_"+x).toString().equals("true")) {
                                selected = true;
                            } else {
                                selected = false;
                            }
                            AddressesModel addressesModel = new AddressesModel(task.getResult().get("fullname_"+x).toString()
                                    ,task.getResult().get("phonenumber_"+x).toString()
                                    ,task.getResult().get("address_full_"+x).toString()
                                    ,selected);
                            addressesModel.setProvinces(task.getResult().get("province_"+x).toString());
                            addressesModel.setDistric(task.getResult().get("distric_"+x).toString());
                            addressesModel.setDescription(task.getResult().get("description_"+x).toString());
                            addressesModel.setAddress(task.getResult().get("address_"+x).toString());
                            addressesModelArrayList.add(addressesModel);

                            if (selected) {
                                selectedAddress = (int)(x -1);
                            }
                        }
                        if (selectedAddress == 0) {
                            addressesModelArrayList.get(selectedAddress).setSelected(true);
                        }
                        if (gotoDelivery) {
                            deliveryIntent = new Intent(context, DeliveryActivity.class);
                        }
                    }
                    if (gotoDelivery) {
                        context.startActivity(deliveryIntent);
                    }
                } else {
                    String error = task.getException().getMessage();
                    Toast.makeText(context,error,Toast.LENGTH_LONG).show();
                }
                dialog.dismiss();
            }
        });
    }

    public static void loadOrders (final Context context, final Dialog loadingDialog,@Nullable final MyOrderAdapter myOrderAdapter) {
        myOrderItemModelArrayList.clear();

        firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_ORDER").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (final QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                firebaseFirestore.collection("ORDERS").document(documentSnapshot.getString("order_id")).get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @RequiresApi(api = Build.VERSION_CODES.O)
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    DocumentSnapshot snapshot = task.getResult();

                                                    String orderStatus = snapshot.getString("order_status");
                                                    long totalItem = (long) snapshot.get("total_item");
                                                    String totalAmount =String.valueOf( snapshot.get("total_amount"));
                                                    String deposit = String.valueOf(snapshot.get("deposit"));
                                                    String orderId = snapshot.getString("order_ID");

                                                    long totalProduct = snapshot.getLong("list_size");

                                                    ArrayList<ProductOrder> productOrders = new ArrayList<>();
                                                    for (int x = 0; x < totalProduct;x++) {
                                                        String productId = snapshot.getString("product_ID_"+String.valueOf(x));
                                                        EbayAPI ebayAPI = new EbayAPI();
                                                        ProductDatabase product = new ProductDatabase();
                                                        product = ebayAPI.getItem(productId);
                                                        String productPrice = String.valueOf(snapshot.get("product_price_"+String.valueOf(x)));
                                                        long quantity = (long) snapshot.get("product_quantity_"+String.valueOf(x));

                                                        productOrders.add(new ProductOrder(product.getImagesUrl().get(0),product.getTitle(),(int)quantity,productPrice));
                                                    }

                                                    Date orderedDate = snapshot.getDate("ordered_date");
                                                    Date payedDate = snapshot.getDate("payed_date");
                                                    Date packedDate = snapshot.getDate("packed_date");
                                                    Date shipedUsaDate = snapshot.getDate("shiped_usa_date");
                                                    Date shipedVnDate = snapshot.getDate("shiped_vn_date");
                                                    Date deliveriedDate = snapshot.getDate("delivery_date");
                                                    Date cancelledDate = snapshot.getDate("cancelled_date");

                                                    String fullname = snapshot.getString("fullname");
                                                    String phoneNum = snapshot.getString("phone_number");
                                                    String address = snapshot.getString("address");

                                                    String userId = snapshot.getString("user_ID");

                                                    MyOrderItemModel myOrderItemModel = new MyOrderItemModel(orderStatus,(int)totalItem,totalAmount,deposit,orderId,(int)totalProduct,productOrders,
                                                            orderedDate,payedDate,packedDate,shipedUsaDate,shipedVnDate,deliveriedDate,cancelledDate,userId);
                                                    myOrderItemModel.setDelivery(fullname,phoneNum,address);
                                                    myOrderItemModelArrayList.add(myOrderItemModel);

                                                    if (myOrderAdapter != null) {
                                                        myOrderAdapter.notifyDataSetChanged();
                                                    }
                                                } else {
                                                    String error = task.getException().getMessage();
                                                    Toast.makeText(context,error,Toast.LENGTH_LONG).show();
                                                }
                                                loadingDialog.dismiss();
                                            }
                                        });
                            }
                            loadingDialog.dismiss();
                        } else {
                            loadingDialog.dismiss();
                            String error = task.getException().getMessage();
                            Toast.makeText(context,error,Toast.LENGTH_LONG).show();
                        }
                        //loadingDialog.dismiss();
                    }
                });
    }

    public static void checkNotification (boolean remove) {

        if (remove) {
            registration.remove();
        } else {
            registration = firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_NOTIFICATION")
                    .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                            if (value != null && value.exists()) {
                                notificationModelArrayList.clear();
                                for (int x = 0;x<(long) value.get("list_size");x++) {
                                    notificationModelArrayList.add(new NotificationModel(value.get("title_"+x).toString(),value.get("body_"+x).toString(),value.getDate("date_"+x),value.getBoolean("readed_"+x)));
                                }
                                if (NotificationActivity.notificationAdater != null) {
                                    NotificationActivity.notificationAdater.notifyDataSetChanged();
                                }
                            }
                        }
                    });
        }
    }
    public static void clearData () {
        cartItemModelArrayList.clear();
        addressesModelArrayList.clear();
        cartList.clear();
        myOrderItemModelArrayList.clear();
    }
}
