package com.dengit.openzhihudaily.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dengit.openzhihudaily.R;
import com.dengit.openzhihudaily.model.CommentElement;
import com.dengit.openzhihudaily.utils.Utils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

/**
 * Created by dengit on 15/10/26.
 */
public class CommentListAdapter extends BaseAdapter {

    private ArrayList<CommentElement> mLongComments = new ArrayList<>();
    private ArrayList<CommentElement> mShortComments = new ArrayList<>();
    private Context mContext;
    private int mToolbarHeight;
    private int mHeaderHeight;
    private int mFooterHeight;
    private View mShortDividerView;
    private HashSet<Target> mStrongRefTargets = new HashSet<>();


    public CommentListAdapter(Context context, int toolbarHeight, int headerHeight, int footerHeight) {
        mContext = context;
        mToolbarHeight = toolbarHeight;
        mHeaderHeight = headerHeight;
        mFooterHeight = footerHeight;
    }

    @Override
    public int getCount() {

        if (mLongComments.size() == 0) {
            if (mShortComments.size() == 0) {
                return 1; // empty_view
            } else {
                return 2 + mShortComments.size(); // 2 is empty_view + short_divider_view
            }
        } else {
            if (mShortComments.size() == 0) {
                return mLongComments.size();
            } else {
                return mLongComments.size() + 1 + mShortComments.size(); // 1 is short_divider_view
            }
        }
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (position == 0 && mLongComments.size() == 0) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.comment_empty_item, parent, false);

            View commentEmptyLayout = view.findViewById(R.id.comment_empty_layout);
            int w = ViewGroup.LayoutParams.MATCH_PARENT;
            int h = mContext.getResources().getDisplayMetrics().heightPixels - (mFooterHeight + mHeaderHeight + mToolbarHeight);
            commentEmptyLayout.setLayoutParams(new AbsListView.LayoutParams(w, h));
            return view;
        }

        if (mLongComments.size() == 0 && position == 1 && mShortDividerView != null) {
            return mShortDividerView;
        }

        if (mLongComments.size() > 0 && position == mLongComments.size() && mShortDividerView != null) {
            return mShortDividerView;
        }

        if (convertView != null && convertView.getTag() == null) {
            convertView = null;
        }

        final CommentViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.comment_item, parent, false);

            viewHolder = new CommentViewHolder();
            viewHolder.avatarImage = (ImageView) convertView.findViewById(R.id.comment_item_avatar_img);

            viewHolder.voteIamge = (ImageView) convertView.findViewById(R.id.comment_item_vote_img);
            viewHolder.avatarName = (TextView) convertView.findViewById(R.id.comment_item_avatar_name);
            viewHolder.voteCount = (TextView) convertView.findViewById(R.id.comment_item_vote_count);
            viewHolder.commentContent = (TextView) convertView.findViewById(R.id.comment_item_content);
            viewHolder.commentTime = (TextView) convertView.findViewById(R.id.comment_item_time);


            convertView.setTag(viewHolder);
        } else {
            viewHolder = (CommentViewHolder) convertView.getTag();
        }

        final CommentElement element;

        if (mLongComments.size() == 0) {
            element = mShortComments.get(position - 2);
        } else {
            element = position > mLongComments.size() ? mShortComments.get(position - mLongComments.size() - 1) : mLongComments.get(position);
        }

        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
        long timeStamp = element.time * 1000L;
        Date date = new Date(timeStamp);

        viewHolder.avatarImage.setImageResource(R.drawable.comment_avatar);
        viewHolder.avatarName.setText(element.author);
        viewHolder.voteCount.setText(String.valueOf(element.likes));
        viewHolder.commentContent.setText(element.content);
        viewHolder.commentTime.setText(sdf.format(date));
        viewHolder.voteIamge.setImageResource(R.drawable.comment_vote);

        Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                viewHolder.avatarImage.setImageBitmap(Utils.getCircleBitmap(bitmap));
                mStrongRefTargets.remove(this);
            }

            @Override
            public void onBitmapFailed() {

            }
        };

        mStrongRefTargets.add(target);

        try {
            Picasso.with(mContext).load(element.avatar).into(target);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return convertView;
    }

    public static ArrayList<CommentElement> build(JSONObject response) {


        ArrayList<CommentElement> longComments = new ArrayList<>();

        try {

            JSONArray commentsJson = response.getJSONArray("comments");
            for (int i = 0; i < commentsJson.length(); i++) {
                JSONObject item = commentsJson.getJSONObject(i);
                CommentElement element = new CommentElement();
                element.id = item.getInt("id");
                element.likes = item.getInt("likes");
                element.time = item.getInt("time");
                element.author = item.getString("author");
                element.content = item.getString("content");
                element.avatar = item.getString("avatar");

                longComments.add(element);
            }

            return longComments;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static ArrayList<CommentElement> buildShortComment(JSONObject response) {

        ArrayList<CommentElement> shortComments = new ArrayList<>();

        try {

            JSONArray commentsJson = response.getJSONArray("comments");
            for (int i = 0; i < commentsJson.length(); i++) {
                JSONObject item = commentsJson.getJSONObject(i);
                CommentElement element = new CommentElement();
                element.id = item.getInt("id");
                element.likes = item.getInt("likes");
                element.time = item.getInt("time");
                element.author = item.getString("author");
                element.content = item.getString("content");
                element.avatar = item.getString("avatar");

                shortComments.add(element);
            }

            return shortComments;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public void setShortDividerView(View shortDividerView) {
        mShortDividerView = shortDividerView;
    }

    public void clearShortComment() {
        mShortComments.clear();
        mShortDividerView = null;
    }

    public void resetData(ArrayList<CommentElement> longComments) {
        mLongComments.clear();
        mLongComments.addAll(longComments);
    }

    public void resetShortComment(ArrayList<CommentElement> shortComments) {
        mShortComments.clear();
        mShortComments.addAll(shortComments);

    }


    private static class CommentViewHolder {
        public TextView avatarName;
        public TextView voteCount;
        public TextView commentContent;
        public TextView commentTime;
        public ImageView avatarImage;
        public ImageView voteIamge;
    }
}
