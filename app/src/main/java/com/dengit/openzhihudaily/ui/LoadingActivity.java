package com.dengit.openzhihudaily.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import com.dengit.openzhihudaily.R;
import com.dengit.openzhihudaily.data.HttpAccessor;
import com.dengit.openzhihudaily.utils.ThreadPoolUtils;
import com.dengit.openzhihudaily.utils.Utils;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;

public class LoadingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        final ImageView imageView = (ImageView) findViewById(R.id.loading_image);

        DisplayMetrics outMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(outMetrics);

        final String resolution = "" + outMetrics.widthPixels + "*" + outMetrics.heightPixels;

        final ScaleAnimation scaleAnimation = new ScaleAnimation(1f, 1.1f, 1f, 1.1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(3000);
        scaleAnimation.setInterpolator(this, android.R.anim.accelerate_decelerate_interpolator);

        if (isStartupImageFileValid(resolution)) {

            ThreadPoolUtils.execute(new Runnable() {
                @Override
                public void run() {
                    final Bitmap bitmap = BitmapFactory.decodeFile(getStartupImageFilePath(resolution));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            imageView.setImageBitmap(bitmap);
                            imageView.startAnimation(scaleAnimation);
                        }
                    });
                }
            });

        } else {
            imageView.setImageResource(R.drawable.splash);
            imageView.startAnimation(scaleAnimation);
        }


        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                Intent intent = new Intent(LoadingActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        }, 3000);


        HttpAccessor.instance().getStartupImage(resolution, new JsonHttpResponseHandler() {
            @Override
            public void onFailure(Throwable e, JSONObject errorResponse) {
                super.onFailure(e, errorResponse);
            }

            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                super.onSuccess(statusCode, response);

                if (statusCode == 200 && response.has("img")) {

                    final JSONObject responseTmp = response;
                    ThreadPoolUtils.execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                String imgUrl = responseTmp.getString("img");
                                final String imgSource = responseTmp.getString("text");
                                Log.d("LoadingActivity", "img: " + imgUrl);

                                Bitmap imageBitmap = HttpAccessor.instance().getStartupImageBySync(imgUrl);
                                if (imageBitmap != null) {
                                    saveStartupImageAsFile(resolution, imageBitmap);
                                } else {
                                    Log.d("*********", "saveStartupImageAsFile imageBitmap is null");
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

                }
            }

        });

    }

    private void saveStartupImageAsFile(String resolution, Bitmap bitmap) {

        try {
            File imageFile = new File(getStartupImageFilePath(resolution));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(imageFile));
            Log.d("*********", "saveStartupImageAsFile is finish, image path: " + imageFile.getAbsolutePath());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getStartupImageFilePath(String resolution) {

        File rootPath = getCacheDir();
        String name = Utils.md5(String.format(HttpAccessor.URL_STARTUP_IMAGE, resolution)) + ".bin";

        return rootPath.getAbsolutePath() + "/" + name;
    }

    private boolean isStartupImageFileValid(String resolution) {

        File imageFile = new File(getStartupImageFilePath(resolution));


        if (!imageFile.exists()) {
            Log.d("*********", "!imageFile.exists()");
            return false;
        }

        if (!imageFile.isFile()) {
            Log.d("*********", "!imageFile.isFile()");
            return false;
        }

        if (imageFile.length() == 0) {
            Log.d("*********", "imageFile.length() == 0");
            return false;
        }

        return true;
    }
}
