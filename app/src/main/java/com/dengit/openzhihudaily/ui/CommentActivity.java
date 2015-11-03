package com.dengit.openzhihudaily.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.dengit.openzhihudaily.R;
import com.dengit.openzhihudaily.adapter.CommentListAdapter;
import com.dengit.openzhihudaily.data.HttpAccessor;
import com.dengit.openzhihudaily.model.CommentElement;
import com.dengit.openzhihudaily.utils.ThreadPoolUtils;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by dengit on 15/10/26.
 */
public class CommentActivity extends AppCompatActivity {

    private ListView mListView;
    private CommentListAdapter mCommentListAdapter;
    private int mNewsId;
    private int mLongCommentCount;
    private int mShortCommentCount;
    private Toolbar mToolbar;
    private View mFooterLayout;
    private boolean mShortCommentShowed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        mNewsId = getIntent().getIntExtra("newsId", 0);
        mLongCommentCount = getIntent().getIntExtra("longCommentCount", 0);
        mShortCommentCount = getIntent().getIntExtra("shortCommentCount", 0);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        mToolbar = (Toolbar) findViewById(R.id.comment_toobar);

        View back = mToolbar.findViewById(R.id.comment_toolbar_back_img);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TextView title = (TextView) mToolbar.findViewById(R.id.comment_toolbar_title);

        title.setText("" + (mLongCommentCount + mShortCommentCount) + "条点评");


        View headerLayout = LayoutInflater.from(this).inflate(R.layout.comment_list_view_header, null, false);
        TextView longCommentView = (TextView) headerLayout.findViewById(R.id.long_comment_tv);

        longCommentView.setText("" + mLongCommentCount + "条长评");
        longCommentView.setTextColor(Color.parseColor("#474747"));
        mFooterLayout = LayoutInflater.from(this).inflate(R.layout.comment_list_view_footer, null, false);

        TextView shortCommentView = (TextView) mFooterLayout.findViewById(R.id.short_comment_tv);

        shortCommentView.setText("" + mShortCommentCount + "条短评");
        shortCommentView.setTextColor(Color.parseColor("#474747"));

        mListView = (ListView) findViewById(R.id.comment_list_view);

        mListView.addHeaderView(headerLayout);
        mListView.addFooterView(mFooterLayout);

        mFooterLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mShortCommentShowed) {
                    mCommentListAdapter.clearShortComment();
                    mListView.addFooterView(mFooterLayout);
                    mCommentListAdapter.notifyDataSetChanged();
                    mShortCommentShowed = false;
                } else {

                    HttpAccessor.instance().getShortComment(mNewsId, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, final JSONObject response) {
                            super.onSuccess(statusCode, response);

                            if (statusCode == 200) {
                                final JSONObject responseTmp = response;
                                ThreadPoolUtils.execute(new Runnable() {
                                    @Override
                                    public void run() {

                                        final ArrayList<CommentElement> shortComments = CommentListAdapter.buildShortComment(responseTmp);

                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {

                                                mCommentListAdapter.setShortDividerView(mFooterLayout);
                                                mCommentListAdapter.resetShortComment(shortComments);
                                                mListView.removeFooterView(mFooterLayout);
                                                mCommentListAdapter.notifyDataSetChanged();

                                                mShortCommentShowed = true;
                                            }
                                        });
                                    }
                                });
                            }
                        }
                    });


                }
            }
        });
        int headerHeight = headerLayout.getMeasuredHeight();
        int footerHeight = mFooterLayout.getMeasuredHeight();
        int toolbarHeight = mToolbar.getMeasuredHeight();

        mCommentListAdapter = new CommentListAdapter(this, toolbarHeight, headerHeight, footerHeight);
        mListView.setAdapter(mCommentListAdapter);

        HttpAccessor.instance().getLongComment(mNewsId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                super.onSuccess(statusCode, response);

                if (statusCode == 200) {
                    final JSONObject responseTmp = response;
                    ThreadPoolUtils.execute(new Runnable() {
                        @Override
                        public void run() {
                            final ArrayList<CommentElement> longComments = CommentListAdapter.build(responseTmp);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mCommentListAdapter.resetData(longComments);
                                    mCommentListAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    });
                }
            }
        });

    }
}
