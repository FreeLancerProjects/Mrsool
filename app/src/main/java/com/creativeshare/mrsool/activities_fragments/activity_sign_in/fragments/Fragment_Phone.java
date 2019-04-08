package com.creativeshare.mrsool.activities_fragments.activity_sign_in.fragments;

import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.creativeshare.mrsool.R;
import com.creativeshare.mrsool.activities_fragments.activity_sign_in.activity.SignInActivity;
import com.creativeshare.mrsool.share.Common;
import com.mukesh.countrypicker.Country;
import com.mukesh.countrypicker.CountryPicker;
import com.mukesh.countrypicker.listeners.OnCountryPickerListener;

import java.util.Locale;

import io.paperdb.Paper;

public class Fragment_Phone extends Fragment implements OnCountryPickerListener {

    private ImageView arrow;
    private LinearLayout ll_country;
    private TextView tv_country,tv_code,tv_note;
    private EditText edt_phone;
    private FloatingActionButton fab;
    private SignInActivity activity;
    private CountryPicker picker;
    private String current_language="";
    private String code = "";
    private String country_code="";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_phone,container,false);
        initView(view);
        return view;
    }

    public static Fragment_Phone newInstance()
    {
        return new Fragment_Phone();
    }
    private void initView(View view) {
        activity = (SignInActivity) getActivity();
        Paper.init(activity);
        current_language = Paper.book().read("lang", Locale.getDefault().getLanguage());
        arrow = view.findViewById(R.id.arrow);
        ll_country = view.findViewById(R.id.ll_country);
        tv_country = view.findViewById(R.id.tv_country);
        tv_code = view.findViewById(R.id.tv_code);
        tv_note = view.findViewById(R.id.tv_note);

        edt_phone = view.findViewById(R.id.edt_phone);
        fab = view.findViewById(R.id.fab);


        if (current_language.equals("ar"))
        {
            arrow.setImageResource(R.drawable.ic_right_arrow);
            arrow.setColorFilter(ContextCompat.getColor(activity,R.color.white), PorterDuff.Mode.SRC_IN);
        }else
            {
                arrow.setImageResource(R.drawable.ic_left_arrow);
                arrow.setColorFilter(ContextCompat.getColor(activity,R.color.white), PorterDuff.Mode.SRC_IN);

            }
        tv_note.setText(getString(R.string.never_share_phone_number)+"\n"+getString(R.string.your_privacy_guaranteed));

        CreateCountryDialog();

        ll_country.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                picker.showDialog(activity);
            }


        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckData();
            }
        });

    }


    private void CheckData() {
        String phone_regex = "^[+]?[0-9]{6,}$";

        String phone = edt_phone.getText().toString().trim();

        if (!TextUtils.isEmpty(phone) && phone.matches(phone_regex)) {
            edt_phone.setError(null);
            Common.CloseKeyBoard(activity, edt_phone);
            String m_phone = code.replace("+","00")+phone;
            activity.signIn(m_phone,country_code);
        } else {
            if (TextUtils.isEmpty(phone)) {
                edt_phone.setError(getString(R.string.field_req));
            } else if (!phone.matches(phone_regex)) {
                edt_phone.setError(getString(R.string.inv_phone));
            }else
                {
                    edt_phone.setError(null);
                }
        }
    }


    private void CreateCountryDialog() {
        CountryPicker.Builder builder = new CountryPicker.Builder()
                .canSearch(true)
                .with(activity)
                .listener(this);
        picker = builder.build();

        TelephonyManager telephonyManager = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);


        if (picker.getCountryFromSIM() != null) {
            updateUi(picker.getCountryFromSIM());

        } else if (telephonyManager != null && picker.getCountryByISO(telephonyManager.getNetworkCountryIso()) != null)
        {
            updateUi(picker.getCountryByISO(telephonyManager.getNetworkCountryIso()));


        } else if (picker.getCountryByLocale(Locale.getDefault()) != null) {
            updateUi(picker.getCountryByLocale(Locale.getDefault()));

        }else
        {
            tv_code.setText("+966");
            tv_country.setText("Saudi Arabia");
            this.country_code = "sa";

        }


    }


    @Override
    public void onSelectCountry(Country country) {
        updateUi(country);
    }

    private void updateUi(Country country) {
        country_code = country.getCode();

        tv_country.setText(country.getName());
        tv_code.setText(country.getDialCode());
        code = country.getDialCode();




    }
}
