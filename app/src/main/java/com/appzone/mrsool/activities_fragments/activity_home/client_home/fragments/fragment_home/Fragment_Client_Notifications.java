package com.appzone.mrsool.activities_fragments.activity_home.client_home.fragments.fragment_home;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appzone.mrsool.R;

public class Fragment_Client_Notifications extends Fragment{


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_client_store,container,false);
        initView(view);
        return view;
    }

    public static Fragment_Client_Notifications newInstance()
    {
        return new Fragment_Client_Notifications();
    }
    private void initView(View view) {

    }
}
