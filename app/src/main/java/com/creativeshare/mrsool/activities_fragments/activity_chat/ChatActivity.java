package com.creativeshare.mrsool.activities_fragments.activity_chat;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.creativeshare.mrsool.R;
import com.creativeshare.mrsool.activities_fragments.activity_home.client_home.activity.ClientHomeActivity;
import com.creativeshare.mrsool.adapters.ChatAdapter;
import com.creativeshare.mrsool.language.Language_Helper;
import com.creativeshare.mrsool.models.ChatUserModel;
import com.creativeshare.mrsool.models.MessageDataModel;
import com.creativeshare.mrsool.models.MessageModel;
import com.creativeshare.mrsool.models.TypingModel;
import com.creativeshare.mrsool.models.UserModel;
import com.creativeshare.mrsool.preferences.Preferences;
import com.creativeshare.mrsool.remote.Api;
import com.creativeshare.mrsool.singletone.UserSingleTone;
import com.creativeshare.mrsool.tags.Tags;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {

    private TextView tv_name;
    private CircleImageView image;
    private ImageView image_call,image_send,image_back;
    private EditText edt_msg_content;
    private LinearLayout ll_back,ll_user_data_container;
    private AppBarLayout app_bar;
    private RecyclerView recView;
    private LinearLayoutManager manager;
    private ProgressBar progBar;
    private List<MessageModel> messageModelList;
    private ChatAdapter adapter;
    private int current_page = 1;
    private boolean isLoading = false;
    private UserSingleTone userSingleTone;
    private UserModel userModel;
    private ChatUserModel chatUserModel;
    private String current_language;
    private Preferences preferences;
    private boolean canSendTyping = true;
    private boolean from = true;
    private MediaPlayer mp;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(Language_Helper.updateResources(base,Language_Helper.getLanguage(base)));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initView();
        getDataFromIntent();
    }

    private void getDataFromIntent()
    {
        Intent intent = getIntent();
        if (intent!=null)
        {
            chatUserModel = (ChatUserModel) intent.getSerializableExtra("data");
            Log.e("room_id",chatUserModel.getRoom_id()+"_____");
            preferences.saveChatUserData(this,chatUserModel);
            UpdateUI(chatUserModel);
            if (intent.hasExtra("from"))
            {
                from = true;
            }else
                {
                    from = false;
                }
        }
    }
    private void initView()
    {
        EventBus.getDefault().register(this);
        messageModelList = new ArrayList<>();
        Paper.init(this);
        current_language = Paper.book().read("lang", Locale.getDefault().getLanguage());
        preferences = Preferences.getInstance();

        userSingleTone = UserSingleTone.getInstance();
        userModel = userSingleTone.getUserModel();

        image_back = findViewById(R.id.image_back);
        if (current_language.equals("ar")) {
            image_back.setImageResource(R.drawable.ic_right_arrow);
        } else {
            image_back.setImageResource(R.drawable.ic_left_arrow);

        }

        ll_user_data_container = findViewById(R.id.ll_user_data_container);
        app_bar = findViewById(R.id.app_bar);


        progBar = findViewById(R.id.progBar);
        progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);

        tv_name = findViewById(R.id.tv_name);
        image = findViewById(R.id.image);
        image_call =findViewById(R.id.image_call);
        image_send =findViewById(R.id.image_send);
        edt_msg_content = findViewById(R.id.edt_msg_content);
        ll_back = findViewById(R.id.ll_back);


        recView = findViewById(R.id.recView);
        recView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recView.setDrawingCacheEnabled(true);
        recView.setItemViewCacheSize(25);
        manager = new LinearLayoutManager(this);
        manager.setStackFromEnd(true);
        recView.setLayoutManager(manager);
        recView.setNestedScrollingEnabled(true);
        recView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy>0)
                {

                    int lastVisibleItemPos = manager.findLastCompletelyVisibleItemPosition();
                    int total_item = recView.getAdapter().getItemCount();

                    if (lastVisibleItemPos >= (total_item-5)&&!isLoading)
                    {
                        int next_page = current_page+1;
                        messageModelList.add(null);
                        adapter.notifyItemInserted(messageModelList.size()-1);
                        isLoading = true;
                        loadMore(next_page);
                    }
                }
            }
        });



        ll_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Back();
            }
        });

        image_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+chatUserModel.getPhone()));
                startActivity(intent);
            }
        });

        image_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = edt_msg_content.getText().toString().trim();
                if (!TextUtils.isEmpty(msg))
                {
                    edt_msg_content.setText("");
                    canSendTyping = true;
                    updateTyingState(Tags.END_TYPING);
                    SendMessage(msg);
                }
            }
        });

        edt_msg_content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                String msg = edt_msg_content.getText().toString();
                if (msg.length()>0)
                {
                    if (canSendTyping)
                    {
                        canSendTyping = false;
                        updateTyingState(Tags.START_TYPING);
                    }
                }else
                {
                    canSendTyping = true;
                    updateTyingState(Tags.END_TYPING);

                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        app_bar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                int totalRang = appBarLayout.getTotalScrollRange();

                if ((totalRang + verticalOffset) <= 50) {

                    ll_user_data_container.setVisibility(View.GONE);
                    image_call.setVisibility(View.GONE);
                } else {

                    ll_user_data_container.setVisibility(View.VISIBLE);
                    image_call.setVisibility(View.VISIBLE);

                }
            }
        });



    }

    ///////////////////////////////////////////////
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void ListenToTyping(TypingModel typingModel)
    {
        if (typingModel.getTyping_value().equals("1"))
        {
            if (adapter==null)
            {
                adapter = new ChatAdapter(messageModelList,userModel.getData().getUser_id(),chatUserModel.getImage(),ChatActivity.this);

                recView.setAdapter(adapter);
            }
            adapter.startTyping(true);
            new MyAsyncTask().execute();

        }else if (typingModel.getTyping_value().equals("2"))
        {
            if (adapter==null)
            {
                adapter = new ChatAdapter(messageModelList,userModel.getData().getUser_id(),chatUserModel.getImage(),ChatActivity.this);
                recView.setAdapter(adapter);

            }

            adapter.endTyping();
            if (mp!=null)
            {
                mp.release();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void ListenToNewMessage(MessageModel messageModel)
    {
        if (adapter==null)
        {
            messageModelList.add(messageModel);
            adapter = new ChatAdapter(messageModelList,userModel.getData().getUser_id(),chatUserModel.getImage(),ChatActivity.this);
            recView.setAdapter(adapter);


        }else
            {
                messageModelList.add(messageModel);
                adapter.notifyItemInserted(messageModelList.size()-1);

            }
    }

    ///////////////////////////////////////////////
    private void updateTyingState(int typingState)
    {

        Api.getService(Tags.base_url)
                .typing(chatUserModel.getRoom_id(),userModel.getData().getUser_id(),chatUserModel.getId(),typingState)
                .enqueue(new Callback<MessageModel>() {
                    @Override
                    public void onResponse(Call<MessageModel> call, Response<MessageModel> response) {

                        if (response.isSuccessful())
                        {
                            Log.e("success typing","true");
                        }else
                        {
                            try {
                                Log.e("Error_code",response.code()+"_"+response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<MessageModel> call, Throwable t) {
                        try {
                            Log.e("Error",t.getMessage());
                        }catch (Exception e){}
                    }
                });
    }
    private void UpdateUI(ChatUserModel chatUserModel)
    {
        preferences.saveChatUserData(this,chatUserModel);
        tv_name.setText(chatUserModel.getName());
        Picasso.with(this).load(Uri.parse(Tags.IMAGE_URL+chatUserModel.getImage())).placeholder(R.drawable.logo_only).fit().into(image);

        getChatMessages();
    }
    private void SendMessage(String msg)
    {
        Api.getService(Tags.base_url)
                .sendMessage(chatUserModel.getRoom_id(),userModel.getData().getUser_id(),chatUserModel.getId(),msg)
                .enqueue(new Callback<MessageModel>() {
                    @Override
                    public void onResponse(Call<MessageModel> call, Response<MessageModel> response) {
                        progBar.setVisibility(View.GONE);
                        if (response.isSuccessful())
                        {
                            if (adapter==null)
                            {
                                messageModelList.add(response.body());
                                adapter.notifyItemInserted(messageModelList.size()-1);

                            }else
                                {
                                    messageModelList.add(response.body());
                                    adapter.notifyItemInserted(messageModelList.size()-1);

                                }

                        }else
                        {

                            try {
                                Log.e("Error_code",response.code()+"_"+response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<MessageModel> call, Throwable t) {
                        try {
                            Log.e("Error",t.getMessage());
                        }catch (Exception e){}
                    }
                });
    }
    private void getChatMessages()
    {
        Api.getService(Tags.base_url)
                .getChatMessages(chatUserModel.getRoom_id(),1)
                .enqueue(new Callback<MessageDataModel>() {
                    @Override
                    public void onResponse(Call<MessageDataModel> call, Response<MessageDataModel> response) {
                        progBar.setVisibility(View.GONE);
                        if (response.isSuccessful())
                        {

                            if (response.body()!=null&&response.body().getData().size()>0)
                            {
                                messageModelList.addAll(response.body().getData());
                                adapter = new ChatAdapter(messageModelList,userModel.getData().getUser_id(),chatUserModel.getImage(),ChatActivity.this);
                                recView.setAdapter(adapter);


                            }
                        }else
                        {

                            Toast.makeText(ChatActivity.this,R.string.failed, Toast.LENGTH_SHORT).show();
                            try {
                                Log.e("Error_code",response.code()+"_"+response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<MessageDataModel> call, Throwable t) {
                        try {
                            progBar.setVisibility(View.GONE);
                            Toast.makeText(ChatActivity.this, getString(R.string.something), Toast.LENGTH_SHORT).show();
                            Log.e("Error",t.getMessage());
                        }catch (Exception e){}
                    }
                });
    }
    private void loadMore(int next_page)
    {
        Api.getService(Tags.base_url)
                .getChatMessages(chatUserModel.getRoom_id(),next_page)
                .enqueue(new Callback<MessageDataModel>() {
                    @Override
                    public void onResponse(Call<MessageDataModel> call, Response<MessageDataModel> response) {
                        if (response.isSuccessful())
                        {
                            messageModelList.remove(messageModelList.size()-1);
                            adapter.notifyDataSetChanged();
                            if (response.body()!=null&&response.body().getData().size()>0)
                            {

                                int old_lastPos = messageModelList.size()-1;


                                messageModelList.addAll(response.body().getData());
                                current_page = response.body().getMeta().getCurrent_page();

                                adapter.notifyItemRangeInserted(old_lastPos,messageModelList.size()-1);


                            }
                            isLoading = false;

                        }else
                        {
                            isLoading = false;
                            messageModelList.remove(messageModelList.size()-1);
                            adapter.notifyDataSetChanged();

                            try {
                                Log.e("Error_code",response.code()+"_"+response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<MessageDataModel> call, Throwable t) {
                        try {
                            isLoading = false;
                            messageModelList.remove(messageModelList.size()-1);
                            adapter.notifyDataSetChanged();
                            Toast.makeText(ChatActivity.this, getString(R.string.something), Toast.LENGTH_SHORT).show();
                            Log.e("Error",t.getMessage());
                        }catch (Exception e){}
                    }
                });
    }



    private void Back()
    {
        if (from)
        {
            finish();
        }else
        {
            Intent intent = new Intent(ChatActivity.this, ClientHomeActivity.class);
            startActivity(intent);
            finish();
        }
    }
    @Override
    public void onBackPressed()
    {
        Back();
    }
    @Override
    public void onDestroy()
    {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
        preferences.clearChatUserData(ChatActivity.this);
        if (mp!=null)
        {
            mp.release();
        }

    }


    public class MyAsyncTask extends AsyncTask<Void,Void,Void>{
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            String sound_typing_path = "android.resource://"+getPackageName()+"/"+R.raw.typing;

            mp = MediaPlayer.create(ChatActivity.this,Uri.parse(sound_typing_path));

        }

        @Override
        protected Void doInBackground(Void... voids) {
            mp.start();
            mp.setLooping(false);
            return null;
        }
    }

}
