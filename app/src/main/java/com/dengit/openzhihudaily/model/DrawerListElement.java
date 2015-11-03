package com.dengit.openzhihudaily.model;

/**
 * Created by dengit on 15/10/31.
 */
public class DrawerListElement {
    public int id;
    public String name;
    public String description;
    public String thumbnail;
    public long color;

    public boolean following;

    public DrawerListElement() {
        following = false;
    }
}
