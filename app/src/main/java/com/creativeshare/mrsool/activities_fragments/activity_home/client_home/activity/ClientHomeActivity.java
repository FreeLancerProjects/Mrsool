package com.creativeshare.mrsool.activities_fragments.activity_home.client_home.activity;

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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.creativeshare.mrsool.R;
import com.creativeshare.mrsool.activities_fragments.activity_home.client_home.fragments.fragment_home.Fragment_Client_Notifications;
import com.creativeshare.mrsool.activities_fragments.activity_home.client_home.fragments.fragment_home.Fragment_Client_Profile;
import com.creativeshare.mrsool.activities_fragments.activity_home.client_home.fragments.fragment_home.Fragment_Client_Store;
import com.creativeshare.mrsool.activities_fragments.activity_home.client_home.fragments.fragment_home.Fragment_Edit_Profile;
import com.creativeshare.mrsool.activities_fragments.activity_home.client_home.fragments.fragment_home.Fragment_Home;
import com.creativeshare.mrsool.activities_fragments.activity_home.client_home.fragments.fragment_home.Fragment_Map;
import com.creativeshare.mrsool.activities_fragments.activity_home.client_home.fragments.fragment_home.Fragment_Reserve_Order;
import com.creativeshare.mrsool.activities_fragments.activity_home.client_home.fragments.fragment_home.Fragment_Search;
import com.creativeshare.mrsool.activities_fragments.activity_home.client_home.fragments.fragment_home.Fragment_Settings;
import com.creativeshare.mrsool.activities_fragments.activity_home.client_home.fragments.fragment_orders.Fragment_Client_Orders;
import com.creativeshare.mrsool.activities_fragments.activity_home.client_home.fragments.fragment_store_details.Fragment_Store_Details;
import com.creativeshare.mrsool.activities_fragments.activity_sign_in.activity.SignInActivity;
import com.creativeshare.mrsool.activities_fragments.terms_conditions.TermsConditionsActivity;
import com.creativeshare.mrsool.language.Language_Helper;
import com.creativeshare.mrsool.models.Favourite_location;
import com.creativeshare.mrsool.models.LocationError;
import com.creativeshare.mrsool.models.PlaceModel;
import com.creativeshare.mrsool.models.UserModel;
import com.creativeshare.mrsool.preferences.Preferences;
import com.creativeshare.mrsool.remote.Api;
import com.creativeshare.mrsool.services.UpdateLocationService;
import com.creativeshare.mrsool.share.Common;
import com.creativeshare.mrsool.singletone.UserSingleTone;
import com.creativeshare.mrsool.tags.Tags;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
    private Fragment_Reserve_Order fragment_reserve_order;
    private Fragment_Search fragment_search;
    private Fragment_Settings fragment_settings;
    private Fragment_Edit_Profile fragment_edit_profile;
    private Fragment_Map fragment_map;
    private UserSingleTone userSingleTone;
    private UserModel userModel;
    private Preferences preferences;
    private Intent intentService;
    private ProgressDialog dialog;
    public  Location location = null;
    private String current_lang;
    private int fragment_count = 0;


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(Language_Helper.updateResources(base,Language_Helper.getLanguage(base)));
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
        Paper.init(this);
        current_lang = Paper.book().read("lang", Locale.getDefault().getLanguage());

        preferences = Preferences.getInstance();
        userSingleTone = UserSingleTone.getInstance();
        userModel = userSingleTone.getUserModel();

        fragmentManager = getSupportFragmentManager();

        if (userModel!=null)
        {
            updateToken();
        }


    }

    private void updateToken() {
        FirebaseInstanceId.getInstance()
                .getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (task.isSuccessful())
                        {
                            String token = task.getResult().getToken();
                            Api.getService(Tags.base_url)
                                    .updateToken(userModel.getData().getUser_id(),token)
                                    .enqueue(new Callback<ResponseBody>() {
                                        @Override
                                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                                            if (response.isSuccessful())
                                            {
                                                Log.e("Success","token updated");
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                                            try {
                                                Log.e("Error",t.getMessage());
                                            }catch (Exception e){}
                                        }
                                    });
                        }
                    }
                });
    }

    ///////////////////////////////////
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void LocationListener(final Location location)
    {
        if (dialog!=null)
        {
            dialog.dismiss();
        }
        if (location!=null)
        {
            if (userModel!=null)
            {
                UpdateUserLocation(location);
            }
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void LocationErrorListener(final LocationError locationError)
    {
        stopService(intentService);
        if (locationError.getStatus()==0)
        {
            StartService(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        }else if (locationError.getStatus()==1)
        {
            StartService(LocationRequest.PRIORITY_LOW_POWER);

        }
    }

    private void UpdateUserLocation(Location location)
    {
        Api.getService(Tags.base_url)
                .updateLocation(userModel.getData().getUser_id(),location.getLatitude(),location.getLongitude())
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful())
                        {
                            Log.e("Success","Location_updated");
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        try {
                            Log.e("Error",t.getMessage());
                        }catch (Exception e){}
                    }
                });
    }

    ///////////////////////////////////

    public void updateUserData(final UserModel userModel)
    {
        this.userModel = userModel;
        if (fragment_client_profile!=null && fragment_client_profile.isAdded())
        {
            new Handler()
                    .postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            fragment_client_profile.updateUI(userModel);
                        }
                    },1);
        }
    }
    ///////////////////////////////////
    public void DisplayFragmentHome()
    {
        fragment_count+=1;
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
    public void DisplayFragmentStore()
    {


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
    public void DisplayFragmentMyOrders()
    {
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
    public void DisplayFragmentNotification()
    {

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
    public void DisplayFragmentProfile()
    {
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
    public void DisplayFragmentSearch()
    {

        if (location!=null)
        {
            fragment_count+=1;

            fragment_search = Fragment_Search.newInstance(location.getLatitude(),location.getLongitude());



            if (fragment_search.isAdded()) {
                fragmentManager.beginTransaction().show(fragment_search).commit();

            } else {
                fragmentManager.beginTransaction().add(R.id.fragment_app_container, fragment_search, "fragment_search").addToBackStack("fragment_search").commit();
            }
        }


    }
    public void DisplayFragmentSettings()
    {

        fragment_count+=1;
        fragment_settings = Fragment_Settings.newInstance();



            if (fragment_settings.isAdded()) {
                fragmentManager.beginTransaction().show(fragment_settings).commit();

            } else {
                fragmentManager.beginTransaction().add(R.id.fragment_app_container, fragment_settings, "fragment_settings").addToBackStack("fragment_settings").commit();
            }



    }
    public void DisplayFragmentReserveOrder(PlaceModel placeModel)
    {


        fragment_count+=1;

        fragment_reserve_order = Fragment_Reserve_Order.newInstance(placeModel);

        if (fragment_reserve_order.isAdded()) {
            fragmentManager.beginTransaction().show(fragment_reserve_order).commit();

        } else {
            fragmentManager.beginTransaction().add(R.id.fragment_app_container, fragment_reserve_order, "fragment_reserve_order").addToBackStack("fragment_reserve_order").commit();
        }



    }
    public void DisplayFragmentMap()
    {
        fragment_count+=1;

        if (location!=null)
        {
            fragment_map = Fragment_Map.newInstance(location.getLatitude(),location.getLongitude());

        }else
            {
                fragment_map = Fragment_Map.newInstance(0.0,0.0);

            }

        if (fragment_map.isAdded()) {
            fragmentManager.beginTransaction().show(fragment_map).commit();

        } else {
            fragmentManager.beginTransaction().add(R.id.fragment_app_container, fragment_map, "fragment_map").addToBackStack("fragment_map").commit();
        }



    }
    public void DisplayFragmentEditProfile()
    {

        fragment_count+=1;

        fragment_edit_profile = Fragment_Edit_Profile.newInstance(this.userModel);


        if (fragment_edit_profile.isAdded()) {
            fragmentManager.beginTransaction().show(fragment_edit_profile).commit();

        } else {
            fragmentManager.beginTransaction().add(R.id.fragment_app_container, fragment_edit_profile, "fragment_edit_profile").addToBackStack("fragment_edit_profile").commit();
        }



    }
    public void DisplayFragmentStoreDetails(PlaceModel placeModel)
    {

        fragment_count+=1;



        fragment_store_details = Fragment_Store_Details.newInstance(placeModel,location.getLatitude(),location.getLongitude());

        if (fragment_store_details.isAdded()) {
            fragmentManager.beginTransaction().show(fragment_store_details).commit();

        } else {
            fragmentManager.beginTransaction().add(R.id.fragment_app_container, fragment_store_details, "fragment_store_details").addToBackStack("fragment_store_details").commit();
        }

    }
    public void getAddressFromMapListener(final Favourite_location favourite_location)
    {

        if (fragment_reserve_order!=null&&fragment_reserve_order.isAdded())
        {
            new Handler()
                    .postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            fragment_reserve_order.updateSelectedLocation(favourite_location);
                            fragmentManager.popBackStack("fragment_map",FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        }
                    },1);
        }
    }
    public void RefreshActivity(String lang)
    {
        Paper.book().write("lang",lang);
        Language_Helper.setNewLocale(this,lang);
        new Handler()
                .postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        Intent intent =  getIntent();
                        finish();
                        startActivity(intent);
                    }
                },1050);



    }
    public void NavigateToTermsActivity(int type)
    {
        Intent intent = new Intent(this, TermsConditionsActivity.class);
        intent.putExtra("type",type);
        startActivity(intent);
        if (current_lang.equals("ar"))
        {
            overridePendingTransition(R.anim.from_right,R.anim.to_left);

        }else
        {
            overridePendingTransition(R.anim.from_left,R.anim.to_right);

        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        List<Fragment> fragmentList = fragmentManager.getFragments();
        for (Fragment fragment : fragmentList) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }


        if (requestCode == 33) {
            if (isGpsOpen()) {
                StartService(LocationRequest.PRIORITY_LOW_POWER);
            } else {
                CreateGpsDialog();
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        List<Fragment> fragmentList = fragmentManager.getFragments();
        for (Fragment fragment : fragmentList) {
            fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

        if (requestCode == gps_req && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (isGpsOpen())
            {
                StartService(LocationRequest.PRIORITY_HIGH_ACCURACY);
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
                StartService(LocationRequest.PRIORITY_HIGH_ACCURACY);
            }else
                {
                    CreateGpsDialog();

                }
        }
    }
    private void StartService(int accuracy)
    {
        if (dialog == null)
        {
            dialog = Common.createProgressDialog(this,getString(R.string.fetching_your_location));
            dialog.setCancelable(true);
            dialog.show();
        }
        intentService = new Intent(this, UpdateLocationService.class);
        intentService.putExtra("accuracy",accuracy);
        startService(intentService);
    }
    private boolean isGpsOpen()
    {
        boolean isOpened = false;
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (manager != null) {
            if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)||manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                isOpened = true;
            }
        }

        return isOpened;
    }
    private void OpenGps()
    {
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

    public void Logout()
    {
        final ProgressDialog dialog =Common.createProgressDialog(this,getString(R.string.wait));
        dialog.show();
        Api.getService(Tags.base_url)
                .logOut(userModel.getData().getUser_id())
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        dialog.dismiss();
                        if (response.isSuccessful())
                        {
                            userSingleTone.clear(ClientHomeActivity.this);
                            Intent intent = new Intent(ClientHomeActivity.this, SignInActivity.class);
                            startActivity(intent);
                            finish();
                            if (current_lang.equals("ar"))
                            {
                                overridePendingTransition(R.anim.from_left,R.anim.to_right);



                            }else
                            {
                                overridePendingTransition(R.anim.from_right,R.anim.to_left);


                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                });
    }
    public void Back()
    {

        if (fragment_count>1)
        {
            super.onBackPressed();
            fragment_count-=1;

        }else
            {
                if (fragment_client_store!=null&&fragment_client_store.isVisible())
                {
                    NavigateToSignInActivity();
                }else
                {
                    DisplayFragmentStore();
                }
            }


    }

    public void NavigateToSignInActivity()
    {

        if (userModel!=null)
        {
            finish();
            if (current_lang.equals("ar"))
            {
                overridePendingTransition(R.anim.from_left,R.anim.to_right);



            }else
            {
                overridePendingTransition(R.anim.from_right,R.anim.to_left);


            }
        }else
        {
            Intent intent = new Intent(ClientHomeActivity.this, SignInActivity.class);
            startActivity(intent);
            finish();
            if (current_lang.equals("ar"))
            {
                overridePendingTransition(R.anim.from_left,R.anim.to_right);



            }else
            {
                overridePendingTransition(R.anim.from_right,R.anim.to_left);


            }
        }

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
