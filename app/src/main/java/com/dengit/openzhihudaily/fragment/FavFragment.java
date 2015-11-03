package com.dengit.openzhihudaily.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.dengit.openzhihudaily.R;
import com.dengit.openzhihudaily.adapter.FavNewsListAdapter;
import com.dengit.openzhihudaily.data.AppDB;
import com.dengit.openzhihudaily.model.NewsListElement;
import com.dengit.openzhihudaily.ui.NewsActivity;
import com.dengit.openzhihudaily.utils.ThreadPoolUtils;

import java.util.ArrayList;

/**
 * Created by dengit on 15/11/3.
 */
public class FavFragment extends Fragment {

    private ListView mListView;
    private FavNewsListAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_fav, container, false);

        mListView = (ListView) view.findViewById(R.id.fav_list_view);
        mAdapter = new FavNewsListAdapter(getContext());

        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.setClass(getContext(), NewsActivity.class);

                NewsListElement newsInfo = (NewsListElement) mAdapter.getItem(position - mListView.getHeaderViewsCount());

                intent.putExtra("news_info", newsInfo);

                startActivity(intent);
            }
        });

        ThreadPoolUtils.execute(new Runnable() {
            @Override
            public void run() {
                final ArrayList<NewsListElement> data = AppDB.instance(getContext()).loadFavNewsList();
                Log.d("**", "favNewsListData.size(): " + data.size());
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.resetListData(data);
                        getActivity().setTitle("" +data.size()+" 条收藏");
                        mAdapter.notifyDataSetChanged();
                    }
                });
            }
        });

        return view;
    }
}
