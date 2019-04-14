package com.creativeshare.mrsool.activities_fragments.activity_home.client_home.fragments.fragment_home;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.creativeshare.mrsool.R;
import com.creativeshare.mrsool.activities_fragments.activity_home.client_home.activity.ClientHomeActivity;
import com.creativeshare.mrsool.models.ChatUserModel;
import com.creativeshare.mrsool.models.UserModel;
import com.creativeshare.mrsool.preferences.Preferences;
import com.creativeshare.mrsool.singletone.UserSingleTone;
import com.creativeshare.mrsool.tags.Tags;
import com.squareup.picasso.Picasso;

import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class Fragment_Chat extends Fragment {
    private static final String TAG="DATA";
    private ClientHomeActivity activity;
    private TextView tv_name;
    private CircleImageView image;
    private ImageView image_call,image_send,image_back;
    private EditText edt_msg_content;
    private LinearLayout ll_back;
    private RecyclerView recView;
    private LinearLayoutManager manager;
    private ProgressBar progBar;
    private int current_page = 1;
    private boolean isLoading = false;
    private UserSingleTone userSingleTone;
    private UserModel userModel;
    private ChatUserModel chatUserModel;
    private String current_language;
    private Preferences preferences;

    public static Fragment_Chat newInstance(ChatUserModel chatUserModel)
    {
        Fragment_Chat fragment_chat = new Fragment_Chat();
        Bundle bundle = new Bundle();
        bundle.putSerializable(TAG,chatUserModel);
        fragment_chat.setArguments(bundle);
        return fragment_chat;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat,container,false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        activity = (ClientHomeActivity) getActivity();
        Paper.init(activity);
        current_language = Paper.book().read("lang",Locale.getDefault().getLanguage());
        preferences = Preferences.getInstance();

        userSingleTone = UserSingleTone.getInstance();
        userModel = userSingleTone.getUserModel();

        image_back = view.findViewById(R.id.image_back);
        if (current_language.equals("ar"))
        {
            image_back.setImageResource(R.drawable.ic_right_arrow);
        }else
            {
                image_back.setImageResource(R.drawable.ic_left_arrow);

            }

        progBar = view.findViewById(R.id.progBar);
        progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(activity, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);

        tv_name = view.findViewById(R.id.tv_name);
        image = view.findViewById(R.id.image);
        image_call = view.findViewById(R.id.image_call);
        image_send = view.findViewById(R.id.image_send);
        edt_msg_content = view.findViewById(R.id.edt_msg_content);
        ll_back = view.findViewById(R.id.ll_back);


        recView = view.findViewById(R.id.recView);
        recView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recView.setDrawingCacheEnabled(true);
        recView.setItemViewCacheSize(25);
        manager = new LinearLayoutManager(activity);
        recView.setLayoutManager(manager);
        //recView.setAdapter(adapter);
        recView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy>0)
                {

                   /* int lastVisibleItemPos = manager.findLastCompletelyVisibleItemPosition();
                    int total_item = recView.getAdapter().getItemCount();

                    if (lastVisibleItemPos >= (total_item-5)&&!isLoading)
                    {
                        int next_page = current_page+1;
                        commentModelList.add(null);
                        adapter.notifyItemInserted(commentModelList.size()-1);
                        isLoading = true;
                        loadMore(next_page);
                    }*/
                }
            }
        });

        Bundle bundle = getArguments();
        if (bundle!=null)
        {
            chatUserModel = (ChatUserModel) bundle.getSerializable(TAG);
            UpdateUI(chatUserModel);
        }

        ll_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.Back();

            }
        });

        image_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL,Uri.parse("tel:"+chatUserModel.getPhone_code()+chatUserModel.getPhone()));
                startActivity(intent);
            }
        });

        image_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               String msg = edt_msg_content.getText().toString().trim();
               if (!TextUtils.isEmpty(msg))
               {
                   SendMessage(msg);
               }
            }
        });


    }



    private void UpdateUI(ChatUserModel chatUserModel) {
        preferences.saveChatUserData(activity,chatUserModel);
        tv_name.setText(chatUserModel.getName());
        Picasso.with(activity).load(Uri.parse(Tags.IMAGE_URL+chatUserModel.getImage())).placeholder(R.drawable.logo_only).fit().into(image);

    }

    private void SendMessage(String msg) {

    }



    /*private void getComments() {
        Api.getService(Tags.base_url)
                .getDelegateComment(userModel.getData().getUser_id(),"driver",1)
                .enqueue(new Callback<CommentDataModel>() {
                    @Override
                    public void onResponse(Call<CommentDataModel> call, Response<CommentDataModel> response) {
                        progBar.setVisibility(View.GONE);
                        if (response.isSuccessful())
                        {

                            if (response.body()!=null&&response.body().getData().size()>0)
                            {
                                tv_no_comments.setVisibility(View.GONE);
                                commentModelList.addAll(response.body().getData());
                            }else
                                {
                                    tv_no_comments.setVisibility(View.VISIBLE);


                                }
                                adapter.notifyDataSetChanged();
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
                    public void onFailure(Call<CommentDataModel> call, Throwable t) {
                        try {
                            progBar.setVisibility(View.GONE);
                            Toast.makeText(activity, getString(R.string.something), Toast.LENGTH_SHORT).show();
                            Log.e("Error",t.getMessage());
                        }catch (Exception e){}
                    }
                });
    }

    private void loadMore(int next_page)
    {
        Api.getService(Tags.base_url)
                .getDelegateComment(userModel.getData().getUser_id(),"driver",next_page)
                .enqueue(new Callback<CommentDataModel>() {
                    @Override
                    public void onResponse(Call<CommentDataModel> call, Response<CommentDataModel> response) {
                        if (response.isSuccessful())
                        {
                            commentModelList.remove(commentModelList.size()-1);
                            adapter.notifyDataSetChanged();
                            if (response.body()!=null&&response.body().getData().size()>0)
                            {

                                int old_lastPos = commentModelList.size()-1;


                                commentModelList.addAll(response.body().getData());
                                current_page = response.body().getMeta().getCurrent_page();

                                adapter.notifyItemRangeInserted(old_lastPos,commentModelList.size()-1);


                            }
                            isLoading = false;

                        }else
                        {
                            isLoading = false;
                            commentModelList.remove(commentModelList.size()-1);
                            adapter.notifyDataSetChanged();

                            try {
                                Log.e("Error_code",response.code()+"_"+response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<CommentDataModel> call, Throwable t) {
                        try {
                            isLoading = false;
                            commentModelList.remove(commentModelList.size()-1);
                            adapter.notifyDataSetChanged();
                            Toast.makeText(activity, getString(R.string.something), Toast.LENGTH_SHORT).show();
                            Log.e("Error",t.getMessage());
                        }catch (Exception e){}
                    }
                });
    }*/


    @Override
    public void onDestroy()
    {
        super.onDestroy();
        preferences.clearChatUserData(activity);
    }
}
