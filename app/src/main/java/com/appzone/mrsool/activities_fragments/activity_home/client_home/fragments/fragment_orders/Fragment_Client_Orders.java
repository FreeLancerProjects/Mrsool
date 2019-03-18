package com.appzone.mrsool.activities_fragments.activity_home.client_home.fragments.fragment_orders;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appzone.mrsool.R;
import com.appzone.mrsool.adapters.ViewPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class Fragment_Client_Orders extends Fragment{

    private TabLayout tab;
    private ViewPager pager;
    private ViewPagerAdapter adapter;
    private List<Fragment> fragmentList;
    private List<String> titleList;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_client_orders,container,false);
        initView(view);
        return view;
    }

    public static Fragment_Client_Orders newInstance()
    {
        return new Fragment_Client_Orders();
    }

    private void initView(View view)
    {
        tab = view.findViewById(R.id.tab);
        pager = view.findViewById(R.id.pager);
        tab.setupWithViewPager(pager);
        pager.setOffscreenPageLimit(3);
        fragmentList = new ArrayList<>();
        titleList = new ArrayList<>();

        fragmentList.add(Fragment_Client_New_Orders.newInstance());
        fragmentList.add(Fragment_Client_Current_Orders.newInstance());
        fragmentList.add(Fragment_Client_Previous_Orders.newInstance());

        titleList.add(getString(R.string.new_order));
        titleList.add(getString(R.string.current));
        titleList.add(getString(R.string.previous));

        adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.AddFragments(fragmentList);
        adapter.AddTitles(titleList);

    }
    public void RefreshOrderFragments()
    {
        Fragment_Client_New_Orders fragment_client_new_orders = (Fragment_Client_New_Orders) fragmentList.get(0);
        Fragment_Client_Current_Orders  fragment_client_current_orders = (Fragment_Client_Current_Orders) fragmentList.get(1);
        Fragment_Client_Previous_Orders fragment_client_previous_orders = (Fragment_Client_Previous_Orders) fragmentList.get(2);
    }
}
