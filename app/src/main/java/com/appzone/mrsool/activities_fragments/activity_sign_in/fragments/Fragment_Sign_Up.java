package com.appzone.mrsool.activities_fragments.activity_sign_in.fragments;

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
import android.widget.Toast;

import com.appzone.mrsool.R;
import com.appzone.mrsool.activities_fragments.activity_sign_in.activity.SignInActivity;
import com.appzone.mrsool.share.Common;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Locale;

public class Fragment_Sign_Up extends Fragment {

    private FloatingActionButton fab;
    private SignInActivity activity;
    private EditText edt_name,edt_email;
    private ImageView image_personal,image_icon1,image_back_photo;
    private LinearLayout ll_back;
    private final int IMG1=1;
    private Uri uri=null;
    private final String read_permission = Manifest.permission.READ_EXTERNAL_STORAGE;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_complete_profile,container,false);
        initView(view);
        return view;
    }

    public static Fragment_Sign_Up newInstance(){
        return new Fragment_Sign_Up();
    }
    private void initView(View view) {

        activity = (SignInActivity) getActivity();

        ll_back  = view.findViewById(R.id.ll_back);
        edt_name = view.findViewById(R.id.edt_name);

        edt_email =view.findViewById(R.id.edt_email);
        image_personal = view.findViewById(R.id.image_personal);
        image_icon1 = view.findViewById(R.id.image_icon1);
        image_back_photo = view.findViewById(R.id.image_back_photo);
        fab = view.findViewById(R.id.fab);


        if (Locale.getDefault().getLanguage().equals("ar"))
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



    }

    private void CheckData()
    {
        String m_name = edt_name.getText().toString().trim();
        String m_email = edt_email.getText().toString().trim();


        if (!TextUtils.isEmpty(m_name)&&
                !TextUtils.isEmpty(m_email)&&
                Patterns.EMAIL_ADDRESS.matcher(m_email).matches()



                )
        {
            Common.CloseKeyBoard(activity,edt_name);
            edt_name.setError(null);
            edt_email.setError(null);

            if (uri==null)
            {
                activity.signUpWithoutImage(m_name,m_email);

            }else
                {
                    activity.signUpWithImage(m_name,m_email,uri);

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



}
