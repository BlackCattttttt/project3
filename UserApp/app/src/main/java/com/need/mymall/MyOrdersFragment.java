package com.need.mymall;

import android.app.Dialog;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class MyOrdersFragment extends Fragment {

    private TabLayout tabLayout;
    private FrameLayout frameLayout;

    public MyOrdersFragment() {
        // Required empty public constructor
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_orders, container, false);

        tabLayout = view.findViewById(R.id.tab_layout_status);
        frameLayout = view.findViewById(R.id.frame_layout);

        final OrderedFragment orderedFragment = new OrderedFragment();
        final PayedFragment payedFragment = new PayedFragment();
        final PackedFragment packedFragment = new PackedFragment();
        final ShipUsaFragment shipUsaFragment = new ShipUsaFragment();
        final ShipVnFragment shipVnFragment = new ShipVnFragment();
        final DeliveryFragment deliveryFragment = new DeliveryFragment();
        final CancelFragment cancelFragment = new CancelFragment();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    setFragment(orderedFragment);
                }
                if (tab.getPosition() == 1) {
                    setFragment(payedFragment);
                }
                if (tab.getPosition() == 2) {
                    setFragment(packedFragment);
                }
                if (tab.getPosition() == 3) {
                    setFragment(shipUsaFragment);
                }
                if (tab.getPosition() == 4) {
                    setFragment(shipVnFragment);
                }
                if (tab.getPosition() == 5) {
                    setFragment(deliveryFragment);
                }
                if (tab.getPosition() == 6) {
                    setFragment(cancelFragment);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        tabLayout.getTabAt(0).select();
        setFragment(orderedFragment);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }
    private void setFragment (Fragment fragment) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(frameLayout.getId(),fragment);
        fragmentTransaction.commit();
    }
}