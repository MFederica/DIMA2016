package com.appetite.recipe;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.appetite.R;
import com.appetite.main.ActivityMain;
import com.appetite.model.Recipe;
import com.appetite.style.GridImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.utils.DiskCacheUtils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a recipe step and makes a call to the
 * specified {@link FragmentRecipePreparation.OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class AdapterRecipePreparation extends RecyclerView.Adapter<AdapterRecipePreparation.ViewHolder> {

    private final List<String> mValues;
    private final Recipe recipe;
    private Context context;
    private final FragmentRecipePreparation.OnListFragmentInteractionListener mListener;
    private ImageLoader imageLoader = ImageLoader.getInstance();

    public AdapterRecipePreparation(Context context, Recipe recipe, FragmentRecipePreparation.OnListFragmentInteractionListener listener) {
        this.context = context;
        mValues = recipe.getStep();
        this.recipe = recipe;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_recipe_preparation_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mStepNumberView.setText(String.valueOf(position+1));
        holder.mStepTextView.setText(mValues.get(position));

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });

        //loads image
        String[] parts = recipe.getImage().split("\\.");
        String imageUri = ActivityMain.PATH_RECIPE_STEP + parts[0] + "_" + (position+1) + "." +parts[1];
        final File image = DiskCacheUtils.findInCache(imageUri, imageLoader.getDiskCache());
        if (image!= null && image.exists()) {
            Picasso.with(context).load(image).fit().centerCrop().into(holder.mStepImageView);
        } else {
            imageLoader.loadImage(imageUri, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String s, View view) {
                    holder.mStepImageView.setImageBitmap(null);
                }

                @Override
                public void onLoadingFailed(String s, View view, FailReason failReason) {

                }

                @Override
                public void onLoadingComplete(String s, View view, final Bitmap bitmap) {
                    Picasso.with(context).load(s).fit().centerCrop().into(holder.mStepImageView);

                }

                @Override
                public void onLoadingCancelled(String s, View view) {

                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mStepNumberView;
        public final GridImageView mStepImageView;
        public final TextView mStepTextView;
        public String mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mStepNumberView = (TextView) view.findViewById(R.id.fragment_recipe_preparation_step_number);
            mStepImageView = (GridImageView) view.findViewById(R.id.fragment_recipe_preparation_step_image);
            mStepTextView = (TextView) view.findViewById(R.id.fragment_recipe_preparation_step_text);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mStepTextView.getText() + "'";
        }
    }
}
