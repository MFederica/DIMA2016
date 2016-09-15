package com.appetite;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.appetite.model.HowToItem;
import com.appetite.FragmentHowTo.OnHowToListFragmentInteractionListener;
import com.google.android.youtube.player.YouTubeThumbnailView;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link HowToItem} and makes a call to the
 * specified {@link OnHowToListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class AdapterHowToList extends RecyclerView.Adapter<AdapterHowToList.ViewHolder> {
    private final static String TAG = AdapterHowToList.class.getSimpleName();

    private final List<HowToItem> mValues;
    private final OnHowToListFragmentInteractionListener mListener;
    private FragmentHowTo fht;

    private Context context;

    public AdapterHowToList(Context context, List<HowToItem> items, OnHowToListFragmentInteractionListener listener, FragmentHowTo fht) {
        this.context = context;
        mValues = items;
        mListener = listener;
        this.fht = fht;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_how_to_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mItem = mValues.get(position);
        Log.e("A", "onBindViewHolder: position = " + position + ", item = "+ holder.mItem.getText());
        holder.mTextView.setText(mValues.get(position).getText());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onHowToListFragmentInteraction(holder.mItem, position); //TODO cambiare
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final YouTubeThumbnailView mThumbnailView;
        public final TextView mTextView;
        public HowToItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mThumbnailView = (YouTubeThumbnailView) view.findViewById(R.id.fragment_how_to_item_thumbnail);
            mTextView = (TextView) view.findViewById(R.id.fragment_how_to_item_text);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mTextView.getText() + "'";
        }
    }
}
