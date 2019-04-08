package com.creativeshare.mrsool.activities_fragments.activity_home.client_home.fragments.fragment_home;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.creativeshare.mrsool.R;
import com.creativeshare.mrsool.activities_fragments.activity_home.client_home.activity.ClientHomeActivity;
import com.creativeshare.mrsool.models.UserModel;
import com.creativeshare.mrsool.preferences.Preferences;
import com.creativeshare.mrsool.share.Common;
import com.creativeshare.mrsool.singletone.UserSingleTone;
import com.creativeshare.mrsool.tags.Tags;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;

public class Fragment_Home extends Fragment {

    private ClientHomeActivity activity;
    private AHBottomNavigation ah_bottom_nav;
    private UserSingleTone userSingleTone;
    private UserModel userModel;
    private Preferences preferences;

    public static Fragment_Home newInstance()
    {
        return new Fragment_Home();
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home,container,false);
        initView(view);
        return view;
    }


    private void initView(View view) {
        activity = (ClientHomeActivity) getActivity();
        preferences = Preferences.getInstance();
        userSingleTone = UserSingleTone.getInstance();
        String session = preferences.getSession(activity);
        if (session.equals(Tags.session_login))
        {
            userSingleTone.setUserModel(preferences.getUserData(activity));
        }
        userModel = userSingleTone.getUserModel();
        ah_bottom_nav = view.findViewById(R.id.ah_bottom_nav);

        setUpBottomNavigation();
        ah_bottom_nav.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                switch (position)
                {
                    case 0:
                        activity.DisplayFragmentStore();
                        break;
                    case 1:
                        if (userModel==null)
                        {
                            Common.CreateUserNotSignInAlertDialog(activity);
                        }else
                        {
                            activity.DisplayFragmentMyOrders();

                        }
                        break;
                    case 2:
                        if (userModel==null)
                        {
                            Common.CreateUserNotSignInAlertDialog(activity);
                        }else
                        {
                            activity.DisplayFragmentNotification();

                        }
                        break;
                    case 3:
                        if (userModel==null)
                        {
                            Common.CreateUserNotSignInAlertDialog(activity);
                        }else
                        {
                            activity.DisplayFragmentProfile();

                        }


                        break;
                }
                return false;
            }
        });

    }

    private void setUpBottomNavigation()
    {

        AHBottomNavigationItem item1 = new AHBottomNavigationItem(getString(R.string.stores),R.drawable.ic_nav_store);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem(getString(R.string.my_orders),R.drawable.ic_nav_order);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem(getString(R.string.notifications),R.drawable.ic_nav_notification);
        AHBottomNavigationItem item4 = new AHBottomNavigationItem(getString(R.string.profile),R.drawable.ic_nav_user);

        ah_bottom_nav.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);
        ah_bottom_nav.setDefaultBackgroundColor(ContextCompat.getColor(activity,R.color.white));
        ah_bottom_nav.setTitleTextSizeInSp(14,12);
        ah_bottom_nav.setForceTint(true);
        ah_bottom_nav.setAccentColor(ContextCompat.getColor(activity,R.color.colorPrimary));
        ah_bottom_nav.setInactiveColor(ContextCompat.getColor(activity,R.color.gray4));

        ah_bottom_nav.addItem(item1);
        ah_bottom_nav.addItem(item2);
        ah_bottom_nav.addItem(item3);
        ah_bottom_nav.addItem(item4);


    }

    public void updateBottomNavigationPosition(int pos)
    {
        ah_bottom_nav.setCurrentItem(pos,false);
    }

}
