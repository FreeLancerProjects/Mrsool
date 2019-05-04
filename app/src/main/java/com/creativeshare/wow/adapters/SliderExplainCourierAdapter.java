package com.creativeshare.wow.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class SliderExplainCourierAdapter extends FragmentPagerAdapter {
    private List<Integer> imgList;
    private List<Fragment> fragmentList;
    private Context context;


    public SliderExplainCourierAdapter(FragmentManager fm,Context context) {
        super(fm);
        this.context = context;
        imgList = new ArrayList<>();
        fragmentList = new ArrayList<>();
    }

    public void AddFragment(List<Fragment> fragmentList)
    {
        this.fragmentList.addAll(fragmentList);
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }
}
