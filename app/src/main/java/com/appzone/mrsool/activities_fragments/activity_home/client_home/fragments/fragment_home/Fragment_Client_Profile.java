package com.appzone.mrsool.activities_fragments.activity_home.client_home.fragments.fragment_home;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.appzone.mrsool.R;
import com.appzone.mrsool.activities_fragments.activity_home.client_home.activity.ClientHomeActivity;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;

import java.util.Locale;

import io.paperdb.Paper;

public class Fragment_Client_Profile extends Fragment{

    private ImageView image_setting,image,arrow;
    private TextView tv_name,tv_balance,tv_order_count,tv_feedback;
    private SimpleRatingBar rateBar;
    private ConstraintLayout cons_logout;
    private String current_language;
    private ClientHomeActivity activity;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_client_profile,container,false);
        initView(view);
        return view;
    }

    public static Fragment_Client_Profile newInstance()
    {
        return new Fragment_Client_Profile();
    }
    private void initView(View view) {

        activity = (ClientHomeActivity) getActivity();
        Paper.init(activity);
        current_language = Paper.book().read("lang", Locale.getDefault().getLanguage());

        arrow = view.findViewById(R.id.arrow);
        if (current_language.equals("ar"))
        {
            arrow.setImageResource(R.drawable.ic_left_arrow);

        }else
            {
                arrow.setImageResource(R.drawable.ic_right_arrow);


            }

        image_setting = view.findViewById(R.id.image_setting);
        image = view.findViewById(R.id.image);
        tv_name = view.findViewById(R.id.tv_name);
        tv_balance = view.findViewById(R.id.tv_balance);
        tv_order_count = view.findViewById(R.id.tv_order_count);
        tv_feedback = view.findViewById(R.id.tv_feedback);
        rateBar = view.findViewById(R.id.rateBar);
        cons_logout = view.findViewById(R.id.cons_logout);

        image_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.DisplayFragmentSettings();
            }
        });



    }
}
