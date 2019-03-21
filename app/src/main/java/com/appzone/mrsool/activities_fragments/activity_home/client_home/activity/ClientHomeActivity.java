package com.appzone.mrsool.activities_fragments.activity_home.client_home.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.appzone.mrsool.R;
import com.appzone.mrsool.activities_fragments.activity_home.client_home.fragments.fragment_home.Fragment_Client_Notifications;
import com.appzone.mrsool.activities_fragments.activity_home.client_home.fragments.fragment_home.Fragment_Client_Profile;
import com.appzone.mrsool.activities_fragments.activity_home.client_home.fragments.fragment_home.Fragment_Client_Store;
import com.appzone.mrsool.activities_fragments.activity_home.client_home.fragments.fragment_home.Fragment_Home;
import com.appzone.mrsool.activities_fragments.activity_home.client_home.fragments.fragment_home.Fragment_Search;
import com.appzone.mrsool.activities_fragments.activity_home.client_home.fragments.fragment_home.Fragment_Settings;
import com.appzone.mrsool.activities_fragments.activity_home.client_home.fragments.fragment_orders.Fragment_Client_Orders;
import com.appzone.mrsool.activities_fragments.activity_home.client_home.fragments.fragment_store_details.Fragment_Store_Details;
import com.appzone.mrsool.language.Language_Helper;
import com.appzone.mrsool.models.PlaceModel;
import com.appzone.mrsool.models.UserModel;
import com.appzone.mrsool.preferences.Preferences;
import com.appzone.mrsool.services.UpdateLocationService;
import com.appzone.mrsool.share.Common;
import com.appzone.mrsool.singletone.UserSingleTone;
import com.appzone.mrsool.tags.Tags;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;

public class ClientHomeActivity extends AppCompatActivity {
    private final String gps_perm = Manifest.permission.ACCESS_FINE_LOCATION;
    private final int gps_req = 22;
    private FragmentManager fragmentManager;
    private Fragment_Home fragment_home;
    private Fragment_Client_Store fragment_client_store;
    private Fragment_Client_Orders fragment_client_orders;
    private Fragment_Client_Notifications fragment_client_notifications;
    private Fragment_Client_Profile fragment_client_profile;
    private Fragment_Store_Details fragment_store_details;
    private Fragment_Search fragment_search;
    private Fragment_Settings fragment_settings;
    private UserSingleTone userSingleTone;
    private UserModel userModel;
    private Preferences preferences;
    private Intent intentService;
    private ProgressDialog dialog;
    private Location location;
    private String current_lang;
    private String lastSelectedFragment="";

    @Override
    protected void attachBaseContext(Context base) {
        Paper.init(base);
        current_lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        super.attachBaseContext(Language_Helper.setLocality(base,current_lang));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_home);
        initView();

