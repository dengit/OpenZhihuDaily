package com.dengit.openzhihudaily.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.dengit.openzhihudaily.R;
import com.dengit.openzhihudaily.adapter.FavNewsListAdapter;
import com.dengit.openzhihudaily.model.NewsListElement;

/**
 * Created by dengit on 15/10/18.
 */
public class FavActivity extends AppCompatActivity{

    private ListView mListView;
    private FavNewsListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fav);

        mListView = (ListView) findViewById(R.id.fav_list_view);
        mAdapter = new FavNewsListAdapter(this);

        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.setClass(FavActivity.this, NewsActivity.class);

                NewsListElement newsInfo = (NewsListElement) mAdapter.getItem(position - mListView.getHeaderViewsCount());

                intent.putExtra("news_info", newsInfo);

                startActivity(intent);
            }
        });
    }
}
