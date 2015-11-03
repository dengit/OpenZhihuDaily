package com.dengit.openzhihudaily.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by dengit on 15/10/14.
 */
public class Utils {


    public static Bitmap getCircleBitmap(Bitmap src) {
        Bitmap bitmap = Bitmap.createBitmap(src.getWidth(), src.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);

        final RectF rectF = new RectF(0, 0, bitmap.getWidth(),
                bitmap.getHeight());

        float r = Math.min(rectF.centerX(), rectF.centerY());
        canvas.drawCircle(rectF.centerX(), rectF.centerY(),
                r, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(src, 0, 0, paint);
        return bitmap;
    }

    public static String readFileFromAssets(Context context, String relativePath) {
        InputStream is = null;
        StringWriter sw = new StringWriter();
        try {
            is = context.getResources().getAssets().open(relativePath);
            BufferedReader br = new BufferedReader(new InputStreamReader(is, "utf-8"));

            char[] buffer = new char[1024];
            int len;
            while ((len = br.read(buffer)) != -1) {
                sw.write(buffer, 0, len);
            }

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return sw.toString();
    }

    public static String md5(String source) {
        if (source == null) {
            return null;
        }

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(source.getBytes());

            byte[] hex = md.digest();

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < hex.length; i++) {
                int tmp = hex[i];
                if (tmp < 0) {
                    tmp += 256;
                }

                if (tmp < 16) {
                    sb.append("0");
                }

                sb.append(Integer.toHexString(tmp));
            }

            return sb.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public static String getNewTitle(String date) {
        SimpleDateFormat from = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat to = new SimpleDateFormat("MM月dd日 EEEE", new Locale("zh-CN"));

        Date today = new Date();

        try {
            Date fromDate = from.parse(date);
            String toDate = to.format(fromDate);
            String todayDate = to.format(today);
            //            Log.d("**", "toDate: " + toDate + ", todayDate: " + todayDate);

            return toDate.equals(todayDate) ? "今日热闻" : toDate;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
