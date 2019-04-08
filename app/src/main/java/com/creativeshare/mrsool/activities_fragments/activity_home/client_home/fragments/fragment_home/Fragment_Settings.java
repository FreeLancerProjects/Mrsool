package com.creativeshare.mrsool.activities_fragments.activity_home.client_home.fragments.fragment_home;

import android.app.AlertDialog;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;

import com.creativeshare.mrsool.R;
import com.creativeshare.mrsool.activities_fragments.activity_home.client_home.activity.ClientHomeActivity;
import com.creativeshare.mrsool.tags.Tags;

import java.util.Locale;

import io.paperdb.Paper;

public class Fragment_Settings extends Fragment{

    private ClientHomeActivity activity;
    private ConstraintLayout cons_back,cons_complains,cons_edit_profile,cons_language,cons_terms,cons_privacy,cons_rate,cons_about;
    private ImageView arrow_back,arrow1,arrow2,arrow3,arrow4,arrow5,arrow6,arrow7;
    private String current_language;
    private String [] language_array;

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

        language_array = new String[]{"English","العربية"};

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
                activity.NavigateToTermsActivity(Tags.APPTERMS);
            }
        });

        cons_privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.NavigateToTermsActivity(Tags.APPPRIVACY);
            }
        });

        cons_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.NavigateToTermsActivity(Tags.APPABOUT);
            }
        });

        cons_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.Back();
            }
        });

        cons_language.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateLanguageDialog();
            }
        });

        cons_edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.DisplayFragmentEditProfile();
            }
        });
    }


    private void CreateLanguageDialog()
    {
        final AlertDialog dialog = new AlertDialog.Builder(activity)
                .setCancelable(true)
                .create();

        View view  = LayoutInflater.from(activity).inflate(R.layout.dialog_language,null);
        Button btn_select = view.findViewById(R.id.btn_select);
        Button btn_cancel = view.findViewById(R.id.btn_cancel);

        final NumberPicker numberPicker = view.findViewById(R.id.numberPicker);

        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(language_array.length-1);
        numberPicker.setDisplayedValues(language_array);
        numberPicker.setWrapSelectorWheel(false);
        if (current_language.equals("ar"))
        {
            numberPicker.setValue(1);

        }else
            {
                numberPicker.setValue(0);

            }
        btn_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                int pos = numberPicker.getValue();
                if (pos == 0)
                {
                    activity.RefreshActivity("en");
                }else
                    {
                        activity.RefreshActivity("ar");

                    }

            }
        });




        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_congratulation_animation;
        dialog.setView(view);
        dialog.show();
    }
}
