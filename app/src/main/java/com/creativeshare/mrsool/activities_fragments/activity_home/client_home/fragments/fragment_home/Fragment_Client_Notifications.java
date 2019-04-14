package com.creativeshare.mrsool.activities_fragments.activity_home.client_home.fragments.fragment_home;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.creativeshare.mrsool.R;
import com.creativeshare.mrsool.activities_fragments.activity_home.client_home.activity.ClientHomeActivity;
import com.creativeshare.mrsool.adapters.NotificationsAdapter;
import com.creativeshare.mrsool.models.NotificationDataModel;
import com.creativeshare.mrsool.models.NotificationModel;
import com.creativeshare.mrsool.models.UserModel;
import com.creativeshare.mrsool.remote.Api;
import com.creativeshare.mrsool.singletone.UserSingleTone;
import com.creativeshare.mrsool.tags.Tags;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_Client_Notifications extends Fragment{

    private ProgressBar progBar;
    private RecyclerView recView;
    private RecyclerView.LayoutManager manager;
    private ClientHomeActivity activity;
    private LinearLayout ll_not;
    private UserModel userModel;
    private List<NotificationModel> notificationModelList;
    private NotificationsAdapter adapter;
    private UserSingleTone userSingleTone;
    private boolean isLoading = false;
    private int current_page = 1;
    private Call<NotificationDataModel> call;
    private boolean isFirstTime = true;


    @Override
    public void onStart()
    {
        super.onStart();
        if (!isFirstTime&&adapter!=null)
        {
            adapter.notifyDataSetChanged();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_client_notifications,container,false);
        initView(view);
        return view;
    }


    public static Fragment_Client_Notifications newInstance()
    {
        return new Fragment_Client_Notifications();
    }
    private void initView(View view) {
        notificationModelList = new ArrayList<>();

        activity = (ClientHomeActivity) getActivity();
        userSingleTone = UserSingleTone.getInstance();
        userModel = userSingleTone.getUserModel();
        ll_not = view.findViewById(R.id.ll_not);

        progBar = view.findViewById(R.id.progBar);
        progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(activity,R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        recView = view.findViewById(R.id.recView);
        adapter = new NotificationsAdapter(notificationModelList,activity,this,userModel.getData().getUser_type());
        recView.setAdapter(adapter);

        recView.setDrawingCacheEnabled(true);
        recView.setItemViewCacheSize(25);
        recView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        manager = new LinearLayoutManager(activity);
        recView.setLayoutManager(manager); recView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy>0)
                {
                    int lastVisibleItem = ((LinearLayoutManager)manager).findLastCompletelyVisibleItemPosition();
                    int totalItems = manager.getItemCount();

                    if (lastVisibleItem>=(totalItems-5)&&!isLoading)
                    {
                        isLoading = true;
                        notificationModelList.add(null);
                        adapter.notifyItemInserted(notificationModelList.size()-1);
                        int next_page = current_page+1;
                        loadMore(next_page);
                    }
                }
            }
        });
        getNotification();

    }

    public void getNotification()
    {
        notificationModelList.clear();


        if (userModel.getData().getUser_type().equals(Tags.TYPE_CLIENT))
        {
            call  = Api.getService(Tags.base_url).getNotification(userModel.getData().getUser_id(),"client", 1);
        }else if (userModel.getData().getUser_type().equals(Tags.TYPE_DELEGATE))
        {
            call  = Api.getService(Tags.base_url).getNotification(userModel.getData().getUser_id(),"driver", 1);

        }


        call.enqueue(new Callback<NotificationDataModel>() {
            @Override
            public void onResponse(Call<NotificationDataModel> call, Response<NotificationDataModel> response) {
                progBar.setVisibility(View.GONE);
                if (response.isSuccessful())
                {

                    if (response.body()!=null&&response.body().getData().size()>0)
                    {
                        ll_not.setVisibility(View.GONE);
                        notificationModelList.addAll(response.body().getData());
                        adapter.notifyDataSetChanged();
                        isFirstTime = false;
                    }else
                    {
                        ll_not.setVisibility(View.VISIBLE);

                    }
                }else
                {

                    Toast.makeText(activity,R.string.failed, Toast.LENGTH_SHORT).show();
                    try {
                        Log.e("Error_code",response.code()+"_"+response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<NotificationDataModel> call, Throwable t) {
                try {
                    progBar.setVisibility(View.GONE);
                    Toast.makeText(activity, getString(R.string.something), Toast.LENGTH_SHORT).show();
                    Log.e("Error",t.getMessage());
                }catch (Exception e){}
            }
        });


    }

    private void loadMore(int page_index)
    {

        if (userModel.getData().getUser_type().equals(Tags.TYPE_CLIENT))
        {
            call  = Api.getService(Tags.base_url).getNotification(userModel.getData().getUser_id(),"client", page_index);
        }else if (userModel.getData().getUser_type().equals(Tags.TYPE_DELEGATE))
        {
            call  = Api.getService(Tags.base_url).getNotification(userModel.getData().getUser_id(),"driver", page_index);

        }


        call.enqueue(new Callback<NotificationDataModel>() {
            @Override
            public void onResponse(Call<NotificationDataModel> call, Response<NotificationDataModel> response) {
                notificationModelList.remove(notificationModelList.size()-1);
                adapter.notifyDataSetChanged();
                isLoading = false;

                if (response.isSuccessful())
                {

                    if (response.body()!=null&&response.body().getData().size()>0)
                    {
                        notificationModelList.addAll(response.body().getData());
                        adapter.notifyDataSetChanged();
                        current_page = response.body().getMeta().getCurrent_page();


                    }
                }else
                {


                    Toast.makeText(activity,R.string.failed, Toast.LENGTH_SHORT).show();
                    try {
                        Log.e("Error_code",response.code()+"_"+response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<NotificationDataModel> call, Throwable t) {
                try {
                    isLoading = false;
                    if (notificationModelList.get(notificationModelList.size()-1)==null)
                    {
                        notificationModelList.remove(notificationModelList.size()-1);
                        adapter.notifyDataSetChanged();
                    }
                    progBar.setVisibility(View.GONE);
                    Toast.makeText(activity, getString(R.string.something), Toast.LENGTH_SHORT).show();
                    Log.e("Error",t.getMessage());
                }catch (Exception e){}
            }
        });

    }

    public void setItemData(NotificationModel notificationModel) {
        activity.DisplayFragmentClientDelegateOffer(notificationModel);
    }
}
