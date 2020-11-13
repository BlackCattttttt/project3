package com.need.mymall;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ProductQuoteFragment extends Fragment {

    public static String orginalPrice;
    public static String transportPrice;
    public static String surchagePrice;
    public static int quantity;
    public static String totalPrice;

    private TextView textViewOriginalPrice;
    private TextView textViewTransportFeePrice;
    private TextView textViewSurchagePrice;
    private TextView textViewQuantity;
    private TextView textViewTotalPrice;

    public ProductQuoteFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_quote, container, false);

        textViewOriginalPrice = view.findViewById(R.id.tv_product_quote_original_price);
        textViewTransportFeePrice = view.findViewById(R.id.tv_product_quote_transportfee_price);
        textViewSurchagePrice= view.findViewById(R.id.tv_product_quote_surchage_price);
        textViewQuantity = view.findViewById(R.id.tv_product_quote_count);
        textViewTotalPrice = view.findViewById(R.id.tv_product_quote_total_price);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        textViewOriginalPrice.setText(exchangeUSD(orginalPrice) + "đ");
        textViewTransportFeePrice.setText(exchangeUSD(transportPrice) + "đ");
        textViewSurchagePrice.setText(exchangeUSD(surchagePrice) + "đ");
        textViewQuantity.setText(quantity+"");
        textViewTotalPrice.setText(exchangeUSD(totalPrice) + "đ");
    }

    public String exchangeUSD (String price) {
        double amount = Double.parseDouble(price) * Double.parseDouble(MainActivity.exchange);
        return String.format("%,.2f",amount);
    }
}