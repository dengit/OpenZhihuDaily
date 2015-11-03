package com.dengit.openzhihudaily.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.dengit.openzhihudaily.R;
import com.dengit.openzhihudaily.adapter.NewsListAdapter;
import com.dengit.openzhihudaily.adapter.RecommendPagerAdapter;
import com.dengit.openzhihudaily.data.AppDB;
import com.dengit.openzhihudaily.data.HttpAccessor;
import com.dengit.openzhihudaily.model.NewsListElement;
import com.dengit.openzhihudaily.model.RecommendElement;
import com.dengit.openzhihudaily.ui.NewsActivity;
import com.dengit.openzhihudaily.utils.ThreadPoolUtils;
import com.dengit.openzhihudaily.utils.Utils;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.viewpagerindicator.PageIndicator;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by dengit on 15/10/12.
 */
public class HomeFragment extends Fragment implements AbsListView.OnScrollListener {

    private static final String TAG = "**HomeFragment";
    public static final String HOME_TITLE = "首页";

    private boolean mUpdating = false;

    private ViewPager mRecommendPager;
    private RecommendPagerAdapter mRecommendPagerAdapter;
    private PageIndicator mRecommendIndicator;

    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {

            int total = mRecommendPagerAdapter.getCount();
            if (total > 0) {
                int newPos = (mRecommendPager.getCurrentItem() + 1) % total;
                mRecommendPager.setCurrentItem(newPos, true);
                handler.postDelayed(this, 5000);
            }
        }
    };

    private ListView mNewsList;
    private NewsListAdapter mNewsListAdapter;
    private SwipeRefreshLayout mHomeRefreshLayout;


    private void updateUI() {
        mRecommendPagerAdapter.notifyDataSetChanged();
        mNewsListAdapter.notifyDataSetChanged();
    }

    @Override
    public Context getContext() {
        return super.getContext();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        View viewPagerGallery = inflater.inflate(R.layout.viewpager_gallery, null, false);

        mRecommendPager = (ViewPager) viewPagerGallery.findViewById(R.id.recommend_pager);
        mRecommendPagerAdapter = new RecommendPagerAdapter(this);
        mRecommendPager.setAdapter(mRecommendPagerAdapter);

        mRecommendIndicator = (PageIndicator) viewPagerGallery.findViewById(R.id.recommend_indicator);
        mRecommendIndicator.setViewPager(mRecommendPager);

        mNewsList = (ListView) view.findViewById(R.id.news_list);
        mNewsList.addHeaderView(viewPagerGallery);

        mNewsListAdapter = new NewsListAdapter(getContext());
        mNewsList.setAdapter(mNewsListAdapter);

        mNewsList.setOnScrollListener(this);

        mNewsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                NewsListElement newsInfo = (NewsListElement) mNewsListAdapter.getItem(position - mNewsList.getHeaderViewsCount());

                if (newsInfo.istag) {
                    return;
                }

                Intent intent = new Intent();
                intent.setClass(getContext(), NewsActivity.class);

                intent.putExtra("news_info", newsInfo);

                startActivityForResult(intent, 0);
            }
        });

        mHomeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_home);
        mHomeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                HttpAccessor.instance().getNewsLatest(new HomeJsonHttpResponseHandler());
            }
        });

        HttpAccessor.instance().getNewsLatest(new HomeJsonHttpResponseHandler());


        return view;
    }

    private class HomeJsonHttpResponseHandler extends JsonHttpResponseHandler {
        @Override
        public void onSuccess(int statusCode, JSONObject response) {
            super.onSuccess(statusCode, response);

            if (statusCode == 200) {

                final JSONObject responseTmp = response;
                ThreadPoolUtils.execute(new Runnable() {
                    @Override
                    public void run() {

                        final HashSet<Integer> newsListWatched = AppDB.instance(getContext()).loadNewsListWatched();
                        final ArrayList<NewsListElement> newsListData = NewsListAdapter.build(responseTmp);
                        final ArrayList<RecommendElement> recommendData = RecommendPagerAdapter.build(responseTmp);

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                handler.removeCallbacks(runnable);

                                mNewsListAdapter.resetWatchedNewsList(newsListWatched);
                                mNewsListAdapter.resetData(newsListData);
                                mRecommendPagerAdapter.resetData(recommendData);

                                updateUI();
                                mHomeRefreshLayout.setRefreshing(false);
                                handler.postDelayed(runnable, 5000);
                            }
                        });

                        AppDB.instance(getContext()).saveNewsLatest(responseTmp);
                    }
                });

            } else {
                mHomeRefreshLayout.setRefreshing(false);
            }
        }

        @Override
        public void onFailure(Throwable e, JSONObject errorResponse) {
            super.onFailure(e, errorResponse);

            ThreadPoolUtils.execute(new Runnable() {
                @Override
                public void run() {

                    final JSONObject response = AppDB.instance(getContext()).loadNewsLatest();
                    if (response == null) {
                        mHomeRefreshLayout.setRefreshing(false);
                        return;
                    }

                    final HashSet<Integer> newsListWatched = AppDB.instance(getContext()).loadNewsListWatched();

                    final ArrayList<NewsListElement> newsListData = NewsListAdapter.build(response);
                    final ArrayList<RecommendElement> recommendData = RecommendPagerAdapter.build(response);

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            handler.removeCallbacks(runnable);

                            mNewsListAdapter.resetWatchedNewsList(newsListWatched);
                            mNewsListAdapter.resetData(newsListData);
                            mRecommendPagerAdapter.resetData(recommendData);

                            updateUI();
                            mHomeRefreshLayout.setRefreshing(false);
                            handler.postDelayed(runnable, 5000);
                        }
                    });
                }
            });

        }
    }

    @Nullable
    @Override
    public View getView() {
        return super.getView();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        handler.removeCallbacks(runnable);
        Log.d("**", "onStop remove runnable");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0 && resultCode == FragmentActivity.RESULT_OK) {
            int newsId = data.getIntExtra("news_id", 0);
            if (newsId != 0) {
                mNewsListAdapter.addWatchedNewsId(newsId);
            }
            updateUI();

            handler.post(runnable);
            Log.d("**", "onActivityResult post runnable");
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        String newTitle = null;
        if (firstVisibleItem == 0) {
            newTitle = HOME_TITLE;
        }

        String currTitle = getActivity().getTitle().toString();

        if (firstVisibleItem >= mNewsList.getHeaderViewsCount()) {
            NewsListElement element = (NewsListElement) mNewsListAdapter.getItem(firstVisibleItem - mNewsList.getHeaderViewsCount());
            newTitle = Utils.getNewTitle(element.date);
        }

        if (!TextUtils.isEmpty(newTitle) && !currTitle.equals(newTitle)) {
            getActivity().setTitle(newTitle);
        }

        if (mUpdating) {
            return;
        }

        if (totalItemCount > mNewsList.getHeaderViewsCount() && firstVisibleItem + visibleItemCount >= totalItemCount) {
            Log.d(TAG, "updating");
            mUpdating = true;

            final String latestDate = mNewsListAdapter.getLatestDate();

            HttpAccessor.instance().getNewsBefore(latestDate, new JsonHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Throwable e, JSONObject errorResponse) {
                    super.onFailure(statusCode, e, errorResponse);
                    Log.d(TAG, "" + e + ", " + errorResponse);


                    ThreadPoolUtils.execute(new Runnable() {
                        @Override
                        public void run() {

                            final JSONObject more = AppDB.instance(getContext()).loadNewsMoreBefore(latestDate);

                            if (more == null) {
                                mUpdating = false;
                                return;
                            }

                            final ArrayList<NewsListElement> moreData = NewsListAdapter.build(more);

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mNewsListAdapter.addData(moreData);
                                    updateUI();
                                }
                            });
                            mUpdating = false;
                        }
                    });

                }

                @Override
                public void onSuccess(int statusCode, JSONObject response) {
                    super.onSuccess(statusCode, response);


                    if (statusCode == 200) {

                        final JSONObject responseTmp = response;

                        ThreadPoolUtils.execute(new Runnable() {
                            @Override
                            public void run() {

                                final ArrayList<NewsListElement> moreData = NewsListAdapter.build(responseTmp);

                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mNewsListAdapter.addData(moreData);
                                        updateUI();
                                    }
                                });

                                AppDB.instance(getContext()).saveNewsMoreBefore(responseTmp, latestDate);

                                mUpdating = false;
                            }
                        });

                    } else {
                        mUpdating = false;
                    }
                }
            });
        }
    }

}
