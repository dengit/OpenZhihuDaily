package com.dengit.openzhihudaily.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by dengit on 15/10/31.
 */
public class NewsListElement implements Serializable {
    public boolean istag;
    public int id;
    public int type;
    public boolean multipic;
    public String title;
    public String date;
    public ArrayList<String> images;

    public NewsListElement() {
        images = new ArrayList<String>();
    }
}
