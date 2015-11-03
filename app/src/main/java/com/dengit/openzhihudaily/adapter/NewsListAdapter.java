package com.dengit.openzhihudaily.adapter;

import android.content.Context;
import android.graphics.Color;
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
import com.dengit.openzhihudaily.utils.Utils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by dengit on 15/10/12.
 */
public class NewsListAdapter extends BaseAdapter{

    private static final String TAG = "**NewsListAdapter";

    private Context mContext;
    private final LayoutInflater mInflater;
//    private String mDate;
    private ArrayList<NewsListElement> mNewsListData;
    private HashSet<Integer> mNewsListWatched;
    private final int mUnreadColor;
    private final int mReadColor;

    public NewsListAdapter(Context context) {
        super();
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mNewsListData = new ArrayList<>();
        mNewsListWatched = new HashSet<>();
        mUnreadColor = mContext.getResources().getColor(R.color.unread_title);
        mReadColor = mContext.getResources().getColor(R.color.read_title);
    }

    public String getLatestDate() {
        return mNewsListData.size() > 0 ? mNewsListData.get(mNewsListData.size() - 1).date : "";
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public int getViewTypeCount() {
        return super.getViewTypeCount();
    }

    @Override
    public int getCount() {
        return mNewsListData.size();
    }

    @Override
    public Object getItem(int position) {
        return mNewsListData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mNewsListData.get(position).id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        NewsListElement element = mNewsListData.get(position);
        if (element.istag) {
            convertView = mInflater.inflate(R.layout.news_list_tag_item, parent, false);
            TextView tagTitle = (TextView) convertView.findViewById(R.id.news_list_item_tag_title);
            tagTitle.setText(Utils.getNewTitle(element.date));
            return convertView;
        }

        if (convertView != null && convertView.getTag() == null) {
            convertView = null;
        }

        ViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.news_list_item, parent, false);
            holder = new ViewHolder();
            holder.mItemTitle = (TextView) convertView.findViewById(R.id.news_list_item_title);
            holder.mItemThumb = (ImageView) convertView.findViewById(R.id.news_list_item_thumb);
            holder.mItemThumbMultipic = (ImageView) convertView.findViewById(R.id.news_list_item_thumb_multipic);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        holder.mItemTitle.setText(element.title);
        holder.mItemTitle.setTextColor(mUnreadColor);

        if (mNewsListWatched.contains((int)getItemId(position))) {
            holder.mItemTitle.setTextColor(mReadColor);
        }

        if (element.images.size() > 0) {

            if (element.multipic) {
                holder.mItemThumbMultipic.setVisibility(View.VISIBLE);
            }

            try {
                Picasso.with(mContext).load(element.images.get(0)).into(holder.mItemThumb);
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, "** picasso load item thumb error" + e.toString());
            }
        }

        return convertView;
    }

    public static ArrayList<NewsListElement> build(JSONObject response) {
        if (response == null) {
            Log.d(TAG, "** response == null");
            return null;
        }

        if (!response.has("stories")) {
            Log.d(TAG, "** !response.has(\"stories\")");
            return null;
        }

        ArrayList<NewsListElement> data = new ArrayList<>();

        try {

            String date = response.getString("date");

            JSONArray stories = response.getJSONArray("stories");

            NewsListElement tagElement = new NewsListElement();
            tagElement.istag = true;
            tagElement.date = date;
            data.add(tagElement);

            for (int i = 0; i < stories.length(); i++) {
                JSONObject item = stories.getJSONObject(i);
                NewsListElement element = new NewsListElement();
                element.id = item.getInt("id");
                element.type = item.getInt("type");
                element.multipic = item.has("multipic") && item.getBoolean("multipic");
                element.title = item.getString("title");
                element.date = date;

                JSONArray images = item.getJSONArray("images");

                for (int j = 0; j < images.length(); j++) {
                    element.images.add(images.getString(j));
                }

                data.add(element);

            }

            return data;

        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "** " + e);
        }
        return null;
    }

    public void addWatchedNewsId(int newsId) {
        mNewsListWatched.add(newsId);
    }

    public void resetWatchedNewsList(HashSet<Integer> newsListWatched) {
        mNewsListWatched.clear();
        mNewsListWatched.addAll(newsListWatched);
    }

    public void resetData(ArrayList<NewsListElement> newsListData) {
        mNewsListData.clear();
        mNewsListData.addAll(newsListData);
    }

    public void addData(ArrayList<NewsListElement> moreData) {
        mNewsListData.addAll(moreData);

    }


    public static class ViewHolder {
        public TextView mItemTitle;
        public ImageView mItemThumb;
        public ImageView mItemThumbMultipic;

    }
}
