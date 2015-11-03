package com.dengit.openzhihudaily.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by dengit on 15/10/17.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "openzhihudaily.db";
    private static final String SQL_CREATE_TABLE_NEWS_LATEST = "CREATE TABLE news_latest (_id INTEGER PRIMARY KEY AUTOINCREMENT, json TEXT NOT NULL);";
    private static final String SQL_CREATE_TABLE_NEWS_PAST = "CREATE TABLE news_past (_id INTEGER PRIMARY KEY AUTOINCREMENT, date INTEGER NOT NULL, json TEXT NOT NULL);";
    private static final String SQL_CREATE_TABLE_NEWS_DETAIL = "CREATE TABLE news_detail (_id INTEGER PRIMARY KEY AUTOINCREMENT, news_id INTEGER NOT NULL, json TEXT NOT NULL);";
    private static final String SQL_CREATE_TABLE_NEWS_DETAIL_FAV = "CREATE TABLE fav_news_detail (_id INTEGER PRIMARY KEY AUTOINCREMENT, news_id INTEGER NOT NULL, json TEXT NOT NULL);";
    private static final String SQL_CREATE_TABLE_NEWS_DETAIL_WATCHED = "CREATE TABLE watched_news_detail (_id INTEGER PRIMARY KEY AUTOINCREMENT, news_id INTEGER NOT NULL);";
    private static final String SQL_CREATE_TABLE_THEME_NEWS = "CREATE TABLE theme_news (_id INTEGER PRIMARY KEY AUTOINCREMENT, theme_id INTEGER NOT NULL, order_id INTEGER NOT NULL, news_id INTEGER NOT NULL, json TEXT NOT NULL);";
    private static final String SQL_CREATE_TABLE_THEME_EXTRA = "CREATE TABLE theme_news_extra (_id INTEGER PRIMARY KEY AUTOINCREMENT, theme_id INTEGER NOT NULL, json TEXT NOT NULL);";
    private static final String SQL_CREATE_TABLE_THEMES = "CREATE TABLE themes (_id INTEGER PRIMARY KEY AUTOINCREMENT, theme_id INTEGER NOT NULL, json TEXT NOT NULL, is_subscribed INTEGER NOT NULL);";


    private static final String SQL_CREATE_INDEX_ON_NEWS_PAST = "CREATE UNIQUE INDEX date ON news_past (date);";
    private static final String SQL_CREATE_INDEX_ON_NEWS_DETAIL = "CREATE UNIQUE INDEX news_id ON news_detail (news_id);";
    private static final String SQL_CREATE_INDEX_ON_FAV_NEWS_DETAIL = "CREATE UNIQUE INDEX fav_news_id ON fav_news_detail (news_id);";
    private static final String SQL_CREATE_INDEX_ON_WATCHED_NEWS_DETAIL = "CREATE UNIQUE INDEX watched_news_id ON watched_news_detail (news_id);";
    private static final String SQL_CREATE_INDEX_ON_WATCHED_THEME_NEWS_1 = "CREATE UNIQUE INDEX theme_news_id ON theme_news (news_id);";
    private static final String SQL_CREATE_INDEX_ON_WATCHED_THEME_NEWS_2 = "CREATE INDEX theme_id ON theme_news (theme_id);";


    public DBHelper(Context context) {
        this(context, DB_NAME, null, 1);
    }

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_NEWS_LATEST);
        db.execSQL(SQL_CREATE_TABLE_NEWS_PAST);
        db.execSQL(SQL_CREATE_TABLE_NEWS_DETAIL);
        db.execSQL(SQL_CREATE_TABLE_NEWS_DETAIL_FAV);
        db.execSQL(SQL_CREATE_TABLE_NEWS_DETAIL_WATCHED);
        db.execSQL(SQL_CREATE_TABLE_THEME_NEWS);
        db.execSQL(SQL_CREATE_TABLE_THEME_EXTRA);
        db.execSQL(SQL_CREATE_TABLE_THEMES);

        db.execSQL(SQL_CREATE_INDEX_ON_NEWS_PAST);
        db.execSQL(SQL_CREATE_INDEX_ON_NEWS_DETAIL);
        db.execSQL(SQL_CREATE_INDEX_ON_FAV_NEWS_DETAIL);
        db.execSQL(SQL_CREATE_INDEX_ON_WATCHED_NEWS_DETAIL);
        db.execSQL(SQL_CREATE_INDEX_ON_WATCHED_THEME_NEWS_1);
        db.execSQL(SQL_CREATE_INDEX_ON_WATCHED_THEME_NEWS_2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
