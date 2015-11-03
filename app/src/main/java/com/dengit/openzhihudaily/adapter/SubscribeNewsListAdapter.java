package com.dengit.openzhihudaily.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dengit.openzhihudaily.R;
import com.dengit.openzhihudaily.model.SubscribeNewsListElement;
import com.dengit.openzhihudaily.model.SubscribeNewsListElement.SubscribeNewsEditorInfo;
import com.dengit.openzhihudaily.utils.Utils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;

/**
 * Created by dengit on 15/10/12.
 */
public class SubscribeNewsListAdapter extends BaseAdapter {

    private static final String TAG = "**SubscribeNewsListAdapter";

    private Context mContext;
    private final LayoutInflater mInflater;

    private String mThemeName;
    private String mThemeDescription;
    private String mThemeImage;
    private String mThemeBackground;
    private String mThemeImageSource;
    private final int mUnreadColor;
    private final int mReadColor;
    private LinkedHashMap<Integer, SubscribeNewsListElement> mTotalSubscribeNewsMapData;
    private ArrayList<SubscribeNewsListElement> mSubscribeNewsListData;
    private ArrayList<SubscribeNewsEditorInfo> mSubscribeNewsEditorInfos;

    private HashSet<Integer> mSubscribeNewsListWatched;
    private HashSet<Target> mStrongRefTargets = new HashSet<>();

    public SubscribeNewsListAdapter(Context context) {
        super();
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mSubscribeNewsListData = new ArrayList<>();
        mSubscribeNewsListWatched = new HashSet<>();
        mSubscribeNewsEditorInfos = new ArrayList<>();
        mTotalSubscribeNewsMapData = new LinkedHashMap<>();
        mUnreadColor = mContext.getResources().getColor(R.color.unread_title);
        mReadColor = mContext.getResources().getColor(R.color.read_title);
    }

    public LinkedHashMap<Integer, SubscribeNewsListElement> getTotalSubscribeNewsMapData() {
        return mTotalSubscribeNewsMapData;
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
        return mSubscribeNewsListData.size();
    }

    @Override
    public Object getItem(int position) {
        return mSubscribeNewsListData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mSubscribeNewsListData.get(position).id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

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

        SubscribeNewsListElement element = mSubscribeNewsListData.get(position);

        holder.mItemTitle.setText(element.title);
        holder.mItemTitle.setTextColor(mUnreadColor);

        if (mSubscribeNewsListWatched.contains((int)getItemId(position))) {
            holder.mItemTitle.setTextColor(mReadColor);
        }

        try {
            if (element.images.size() > 0) {
                if (element.multipic) {
                    holder.mItemThumbMultipic.setVisibility(View.VISIBLE);
                }

                Picasso.with(mContext).load(element.images.get(0)).into(holder.mItemThumb);
            } else {
                holder.mItemThumb.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "** picasso load item thumb error" + e.toString());
        }

        return convertView;
    }

