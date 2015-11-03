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
import com.dengit.openzhihudaily.model.DrawerListElement;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by dengit on 15/10/13.
 */
public class DrawerListAdapter extends BaseAdapter{

    private static final String TAG = "**DrawerListAdapter";
    public static final int HOME_POS = -1;

    private int mCurrCategoryPos = HOME_POS; // home item
    private Context mContext;
    private LayoutInflater mInflater;

    private ArrayList<DrawerListElement> mDrawerListData;

    public DrawerListAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mDrawerListData = new ArrayList<>();
    }

    public int getCurrCategoryPos() {
        return mCurrCategoryPos;
    }

    @Override
    public int getCount() {
        return mDrawerListData.size();
    }

    @Override
    public Object getItem(int position) {
        return mDrawerListData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mDrawerListData.get(position).id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.drawer_list_item, parent, false);
            holder = new ViewHolder();
            holder.mItemName = (TextView) convertView.findViewById(R.id.drawer_item_title);
            holder.mItemFollow = (ImageView) convertView.findViewById(R.id.drawer_item_subscribe_button);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        DrawerListElement element = mDrawerListData.get(position);

        holder.mItemName.setText(element.name);

        if (mCurrCategoryPos == position) {
            convertView.setBackgroundResource(R.color.drawer_item_selected);
        } else {
            convertView.setBackgroundResource(R.color.drawer_item_normal);
        }

        return convertView;
    }

    public static ArrayList<DrawerListElement> build(JSONObject response) {
        if (!response.has("others")) {
            Log.d(TAG, "** !response.has(\"others\")");
            return null;
        }

        ArrayList<DrawerListElement> data = new ArrayList<>();
        try {

            JSONArray others = response.getJSONArray("others");

            for (int i = 0; i < others.length(); i++) {
                JSONObject item = others.getJSONObject(i);
                DrawerListElement element = new DrawerListElement();
                element.id = item.getInt("id");
                element.color = item.getLong("color");
                element.name = item.getString("name");
                element.thumbnail = item.getString("thumbnail");
                element.description = item.getString("description");

                data.add(element);

            }

            return data;

        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "** " + e);
        }
        return null;
    }

    public void setSelectedItem(int position) {
        mCurrCategoryPos = position;
        notifyDataSetChanged();
    }

    public void resetData(ArrayList<DrawerListElement> drawerListData) {
        mDrawerListData.clear();
        mDrawerListData.addAll(drawerListData);
    }


    private static class ViewHolder {
        public TextView mItemName;
        public ImageView mItemFollow;
    }
}
