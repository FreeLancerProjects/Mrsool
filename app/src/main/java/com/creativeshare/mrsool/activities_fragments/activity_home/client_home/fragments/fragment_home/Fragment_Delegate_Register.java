package com.creativeshare.mrsool.activities_fragments.activity_home.client_home.fragments.fragment_home;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.creativeshare.mrsool.R;
import com.creativeshare.mrsool.activities_fragments.activity_home.client_home.activity.ClientHomeActivity;
import com.creativeshare.mrsool.share.Common;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Locale;

import io.paperdb.Paper;

public class Fragment_Delegate_Register extends Fragment {

    private ImageView image_id, image_id_icon, image_license, image_license_icon, arrow, image_register;
    private LinearLayout ll_back;
    private FrameLayout fl_id_image, fl_license_image;
    private EditText edt_national_num, edt_address;
    private ClientHomeActivity activity;
    private String current_language;
    private final String READ_PERM = Manifest.permission.READ_EXTERNAL_STORAGE;
    private final int IMG_REQ1 = 1, IMG_REQ2 = 2;
    private Uri imgUri1 = null, imgUri2 = null;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_delegate_register, container, false);
        initView(view);
        return view;
    }

    public static Fragment_Delegate_Register newInstance() {
        return new Fragment_Delegate_Register();
    }

    private void initView(View view) {

        activity = (ClientHomeActivity) getActivity();
        Paper.init(activity);
        current_language = Paper.book().read("lang", Locale.getDefault().getLanguage());

        arrow = view.findViewById(R.id.arrow);
        if (current_language.equals("ar")) {
            arrow.setImageResource(R.drawable.ic_right_arrow);

        } else {
            arrow.setImageResource(R.drawable.ic_left_arrow);


        }

        image_id = view.findViewById(R.id.image_id);
        image_id_icon = view.findViewById(R.id.image_id_icon);
        image_license = view.findViewById(R.id.image_license);
        image_license_icon = view.findViewById(R.id.image_license_icon);
        image_register = view.findViewById(R.id.image_register);
        ll_back = view.findViewById(R.id.ll_back);
        edt_national_num = view.findViewById(R.id.edt_national_num);
        edt_address = view.findViewById(R.id.edt_address);

        fl_id_image = view.findViewById(R.id.fl_id_image);
        fl_license_image = view.findViewById(R.id.fl_license_image);


        ll_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.Back();
            }
        });

        fl_id_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckPermission(IMG_REQ1);
            }
        });

        fl_license_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckPermission(IMG_REQ2);
            }
        });

        image_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckData();
            }
        });

    }

    private void CheckData() {
        String m_national_id = edt_national_num.getText().toString().trim();
        String m_address = edt_address.getText().toString().trim();

        if (!TextUtils.isEmpty(m_national_id) &&
                !TextUtils.isEmpty(m_address)&&
                imgUri1!=null&&
                imgUri2!=null
                
        )
        {
            edt_address.setError(null);
            edt_national_num.setError(null);
            Common.CloseKeyBoard(activity,edt_national_num);
            activity.registerDelegate(m_national_id,m_address,imgUri1,imgUri2);
            
        }else 
            {
                if (TextUtils.isEmpty(m_national_id))
                {
                    edt_national_num.setError(getString(R.string.field_req));

                }else 
                    {
                        edt_national_num.setError(null);

                    }

                if (TextUtils.isEmpty(m_address))
                {
                    edt_address.setError(getString(R.string.field_req));

                }else
                {
                    edt_address.setError(null);

                }
                
                if (imgUri1==null)
                {
                    Toast.makeText(activity, R.string.choose_identity_card_image, Toast.LENGTH_SHORT).show();
                }

                if (imgUri2==null)
                {
                    Toast.makeText(activity, R.string.choose_license_image, Toast.LENGTH_SHORT).show();
                }
            }
    }


    private void CheckPermission(int img_req) {
        if (ActivityCompat.checkSelfPermission(activity, READ_PERM) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{READ_PERM}, img_req);
        } else {
            SelectImage(img_req);
        }
    }

    private void SelectImage(int img_req) {

        Intent intent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        } else {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }

        intent.setType("image/*");
        startActivityForResult(intent, img_req);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == IMG_REQ1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                SelectImage(IMG_REQ1);
            } else {
                Toast.makeText(activity, getString(R.string.perm_image_denied), Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == IMG_REQ2) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                SelectImage(IMG_REQ2);
            } else {
                Toast.makeText(activity, getString(R.string.perm_image_denied), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMG_REQ1 && resultCode == Activity.RESULT_OK && data != null) {
            imgUri1 = data.getData();
            image_id_icon.setVisibility(View.GONE);
            File file = new File(Common.getImagePath(activity, imgUri1));
            Picasso.with(activity).load(file).fit().into(image_id);



        } else if (requestCode == IMG_REQ2 && resultCode == Activity.RESULT_OK && data != null) {
            imgUri2 = data.getData();
            image_license_icon.setVisibility(View.GONE);
            File file = new File(Common.getImagePath(activity, imgUri2));

            Picasso.with(activity).load(file).fit().into(image_license);

        }
    }
}
