package com.creativeshare.mrsool.activities_fragments.activity_home.client_home.fragments.fragment_store_details;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.creativeshare.mrsool.R;
import com.creativeshare.mrsool.models.PlaceModel;

public class Fragment_Pending_Orders extends Fragment {

    private static final String TAG = "Data";

    private PlaceModel placeModel;

    public static Fragment_Pending_Orders newInstance(PlaceModel placeModel)
    {
        Fragment_Pending_Orders fragment_pending_orders = new Fragment_Pending_Orders();
        Bundle bundle = new Bundle();
        bundle.putSerializable(TAG,placeModel);

        fragment_pending_orders.setArguments(bundle);
        return fragment_pending_orders;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pending_order,container,false);
        initView(view);
        return view;
    }

    private void initView(View view) {

        Bundle bundle = getArguments();
        if (bundle!=null)
        {
            placeModel = (PlaceModel) bundle.getSerializable(TAG);
            updateUI(placeModel);

        }
    }

    private void updateUI(PlaceModel placeModel) {

    }
}