    public static void build(JSONObject response, ArrayList<SubscribeNewsListElement> newsListData, ArrayList<SubscribeNewsEditorInfo> newsEditorInfos) {

        if (response == null) {
            Log.d(TAG, "** response == null");
            return;
        }

        if (!response.has("stories")) {
            Log.d(TAG, "** !response.has(\"stories\")");
            return;
        }

        try {

            JSONArray stories = response.getJSONArray("stories");

            for (int i = 0; i < stories.length(); i++) {
                JSONObject item = stories.getJSONObject(i);
                SubscribeNewsListElement element = new SubscribeNewsListElement();
                element.id = item.getInt("id");
                element.type = item.getInt("type");
                element.multipic = item.has("multipic") && item.getBoolean("multipic");
                element.title = item.getString("title");

                if (item.has("images")) {
                    JSONArray images = item.getJSONArray("images");

                    for (int j = 0; j < images.length(); j++) {
                        element.images.add(images.getString(j));
                    }
                }

                newsListData.add(element);

            }

            if (response.has("editors")) {
                JSONArray editors = response.getJSONArray("editors");

                for (int i = 0; i < editors.length(); i++) {
                    JSONObject item = editors.getJSONObject(i);
                    SubscribeNewsEditorInfo editor = new SubscribeNewsEditorInfo();
                    editor.id = item.getLong("id");
                    editor.name = item.getString("name");
                    editor.avatar = item.getString("avatar");
                    editor.url = item.has("url") ? item.getString("url") : "";
                    editor.bio = item.has("bio") ? item.getString("bio") : "";

                    newsEditorInfos.add(editor);

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "** " + e);
        }
    }

    public static boolean buildMore(JSONObject response,
                                    ArrayList<SubscribeNewsListElement> subscribeNewsListData) {


        if (response == null) {
            Log.d(TAG, "** response == null");
            return true;
        }

        if (!response.has("stories")) {
            Log.d(TAG, "** !response.has(\"stories\")");
            return true;
        }

        try {

            JSONArray stories = response.getJSONArray("stories");

            if (stories.length() == 0) {
                Log.d(TAG, "stories.length() == 0");
                return true;
            }

            for (int i = 0; i < stories.length(); i++) {
                JSONObject item = stories.getJSONObject(i);
                SubscribeNewsListElement element = new SubscribeNewsListElement();
                element.id = item.getInt("id");

//                if (mSubscribeNewsListData.size() > 0 &&
//                    mSubscribeNewsListData.get(mSubscribeNewsListData.size()-1).id == element.id) {
//                    continue;
//                }
                if (i == 0) {//skip redundant item
                    continue;
                }

                element.type = item.getInt("type");
                element.title = item.getString("title");

                if (item.has("images")) {
                    JSONArray images = item.getJSONArray("images");

                    for (int j = 0; j < images.length(); j++) {
                        element.images.add(images.getString(j));
                    }
                }

                subscribeNewsListData.add(element);

            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "** " + e);
        }

        return false;
    }

    public long getLastNewsId() {
        return mSubscribeNewsListData.size() > 0 ? mSubscribeNewsListData.get(mSubscribeNewsListData.size()-1).id : 0;
    }

    public void addWatchedNewsId(int newsId) {
        mSubscribeNewsListWatched.add(newsId);
    }

    public void updateHeader(View header) {

        TextView themeTitle = (TextView) header.findViewById(R.id.theme_title);
        themeTitle.setText(mThemeDescription);

        LinearLayout editorLayout = (LinearLayout) header.findViewById(R.id.editor_layout);

        int w = mContext.getResources().getDimensionPixelSize(R.dimen.editor_image);
        int h = mContext.getResources().getDimensionPixelSize(R.dimen.editor_image);
        int marginR = mContext.getResources().getDimensionPixelSize(R.dimen.editor_margin_right);

        for (int i = 0; i < mSubscribeNewsEditorInfos.size(); i++) {

            SubscribeNewsEditorInfo editorInfo = mSubscribeNewsEditorInfos.get(i);

            final ImageView imageView = new ImageView(mContext);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(w, h);

            if (i < mSubscribeNewsEditorInfos.size() - 1) {
                params.setMargins(0, 0, marginR, 0);
            }

            editorLayout.addView(imageView, params);

            Target target = new Target() {

                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    imageView.setImageBitmap(Utils.getCircleBitmap(bitmap));
                    mStrongRefTargets.remove(this);
                }

                @Override
                public void onBitmapFailed() {

                }
            };

            mStrongRefTargets.add(target);

            try {
                Picasso.with(mContext).load(editorInfo.avatar).into(target);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public void resetWatchedNewsList(HashSet<Integer> newsListWatched) {
        mSubscribeNewsListWatched.clear();
        mSubscribeNewsListWatched.addAll(newsListWatched);
    }

    public void resetListData(ArrayList<SubscribeNewsListElement> newsListData, ArrayList<SubscribeNewsEditorInfo> newsEditorInfos) {
        mSubscribeNewsListData.clear();
        mSubscribeNewsEditorInfos.clear();

        mSubscribeNewsListData.addAll(newsListData);
        mSubscribeNewsEditorInfos.addAll(newsEditorInfos);
    }

    public void addData(ArrayList<SubscribeNewsListElement> subscribeNewsListData) {
        mSubscribeNewsListData.addAll(subscribeNewsListData);
    }

    public String getThemeBackgroundUrl() {
        return mThemeBackground;
    }

    public void resetHeaderData(JSONObject response) {
        if (response == null) {
            Log.d(TAG, "** response == null");
            return;
        }

        if (!response.has("stories")) {
            Log.d(TAG, "** !response.has(\"stories\")");
            return;
        }

        try {

            mThemeName = response.getString("name");
            mThemeDescription = response.getString("description");
            mThemeImage = response.getString("image");
            mThemeBackground = response.getString("background");
            mThemeImageSource = response.getString("image_source");
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "** " + e);
        }
    }



    private static class ViewHolder {
        public TextView mItemTitle;
        public ImageView mItemThumb;
        public ImageView mItemThumbMultipic;
    }

}

