package com.appetite;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.appetite.model.Category;
import com.appetite.model.HowToItem;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.utils.DiskCacheUtils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Federica on 09/09/2016.
 */
public class AdapterHowToList extends ArrayAdapter<HowToItem> {

    private final LayoutInflater inflater;

    public AdapterHowToList(Context context, int textViewResourceId, List<HowToItem> objects) {
        super(context, textViewResourceId, objects);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null) {
            view = inflater.inflate(R.layout.list_item, null);
        }

        TextView textView = (TextView) view.findViewById(R.id.list_item_text);
        textView.setText(getItem(position).getTitle());
        TextView disabledText = (TextView) view.findViewById(R.id.list_item_disabled_text);
        disabledText.setText(getItem(position).getDisabledText());

        if (isEnabled(position)) {
            disabledText.setVisibility(View.INVISIBLE);
            textView.setTextColor(Color.WHITE);
        } else {
            disabledText.setVisibility(View.VISIBLE);
            textView.setTextColor(Color.GRAY);
        }

        return view;
    }


    @Override
    public boolean areAllItemsEnabled() {
        // have to return true here otherwise disabled items won't show a divider in the list.
        return true;
    }

    @Override
    public boolean isEnabled(int position) {
        return getItem(position).isEnabled();
    }

    public boolean anyDisabled() {
        for (int i = 0; i < getCount(); i++) {
            if (!isEnabled(i)) {
                return true;
            }
        }
        return false;
    }

}
