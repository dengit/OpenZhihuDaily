package com.dengit.openzhihudaily.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dengit.openzhihudaily.R;
import com.dengit.openzhihudaily.model.NewsListElement;
import com.dengit.openzhihudaily.model.RecommendElement;
import com.dengit.openzhihudaily.ui.NewsActivity;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by dengit on 15/10/13.
 */
public class RecommendPagerAdapter extends PagerAdapter {

    public static final String TAG = "**RecommendPagerAdapter";

    private final Context mContext;
    private final LayoutInflater mInflater;
    private Fragment mFragment;
    private ArrayList<RecommendElement> mRecommendData;
    private int mOverlayColor;

    public RecommendPagerAdapter(Fragment fragment) {
        mContext = fragment.getContext();
        mInflater = LayoutInflater.from(mContext);
        mFragment = fragment;
        mRecommendData = new ArrayList<>();
        mOverlayColor = mContext.getResources().getColor(R.color.overlay_grey);
    }

    //todo use Gson
    public static ArrayList<RecommendElement> build(JSONObject response) {
        if (response == null) {
            Log.d(TAG, "** response == null");
            return null;
        }

        if (!response.has("top_stories")) {
            Log.d(TAG, "** !response.has(\"top_stories\")");
            return null;
        }

        ArrayList<RecommendElement> data = new ArrayList<>();

        try {
            JSONArray topStories = response.getJSONArray("top_stories");

            for (int i = 0; i < topStories.length(); i++) {
                JSONObject item = topStories.getJSONObject(i);
                RecommendElement element = new RecommendElement();
                element.id = item.getInt("id");
                element.type = item.getInt("type");
                element.title = item.getString("title");
                element.images.add(item.getString("image"));

                data.add(element);

            }

            return data;

        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "** " + e);
        }

        return null;
    }

    @Override
    public int getCount() {
        return mRecommendData.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View recommendView = mInflater.inflate(R.layout.recommend_view, container, false);

        recommendView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent();
                            intent.setClass(mContext, NewsActivity.class);

                            NewsListElement newsInfo = getItem(position);

                            intent.putExtra("news_info", newsInfo);
                            mFragment.startActivityForResult(intent, 0);
                        }
                    });

        RecommendElement element = mRecommendData.get(position);
        final ImageView imageView = (ImageView) recommendView.findViewById(R.id.recommend_image);
        TextView titleView = (TextView) recommendView.findViewById(R.id.recommend_title);
        titleView.setText(element.title);

        try {
            //Callback is strong ref, watch out leak
            Picasso.with(mContext).load(element.images.get(0)).into(imageView, new Callback() {
                @Override
                public void onError() {

                }

                @Override
                public void onSuccess() {
                    Drawable drawable = imageView.getDrawable();
                    drawable.setColorFilter(mOverlayColor, PorterDuff.Mode.MULTIPLY);
                }
            });
        } catch (Exception e) {
            Log.d(TAG, "** Picasso recommend image error");
        }

        container.addView(recommendView);
        return recommendView;
    }

    public RecommendElement getItem(int position) {
        return mRecommendData.get(position);
    }
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return super.getPageTitle(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View)object);
    }

    public void resetData(ArrayList<RecommendElement> recommendData) {
        mRecommendData.clear();
        mRecommendData.addAll(recommendData);
    }
}
