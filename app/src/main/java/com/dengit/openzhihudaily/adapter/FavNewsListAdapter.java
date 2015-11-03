package com.dengit.openzhihudaily.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dengit.openzhihudaily.R;
import com.dengit.openzhihudaily.data.AppDB;
import com.dengit.openzhihudaily.model.NewsListElement;
import com.dengit.openzhihudaily.utils.ThreadPoolUtils;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;

/**
 * Created by dengit on 15/10/18.
 */
public class FavNewsListAdapter extends BaseAdapter {
    private static final String TAG = "**FavNewsListAdapter";

    private final int mReadColor;

    private final LayoutInflater mInflater;
    private Context mContext;

    private ArrayList<NewsListElement> mFavNewsListData;
    private Handler handler = new Handler();

    public FavNewsListAdapter(final Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mFavNewsListData = new ArrayList<>();
        mReadColor = mContext.getResources().getColor(R.color.read_title);
        ThreadPoolUtils.execute(new Runnable() {
            @Override
            public void run() {
                AppDB.instance(context).loadFavNewsList(mFavNewsListData);
                Log.d("**", "mFavNewsListData.size(): " + mFavNewsListData.size());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }
                });
            }
        });
    }

    @Override
    public int getCount() {
        return mFavNewsListData.size();
    }

    @Override
    public Object getItem(int position) {
        return mFavNewsListData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        NewsListAdapter.ViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.news_list_item, parent, false);
            holder = new NewsListAdapter.ViewHolder();
            holder.mItemTitle = (TextView) convertView.findViewById(R.id.news_list_item_title);
            holder.mItemThumb = (ImageView) convertView.findViewById(R.id.news_list_item_thumb);
            convertView.setTag(holder);
        } else {
            holder = (NewsListAdapter.ViewHolder) convertView.getTag();
        }

        NewsListElement element = mFavNewsListData.get(position);

        holder.mItemTitle.setText(element.title);
        holder.mItemTitle.setTextColor(mReadColor);

        if (element.images.size() > 0) {
            try {
                Picasso.with(mContext).load(element.images.get(0)).into(holder.mItemThumb);
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("**", "** picasso load item thumb error" + e.toString());
            }
        }


        return convertView;
    }
}
