package com.appetite.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.youtube.player.YouTubeThumbnailView;

import java.io.Serializable;

/**
 * Created by Federica on 10/09/2016.
 */
public class HowToItem implements Parcelable {

    private final String text;
    private final String videoId;
    private final String thumbnail;

    public HowToItem(String text, String videoId, String thumbnail) {
        this.text = text;
        this.videoId = videoId;
        this.thumbnail = thumbnail;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getText() {
        return text;
    }

    public String getVideoId() {
        return videoId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(text);
        dest.writeString(videoId);
        dest.writeString(thumbnail);
    }

    private HowToItem(Parcel in) {
        text = in.readString();
        videoId = in.readString();
        thumbnail = in.readString();
    }

    public static final Parcelable.Creator<HowToItem> CREATOR = new Parcelable.Creator<HowToItem>() {
        public HowToItem createFromParcel(Parcel in) {
            return new HowToItem(in);
        }

        public HowToItem[] newArray(int size) {
            return new HowToItem[size];
        }
    };

}