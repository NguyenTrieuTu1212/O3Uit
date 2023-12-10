package com.example.o3uit;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class ViewPageFragment extends FragmentPagerAdapter {
    private Bundle fragmentBundle;
    public ViewPageFragment(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
        fragmentBundle = new Bundle();
    }


    public void setFragmentBundle(Bundle data) {
        this.fragmentBundle = data;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:{
                return new MapFragment();
            }
            case 1:{
                return new WeatherFragment();
            }
            case 2:{
                return new ChartFragment();
            }
            default:

                return  new MapFragment();
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        String title="";
        switch (position){
            case 0:{
                title="MAP";
                break;
            }
            case 1:{
                title="WEATHER";
                break;
            }
            case 2:{
                title="CHART";
                break;
            }
        }
        return title;
    }
}
