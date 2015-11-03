package com.dengit.openzhihudaily.data;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.GZIPInputStream;

/**
 * Created by dengit on 15/10/12.
 */
public class HttpAccessor {

    public static final String URL_STARTUP_IMAGE = "http://news-at.zhihu.com/api/4/start-image/%s"; //%s->1080*1776
    public static final String URL_NEWS_LATEST = "http://news-at.zhihu.com/api/4/news/latest";
    public static final String URL_NEWS_DETAIL = "http://news-at.zhihu.com/api/4/news/%s"; //%s->news_id
    public static final String URL_NEWS_BEFORE = "http://news.at.zhihu.com/api/4/news/before/%s"; //%s->date
    public static final String URL_SUBSCRIBE_NEWS_BEFORE = "http://news-at.zhihu.com/api/4/theme/%s/before/%s"; //%s->themeId %s->lastNewsId
    public static final String URL_NEWS_EXTRA = "http://news-at.zhihu.com/api/4/story-extra/%s"; //%s->news_id
    public static final String URL_NEWS_THEMES = "http://news-at.zhihu.com/api/4/themes";
    public static final String URL_NEWS_THEME_DETAIL = "http://news-at.zhihu.com/api/4/theme/%s"; //%s->theme_id
    public static final String URL_NEWS_LONG_COMMENT = "http://news-at.zhihu.com/api/4/story/%s/long-comments"; //%s->news_id
    public static final String URL_NEWS_SHORT_COMMENT = "http://news-at.zhihu.com/api/4/story/%s/short-comments"; //%s->news_id
    public static final String URL_NEWS_SHORT_COMMENT_BEFORE = "http://news-at.zhihu.com/api/4/story/%s/short-comments/before/%s"; //%s->news_id %s->comment_id


    private static HttpAccessor mInstance;

    private HttpAccessor() {

    }

    public static HttpAccessor instance() {
        if (mInstance == null) {
            mInstance = new HttpAccessor();
        }

        return mInstance;
    }

    private void get(String request, JsonHttpResponseHandler handler) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(10 * 1000);
        client.get(request, handler);
    }


    private String getBySync(String request) {

        HttpURLConnection urlConn = null;
        try {
            URL url = new URL(request);
            urlConn = (HttpURLConnection) url.openConnection();
            urlConn.setConnectTimeout(10 * 1000);
            urlConn.setReadTimeout(10 * 1000);
            urlConn.setRequestProperty("Accept-Encoding", "gzip");
            InputStreamReader in = new InputStreamReader(new GZIPInputStream(urlConn.getInputStream()));

            BufferedReader buffer = new BufferedReader(in);

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = buffer.readLine()) != null) {
                sb.append(line).append('\n');
            }

            return sb.toString();

        } catch (Exception e) {
            e.printStackTrace();
            Log.d("**", "Exception e: " + e);
            return null;
        } finally {
            if (urlConn != null) {
                urlConn.disconnect();
            }
        }


    }


    private Bitmap getBitmapBySync(String request) {


        HttpURLConnection urlConn = null;
        try {
            URL url = new URL(request);
            urlConn = (HttpURLConnection) url.openConnection();
            urlConn.setConnectTimeout(10 * 1000);
            urlConn.setReadTimeout(10 * 1000);
            urlConn.setRequestProperty("Accept-Encoding", "gzip");
//            InputStream in = new GZIPInputStream(urlConn.getInputStream());
            InputStream in = urlConn.getInputStream();

            BufferedInputStream bi = new BufferedInputStream(in);

            ByteArrayOutputStream bo = new ByteArrayOutputStream();

            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = bi.read(buffer)) != -1) {
                bo.write(buffer, 0, len);
            }

            byte[] bytes = bo.toByteArray();

            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

        } catch (Exception e) {
            e.printStackTrace();
            Log.d("**", "Exception e: " + e);

        } finally {
            if (urlConn != null) {
                urlConn.disconnect();
            }
        }

        return null;
    }


    public void getStartupImage(String resolution, JsonHttpResponseHandler handler) {
        get(String.format(URL_STARTUP_IMAGE, resolution), handler);
    }

    public Bitmap getStartupImageBySync(String request) {
        return getBitmapBySync(request);
    }

    public void getNewsLatest(JsonHttpResponseHandler handler) {
        get(URL_NEWS_LATEST, handler);
    }

    public void getNewsDetail(int newsId, JsonHttpResponseHandler handler) {
        get(String.format(URL_NEWS_DETAIL, String.valueOf(newsId)), handler);
    }

    public void getLongComment(int newsId, JsonHttpResponseHandler handler) {
        get(String.format(URL_NEWS_LONG_COMMENT, String.valueOf(newsId)), handler);
    }

    public void getShortComment(int newsId, JsonHttpResponseHandler handler) {
        get(String.format(URL_NEWS_SHORT_COMMENT, String.valueOf(newsId)), handler);
    }


    public void getShortCommentBefore(int newsId, int commentId, JsonHttpResponseHandler handler) {
        get(String.format(URL_NEWS_SHORT_COMMENT_BEFORE, String.valueOf(newsId), String.valueOf(commentId)), handler);
    }

    public void getNewsExtra(int newsId, JsonHttpResponseHandler handler) {
        get(String.format(URL_NEWS_EXTRA, String.valueOf(newsId)), handler);
    }

    public String getNewsDetailBySync(int newsId) {
        return getBySync(String.format(URL_NEWS_DETAIL, String.valueOf(newsId)));
    }

    public void getNewsThemes(JsonHttpResponseHandler handler) {
        get(URL_NEWS_THEMES, handler);
    }

    public void getNewsSpecifiedTheme(int themeId, JsonHttpResponseHandler handler) {
        get(String.format(URL_NEWS_THEME_DETAIL, String.valueOf(themeId)), handler);
    }

    public void getNewsBefore(String date, JsonHttpResponseHandler handler) {
        get(String.format(URL_NEWS_BEFORE, date), handler);
    }

    public void getSubscribeNewsBefore(int themeId, int lastNewsId, JsonHttpResponseHandler handler) {
        get(String.format(URL_SUBSCRIBE_NEWS_BEFORE, String.valueOf(themeId), String.valueOf(lastNewsId)), handler);
    }


}
