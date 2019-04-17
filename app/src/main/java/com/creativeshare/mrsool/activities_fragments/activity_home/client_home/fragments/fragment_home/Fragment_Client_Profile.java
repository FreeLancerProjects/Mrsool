package com.creativeshare.mrsool.activities_fragments.activity_home.client_home.fragments.fragment_home;

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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.creativeshare.mrsool.R;
import com.creativeshare.mrsool.activities_fragments.activity_home.client_home.activity.ClientHomeActivity;
import com.creativeshare.mrsool.models.UserModel;
import com.creativeshare.mrsool.singletone.UserSingleTone;
import com.creativeshare.mrsool.tags.Tags;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.squareup.picasso.Picasso;

import java.util.Currency;
import java.util.Locale;

import io.paperdb.Paper;

public class Fragment_Client_Profile extends Fragment{

    private ImageView image_setting,image,arrow,arrow2;
    private TextView tv_name,tv_balance,tv_order_count,tv_feedback;
    private SimpleRatingBar rateBar;
    private ConstraintLayout cons_logout,cons_register_delegate,cons_comment;
    private LinearLayout ll_telegram;
    private String current_language;
    private ClientHomeActivity activity;
    private UserModel userModel;
    private UserSingleTone userSingleTone;
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

    private void initView(View view)
    {

        activity = (ClientHomeActivity) getActivity();
        userSingleTone = UserSingleTone.getInstance();
        userModel = userSingleTone.getUserModel();
        Paper.init(activity);
        current_language = Paper.book().read("lang", Locale.getDefault().getLanguage());

        arrow = view.findViewById(R.id.arrow);
        arrow2 = view.findViewById(R.id.arrow2);

        if (current_language.equals("ar"))
        {
            arrow.setImageResource(R.drawable.ic_left_arrow);
            arrow2.setImageResource(R.drawable.ic_left_arrow);

        }else
            {
                arrow.setImageResource(R.drawable.ic_right_arrow);
                arrow2.setImageResource(R.drawable.ic_right_arrow);


            }

        image_setting = view.findViewById(R.id.image_setting);
        image = view.findViewById(R.id.image);
        tv_name = view.findViewById(R.id.tv_name);
        tv_balance = view.findViewById(R.id.tv_balance);
        tv_order_count = view.findViewById(R.id.tv_order_count);
        tv_feedback = view.findViewById(R.id.tv_feedback);
        rateBar = view.findViewById(R.id.rateBar);
        cons_register_delegate = view.findViewById(R.id.cons_register_delegate);
        cons_comment = view.findViewById(R.id.cons_comment);
        cons_logout = view.findViewById(R.id.cons_logout);

        ll_telegram = view.findViewById(R.id.ll_telegram);

        image_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.DisplayFragmentSettings();
            }
        });


        cons_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.Logout();
            }
        });

        cons_register_delegate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.DisplayFragmentRegisterDelegate();
            }
        });
        cons_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userModel.getData().getUser_type().equals(Tags.TYPE_DELEGATE))
                {
                    activity.DisplayFragmentDelegateComment();

                }
            }
        });
       /* ll_telegram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(""));
                startActivity(intent);
            }
        });*/

        updateUI(userModel);

    }
    public void updateUI(UserModel userModel)
    {
        if (userModel!=null)
        {
            this.userModel = userModel;

            tv_name.setText(userModel.getData().getUser_full_name());
            tv_order_count.setText(String.valueOf(userModel.getData().getNum_orders()));
            Picasso.with(activity).load(Uri.parse(Tags.IMAGE_URL+userModel.getData().getUser_image())).placeholder(R.drawable.logo_only).into(image);
            Currency currency = Currency.getInstance(new Locale(current_language,userModel.getData().getUser_country()));
            tv_balance.setText(String.valueOf(userModel.getData().getAccount_balance())+" "+currency.getSymbol());

            if(userModel.getData().getUser_type().equals(Tags.TYPE_DELEGATE))
            {
                tv_feedback.setText(String.valueOf(userModel.getData().getNum_comments()));

                SimpleRatingBar.AnimationBuilder builder = rateBar.getAnimationBuilder()
                        .setRepeatCount(0)
                        .setDuration(1500)
                        .setRatingTarget((float) userModel.getData().getRate());
                builder.start();

            }

        }
    }

    public void updateUserData(UserModel userModel)
    {
        this.userModel = userModel;
        userSingleTone.setUserModel(userModel);
        updateUI(userModel);
    }
}
