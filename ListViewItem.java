package com.example.win10.sigma;

import android.graphics.drawable.Drawable;

/**
 * Created by WIN10 on 2017-04-26.
 */

public class ListViewItem {
    private String addressStr ;
    private String dateStr ;
    private String videonameStr ;
   // private Icon icon;
    private Drawable icon;

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }
    public void setAddress(String address) {
        addressStr = address ;
    }
    public void setDate(String date) {
        dateStr = date ;
    }
    public void setVideoname(String videoname) {
        videonameStr = videoname ;
    }

    public Drawable getIcon() {
        return this.icon;
    }
    public String getAddress() {
        return this.addressStr ;
    }
    public String getDate() {
        return this.dateStr ;
    }
    public String getVideoname() {
        return this.videonameStr ;
    }
}
