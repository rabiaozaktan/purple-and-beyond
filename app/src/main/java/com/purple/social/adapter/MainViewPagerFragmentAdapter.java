package com.purple.social.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;


import com.purple.social.fragment.NewsFragment;
import com.purple.social.fragment.WeatherForecastFragment;

import java.util.ArrayList;
import java.util.List;

public class MainViewPagerFragmentAdapter extends FragmentStatePagerAdapter {

    private List<String> titleList;

    public MainViewPagerFragmentAdapter(@NonNull FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

        titleList = new ArrayList<>();
        titleList.add("News");
        titleList.add("Weather forecast");
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new NewsFragment();
            case 1:
                return new WeatherForecastFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }


    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titleList.get(position);
    }

}