        if (savedInstanceState == null) {
            CheckPermission();
            DisplayFragmentHome();
            if (!EventBus.getDefault().isRegistered(this))
            {
                EventBus.getDefault().register(this);
            }
        }
    }


    private void initView() {
        preferences = Preferences.getInstance();
        userSingleTone = UserSingleTone.getInstance();
        String session = preferences.getSession(this);
        if (session.equals(Tags.session_login)) {
            userSingleTone.setUserModel(preferences.getUserData(this));
        }
        userModel = userSingleTone.getUserModel();

        fragmentManager = getSupportFragmentManager();


    }
    ///////////////////////////////////
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void LocationListener(final Location location)
    {
        if (location!=null)
        {
            ClientHomeActivity.this.location = location;
        }
        if (fragment_client_store!=null&&fragment_client_store.isAdded())
        {
            new Handler()
                    .postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            fragment_client_store.getNearbyPlaces(location,"restaurant");
                            if (intentService!=null)
                            {
                                stopService(intentService);
                                EventBus.getDefault().unregister(ClientHomeActivity.this);
                            }
                        }
                    },1);
        }
    }
    ///////////////////////////////////

    public void DisplayFragmentHome() {
        lastSelectedFragment = "";

        if (fragment_home == null) {
            fragment_home = Fragment_Home.newInstance();
        }

        if (fragment_home.isAdded()) {
            fragmentManager.beginTransaction().show(fragment_home).commit();

        } else {
            fragmentManager.beginTransaction().add(R.id.fragment_app_container, fragment_home, "fragment_home").addToBackStack("fragment_home").commit();
            DisplayFragmentStore();

        }

    }

    public void DisplayFragmentStore() {

        lastSelectedFragment = "fragment_client_store";

        if (fragment_home != null && fragment_home.isAdded()) {
            fragment_home.updateBottomNavigationPosition(0);
        }
        if (fragment_client_orders != null && fragment_client_orders.isAdded()) {
            fragmentManager.beginTransaction().hide(fragment_client_orders).commit();
        }
        if (fragment_client_notifications != null && fragment_client_notifications.isAdded()) {
            fragmentManager.beginTransaction().hide(fragment_client_notifications).commit();
        }
        if (fragment_client_profile != null && fragment_client_profile.isAdded()) {
            fragmentManager.beginTransaction().hide(fragment_client_profile).commit();
        }

        if (fragment_client_store == null) {
            fragment_client_store = Fragment_Client_Store.newInstance();
        }

        if (fragment_client_store.isAdded()) {
            fragmentManager.beginTransaction().show(fragment_client_store).commit();

        } else {
            fragmentManager.beginTransaction().add(R.id.fragment_home_container, fragment_client_store, "fragment_client_store").addToBackStack("fragment_client_store").commit();
        }

    }

    public void DisplayFragmentMyOrders() {
        if (fragment_home != null && fragment_home.isAdded()) {
            fragment_home.updateBottomNavigationPosition(1);
        }

        if (fragment_client_store != null && fragment_client_store.isAdded()) {
            fragmentManager.beginTransaction().hide(fragment_client_store).commit();
        }
        if (fragment_client_notifications != null && fragment_client_notifications.isAdded()) {
            fragmentManager.beginTransaction().hide(fragment_client_notifications).commit();
        }
        if (fragment_client_profile != null && fragment_client_profile.isAdded()) {
            fragmentManager.beginTransaction().hide(fragment_client_profile).commit();
        }

        if (fragment_client_orders == null) {
            fragment_client_orders = Fragment_Client_Orders.newInstance();
        }

        if (fragment_client_orders.isAdded()) {
            fragmentManager.beginTransaction().show(fragment_client_orders).commit();

        } else {
            fragmentManager.beginTransaction().add(R.id.fragment_home_container, fragment_client_orders, "fragment_client_orders").addToBackStack("fragment_client_orders").commit();
        }

    }

    public void DisplayFragmentNotification() {

        if (fragment_home != null && fragment_home.isAdded()) {
            fragment_home.updateBottomNavigationPosition(2);
        }
        if (fragment_client_store != null && fragment_client_store.isAdded()) {
            fragmentManager.beginTransaction().hide(fragment_client_store).commit();
        }
        if (fragment_client_orders != null && fragment_client_orders.isAdded()) {
            fragmentManager.beginTransaction().hide(fragment_client_orders).commit();
        }
        if (fragment_client_profile != null && fragment_client_profile.isAdded()) {
            fragmentManager.beginTransaction().hide(fragment_client_profile).commit();
        }

        if (fragment_client_notifications == null) {
            fragment_client_notifications = Fragment_Client_Notifications.newInstance();
        }

        if (fragment_client_notifications.isAdded()) {
            fragmentManager.beginTransaction().show(fragment_client_notifications).commit();

        } else {
            fragmentManager.beginTransaction().add(R.id.fragment_home_container, fragment_client_notifications, "fragment_client_notifications").addToBackStack("fragment_client_notifications").commit();
        }

    }

    public void DisplayFragmentProfile() {
        if (fragment_home != null && fragment_home.isAdded()) {
            fragment_home.updateBottomNavigationPosition(3);
        }
        if (fragment_client_store != null && fragment_client_store.isAdded()) {
            fragmentManager.beginTransaction().hide(fragment_client_store).commit();
        }
        if (fragment_client_orders != null && fragment_client_orders.isAdded()) {
            fragmentManager.beginTransaction().hide(fragment_client_orders).commit();
        }
        if (fragment_client_notifications != null && fragment_client_notifications.isAdded()) {
            fragmentManager.beginTransaction().hide(fragment_client_notifications).commit();
        }

        if (fragment_client_profile == null) {
            fragment_client_profile = Fragment_Client_Profile.newInstance();
        }

        if (fragment_client_profile.isAdded()) {
            fragmentManager.beginTransaction().show(fragment_client_profile).commit();

        } else {
            fragmentManager.beginTransaction().add(R.id.fragment_home_container, fragment_client_profile, "fragment_client_profile").addToBackStack("fragment_client_profile").commit();
        }

    }

    public void DisplayFragmentSearch() {

        if (location!=null)
        {
            lastSelectedFragment = "fragment_search";

            fragment_search = Fragment_Search.newInstance(location.getLatitude(),location.getLongitude());

            if (fragment_home!=null&&fragment_home.isAdded())
            {
                fragmentManager.beginTransaction().hide(fragment_home).commit();
            }

            if (fragment_search.isAdded()) {
                fragmentManager.beginTransaction().show(fragment_search).commit();

            } else {
                fragmentManager.beginTransaction().add(R.id.fragment_app_container, fragment_search, "fragment_search").addToBackStack("fragment_search").commit();
            }
        }


    }

    public void DisplayFragmentSettings() {



            fragment_settings = Fragment_Settings.newInstance();

            if (fragment_home!=null&&fragment_home.isAdded())
            {
                fragmentManager.beginTransaction().hide(fragment_home).commit();
            }

            if (fragment_settings.isAdded()) {
                fragmentManager.beginTransaction().show(fragment_settings).commit();

            } else {
                fragmentManager.beginTransaction().add(R.id.fragment_app_container, fragment_settings, "fragment_settings").addToBackStack("fragment_settings").commit();
            }



    }


    public void DisplayFragmentStoreDetails(PlaceModel placeModel)
    {

        if (fragment_home != null && fragment_home.isAdded()) {
            fragmentManager.beginTransaction().hide(fragment_home).commit();
        }

        fragment_store_details = Fragment_Store_Details.newInstance(placeModel,location.getLatitude(),location.getLongitude());

        if (fragment_store_details.isAdded()) {
            fragmentManager.beginTransaction().show(fragment_store_details).commit();

        } else {
            fragmentManager.beginTransaction().add(R.id.fragment_app_container, fragment_store_details, "fragment_store_details").addToBackStack("fragment_store_details").commit();
        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        List<Fragment> fragmentList = fragmentManager.getFragments();
        for (Fragment fragment : fragmentList) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }

        if (requestCode == 33) {
            if (isGpsOpen()) {
                StartService();
            } else {
                CreateGpsDialog();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        List<Fragment> fragmentList = fragmentManager.getFragments();
        for (Fragment fragment : fragmentList) {
            fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

        if (requestCode == gps_req && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (isGpsOpen())
            {
                StartService();
            }else
                {
                    CreateGpsDialog();
                }
        }
    }

    private void CheckPermission()
    {
        if (ActivityCompat.checkSelfPermission(this, gps_perm) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{gps_perm}, gps_req);
        } else {

            if (isGpsOpen())
            {
                StartService();
            }else
                {
                    CreateGpsDialog();

                }
        }
    }
    private void StartService() {
        dialog = Common.createProgressDialog(this,getString(R.string.fetching_your_location));
        dialog.setCancelable(false);
        dialog.show();
        intentService = new Intent(this, UpdateLocationService.class);
        startService(intentService);
    }

    private boolean isGpsOpen() {
        boolean isOpened = false;
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (manager != null) {
            if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                isOpened = true;
            }
        }

        return isOpened;
    }

    private void OpenGps() {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivityForResult(intent, 33);
    }

    private void CreateGpsDialog()
    {
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setCancelable(true)
                .create();

        View view = LayoutInflater.from(this).inflate(R.layout.gps_dialog,null);
        Button btn_allow = view.findViewById(R.id.btn_allow);
        Button btn_deny = view.findViewById(R.id.btn_deny);

        btn_allow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                OpenGps();
            }
        });
        btn_deny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        dialog.getWindow().getAttributes().windowAnimations=R.style.dialog_congratulation_animation;
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_window_bg);
        dialog.setView(view);
        dialog.show();
    }
    public void DismissDialog()
    {
        if (dialog!=null)
        {
            dialog.dismiss();
        }
    }

    public void Back()
    {
        if (fragment_store_details!=null&&fragment_store_details.isVisible())
        {
            if (lastSelectedFragment.equals("fragment_search"))
            {
                super.onBackPressed();
            }else if (lastSelectedFragment.equals("fragment_client_store"))
            {
                super.onBackPressed();

                DisplayFragmentHome();

            }

        }
        else if (fragment_search!=null&&fragment_search.isVisible())
        {
            super.onBackPressed();
            DisplayFragmentHome();

        }else if (fragment_settings!=null&&fragment_settings.isVisible())
        {
            super.onBackPressed();
            DisplayFragmentHome();

        }
        else if (fragment_home!=null&&fragment_home.isVisible())
        {
            if (fragment_client_store!=null&&fragment_client_store.isVisible())
            {
                finish();
            }else
            {
                DisplayFragmentStore();
            }
        }else
            {
                String name = fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount()-1).getName();
                if (name.equals("fragment_search"))
                {

                }else if (name.equals("fragment_store_details"))
                {
                    super.onBackPressed();
                    DisplayFragmentHome();
                }
                else if (name.equals("fragment_settings"))
                {
                    super.onBackPressed();
                    DisplayFragmentHome();
                }
            }
        /*if (fragment_store_details!=null&&fragment_store_details.isVisible())
        {
            fragmentManager.popBackStack("fragment_store_details",FragmentManager.POP_BACK_STACK_INCLUSIVE);
            DisplayFragmentHome();
        }else if (fragment_search!=null&&fragment_search.isVisible())
        {
            fragmentManager.popBackStack("fragment_search",FragmentManager.POP_BACK_STACK_INCLUSIVE);
            DisplayFragmentHome();
        }
        else if (fragment_home!=null&&!fragment_home.isVisible())
        {
            DisplayFragmentHome();

        }else
        {
            if (fragment_client_store!=null&&fragment_client_store.isVisible())
            {
                finish();
            }else
                {
                    DisplayFragmentStore();
                }

        }*/
    }
    @Override
    public void onBackPressed() {
        Back();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (intentService!=null)
        {
            stopService(intentService);
        }

        if (EventBus.getDefault().isRegistered(this))
        {
            EventBus.getDefault().unregister(this);
        }
    }
}
