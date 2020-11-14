package com.need.mymall;

import android.hardware.usb.UsbConfiguration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.ArrayList;

public class SliderAdapter extends PagerAdapter {

    private ArrayList<SliderModer> sliderModerArrayList;

    public SliderAdapter(ArrayList<SliderModer> sliderModerArrayList) {
        this.sliderModerArrayList = sliderModerArrayList;
    }

    @Override
    public int getCount() {
        return sliderModerArrayList.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.slide_layout,container,false);
        ImageView banner = view.findViewById(R.id.banner_slider);
        banner.setImageResource(sliderModerArrayList.get(position).getBanner());
        container.addView(view,0);
        return view;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
