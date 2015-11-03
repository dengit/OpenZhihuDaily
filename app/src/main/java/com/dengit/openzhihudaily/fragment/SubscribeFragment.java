package com.dengit.openzhihudaily.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.dengit.openzhihudaily.R;
import com.dengit.openzhihudaily.adapter.SubscribeNewsListAdapter;
import com.dengit.openzhihudaily.data.AppDB;
import com.dengit.openzhihudaily.data.HttpAccessor;
import com.dengit.openzhihudaily.model.SubscribeNewsListElement;
import com.dengit.openzhihudaily.model.SubscribeNewsListElement.SubscribeNewsEditorInfo;
import com.dengit.openzhihudaily.ui.NewsActivity;
import com.dengit.openzhihudaily.utils.ThreadPoolUtils;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SubscribeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class SubscribeFragment extends Fragment implements AbsListView.OnScrollListener {

    public static final String TAG = "**SubscribeFragment";

    private boolean mUpdating;
    private boolean mIsEnd;
    private int mThemeId;
    private OnFragmentInteractionListener mListener;

    private ListView mSubscribeNewsList;
    private SubscribeNewsListAdapter mSubscribeNewsListAdapter;
    private ImageView mThemeImage;
    private SwipeRefreshLayout mSwipeRefreshSubscribeLayout;
    private View mSubscribeHeader;

    public SubscribeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mThemeId = getArguments().getInt("themeId");
        Log.d(TAG, "mThemeId: " + mThemeId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_subcribe, container, false);
        mSubscribeHeader = inflater.inflate(R.layout.subscribe_header, null);
        mThemeImage = (ImageView) mSubscribeHeader.findViewById(R.id.theme_image);

        mSwipeRefreshSubscribeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_subscribed);

        mSwipeRefreshSubscribeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                HttpAccessor.instance().getNewsSpecifiedTheme(mThemeId, new SubscribedJsonHttpResponseHandler());
            }
        });

        mSubscribeNewsList = (ListView) view.findViewById(R.id.theme_news_list);

        mSubscribeNewsList.addHeaderView(mSubscribeHeader);

        mSubscribeNewsListAdapter = new SubscribeNewsListAdapter(getContext());
        mSubscribeNewsList.setAdapter(mSubscribeNewsListAdapter);
        mSubscribeNewsList.setOnScrollListener(this);

        mSubscribeNewsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.setClass(getContext(), NewsActivity.class);

                SubscribeNewsListElement newsInfo = (SubscribeNewsListElement) mSubscribeNewsListAdapter.getItem(position - mSubscribeNewsList.getHeaderViewsCount());

                intent.putExtra("news_info", newsInfo);

                startActivityForResult(intent, 0);
            }
        });


        HttpAccessor.instance().getNewsSpecifiedTheme(mThemeId, new SubscribedJsonHttpResponseHandler());
        return view;
    }


    private class SubscribedJsonHttpResponseHandler extends JsonHttpResponseHandler {
        @Override
        public void onFailure(int statusCode, Throwable e, JSONObject errorResponse) {
            super.onFailure(statusCode, e, errorResponse);

            ThreadPoolUtils.execute(new Runnable() {
                @Override
                public void run() {

                    final JSONObject response = AppDB.instance(getContext()).loadNewsSpecifiedThemeNew(mThemeId);

                    if (response == null) {
                        Log.d("**", "response == null");
                        mSwipeRefreshSubscribeLayout.setRefreshing(false);
                        return;
                    }

                    final HashSet<Integer> newsListWatched = AppDB.instance(getContext()).loadNewsListWatched();

                    final ArrayList<SubscribeNewsListElement> newsListData = new ArrayList<>();
                    final ArrayList<SubscribeNewsEditorInfo> newsEditorInfos = new ArrayList<>();
                    SubscribeNewsListAdapter.build(response, newsListData, newsEditorInfos);

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            mSubscribeNewsListAdapter.resetHeaderData(response);
                            mSubscribeNewsListAdapter.resetWatchedNewsList(newsListWatched);
                            mSubscribeNewsListAdapter.resetListData(newsListData, newsEditorInfos);

                            try {
                                Picasso.with(getContext()).load(mSubscribeNewsListAdapter.getThemeBackgroundUrl()).into(mThemeImage);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }

                            mSubscribeNewsListAdapter.updateHeader(mSubscribeHeader);
                            mSubscribeNewsListAdapter.notifyDataSetChanged();
                            mSwipeRefreshSubscribeLayout.setRefreshing(false);
                        }
                    });
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
                        final HashSet<Integer> newsListWatched = AppDB.instance(getContext()).loadNewsListWatched();
                        final ArrayList<SubscribeNewsListElement> newsListData = new ArrayList<>();
                        final ArrayList<SubscribeNewsEditorInfo> newsEditorInfos = new ArrayList<>();
                        SubscribeNewsListAdapter.build(responseTmp, newsListData, newsEditorInfos);

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                mSubscribeNewsListAdapter.resetHeaderData(responseTmp);
                                mSubscribeNewsListAdapter.resetWatchedNewsList(newsListWatched);
                                mSubscribeNewsListAdapter.resetListData(newsListData, newsEditorInfos);


                                try {
                                    Picasso.with(getContext()).load(mSubscribeNewsListAdapter.getThemeBackgroundUrl()).into(mThemeImage);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                mSubscribeNewsListAdapter.updateHeader(mSubscribeHeader);
                                mSubscribeNewsListAdapter.notifyDataSetChanged();
                                mSwipeRefreshSubscribeLayout.setRefreshing(false);
                            }
                        });

                        AppDB.instance(getContext()).saveNewsSpecifiedThemeNew(mThemeId, responseTmp, mSubscribeNewsListAdapter.getTotalSubscribeNewsMapData());
                    }
                });

            } else {
                mSwipeRefreshSubscribeLayout.setRefreshing(false);
            }
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0 && resultCode == FragmentActivity.RESULT_OK) {
            int newsId = data.getIntExtra("news_id", 0);
            if (newsId != 0) {
                mSubscribeNewsListAdapter.addWatchedNewsId(newsId);
            }
            mSubscribeNewsListAdapter.notifyDataSetChanged();
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public static Fragment newInstance(int themeId) {
        Fragment fragment = new SubscribeFragment();
        Bundle args = new Bundle();
        args.putInt("themeId", themeId);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        if (mUpdating) {
            return;
        }

        if (totalItemCount > mSubscribeNewsList.getHeaderViewsCount() && firstVisibleItem + visibleItemCount >= totalItemCount && !mIsEnd) {
            Log.d(TAG, "updating");
            mUpdating = true;

            final int lastNewsId = (int) mSubscribeNewsListAdapter.getLastNewsId();
            HttpAccessor.instance().getSubscribeNewsBefore(mThemeId, lastNewsId, new JsonHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Throwable e, JSONObject errorResponse) {
                    super.onFailure(statusCode, e, errorResponse);
                    Log.d(TAG, "" + e + ", " + errorResponse);

                    ThreadPoolUtils.execute(new Runnable() {
                        @Override
                        public void run() {

                            JSONObject response = AppDB.instance(getContext()).loadSubscribeNewsBeforeNew(lastNewsId);

                            if (response == null) {
                                Log.d("**", "response == null");
                                mIsEnd = true;
                                mUpdating = false;
                                return;
                            }

                            final ArrayList<SubscribeNewsListElement> moreData = new ArrayList<>();
                            mIsEnd = SubscribeNewsListAdapter.buildMore(response, moreData);

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mSubscribeNewsListAdapter.addData(moreData);
                                    mSubscribeNewsListAdapter.notifyDataSetChanged();
                                }
                            });

                            mUpdating = false;
                        }
                    });

                }

                @Override
                public void onSuccess(int statusCode, JSONObject response) {
                    super.onSuccess(statusCode, response);

                    final int statusCodeTmp = statusCode;
                    final JSONObject responseTmp = response;

                    ThreadPoolUtils.execute(new Runnable() {
                        @Override
                        public void run() {
                            if (statusCodeTmp == 200) {

                                final ArrayList<SubscribeNewsListElement> moreData = new ArrayList<>();
                                mIsEnd = SubscribeNewsListAdapter.buildMore(responseTmp, moreData);

                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mSubscribeNewsListAdapter.addData(moreData);
                                        mSubscribeNewsListAdapter.notifyDataSetChanged();
                                    }
                                });

                                AppDB.instance(getContext()).saveSubscribeNewsBeforeNew(mThemeId, lastNewsId, responseTmp, mSubscribeNewsListAdapter.getTotalSubscribeNewsMapData());

                            }
                            mUpdating = false;
                        }
                    });

                }
            });
            //            }
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
