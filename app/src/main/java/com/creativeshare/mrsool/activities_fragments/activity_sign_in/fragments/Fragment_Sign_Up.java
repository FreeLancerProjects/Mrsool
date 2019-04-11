package com.creativeshare.mrsool.activities_fragments.activity_sign_in.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.creativeshare.mrsool.R;
import com.creativeshare.mrsool.activities_fragments.activity_sign_in.activity.SignInActivity;
import com.creativeshare.mrsool.share.Common;
import com.creativeshare.mrsool.tags.Tags;
import com.squareup.picasso.Picasso;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.io.File;
import java.util.Calendar;
import java.util.Locale;

import co.ceryle.segmentedbutton.SegmentedButtonGroup;
import io.paperdb.Paper;

public class Fragment_Sign_Up extends Fragment implements DatePickerDialog.OnDateSetListener {

    private FloatingActionButton fab;
    private SignInActivity activity;
    private EditText edt_name,edt_email;
    private ImageView image_personal,image_icon1,image_back_photo;
    private LinearLayout ll_back,ll_birth_date;
    private TextView tv_birth_date;
    private final int IMG1=1;
    private Uri uri=null;
    private final String read_permission = Manifest.permission.READ_EXTERNAL_STORAGE;
    private SegmentedButtonGroup segmentGroup;
    private String current_language;
    private int gender = Tags.MALE;
    private long date_of_birth=0;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up,container,false);
        initView(view);
        return view;
    }

    public static Fragment_Sign_Up newInstance(){
        return new Fragment_Sign_Up();
    }
    private void initView(View view) {

        activity = (SignInActivity) getActivity();
        Paper.init(activity);
        current_language = Paper.book().read("lang",Locale.getDefault().getLanguage());
        ll_back  = view.findViewById(R.id.ll_back);

        ll_birth_date  = view.findViewById(R.id.ll_birth_date);
        tv_birth_date  = view.findViewById(R.id.tv_birth_date);

        edt_name = view.findViewById(R.id.edt_name);
        edt_email =view.findViewById(R.id.edt_email);
        image_personal = view.findViewById(R.id.image_personal);
        image_icon1 = view.findViewById(R.id.image_icon1);
        image_back_photo = view.findViewById(R.id.image_back_photo);
        segmentGroup = view.findViewById(R.id.segmentGroup);



        fab = view.findViewById(R.id.fab);


        if (current_language.equals("ar"))
        {
            image_back_photo.setImageResource(R.drawable.ic_right_arrow);
            image_back_photo.setColorFilter(ContextCompat.getColor(activity,R.color.colorPrimary), PorterDuff.Mode.SRC_IN);


        }else
        {

            image_back_photo.setImageResource(R.drawable.ic_left_arrow);
            image_back_photo.setColorFilter(ContextCompat.getColor(activity,R.color.colorPrimary), PorterDuff.Mode.SRC_IN);


        }

        image_personal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Check_ReadPermission(IMG1);
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckData();
            }
        });
        ll_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.Back();
            }
        });
        ll_birth_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateDateDialog();
            }
        });
        segmentGroup.setOnClickedButtonListener(new SegmentedButtonGroup.OnClickedButtonListener() {
            @Override
            public void onClickedButton(int position) {
                if (position == 0)
                {
                    gender = Tags.MALE;
                }else
                    {
                        gender = Tags.FEMALE;
                    }
            }
        });


    }

    private void CreateDateDialog() {
        Calendar calendar = Calendar.getInstance(new Locale(current_language));
        Calendar calendar_now = Calendar.getInstance(new Locale(current_language));

        calendar.set(Calendar.DAY_OF_MONTH,1);
        calendar.set(Calendar.MONTH,0);
        calendar.set(Calendar.YEAR,(calendar_now.get(Calendar.YEAR)-20));

        DatePickerDialog dialog = DatePickerDialog.newInstance(this,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
        dialog.setOkText(getString(R.string.select));
        dialog.setCancelText(getString(R.string.cancel));
        dialog.setAccentColor(ContextCompat.getColor(activity,R.color.colorPrimary));
        dialog.setOkColor(ContextCompat.getColor(activity,R.color.colorPrimary));
        dialog.setCancelColor(ContextCompat.getColor(activity,R.color.gray4));
        dialog.setLocale(new Locale(current_language));
        dialog.setVersion(DatePickerDialog.Version.VERSION_1);
        dialog.setMaxDate(calendar);
        dialog.show(activity.getFragmentManager(),"");

    }

    private void CheckData()
    {
        String m_name = edt_name.getText().toString().trim();
        String m_email = edt_email.getText().toString().trim();


        if (!TextUtils.isEmpty(m_name)&&
                !TextUtils.isEmpty(m_email)&&
                Patterns.EMAIL_ADDRESS.matcher(m_email).matches()&&
                date_of_birth!=0
        )
        {
            Common.CloseKeyBoard(activity,edt_name);
            edt_name.setError(null);
            edt_email.setError(null);
            tv_birth_date.setError(null);

            if (uri==null)
            {
                activity.signUpWithoutImage(m_name,m_email,gender,date_of_birth);

            }else
                {
                    activity.signUpWithImage(m_name,m_email,gender,uri,date_of_birth);

                }

        }else
        {
            if (TextUtils.isEmpty(m_name))
            {
                edt_name.setError(getString(R.string.field_req));
            }else
            {
                edt_name.setError(null);

            }

            if (TextUtils.isEmpty(m_email))
            {
                edt_email.setError(getString(R.string.field_req));
            }else if (!Patterns.EMAIL_ADDRESS.matcher(m_email).matches())
            {
                edt_email.setError(getString(R.string.inv_email));
            }
            else
            {
                edt_email.setError(null);

            }

            if (date_of_birth == 0)
            {
                tv_birth_date.setError(getString(R.string.field_req));
            }else {
                tv_birth_date.setError(null);
            }


        }
    }

    private void Check_ReadPermission(int img_req)
    {
        if (ContextCompat.checkSelfPermission(activity,read_permission)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(activity,new String[]{read_permission},img_req);
        }else
        {
            select_photo(img_req);
        }
    }
    private void select_photo(int img1)
    {
        Intent intent ;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
        {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        }else
        {
            intent = new Intent(Intent.ACTION_GET_CONTENT);

        }
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setType("image/*");
        startActivityForResult(intent,img1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMG1 && resultCode == Activity.RESULT_OK && data!=null)
        {
            image_icon1.setVisibility(View.GONE);
            uri = data.getData();

            String path = Common.getImagePath(activity,uri);
            if (path!=null)
            {
                Picasso.with(activity).load(new File(path)).fit().into(image_personal);

            }else
            {
                Picasso.with(activity).load(uri).fit().into(image_personal);

            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == IMG1)
        {
            if (grantResults.length>0)
            {
                if (grantResults[0]==PackageManager.PERMISSION_GRANTED)
                {
                    select_photo(IMG1);
                }else
                {
                    Toast.makeText(activity,getString(R.string.perm_image_denied), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        tv_birth_date.setText(dayOfMonth+"/"+(monthOfYear+1)+"/"+year);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR,year);
        calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
        calendar.set(Calendar.MONTH,monthOfYear);
        date_of_birth = calendar.getTimeInMillis()/1000;
    }
}
