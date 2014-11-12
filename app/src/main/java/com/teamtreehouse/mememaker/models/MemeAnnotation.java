package com.teamtreehouse.mememaker.models;

import java.io.Serializable;

/**
 * Created by Evan Anger on 8/17/14.
 */
public class MemeAnnotation implements Serializable {
    private int mId = -1;
    private String mColor;
    private String mTitle;
    private int mLocationX;
    private int mLocationY;

    public MemeAnnotation() {

    }
    public MemeAnnotation(int id, String color, String title, int locationX, int locationY) {
        mId = id;
        mColor = color;
        mTitle = title;
        mLocationX = locationX;
        mLocationY = locationY;
    }

    public int getId() { return mId; }
    public boolean hasBeenSaved() { return (getId() != -1); }

    public String getColor() { return mColor; }
    public void setColor(String color) {
        mColor = color;
    }

    public String getTitle() {
        return mTitle;
    }
    public void setTitle(String text) { mTitle = text; }

    public int getLocationX() {
        return mLocationX;
    }
    public void setLocationX(int x) {
        mLocationX = x;
    }

    public int getLocationY() {
        return mLocationY;
    }
    public void setLocationY(int y) {
        mLocationY = y;
    }
}

