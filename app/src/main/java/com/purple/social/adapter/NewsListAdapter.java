package com.purple.social.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.purple.social.R;
import com.purple.social.model.News;

import java.util.List;

public class NewsListAdapter extends BaseAdapter {

    private LayoutInflater layoutInflater;
    private Activity context;
    private List<News> myList;
    private String urlSting;

    public NewsListAdapter(Activity context, List<News> myList) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.myList = myList;
        this.context = context;
    }

    public List<News> getMyList() {
        return myList;
    }

    private void updateResults(List<News> myList) {
        this.myList = myList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return myList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View lineView = layoutInflater.inflate(R.layout.list_view_news, null);

        ProgressBar pb_1 = lineView.findViewById(R.id.pb_1);
        ImageView iv_image = lineView.findViewById(R.id.iv_image);
        TextView tv_news = lineView.findViewById(R.id.tv_news);
        Button btn_go_news = lineView.findViewById(R.id.btn_go_news);

        pb_1.setVisibility(View.VISIBLE);
        Glide.with(context)
                .load(myList.get(position).getImage())
                //.override(300, 300)
                //.centerCrop()
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        pb_1.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(iv_image);

        btn_go_news.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(myList.get(0).getUrl()));
                context.startActivity(i);
            }
        });

        tv_news.setText(myList.get(position).getDescription());

        return lineView;
    }
}
