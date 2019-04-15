package com.creativeshare.mrsool.adapters;

import android.content.Context;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.creativeshare.mrsool.R;
import com.creativeshare.mrsool.models.MessageModel;
import com.creativeshare.mrsool.tags.Tags;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;
import pl.tajchert.waitingdots.DotsTextView;

public class ChatAdapter extends RecyclerView.Adapter {

    private final int ITEM_MESSAGE_LEFT = 1;
    private final int ITEM_MESSAGE_RIGHT = 2;
    private final int ITEM_LOAD_MORE = 3;
    private final int ITEM_TYPING = 4;
    private boolean isTyping = false;

    private List<MessageModel> messageModelList;
    private String current_user_id;
    private String chat_user_image;
    private Context context;

    public ChatAdapter(List<MessageModel> messageModelList, String current_user_id, String chat_user_image, Context context) {
        this.messageModelList = messageModelList;
        this.current_user_id = current_user_id;
        this.chat_user_image = chat_user_image;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_MESSAGE_LEFT) {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_message_left_row, parent, false);
            return new MsgLeftHolder(view);
        } else if (viewType == ITEM_MESSAGE_RIGHT) {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_message_right_row, parent, false);
            return new MsgRightHolder(view);
        } else if (viewType == ITEM_LOAD_MORE) {
            View view = LayoutInflater.from(context).inflate(R.layout.load_more_row, parent, false);
            return new MsgRightHolder(view);
        }else
            {
                View view = LayoutInflater.from(context).inflate(R.layout.typing_row,parent,false);
                return new MsgRightHolder(view);
            }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        MessageModel messageModel = messageModelList.get(holder.getAdapterPosition());
        if (holder instanceof MsgLeftHolder) {
            MsgLeftHolder msgLeftHolder = (MsgLeftHolder) holder;
            msgLeftHolder.BindData(messageModel);

        } else if (holder instanceof MsgRightHolder) {
            MsgRightHolder msgRightHolder = (MsgRightHolder) holder;
            msgRightHolder.BindData(messageModel);
        }
        else if (holder instanceof LoadMoreHolder) {
            LoadMoreHolder loadMoreHolder = (LoadMoreHolder) holder;
            loadMoreHolder.progressBar.setIndeterminate(true);

        }
        else if (holder instanceof TypingHolder) {
            TypingHolder typingHolder = (TypingHolder) holder;
            typingHolder.tv_wait_dot.show();
            typingHolder.tv_wait_dot.start();

        }
    }

    @Override
    public int getItemCount() {
        return messageModelList.size();
    }

    public class MsgLeftHolder extends RecyclerView.ViewHolder {
        private CircleImageView image;
        private TextView tv_message_content, tv_time;

        public MsgLeftHolder(View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.image);
            tv_message_content = itemView.findViewById(R.id.tv_message_content);
            tv_time = itemView.findViewById(R.id.tv_time);

        }


        public void BindData(MessageModel messageModel) {

            Picasso.with(context).load(Tags.IMAGE_URL + chat_user_image).fit().priority(Picasso.Priority.HIGH).into(image);
            tv_message_content.setText(messageModel.getMessage());

            Paper.init(context);
            String lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
            SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm aa", new Locale(lang));
            String msg_time = dateFormat.format(new Date(Long.parseLong(messageModel.getDate()) * 1000));
            tv_time.setText(msg_time);
        }
    }

    public class MsgRightHolder extends RecyclerView.ViewHolder {
        private TextView tv_message_content, tv_time;

        public MsgRightHolder(View itemView) {
            super(itemView);
            tv_message_content = itemView.findViewById(R.id.tv_message_content);
            tv_time = itemView.findViewById(R.id.tv_time);
        }

        public void BindData(MessageModel messageModel) {

            tv_message_content.setText(messageModel.getMessage());

            Paper.init(context);
            String lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
            SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm aa", new Locale(lang));
            String msg_time = dateFormat.format(new Date(Long.parseLong(messageModel.getDate()) * 1000));
            tv_time.setText(msg_time);
        }
    }

    public class LoadMoreHolder extends RecyclerView.ViewHolder {
        private ProgressBar progressBar;

        public LoadMoreHolder(View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progBar);
            progressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        }


    }

    public class TypingHolder extends RecyclerView.ViewHolder {
        private CircleImageView image;
        private DotsTextView tv_wait_dot;

        public TypingHolder(View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.image);
            tv_wait_dot = itemView.findViewById(R.id.tv_wait_dot);

            Picasso.with(context).load(Uri.parse(Tags.IMAGE_URL + chat_user_image)).fit().into(image);
            tv_wait_dot.setPeriod(1000);
        }


    }


    public void startTyping(boolean isTyping) {
        this.isTyping = isTyping;
        notifyDataSetChanged();
    }

    public void endTyping() {
        this.isTyping = false;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (messageModelList.size()>0)
        {
            MessageModel messageModel = messageModelList.get(position);
            if (messageModel == null) {
                return ITEM_LOAD_MORE;
            } else if (isTyping) {
                return ITEM_TYPING;

            } else if (messageModel.getTo_user().equals(current_user_id)) {
                return ITEM_MESSAGE_RIGHT;
            } else {
                return ITEM_MESSAGE_RIGHT;
            }
        }else
            {
                return ITEM_TYPING;

            }


    }
}
