package com.purple.social.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;


import com.purple.social.R;
import com.purple.social.model.Message;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MesagesRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Message> myList;
    private LayoutInflater layoutInflater;
    private Context context;
    private FragmentManager fragmentManager;

    public MesagesRecyclerViewAdapter(Context context, List<Message> myList, FragmentManager fragmentManager) {
        this.layoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.myList = myList;
        this.fragmentManager = fragmentManager;

    }

    @Override
    public int getItemViewType(int position) {
        return myList.get(position).getTypeView();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case 1: {
                View v1 = inflater.inflate(R.layout.recycler_view_item_right_message_item, null);
                viewHolder = new RightMessage(v1);
                break;
            }
            case 2: {
                View v2 = inflater.inflate(R.layout.recycler_view_item_left_message_item, null);
                viewHolder = new LeftMessage(v2);
                break;
            }
            case 3: {
                View v3 = inflater.inflate(R.layout.item_empty, parent, false);
                viewHolder = new EmptyHolder(v3);
                break;
            }
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {

        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");


        switch (getItemViewType(position)) {
            case 1:
                final RightMessage rightMessage = (RightMessage) viewHolder;

                rightMessage.tv_message.setText(myList.get(position).getMessage());
                String hour = formatter.format(new Date(myList.get(position).getCreatedTime()));

                if (hour.equals("02:00")){
                    rightMessage.tv_time.setVisibility(View.GONE);
                }else {
                    rightMessage.tv_time.setVisibility(View.VISIBLE);
                    rightMessage.tv_time.setText(hour);
                }


                break;
            case 2: {
                final LeftMessage leftMessage = (LeftMessage) viewHolder;

                leftMessage.tv_message.setText(myList.get(position).getMessage());
                String hour1 = formatter.format(new Date(myList.get(position).getCreatedTime()));

                if (hour1.equals("02:00")){
                    leftMessage.tv_time.setVisibility(View.GONE);
                }else {
                    leftMessage.tv_time.setVisibility(View.VISIBLE);
                    leftMessage.tv_time.setText(hour1);
                }
                break;
            }
        }
    }

    private void updateResults(List<Message> myList) {
        this.myList = myList;
        notifyDataSetChanged();
    }

    public List<Message> getAdepterList() {
        return myList;
    }

    @Override
    public int getItemCount() {
        return myList.size();
    }

    public static class RightMessage extends RecyclerView.ViewHolder {
        public RelativeLayout ll_toolbar, ll_bottombar;
        public TextView tv_message, tv_time;

        public RightMessage(View itemView) {
            super(itemView);
            this.ll_toolbar = itemView.findViewById(R.id.ll_toolbar);
            this.tv_message = itemView.findViewById(R.id.tv_message);
            this.tv_time = itemView.findViewById(R.id.tv_time);
            this.ll_bottombar = itemView.findViewById(R.id.ll_bottombar);
        }
    }

    public static class LeftMessage extends RecyclerView.ViewHolder {
        public RelativeLayout ll_toolbar, ll_bottombar;
        public TextView tv_message, tv_time;

        public LeftMessage(View itemView) {
            super(itemView);
            this.ll_toolbar = itemView.findViewById(R.id.ll_toolbar);
            this.tv_message = itemView.findViewById(R.id.tv_message);
            this.tv_time = itemView.findViewById(R.id.tv_time);
            this.ll_bottombar = itemView.findViewById(R.id.ll_bottombar);
        }
    }

    public static class EmptyHolder extends RecyclerView.ViewHolder {
        public EmptyHolder(View itemView) {
            super(itemView);
        }
    }
}
