package com.creativeshare.mrsool.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.creativeshare.mrsool.R;
import com.creativeshare.mrsool.activities_fragments.activity_home.client_home.fragments.fragment_home.Fragment_Client_Store;

import java.util.List;

public class QueryAdapter extends RecyclerView.Adapter<QueryAdapter.MyHolder> {

    private List<String> queryList;
    private Context context;
    private Fragment_Client_Store fragment;
    private int lastSelectedPos = 0;
    private SparseBooleanArray booleanArray;
    public QueryAdapter(List<String> queryList, Context context, Fragment_Client_Store fragment) {
        this.queryList = queryList;
        this.context = context;
        this.fragment = fragment;
        booleanArray = new SparseBooleanArray();
        booleanArray.put(0,true);

    }


    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.query_row, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder holder, int position) {

        String query = queryList.get(position);
        holder.BindData(query);

        if (booleanArray.get(position,false))
        {
            holder.tv_query.setBackgroundResource(R.drawable.tv_query_selected_bg);
        }else
            {
                holder.tv_query.setBackgroundResource(R.drawable.tv_query_unselected_bg);
            }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lastSelectedPos = holder.getAdapterPosition();
                booleanArray.clear();
                booleanArray.put(lastSelectedPos,true);
                fragment.setQueryItemData(holder.getAdapterPosition());
                notifyDataSetChanged();

            }
        });
    }

    @Override
    public int getItemCount() {
        return queryList.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        private TextView tv_query;

        public MyHolder(View itemView) {
            super(itemView);

            tv_query = itemView.findViewById(R.id.tv_query);



        }

        public void BindData(String query) {

            tv_query.setText(query);
        }
    }
}
