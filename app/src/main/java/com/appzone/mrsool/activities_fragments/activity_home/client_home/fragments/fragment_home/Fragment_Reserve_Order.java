package com.appzone.mrsool.activities_fragments.activity_home.client_home.fragments.fragment_home;

import android.app.AlertDialog;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appzone.mrsool.R;
import com.appzone.mrsool.activities_fragments.activity_home.client_home.activity.ClientHomeActivity;
import com.appzone.mrsool.models.Favourite_location;
import com.appzone.mrsool.models.PlaceModel;
import com.appzone.mrsool.preferences.Preferences;
import com.appzone.mrsool.share.Common;
import com.squareup.picasso.Picasso;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.Locale;

import io.paperdb.Paper;

public class Fragment_Reserve_Order extends Fragment {
    private static final String TAG = "DATA";
    private ClientHomeActivity activity;
    private ImageView image, arrow, image_reserve;
    private TextView tv_place_name, tv_place_address, tv_address;
    private LinearLayout ll_back, ll_delivery_location, ll_fav_address, ll_fav_map_loc, ll_choose_delivery_time;
    private CheckBox checkbox;
    private ExpandableLayout expandLayout;
    private TextView tv_fav_address_title, tv_fav_address, tv_delivery_time;
    private EditText edt_order_details;
    private PlaceModel placeModel;
    private String current_language;
    private Preferences preferences;
    private Favourite_location favourite_location;
    ////////////////////////////////////////////////
    private Favourite_location selected_location = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reserve_order, container, false);
        initView(view);
        return view;
    }

    public static Fragment_Reserve_Order newInstance(PlaceModel placeModel) {
        Fragment_Reserve_Order fragment_reserve_order = new Fragment_Reserve_Order();
        Bundle bundle = new Bundle();
        bundle.putSerializable(TAG, placeModel);
        fragment_reserve_order.setArguments(bundle);
        return fragment_reserve_order;

    }

    private void initView(View view) {

        activity = (ClientHomeActivity) getActivity();
        preferences = Preferences.getInstance();
        Paper.init(activity);
        current_language = Paper.book().read("lang", Locale.getDefault().getLanguage());
        arrow = view.findViewById(R.id.arrow);

        if (current_language.equals("ar")) {
            arrow.setImageResource(R.drawable.ic_right_arrow);
            arrow.setColorFilter(ContextCompat.getColor(activity, R.color.white), PorterDuff.Mode.SRC_IN);
        } else {
            arrow.setImageResource(R.drawable.ic_left_arrow);
            arrow.setColorFilter(ContextCompat.getColor(activity, R.color.white), PorterDuff.Mode.SRC_IN);


        }
        ll_back = view.findViewById(R.id.ll_back);
        image = view.findViewById(R.id.image);
        image_reserve = view.findViewById(R.id.image_reserve);
        tv_place_name = view.findViewById(R.id.tv_place_name);
        tv_place_address = view.findViewById(R.id.tv_place_address);
        tv_address = view.findViewById(R.id.tv_address);
        ll_delivery_location = view.findViewById(R.id.ll_delivery_location);
        ll_fav_address = view.findViewById(R.id.ll_fav_address);
        ll_fav_map_loc = view.findViewById(R.id.ll_fav_map_loc);
        ll_choose_delivery_time = view.findViewById(R.id.ll_choose_delivery_time);
        checkbox = view.findViewById(R.id.checkbox);
        expandLayout = view.findViewById(R.id.expandLayout);
        tv_fav_address_title = view.findViewById(R.id.tv_fav_address_title);
        tv_fav_address = view.findViewById(R.id.tv_fav_address);
        tv_delivery_time = view.findViewById(R.id.tv_delivery_time);
        edt_order_details = view.findViewById(R.id.edt_order_details);




        ll_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.Back();
            }
        });
        ll_delivery_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (preferences.getFavouriteLocation(activity)!=null)
                {

                    if (expandLayout.isExpanded())
                    {
                        expandLayout.collapse(true);
                    }else
                        {
                            expandLayout.expand(true);
                        }
                }else
                    {
                        if (!checkbox.isChecked())
                        {
                            //Display fragment map

                        }
                    }
            }
        });

        ll_fav_map_loc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Display fragment map
            }
        });

        ll_fav_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                updateSelectedAddress(favourite_location,true);
            }
        });


        checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkbox.isChecked())
                {
                    DisplayAlertEnterAddressTitle();
                }
            }
        });


        Bundle bundle = getArguments();
        if (bundle != null) {
            placeModel = (PlaceModel) bundle.getSerializable(TAG);
            updateUI(placeModel);
        }
    }




    private void updateUI(PlaceModel placeModel) {
        if (placeModel!=null)
        {
            tv_place_name.setText(placeModel.getName());
            tv_place_address.setText(placeModel.getAddress());
            Picasso.with(activity).load(placeModel.getIcon()).fit().into(image);


        }
        favourite_location = preferences.getFavouriteLocation(activity);

        if (favourite_location !=null)
        {
            tv_fav_address_title.setText(favourite_location.getName());
            tv_fav_address.setText(favourite_location.getStreet()+"\n"+favourite_location.getAddress());
        }

    }

    public void updateSelectedAddress(Favourite_location selected_location,boolean checked)
    {

        checkbox.setVisibility(View.VISIBLE);

        this.selected_location = selected_location;


        tv_address.setText(selected_location.getStreet()+"\n"+selected_location.getAddress());
        if (checked)
        {
            checkbox.setChecked(true);

        }else
            {
                checkbox.setChecked(false);

            }
    }

    private void DisplayAlertEnterAddressTitle()
    {
        final AlertDialog dialog = new AlertDialog.Builder(activity)
                .setCancelable(true)
                .create();

        View view  = LayoutInflater.from(activity).inflate(R.layout.dialog_address_fav,null);
        TextView tv_address = view.findViewById(R.id.tv_address);
        final EditText edt_name = view.findViewById(R.id.edt_name);
        Button btn_save = view.findViewById(R.id.btn_save);
        Button btn_cancel = view.findViewById(R.id.btn_cancel);

        tv_address.setText(selected_location.getStreet()+"\n"+selected_location.getAddress());

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edt_name.getText().toString().trim();
                if (!TextUtils.isEmpty(name))
                {
                    edt_name.setError(null);
                    Common.CloseKeyBoard(activity,edt_name);

                    Favourite_location favourite_location = new Favourite_location(name,selected_location.getStreet(),selected_location.getAddress(),selected_location.getLat(),selected_location.getLng());
                    preferences.SaveFavouriteLocation(activity,favourite_location);
                    dialog.dismiss();

                }else
                    {
                        edt_name.setError(getString(R.string.field_req));
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
