package com.dengit.openzhihudaily.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.dengit.openzhihudaily.R;
import com.dengit.openzhihudaily.adapter.DrawerListAdapter;
import com.dengit.openzhihudaily.data.AppDB;
import com.dengit.openzhihudaily.data.HttpAccessor;
import com.dengit.openzhihudaily.fragment.FavFragment;
import com.dengit.openzhihudaily.fragment.HomeFragment;
import com.dengit.openzhihudaily.fragment.SubscribeFragment;
import com.dengit.openzhihudaily.model.DrawerListElement;
import com.dengit.openzhihudaily.utils.ThreadPoolUtils;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SubscribeFragment.OnFragmentInteractionListener {

    private Fragment mCurrFragment;
    private ListView mDrawerList;

    private DrawerListAdapter mDrawerListAdapter;
    private TextView draw_download_progress;
    private View mDrawerHeaderFirstItem;
    private Toolbar mToolbar;
    private boolean mFavFragOpened = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mToolbar.setTitle(HomeFragment.HOME_TITLE);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                //todo load themes from net or db
            }
        };
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        mDrawerList = (ListView) drawer.findViewById(R.id.drawer_theme_list);
        View drawerHeader = LayoutInflater.from(this).inflate(R.layout.nav_header_main, null);
        mDrawerHeaderFirstItem = LayoutInflater.from(this).inflate(R.layout.nav_header_first_item, null);
        mDrawerList.addHeaderView(drawerHeader);
        mDrawerList.addHeaderView(mDrawerHeaderFirstItem);

        drawerHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        View fav = drawerHeader.findViewById(R.id.fav_layout);
        fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!mFavFragOpened) {
                    mFavFragOpened = true;
                    invalidateOptionsMenu();
                }

                switchTheme("0 条收藏", new FavFragment());
            }
        });

        draw_download_progress = (TextView) drawerHeader.findViewById(R.id.draw_download_progress_text);
        View draw_download = drawerHeader.findViewById(R.id.draw_download);
        draw_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                new DownloadOfflineTask().execute();
            }
        });

        mDrawerHeaderFirstItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mFavFragOpened) {
                    mFavFragOpened = false;
                    invalidateOptionsMenu();
                }

                switchSelectedBackground(DrawerListAdapter.HOME_POS); //from others to home
                switchTheme(HomeFragment.HOME_TITLE, new HomeFragment());
            }
        });

        mDrawerListAdapter = new DrawerListAdapter(this);
        mDrawerList.setAdapter(mDrawerListAdapter);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("**", "click position: " + position);

                if (mFavFragOpened) {
                    mFavFragOpened = false;
                    invalidateOptionsMenu();
                }

                switchSelectedBackground(position - mDrawerList.getHeaderViewsCount());

                DrawerListElement element = (DrawerListElement) mDrawerListAdapter.getItem(position - mDrawerList.getHeaderViewsCount());

                switchTheme(element.name, SubscribeFragment.newInstance(element.id));
            }
        });


        Fragment fragment = new HomeFragment();
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.container, fragment).commit();

        mCurrFragment = fragment;
        HttpAccessor.instance().getNewsThemes(new JsonHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Throwable e, JSONObject errorResponse) {
                super.onFailure(statusCode, e, errorResponse);

                ThreadPoolUtils.execute(new Runnable() {
                    @Override
                    public void run() {
                        final JSONObject response = AppDB.instance(MainActivity.this).loadNewsThemes();
                        if (response == null) {
                            Log.d("**", "saveNewsThemes response == null");
                            return;
                        }


                        final ArrayList<DrawerListElement> drawerListData = DrawerListAdapter.build(response);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mDrawerListAdapter.resetData(drawerListData);
                                mDrawerListAdapter.notifyDataSetChanged();
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

                            final ArrayList<DrawerListElement> drawerListData = DrawerListAdapter.build(responseTmp);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mDrawerListAdapter.resetData(drawerListData);
                                    mDrawerListAdapter.notifyDataSetChanged();
                                }
                            });

                            AppDB.instance(MainActivity.this).saveNewsThemes(responseTmp);
                        }
                    });

                }
            }
        });
    }

    private void switchSelectedBackground(int newPos) {

        int currCategoryPos = mDrawerListAdapter.getCurrCategoryPos();

        if (currCategoryPos == newPos) {
            return;
        }

        boolean invalidMenu = false;
        if (currCategoryPos == DrawerListAdapter.HOME_POS) { //from home to others
            mDrawerHeaderFirstItem.setBackgroundResource(R.color.drawer_item_normal);
            invalidMenu = true;
        } else if (newPos == DrawerListAdapter.HOME_POS) { //from others to home
            mDrawerHeaderFirstItem.setBackgroundResource(R.color.drawer_item_selected);
            invalidMenu = true;
        }

        mDrawerListAdapter.setSelectedItem(newPos);

        if (invalidMenu) {
            supportInvalidateOptionsMenu();
        }
    }

    private void switchTheme(String title, Fragment toFragment) {
        mToolbar.setTitle(title);
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().remove(mCurrFragment).commit();
        manager.beginTransaction().replace(R.id.container, toFragment).commit();
        mCurrFragment = toFragment;

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        if (mFavFragOpened) {
            menu.clear();
            return true;
        }

        int currCategoryPos = mDrawerListAdapter.getCurrCategoryPos();

        if (currCategoryPos == DrawerListAdapter.HOME_POS) {
            getMenuInflater().inflate(R.menu.main, menu);
        } else {
            getMenuInflater().inflate(R.menu.subcribe, menu);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
    }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        return false;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private class DownloadOfflineTask extends AsyncTask<Void, Integer, Void> {

        @Override
        protected Void doInBackground(Void... params) {


            JSONObject newsLatest = AppDB.instance(MainActivity.this).loadNewsLatest();

            if (newsLatest == null) {
                Log.d("**", "newsLatest load null");
                return null;
            }

            ArrayList<Integer> newsLatestIds = new ArrayList<>();

            try {

                JSONArray stories = newsLatest.getJSONArray("stories");
                JSONArray top_stories = newsLatest.getJSONArray("top_stories");

                for (int i = 0; i < top_stories.length(); i++) {
                    newsLatestIds.add(top_stories.getJSONObject(i).getInt("id"));
                }

                for (int i = 0; i < stories.length(); i++) {
                    newsLatestIds.add(stories.getJSONObject(i).getInt("id"));
                }

                int count = newsLatestIds.size();
                for (int i = 0; i < count; i++) {
                    int newsId = newsLatestIds.get(i);
                    String response = HttpAccessor.instance().getNewsDetailBySync(newsId);

                    if (response.length() == 0) {
                        Log.d("*11*", "newsId: " + newsId + ", response.len:" + response.length());
                    }

                    JSONObject responseJson = new JSONObject(response);
                    AppDB.instance(MainActivity.this).saveNewsDetail(newsId, responseJson);

                    publishProgress((i + 1) * 100 / count);
                }

            } catch (Exception e) {
                e.printStackTrace();
                Log.d("**", "Exception e: " + e);
            }

            return null;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            int progressPercent = values[0];
            String text = progressPercent == 100 ? "完成" : String.valueOf(progressPercent) + "%";
            draw_download_progress.setText(text);
        }
    }
}
