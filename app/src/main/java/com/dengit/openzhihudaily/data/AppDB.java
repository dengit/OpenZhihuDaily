package com.dengit.openzhihudaily.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.dengit.openzhihudaily.model.NewsListElement;
import com.dengit.openzhihudaily.model.SubscribeNewsListElement;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;

/**
 * Created by dengit on 15/10/17.
 */
public class AppDB {
    private static AppDB mInstance;
    private SQLiteDatabase mDatabase;

    public AppDB(DBHelper helper) {
        mDatabase = helper.getWritableDatabase();
    }

    public static AppDB instance(Context context) {
        if (mInstance == null) {
            mInstance = new AppDB(new DBHelper(context));
        }

        return mInstance;
    }

    public JSONObject loadNewsLatest() {
        Cursor cursor = mDatabase.query("news_latest", null, null, null, null, null, null);
        try {
            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                String json = cursor.getString(cursor.getColumnIndex("json"));
                return new JSONObject(json);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("**", "Exception: " + e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    public void saveNewsLatest(JSONObject response) {
        ContentValues values = new ContentValues();
        values.put("json", response.toString());

        Cursor cursor = mDatabase.query("news_latest", null, null, null, null, null, null);
        try {
            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                int id = cursor.getInt(cursor.getColumnIndex("_id"));
                mDatabase.update("news_latest", values, "_id=" + id, null);
            } else {
                mDatabase.insert("news_latest", null, values);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

    }

    public JSONObject loadNewsMoreBefore(String date) {
        Cursor cursor = mDatabase.query("news_past", null, "date=" + date, null, null, null, null);
        try {
            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                String json = cursor.getString(cursor.getColumnIndex("json"));
                return new JSONObject(json);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("**", "Exception: " + e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    public void saveNewsMoreBefore(JSONObject response, String date) {
        ContentValues values = new ContentValues();
        values.put("date", Integer.valueOf(date));
        values.put("json", response.toString());

        Cursor cursor = mDatabase.query("news_past", null, "date=" + date, null, null, null, null);
        try {
            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                mDatabase.update("news_past", values, "date=" + date, null);
            } else {
                mDatabase.insert("news_past", null, values);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public JSONObject loadNewsSpecifiedThemeNew(long mThemeId) {
        JSONObject json = new JSONObject();

        Cursor cursor = mDatabase.query("theme_news", null, "theme_id=" + mThemeId, null, null, null, null, "20");

        Cursor cursorExtra = mDatabase.query("theme_news_extra", null, "theme_id=" + mThemeId, null, null, null, null, null);

        try {
            JSONObject extraJson = null;
            JSONArray arrayTmp = null;


            if (cursorExtra != null && cursorExtra.getCount() > 0 && cursorExtra.moveToFirst()) {
                String extraJsonStr = cursorExtra.getString(cursorExtra.getColumnIndex("json"));
                extraJson = new JSONObject(extraJsonStr);
            }

            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                arrayTmp = new JSONArray();
                do {
                    String jsonStr = cursor.getString(cursor.getColumnIndex("json"));
                    JSONObject jsonTmp = new JSONObject(jsonStr);
                    arrayTmp.put(jsonTmp);
                } while (cursor.moveToNext());

            }

            if (extraJson == null || arrayTmp == null) {
                Log.d("**", "extraJson == null || arrayTmp == null");
                return null;
            }

            json.put("color", extraJson.getInt("color"));
            json.put("name", extraJson.getString("name"));
            json.put("image", extraJson.getString("image"));
            json.put("background", extraJson.getString("background"));
            json.put("description", extraJson.getString("description"));
            json.put("image_source", extraJson.getString("image_source"));

            if (extraJson.has("editors")) {
                json.put("editors", extraJson.getJSONArray("editors"));
            }

            json.put("stories", arrayTmp);

            return json;

        } catch (Exception e) {
            e.printStackTrace();
            Log.d("**", "Exception: " + e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (cursorExtra != null) {
                cursorExtra.close();
            }
        }
        return null;
    }

    public void saveNewsSpecifiedThemeNew(long mThemeId, JSONObject response, LinkedHashMap<Integer, SubscribeNewsListElement> subscribeNewsMapData) {

        Cursor cursor = mDatabase.query("theme_news_extra", null, "theme_id=" + mThemeId, null, null, null, null);
        try {
            JSONObject extraJson = new JSONObject();
            extraJson.put("color", response.getLong("color"));
            extraJson.put("name", response.getString("name"));
            extraJson.put("image", response.getString("image"));
            extraJson.put("background", response.getString("background"));
            extraJson.put("description", response.getString("description"));
            extraJson.put("image_source", response.getString("image_source"));

            if (response.has("editors")) {
                extraJson.put("editors", response.getJSONArray("editors"));
            }

            ContentValues values = new ContentValues();
            values.put("theme_id", mThemeId);
            values.put("json", extraJson.toString());


            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                mDatabase.update("theme_news_extra", values, "theme_id=" + mThemeId, null);
            } else {
                mDatabase.insert("theme_news_extra", null, values);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("**", "Exception: " + e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        LinkedHashMap<Integer, SubscribeNewsListElement> newSubscribeNewsMapData = new LinkedHashMap<>();


        try {
            JSONArray storiesJson = response.getJSONArray("stories");

            for (int i = 0; i < storiesJson.length(); i++) {
                JSONObject item = storiesJson.getJSONObject(i);

                SubscribeNewsListElement element = new SubscribeNewsListElement();
                element.id = item.getInt("id");

                element.type = item.getInt("type");
                element.title = item.getString("title");

                if (item.has("images")) {
                    JSONArray images = item.getJSONArray("images");

                    for (int j = 0; j < images.length(); j++) {
                        element.images.add(images.getString(j));
                    }
                }

                newSubscribeNewsMapData.put(element.id, element);
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.d("**", "Exception: " + e);
        }

        if (subscribeNewsMapData.size() == 0) {

            cursor = mDatabase.query("theme_news", null, "theme_id=" + mThemeId, null, null, null, null);
            try {
                if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {

                    do {
                        String itemJson = cursor.getString(cursor.getColumnIndex("json"));

                        JSONObject item = new JSONObject(itemJson);

                        SubscribeNewsListElement element = new SubscribeNewsListElement();
                        element.id = item.getInt("id");

                        element.type = item.getInt("type");
                        element.title = item.getString("title");

                        if (item.has("images")) {
                            JSONArray images = item.getJSONArray("images");

                            for (int j = 0; j < images.length(); j++) {
                                element.images.add(images.getString(j));
                            }
                        }

                        subscribeNewsMapData.put(element.id, element);
                    } while (cursor.moveToNext());
                } else if (cursor != null && cursor.getCount() == 0) {
                    //                oldLatestRefreshId = -1;
                } else {
                    Log.d("**", "query refresh_id error");
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }

        }

        LinkedHashSet<Integer> newSet = new LinkedHashSet<>();
        Set<Integer> oldSet = subscribeNewsMapData.keySet();


        newSet.addAll(newSubscribeNewsMapData.keySet());
        newSet.addAll(oldSet);

        boolean needSave = false;

        if (newSet.size() != oldSet.size()) {
            needSave = true;
        } else {
            Integer[] newArray = new Integer[newSet.size()];
            Integer[] oldArray = new Integer[oldSet.size()];

            newSet.toArray(newArray);
            oldSet.toArray(oldArray);

            needSave = !Arrays.equals(newArray, oldArray);
        }


        if (!needSave) {
            Log.d("**", "no need save");
            return;
        }


        LinkedHashMap<Integer, SubscribeNewsListElement> newTotalSubscribeNewsMapData = new LinkedHashMap<>();

        mDatabase.beginTransaction();

        try {

            mDatabase.delete("theme_news", "theme_id=" + mThemeId, null);

            int orderId = 1;
            for (Iterator<Integer> it = newSet.iterator(); it.hasNext(); ) {
                int newsId = it.next();

                SubscribeNewsListElement element;

                if (newSubscribeNewsMapData.containsKey(newsId)) {
                    element = newSubscribeNewsMapData.get(newsId);
                } else if (subscribeNewsMapData.containsKey(newsId)) {
                    element = subscribeNewsMapData.get(newsId);
                } else {
                    Log.d("**", "no exist key");
                    return;
                }

                newTotalSubscribeNewsMapData.put(newsId, element);

                JSONObject json = new JSONObject();
                JSONArray arrayJson = new JSONArray();

                for (int j = 0; j < element.images.size(); j++) {
                    arrayJson.put(element.images.get(j));
                }

                json.put("id", element.id);
                json.put("type", element.type);
                json.put("title", element.title);
                json.put("images", arrayJson);


                ContentValues values = new ContentValues();
                values.put("theme_id", mThemeId);
                values.put("order_id", orderId++);
                values.put("news_id", newsId);
                values.put("json", json.toString());

                mDatabase.insert("theme_news", null, values);
            }

            mDatabase.setTransactionSuccessful();


        } catch (Exception e) {
            e.printStackTrace();
            Log.d("**", "Exception: " + e);
            return;
        } finally {
            mDatabase.endTransaction();
        }

        subscribeNewsMapData.clear();
        subscribeNewsMapData.putAll(newTotalSubscribeNewsMapData);
    }

    public void saveNewsDetail(int mNewsId, JSONObject response) {
        ContentValues values = new ContentValues();
        values.put("news_id", mNewsId);
        values.put("json", response.toString());

        Cursor cursor = mDatabase.query("news_detail", null, "news_id=" + mNewsId, null, null, null, null);
        try {
            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                mDatabase.update("news_detail", values, "news_id=" + mNewsId, null);
            } else {
                mDatabase.insert("news_detail", null, values);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public boolean getNewsDetailFavStatus(int mNewsId) {
        Cursor cursor = mDatabase.query("fav_news_detail", null, "news_id=" + mNewsId, null, null, null, null);
        try {
            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("**", "Exception: " + e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return false;
    }

    public void setNewsDetailFavStatus(NewsListElement newsInfo, boolean isFav) {

        JSONObject json = new JSONObject();
        JSONArray arr = new JSONArray();

        try {

            json.put("id", newsInfo.id);
            json.put("title", newsInfo.title);
            json.put("type", newsInfo.type);

            if (newsInfo.images.size() > 0) {
                for (int i = 0; i < newsInfo.images.size(); i++) {
                    arr.put(newsInfo.images.get(i));
                }

                json.put("images", arr);
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.d("**", "Exception e: " + e);
            return;
        }

        ContentValues values = new ContentValues();
        values.put("news_id", newsInfo.id);
        values.put("json", json.toString());

        Cursor cursor = mDatabase.query("fav_news_detail", null, "news_id=" + newsInfo.id, null, null, null, null);
        try {
            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                if (!isFav) {
                    mDatabase.delete("fav_news_detail", "news_id=" + newsInfo.id, null);
                } else {
                    Log.d("**", "setNewsDetailFavStatus record already exist");
                }
            } else {
                if (isFav) {
                    mDatabase.insert("fav_news_detail", null, values);
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public HashSet<Integer> loadNewsListWatched() {
        HashSet<Integer> newsListWatched = new HashSet<>();
        Cursor cursor = mDatabase.query("watched_news_detail", null, null, null, null, null, null);
        try {
            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                do {
                    newsListWatched.add(cursor.getInt(cursor.getColumnIndex("news_id")));
                } while (cursor.moveToNext());
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.d("**", "Exception: " + e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return newsListWatched;
    }

    public void setNewsDetailWatched(int mNewsId) {
        ContentValues values = new ContentValues();
        values.put("news_id", mNewsId);

        Cursor cursor = mDatabase.query("watched_news_detail", null, "news_id=" + mNewsId, null, null, null, null);
        try {
            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                Log.d("**", "already is watched news id: " + mNewsId);
            } else {
                mDatabase.insert("watched_news_detail", null, values);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public ArrayList<NewsListElement> loadFavNewsList() {
        ArrayList<NewsListElement> favNewsListData = new ArrayList<>();

        Cursor cursor = mDatabase.query("fav_news_detail", null, null, null, null, null, null);

        LinkedList<NewsListElement> tmpData = new LinkedList<>();
        try {
            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                do {
                    String jsonStr = cursor.getString(cursor.getColumnIndex("json"));
                    JSONObject json = new JSONObject(jsonStr);

                    NewsListElement element = new NewsListElement();
                    element.id = json.getInt("id");
                    element.type = json.getInt("type");
                    element.title = json.getString("title");

                    if (json.has("images")) {
                        JSONArray images = json.getJSONArray("images");

                        for (int j = 0; j < images.length(); j++) {
                            element.images.add(images.getString(j));
                        }

                    }

                    tmpData.addFirst(element);
                } while (cursor.moveToNext());

                if (tmpData.size() > 0) {
                    favNewsListData.addAll(tmpData);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("**", "Exception: " + e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return favNewsListData;
    }

    public JSONObject loadSubscribeNewsBeforeNew(int lastNewsId) {

        JSONObject json = new JSONObject();
        JSONArray stories = new JSONArray();

        int themeId = -1;
        int orderId = -1;
        int limit = 20;


        Cursor cursor = mDatabase.query("theme_news", null, "news_id=" + lastNewsId, null, null, null, null);

        try {
            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                themeId = cursor.getInt(cursor.getColumnIndex("theme_id"));
                orderId = cursor.getInt(cursor.getColumnIndex("order_id"));
            } else {
                Log.d("**", "loadSubscribeNewsBeforeNew error");
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("**", "Exception: " + e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        cursor = mDatabase.query("theme_news", null, "theme_id=" + themeId + " AND order_id > " + orderId, null, null, null, null, String.valueOf(limit));

        try {
            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                do {
                    int newsId = cursor.getInt(cursor.getColumnIndex("news_id"));
                    String jsonStr = cursor.getString(cursor.getColumnIndex("json"));
                    JSONObject jsonItem = new JSONObject(jsonStr);
                    stories.put(jsonItem);

                } while (cursor.moveToNext());

            } else if (cursor != null && cursor.getCount() == 0) {

            } else {
                Log.d("**", "loadSubscribeNewsBefore error");
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("**", "Exception: " + e);
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }


        if (stories.length() == 0) {
            Log.d("**", "load more is end");
            return null;
        }

        try {
            json.put("stories", stories);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("**", "Exception: " + e);
        }

        return json;
    }

    public void saveSubscribeNewsBeforeNew(int mThemeId, int lastNewsId, JSONObject response, LinkedHashMap<Integer, SubscribeNewsListElement> subscribeNewsMapData) {

        Log.d("**", "saveSubscribeNewsBeforeNew 1: " + subscribeNewsMapData.size());
        if (subscribeNewsMapData.size() == 0) {//todo subscribeNewsMapData.size() alwasy > 0?

            Cursor cursor = mDatabase.query("theme_news", null, "theme_id=" + mThemeId, null, null, null, null);
            try {
                if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {

                    do {
                        String itemJson = cursor.getString(cursor.getColumnIndex("json"));

                        JSONObject item = new JSONObject(itemJson);

                        SubscribeNewsListElement element = new SubscribeNewsListElement();
                        element.id = item.getInt("id");

                        element.type = item.getInt("type");
                        element.title = item.getString("title");

                        if (item.has("images")) {
                            JSONArray images = item.getJSONArray("images");

                            for (int j = 0; j < images.length(); j++) {
                                element.images.add(images.getString(j));
                            }
                        }

                        subscribeNewsMapData.put(element.id, element);
                    } while (cursor.moveToNext());
                } else if (cursor != null && cursor.getCount() == 0) {
                    //                oldLatestRefreshId = -1;
                } else {
                    Log.d("**", "query refresh_id error");
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }

        }

        if (!subscribeNewsMapData.containsKey(lastNewsId)) {
            Log.d("**", "saveSubscribeNewsBeforeNew error");
            return;
        }

        LinkedHashMap<Integer, SubscribeNewsListElement> splitA = new LinkedHashMap<>();
        LinkedHashMap<Integer, SubscribeNewsListElement> splitB = new LinkedHashMap<>();
        LinkedHashMap<Integer, SubscribeNewsListElement> split = splitA;

        for (Iterator<Integer> it = subscribeNewsMapData.keySet().iterator(); it.hasNext(); ) {
            int newsId = it.next();

            if (newsId == lastNewsId) {
                split.put(newsId, subscribeNewsMapData.get(newsId));
                split = splitB;
                continue;
            }

            split.put(newsId, subscribeNewsMapData.get(newsId));
        }


        LinkedHashMap<Integer, SubscribeNewsListElement> incSubscribeNewsMapData = new LinkedHashMap<>();


        try {
            JSONArray storiesJson = response.getJSONArray("stories");

            for (int i = 0; i < storiesJson.length(); i++) {
                JSONObject item = storiesJson.getJSONObject(i);

                SubscribeNewsListElement element = new SubscribeNewsListElement();
                element.id = item.getInt("id");

                element.type = item.getInt("type");
                element.title = item.getString("title");

                if (item.has("images")) {
                    JSONArray images = item.getJSONArray("images");

                    for (int j = 0; j < images.length(); j++) {
                        element.images.add(images.getString(j));
                    }
                }

                incSubscribeNewsMapData.put(element.id, element);
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.d("**", "Exception: " + e);
        }


        LinkedHashSet<Integer> newSet = new LinkedHashSet<>();
        Set<Integer> oldSet = splitB.keySet();

        newSet.addAll(incSubscribeNewsMapData.keySet());
        newSet.addAll(oldSet);

        boolean needSave = false;

        if (newSet.size() != oldSet.size()) {
            needSave = true;
        } else {
            Integer[] newArray = new Integer[newSet.size()];
            Integer[] oldArray = new Integer[oldSet.size()];

            newSet.toArray(newArray);
            oldSet.toArray(oldArray);

            needSave = !Arrays.equals(newArray, oldArray);
        }


        if (!needSave) {
            Log.d("**", "no need save");
            return;
        }


        LinkedHashMap<Integer, SubscribeNewsListElement> newTotalSubscribeNewsMapData = new LinkedHashMap<>();

        newTotalSubscribeNewsMapData.putAll(splitA);

        mDatabase.beginTransaction();

        try {

            mDatabase.delete("theme_news", "theme_id=" + mThemeId, null);

            for (Iterator<Integer> it = newSet.iterator(); it.hasNext(); ) {
                int newsId = it.next();

                SubscribeNewsListElement element;

                if (incSubscribeNewsMapData.containsKey(newsId)) {
                    element = incSubscribeNewsMapData.get(newsId);
                } else if (splitB.containsKey(newsId)) {
                    element = splitB.get(newsId);
                } else {
                    Log.d("**", "no exist key");
                    return;
                }

                newTotalSubscribeNewsMapData.put(newsId, element);
            }


            int orderId = 1;
            for (Iterator<Integer> it = newTotalSubscribeNewsMapData.keySet().iterator(); it.hasNext(); ) {
                int newsId = it.next();
                SubscribeNewsListElement element = newTotalSubscribeNewsMapData.get(newsId);

                JSONObject json = new JSONObject();
                JSONArray arrayJson = new JSONArray();

                for (int j = 0; j < element.images.size(); j++) {
                    arrayJson.put(element.images.get(j));
                }

                json.put("id", element.id);
                json.put("type", element.type);
                json.put("title", element.title);
                json.put("images", arrayJson);


                ContentValues values = new ContentValues();
                values.put("theme_id", mThemeId);
                values.put("order_id", orderId++);
                values.put("news_id", newsId);
                values.put("json", json.toString());

                mDatabase.insert("theme_news", null, values);
            }

            mDatabase.setTransactionSuccessful();


        } catch (Exception e) {
            e.printStackTrace();
            Log.d("**", "Exception: " + e);
            return;
        } finally {
            mDatabase.endTransaction();
        }

        subscribeNewsMapData.clear();
        subscribeNewsMapData.putAll(newTotalSubscribeNewsMapData);

        Log.d("**", "saveSubscribeNewsBeforeNew 2: " + subscribeNewsMapData.size());
    }

    public JSONObject loadNewsDetail(int mNewsId) {
        Cursor cursor = mDatabase.query("news_detail", null, "news_id=" + mNewsId, null, null, null, null);
        try {
            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                String json = cursor.getString(cursor.getColumnIndex("json"));
                return new JSONObject(json);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("**", "Exception: " + e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return null;
    }

    public void saveNewsThemes(JSONObject response) {


        HashSet<Integer> subscribedThemeIds = new HashSet<>();

        Cursor cursor = mDatabase.query("themes", null, "is_subscribed=" + 1, null, null, null, null);
        try {
            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                do {
                    subscribedThemeIds.add(cursor.getInt(cursor.getColumnIndex("theme_id")));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("**", "subscribedThemeIds error");
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        mDatabase.beginTransaction();

        try {
            JSONArray themes = response.getJSONArray("others");

            mDatabase.delete("themes", "_id > 0", null);

            for (int i = 0; i < themes.length(); i++) {
                JSONObject item = themes.getJSONObject(i);
                int themeId = item.getInt("id");

                ContentValues values = new ContentValues();
                values.put("theme_id", themeId);
                values.put("is_subscribed", subscribedThemeIds.contains(themeId) ? 1 : 0);
                values.put("json", item.toString());

                mDatabase.insert("themes", null, values);
            }

            mDatabase.setTransactionSuccessful();

        } catch (Exception e) {
            e.printStackTrace();
            Log.d("**", "saveNewsThemes, e: " + e);
        } finally {
            mDatabase.endTransaction();
        }
    }

    public JSONObject loadNewsThemes() {
        JSONObject json = new JSONObject();
        JSONArray arr = new JSONArray();

        Cursor cursor = mDatabase.query("themes", null, null, null, null, null, null);
        try {
            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                do {
                    String itemJsonStr = cursor.getString(cursor.getColumnIndex("json"));
                    JSONObject itemJson = new JSONObject(itemJsonStr);
                    arr.put(itemJson);
                } while (cursor.moveToNext());

                json.put("others", arr);
                return json;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("**", "Exception: " + e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return null;
    }

}

//todo drawer list request again
//todo HomeFragement onScroll isEnd
//todo  onScroll updating bug sometimes happen
