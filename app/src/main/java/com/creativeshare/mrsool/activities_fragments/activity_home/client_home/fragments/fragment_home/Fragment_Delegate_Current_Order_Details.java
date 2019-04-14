package com.creativeshare.mrsool.activities_fragments.activity_home.client_home.fragments.fragment_home;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.creativeshare.mrsool.R;
import com.creativeshare.mrsool.activities_fragments.activity_home.client_home.activity.ClientHomeActivity;
import com.creativeshare.mrsool.models.ChatUserModel;
import com.creativeshare.mrsool.models.OrderDataModel;
import com.creativeshare.mrsool.models.UserModel;
import com.creativeshare.mrsool.singletone.UserSingleTone;
import com.creativeshare.mrsool.tags.Tags;

import java.util.Currency;
import java.util.Locale;

import io.paperdb.Paper;

public class Fragment_Delegate_Current_Order_Details extends Fragment {
    private static final String TAG = "ORDER";
    private ClientHomeActivity activity;
    private ImageView image_back,image_arrow,image_arrow2,image_chat;
    private LinearLayout ll_back;
    private String current_lang;
    private TextView tv_client_name,tv_address,tv_order_details,tv_order_state,tv_order_next_state;
    private FrameLayout fl_map,fl_update_order_state;
    private OrderDataModel.OrderModel order;
    private UserSingleTone userSingleTone;
    private UserModel userModel;
    private int order_state;




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_delegate_current_order_details,container,false);
        initView(view);
        return view;
    }

    public static Fragment_Delegate_Current_Order_Details newInstance(OrderDataModel.OrderModel order)
    {
        Bundle bundle = new Bundle();
        bundle.putSerializable(TAG,order);
        Fragment_Delegate_Current_Order_Details fragment_delegate_Current_order_details = new Fragment_Delegate_Current_Order_Details();
        fragment_delegate_Current_order_details.setArguments(bundle);
        return fragment_delegate_Current_order_details;
    }
    private void initView(View view) {
        activity = (ClientHomeActivity) getActivity();
        userSingleTone = UserSingleTone.getInstance();
        userModel = userSingleTone.getUserModel();
        Paper.init(activity);
        current_lang = Paper.book().read("lang", Locale.getDefault().getLanguage());


        image_back = view.findViewById(R.id.image_back);
        image_arrow = view.findViewById(R.id.image_arrow);
        image_arrow2 = view.findViewById(R.id.image_arrow2);

        if (current_lang.equals("ar"))
        {
            image_back.setImageResource(R.drawable.ic_right_arrow);
            image_arrow.setImageResource(R.drawable.ic_right_arrow);
            image_arrow2.setImageResource(R.drawable.ic_right_arrow);

        }else
        {
            image_back.setImageResource(R.drawable.ic_left_arrow);
            image_arrow.setImageResource(R.drawable.ic_left_arrow);
            image_arrow2.setImageResource(R.drawable.ic_left_arrow);

        }
        image_chat = view.findViewById(R.id.image_chat);

        ll_back = view.findViewById(R.id.ll_back);
        tv_order_details = view.findViewById(R.id.tv_order_details);
        tv_order_state = view.findViewById(R.id.tv_order_state);
        tv_order_next_state = view.findViewById(R.id.tv_order_next_state);
        tv_client_name = view.findViewById(R.id.tv_client_name);
        tv_address = view.findViewById(R.id.tv_address);
        fl_map = view.findViewById(R.id.fl_map);
        fl_update_order_state = view.findViewById(R.id.fl_update_order_state);

        Bundle bundle = getArguments();
        if (bundle!=null)
        {
            this.order = (OrderDataModel.OrderModel) bundle.getSerializable(TAG);
            UpdateUI(this.order);
        }

        ll_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.Back();
            }
        });


        fl_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              activity.DisplayFragmentMapLocationDetails(Double.parseDouble(order.getPlace_lat()),Double.parseDouble(order.getPlace_long()),order.getClient_address());
            }
        });

        image_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ChatUserModel chatUserModel = new ChatUserModel(order.getClient_user_full_name(),order.getClient_user_image(),order.getClient_id(),order.getRoom_id_fk(),order.getClient_user_phone_code(),order.getClient_user_phone());
                activity.DisplayFragmentChat(chatUserModel);
            }
        });

        fl_update_order_state.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                activity.UpdateOrderMovement(order.getClient_id(),order.getDriver_id(),order.getOrder_id(),order_state);

            }
        });



    }

    private void UpdateUI(OrderDataModel.OrderModel order)
    {
        Currency currency = Currency.getInstance(new Locale(current_lang,userModel.getData().getUser_country()));
        tv_address.setText(order.getClient_address());
        tv_client_name.setText(order.getClient_user_full_name());
        tv_order_details.setText(order.getOrder_details()+"\n"+getString(R.string.delivery_cost)+":"+order.getDriver_offer()+currency.getSymbol());

        order_state = Integer.parseInt(order.getOrder_movement());
        updateOrderState(order_state);

    }

    private void updateOrderState(int state)
    {

        switch (state)
        {
            case 0:
                tv_order_state.setText(getString(R.string.accepted));
                tv_order_next_state.setText(getString(R.string.collect_order));
                break;
            case Tags.STATE_DELEGATE_COLLECTED_ORDER:
                tv_order_state.setText(getString(R.string.collected_order));
                tv_order_next_state.setText(getString(R.string.deliver_order));
                break;
            case Tags.STATE_DELEGATE_DELIVERED_ORDER:
                tv_order_state.setText(getString(R.string.delivered_order));
                fl_update_order_state.setVisibility(View.INVISIBLE);
                break;

        }

    }





}
