package com.appetite.model;

import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Federica on 10/09/2016.
 */
public class HowToItem implements Serializable { //TODO parcelable?

    private final String text;
    private final String videoId;

    public HowToItem(String text, String videoId) {
        this.text = text;
        this.videoId = videoId;
    }

    public String getText() {
        return text;
    }

    public String getVideoId() {
        return videoId;
    }

}
