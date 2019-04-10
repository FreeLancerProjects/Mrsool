package com.creativeshare.mrsool.activities_fragments.activity_home.client_home.fragments.fragment_home;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.creativeshare.mrsool.R;
import com.creativeshare.mrsool.activities_fragments.activity_home.client_home.activity.ClientHomeActivity;
import com.creativeshare.mrsool.models.UserModel;
import com.creativeshare.mrsool.preferences.Preferences;
import com.creativeshare.mrsool.share.Common;
import com.creativeshare.mrsool.singletone.UserSingleTone;
import com.creativeshare.mrsool.tags.Tags;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.squareup.picasso.Picasso;

import java.util.Locale;

import co.ceryle.segmentedbutton.SegmentedButtonGroup;
import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class Fragment_Edit_Profile extends Fragment {

    private final static  String TAG = "Data";
    private FloatingActionButton fab;
    private ClientHomeActivity activity;
    private EditText edt_name,edt_email;
    private ImageView arrow;
    private CircleImageView image;
    private LinearLayout ll_back;
    private SimpleRatingBar rateBar;
    private final int IMG1=1;
    private Uri uri=null;
    private final String read_permission = Manifest.permission.READ_EXTERNAL_STORAGE;
    private final String camera_permission = Manifest.permission.CAMERA;
    private SegmentedButtonGroup segmentGroup;
    private String current_language;
    private int gender;
    private UserModel userModel;
    private Preferences preferences;
    private UserSingleTone userSingleTone;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile,container,false);
        initView(view);
        return view;
    }

    public static Fragment_Edit_Profile newInstance(UserModel userModel){

        Bundle bundle = new Bundle();
        bundle.putSerializable(TAG,userModel);

        Fragment_Edit_Profile fragment_edit_profile = new Fragment_Edit_Profile();
        fragment_edit_profile.setArguments(bundle);

        return fragment_edit_profile;
    }
    private void initView(View view) {

        activity = (ClientHomeActivity) getActivity();
        userSingleTone = UserSingleTone.getInstance();
        preferences = Preferences.getInstance();

        Paper.init(activity);
        current_language = Paper.book().read("lang",Locale.getDefault().getLanguage());
        ll_back  = view.findViewById(R.id.ll_back);
        arrow =view.findViewById(R.id.arrow);
        image =view.findViewById(R.id.image);
        edt_name = view.findViewById(R.id.edt_name);
        edt_email =view.findViewById(R.id.edt_email);
        rateBar =view.findViewById(R.id.rateBar);

        segmentGroup = view.findViewById(R.id.segmentGroup);
        segmentGroup.setVisibility(View.INVISIBLE);
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

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Check_ReadPermission();
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

        Bundle bundle = getArguments();

        if (bundle!=null)
        {
            userModel = (UserModel) bundle.getSerializable(TAG);
            UpdateUI(userModel);
        }


    }

    private void UpdateUI(UserModel userModel) {

        if (userModel!=null)
        {
            Picasso.with(activity).load(Uri.parse(Tags.IMAGE_URL+userModel.getData().getUser_image())).fit().placeholder(R.drawable.logo_only).into(image);
            edt_name.setText(userModel.getData().getUser_full_name());
            edt_email.setText(userModel.getData().getUser_email());
            if(userModel.getData().getUser_gender().equals(String.valueOf(Tags.MALE)))
            {
                segmentGroup.setPosition(0);
                gender = Tags.MALE;
            }else
                {
                    segmentGroup.setPosition(1);
                    gender = Tags.FEMALE;

                }
                segmentGroup.setVisibility(View.VISIBLE);
            SimpleRatingBar.AnimationBuilder builder = rateBar.getAnimationBuilder()
                    .setDuration(1000)
                    .setRatingTarget(0.0f)
                    .setRepeatCount(0)
                    .setInterpolator(new AccelerateDecelerateInterpolator());

            builder.start();

        }
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

            UpdateUserData(m_name,m_email);


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

    private void UpdateUserData(String m_name, String m_email) {

    }

    private void Check_ReadPermission()
    {
        if (ContextCompat.checkSelfPermission(activity,read_permission)!= PackageManager.PERMISSION_GRANTED &&(ContextCompat.checkSelfPermission(activity,camera_permission)!= PackageManager.PERMISSION_GRANTED))
        {
            ActivityCompat.requestPermissions(activity,new String[]{read_permission,camera_permission},IMG1);
        }else
        {
            CreateImageAlertDialog();
        }
    }
    private void select_photo(int type)
    {
        Intent  intent = new Intent();

        if (type == 1)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            {
                intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            }else
            {
                intent.setAction(Intent.ACTION_GET_CONTENT);

            }

            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setType("image/*");
            startActivityForResult(intent,IMG1);

        }else if (type ==2)
        {
            try {
                intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,IMG1);
            }catch (SecurityException e)
            {
                Toast.makeText(activity,R.string.perm_image_denied, Toast.LENGTH_SHORT).show();
            }
            catch (Exception e)
            {
                Toast.makeText(activity,R.string.perm_image_denied, Toast.LENGTH_SHORT).show();

            }



        }



    }

    private void CreateImageAlertDialog()
    {

        final AlertDialog dialog = new AlertDialog.Builder(activity)
                .setCancelable(true)
                .create();


        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_select_image,null);
        Button btn_camera = view.findViewById(R.id.btn_camera);
        Button btn_gallery = view.findViewById(R.id.btn_gallery);
        Button btn_cancel = view.findViewById(R.id.btn_cancel);



        btn_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                select_photo(2);

            }
        });

        btn_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                select_photo(1);


            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.getWindow().getAttributes().windowAnimations= R.style.dialog_congratulation_animation;
        dialog.setCanceledOnTouchOutside(false);
        dialog.setView(view);
        dialog.show();
    }
    private void UpdateImage(Uri uri) {

    }

    private void UpdateUserData(UserModel userModel)
    {
        this.userModel = userModel;
        preferences.create_update_userData(activity,userModel);
        userSingleTone.setUserModel(userModel);
        UpdateUI(userModel);
        activity.updateUserData(userModel);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMG1 && resultCode == Activity.RESULT_OK && data!=null)
        {
            uri = data.getData();

            UpdateImage(uri);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == IMG1)
        {
            if (grantResults.length>0)
            {
                if (grantResults[0]==PackageManager.PERMISSION_GRANTED && grantResults[1]==PackageManager.PERMISSION_GRANTED)
                {
                    CreateImageAlertDialog();

                }else
                {
                    Toast.makeText(activity,getString(R.string.perm_image_denied), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }





}
