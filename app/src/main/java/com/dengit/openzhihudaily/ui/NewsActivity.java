package com.dengit.openzhihudaily.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dengit.openzhihudaily.R;
import com.dengit.openzhihudaily.data.AppDB;
import com.dengit.openzhihudaily.data.HttpAccessor;
import com.dengit.openzhihudaily.model.NewsListElement;
import com.dengit.openzhihudaily.utils.ThreadPoolUtils;
import com.dengit.openzhihudaily.utils.Utils;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

/**
 * Created by dengit on 15/10/14.
 */
public class NewsActivity extends AppCompatActivity {

    private int mNewsId;
    private NewsListElement mNewsInfo;
    private TextView mCommentView;
    private TextView mLikeView;

    private WebView mWebView;
    private NewsDetail mNewsDetail = new NewsDetail();
    private NewsDetailExtra mNewsDetailExtra = new NewsDetailExtra();

    @Override
    public void onBackPressed() {
        Intent data = new Intent();
        data.putExtra("news_id", mNewsId);
        setResult(RESULT_OK, data);
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mNewsInfo = (NewsListElement) getIntent().getSerializableExtra("news_info");

        mNewsId = mNewsInfo.id;

        setContentView(R.layout.activity_news);

        Toolbar toolbar = (Toolbar) findViewById(R.id.news_toolbar);
        setSupportActionBar(toolbar);

        ImageView homeUp = (ImageView) toolbar.findViewById(R.id.home_up_image);
        mCommentView = (TextView) toolbar.findViewById(R.id.comment_text);
        View commentLayout = toolbar.findViewById(R.id.comment_layout);
        commentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setClass(NewsActivity.this, CommentActivity.class);

                intent.putExtra("newsId", mNewsId);
                intent.putExtra("longCommentCount", mNewsDetailExtra.long_comments);
                intent.putExtra("shortCommentCount", mNewsDetailExtra.short_comments);
                startActivity(intent);
            }
        });

        mLikeView = (TextView) toolbar.findViewById(R.id.like_text);
        homeUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent data = new Intent();
                data.putExtra("news_id", mNewsId);
                setResult(RESULT_OK, data);

                finish();
            }
        });
        final ImageView favAction = (ImageView) toolbar.findViewById(R.id.fav_image);
        favAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ThreadPoolUtils.execute(new Runnable() {
                    @Override
                    public void run() {
                        final boolean isFav = AppDB.instance(NewsActivity.this).getNewsDetailFavStatus(mNewsId);
                        AppDB.instance(NewsActivity.this).setNewsDetailFavStatus(mNewsInfo, !isFav);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                if (isFav) {
                                    favAction.setImageResource(R.drawable.favorites);
                                    Toast.makeText(NewsActivity.this, R.string.unfav, Toast.LENGTH_SHORT).show();
                                } else {
                                    favAction.setImageResource(R.drawable.collected);
                                    Toast.makeText(NewsActivity.this, R.string.fav, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
            }
        });

        ThreadPoolUtils.execute(new Runnable() {
            @Override
            public void run() {
                final boolean isFav = AppDB.instance(NewsActivity.this).getNewsDetailFavStatus(mNewsId);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (isFav) {
                            favAction.setImageResource(R.drawable.collected);
                        } else {
                            favAction.setImageResource(R.drawable.favorites);
                        }
                    }
                });
            }
        });


        ThreadPoolUtils.execute(new Runnable() {
            @Override
            public void run() {
                AppDB.instance(NewsActivity.this).setNewsDetailWatched(mNewsId);
            }
        });

        View toTop = toolbar.findViewById(R.id.return_to_top);
        toTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWebView.scrollTo(0, 0);
            }
        });
        mWebView = (WebView) findViewById(R.id.news_webview);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return true;
            }
        });

        HttpAccessor.instance().getNewsDetail(mNewsId, new DetailJsonHttpResponseHandler());
        HttpAccessor.instance().getNewsExtra(mNewsId, new JsonHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, Throwable e, JSONObject errorResponse) {
                super.onFailure(statusCode, e, errorResponse);
            }

            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                super.onSuccess(statusCode, response);

                if (statusCode == 200) {

                    mNewsDetailExtra.build(response);
                    mCommentView.setText(String.valueOf(mNewsDetailExtra.comments));
                    mLikeView.setText(String.valueOf(mNewsDetailExtra.popularity));
                }
            }
        });

    }

    private class DetailJsonHttpResponseHandler extends JsonHttpResponseHandler {
        @Override
        public void onFailure(int statusCode, Throwable e, JSONObject errorResponse) {
            super.onFailure(statusCode, e, errorResponse);

            ThreadPoolUtils.execute(new Runnable() {
                @Override
                public void run() {
                    JSONObject response = AppDB.instance(NewsActivity.this).loadNewsDetail(mNewsId);

                    if (response == null) {
                        Log.d("**", "** response == null");
                        return;
                    }

                    mNewsDetail.build(response);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            webViewLoadData(mNewsDetail);
                        }
                    });
                }
            });
        }

        @Override
        public void onSuccess(int statusCode, JSONObject response) {
            super.onSuccess(statusCode, response);

            final JSONObject responseTmp = response;


            if (statusCode == 200) {

                ThreadPoolUtils.execute(new Runnable() {
                    @Override
                    public void run() {
                        mNewsDetail.build(responseTmp);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                webViewLoadData(mNewsDetail);
                            }
                        });

                        AppDB.instance(NewsActivity.this).saveNewsDetail(mNewsId, responseTmp);
                    }
                });
            }
        }
    }

    private void webViewLoadData(NewsDetail mNewsDetail) {
        mWebView.loadDataWithBaseURL(null, mNewsDetail.buildHtml(this), "text/html", "UTF-8", null);
    }


    private static class NewsDetail {


        private static final String TAG = "**NewsDetail";

        public String buildHtml(Context context) {
            try {
                //todo body is null when response.type = 1
                String template = Utils.readFileFromAssets(context, "template.html");
                String html = template.replace("{content}", body);
    
    
                String imgHolder = "<div class=\"img-wrap\">" +
                                    "<h1 class=\"headline-title\">%s</h1>" +
                                    "<span class=\"img-source\">%s</span>" +
                                    "<img src=\"%s\" alt=\"\">" +
                                    "<div class=\"img-mask\"></div>";
    
    
                imgHolder = String.format(imgHolder, title, image_source, image);
    
                String oldImgHolder = "<div class=\"img-place-holder\">";
    
                return html.replace(oldImgHolder, imgHolder);
            
            } catch (Exception e) {
                    e.printStackTrace();
            }

            return "";
        }

        private class NewsDetailSection {
            public int id;
            public String thumbnail;
            public String name;
        }


        public int id; //": 7278565,
        public String image_source; //": "《念念》",
        public String title;
        public String image;
        public String share_url;
        public String body; //html
        public NewsDetailSection section = new NewsDetailSection();

        public void build(JSONObject response) {
            if (response == null) {
                Log.d(TAG, "** response == null");
                return;
            }

            if (!response.has("body")) {
                Log.d(TAG, "** !response.has(\"body\")");
                return;
            }

            try {

                id = response.getInt("id");
                image_source = response.has("image_source") ? response.getString("image_source") : "";
                image = response.has("image") ? response.getString("image") : "";
                title = response.getString("title");
                share_url = response.getString("share_url");
                body = response.getString("body");

                if (response.has("section")) {

                    JSONObject entry = response.getJSONObject("section");

                    section.id = entry.getInt("id");
                    section.thumbnail = entry.getString("thumbnail");
                    section.name = entry.getString("name");
                }

            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, "** " + e);
            }
        }
    }


    public static class NewsDetailExtra {
        public int comments;
        public int long_comments;
        public int short_comments;
        public int popularity;
        public boolean vote_status;
        public boolean favorite;

        public void build(JSONObject response) {

            try {
                comments = response.getInt("comments");
                long_comments = response.getInt("long_comments");
                short_comments = response.getInt("short_comments");
                popularity = response.getInt("popularity");
                vote_status = response.has("vote_status") && response.getBoolean("vote_status");
                favorite = response.has("favorite") && response.getBoolean("favorite");

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
