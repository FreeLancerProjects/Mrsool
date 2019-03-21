package com.appzone.mrsool.activities_fragments.activity_home.client_home.fragments.fragment_home;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.appzone.mrsool.R;
import com.appzone.mrsool.activities_fragments.activity_home.client_home.activity.ClientHomeActivity;
import com.appzone.mrsool.activities_fragments.terms_conditions.TermsConditionsActivity;

import java.util.Locale;

import io.paperdb.Paper;

public class Fragment_Settings extends Fragment{

    private ClientHomeActivity activity;
    private ConstraintLayout cons_back,cons_complains,cons_edit_profile,cons_language,cons_terms,cons_privacy,cons_rate,cons_about;
    private ImageView arrow_back,arrow1,arrow2,arrow3,arrow4,arrow5,arrow6,arrow7;
    private String current_language;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings,container,false);
        initView(view);
        return view;
    }

    public static Fragment_Settings newInstance()
    {
        return new Fragment_Settings();
    }
    private void initView(View view) {

        activity = (ClientHomeActivity) getActivity();
        Paper.init(activity);
        current_language = Paper.book().read("lang",Locale.getDefault().getLanguage());

        arrow_back = view.findViewById(R.id.arrow_back);
        arrow1 = view.findViewById(R.id.arrow1);
        arrow2 = view.findViewById(R.id.arrow2);
        arrow3 = view.findViewById(R.id.arrow3);
        arrow4 = view.findViewById(R.id.arrow4);
        arrow5 = view.findViewById(R.id.arrow5);
        arrow6 = view.findViewById(R.id.arrow6);
        arrow7 = view.findViewById(R.id.arrow7);

        if (current_language.equals("ar"))
        {
            arrow_back.setImageResource(R.drawable.ic_right_arrow);

            arrow1.setImageResource(R.drawable.ic_left_arrow);
            arrow2.setImageResource(R.drawable.ic_left_arrow);
            arrow3.setImageResource(R.drawable.ic_left_arrow);
            arrow4.setImageResource(R.drawable.ic_left_arrow);
            arrow5.setImageResource(R.drawable.ic_left_arrow);
            arrow6.setImageResource(R.drawable.ic_left_arrow);
            arrow7.setImageResource(R.drawable.ic_left_arrow);

        }else
            {
                arrow_back.setImageResource(R.drawable.ic_left_arrow);
                arrow1.setImageResource(R.drawable.ic_right_arrow);
                arrow2.setImageResource(R.drawable.ic_right_arrow);
                arrow3.setImageResource(R.drawable.ic_right_arrow);
                arrow4.setImageResource(R.drawable.ic_right_arrow);
                arrow5.setImageResource(R.drawable.ic_right_arrow);
                arrow6.setImageResource(R.drawable.ic_right_arrow);
                arrow7.setImageResource(R.drawable.ic_right_arrow);


            }

        cons_back = view.findViewById(R.id.cons_back);
        cons_complains = view.findViewById(R.id.cons_complains);
        cons_edit_profile = view.findViewById(R.id.cons_edit_profile);
        cons_language = view.findViewById(R.id.cons_language);
        cons_terms = view.findViewById(R.id.cons_terms);
        cons_privacy = view.findViewById(R.id.cons_privacy);
        cons_rate = view.findViewById(R.id.cons_rate);
        cons_about = view.findViewById(R.id.cons_about);

        cons_rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + activity.getPackageName())));
                } catch (android.content.ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + activity.getPackageName())));
                }
            }
        });

        cons_terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigateToTermsActivity(1);
            }
        });

        cons_privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigateToTermsActivity(2);
            }
        });

        cons_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigateToTermsActivity(3);
            }
        });

        cons_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.Back();
            }
        });


    }

    public void NavigateToTermsActivity(int type)
    {
        Intent intent = new Intent(activity, TermsConditionsActivity.class);
        intent.putExtra("type",type);
        startActivity(intent);

    }
}
