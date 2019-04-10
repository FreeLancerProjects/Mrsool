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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.creativeshare.mrsool.R;
import com.creativeshare.mrsool.activities_fragments.activity_home.client_home.activity.ClientHomeActivity;
import com.creativeshare.mrsool.adapters.DelegatesAdapter;
import com.creativeshare.mrsool.models.NearDelegateDataModel;
import com.creativeshare.mrsool.remote.Api;
import com.creativeshare.mrsool.tags.Tags;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_Delegates extends Fragment {
    private static final String TAG2 = "LAT";
    private static final String TAG3 = "LNG";
    private ClientHomeActivity activity;
    private ImageView arrow;
    private LinearLayout ll_back;
    private RecyclerView recView;
    private LinearLayoutManager manager;
    private List<NearDelegateDataModel.DelegateModel> delegateModelList;
    private DelegatesAdapter adapter;
    private ProgressBar progBar;
    private double lat = 0.0, lng = 0.0;
    private String current_language;
    private int current_page = 0;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_delegates, container, false);
        initView(view);
        return view;
    }

    public static Fragment_Delegates newInstance(double lat, double lng) {
        Fragment_Delegates fragment_delegates = new Fragment_Delegates();
        Bundle bundle = new Bundle();
        bundle.putDouble(TAG2, lat);
        bundle.putDouble(TAG3, lng);
        fragment_delegates.setArguments(bundle);
        return fragment_delegates;
    }

    private void initView(View view) {
        activity = (ClientHomeActivity) getActivity();
        Paper.init(activity);
        current_language = Paper.book().read("lang", Locale.getDefault().getLanguage());
        delegateModelList = new ArrayList<>();

        Bundle bundle = getArguments();
        if (bundle != null) {
            lat = bundle.getDouble(TAG2);
            lng = bundle.getDouble(TAG3);
        }

        arrow = view.findViewById(R.id.arrow);

        if (current_language.equals("ar")) {
            arrow.setImageResource(R.drawable.ic_right_arrow);
        } else {
            arrow.setImageResource(R.drawable.ic_right_arrow);

        }

        ll_back = view.findViewById(R.id.ll_back);

        recView = view.findViewById(R.id.recView);
        manager = new LinearLayoutManager(activity);
        recView.setLayoutManager(manager);
        adapter = new DelegatesAdapter(delegateModelList, activity, this);
        recView.setAdapter(adapter);
        recView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy>0)
                {

                    int lastVisibleItemPos = manager.findLastCompletelyVisibleItemPosition();
                    int total_item = recView.getAdapter().getItemCount();

                    if (lastVisibleItemPos >= (total_item-5))
                    {
                        int next_page = current_page+1;
                        loadMore(next_page);
                    }
                }
            }
        });

        progBar = view.findViewById(R.id.progBar);
        progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(activity, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);

        getDelegates();

        ll_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.Back();
            }
        });

    }

    private void getDelegates() {
        Api.getService(Tags.base_url)
                .getDelegate(lat, lng, 1)
                .enqueue(new Callback<NearDelegateDataModel>() {
                    @Override
                    public void onResponse(Call<NearDelegateDataModel> call, Response<NearDelegateDataModel> response) {
                        progBar.setVisibility(View.GONE);
                        if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                            delegateModelList.addAll(response.body().getData());
                            adapter.notifyDataSetChanged();
                        } else {
                            try {
                                Toast.makeText(activity, R.string.failed, Toast.LENGTH_SHORT).show();
                                Log.e("Error_code", response.code() + "" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                    }

                    @Override
                    public void onFailure(Call<NearDelegateDataModel> call, Throwable t) {
                        try {
                            progBar.setVisibility(View.GONE);
                            Toast.makeText(activity, R.string.something, Toast.LENGTH_SHORT).show();
                            Log.e("Error", t.getMessage());
                        } catch (Exception e) {

                        }
                    }
                });
    }


    private void loadMore(int page_index) {
        Api.getService(Tags.base_url)
                .getDelegate(lat, lng, page_index)
                .enqueue(new Callback<NearDelegateDataModel>() {
                    @Override
                    public void onResponse(Call<NearDelegateDataModel> call, Response<NearDelegateDataModel> response) {
                        progBar.setVisibility(View.GONE);
                        if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                            delegateModelList.remove(delegateModelList.size() - 1);
                            int old_pos = delegateModelList.size() - 1;

                            if (response.body().getData().size() > 0) {

                                current_page = response.body().getMeta().getCurrent_page();
                                delegateModelList.addAll(response.body().getData());

                                adapter.notifyItemRangeInserted(old_pos,delegateModelList.size());
                            }else
                                {
                                    adapter.notifyDataSetChanged();
                                }



                        } else {
                            try {
                                Toast.makeText(activity, R.string.failed, Toast.LENGTH_SHORT).show();
                                Log.e("Error_code", response.code() + "" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                    }

                    @Override
                    public void onFailure(Call<NearDelegateDataModel> call, Throwable t) {
                        try {
                            progBar.setVisibility(View.GONE);
                            Toast.makeText(activity, R.string.something, Toast.LENGTH_SHORT).show();
                            Log.e("Error", t.getMessage());
                        } catch (Exception e) {

                        }
                    }
                });
    }

    public void setItemData(NearDelegateDataModel.DelegateModel delegateModel) {
        activity.setDelegate_id(delegateModel.getDriver_id());
    }
}
