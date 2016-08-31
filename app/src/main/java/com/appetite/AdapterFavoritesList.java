package com.appetite;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.appetite.FragmentShoppingList.OnShoppingListFragmentInteractionListener;
import com.appetite.model.FavoriteItem;
import com.appetite.model.FavoritesHelper;
import com.appetite.model.Recipe;
import com.appetite.model.ShoppingItem;
import com.appetite.model.ShoppingListHelper;
import com.appetite.style.GridImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.utils.DiskCacheUtils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link ShoppingItem} and makes a call to the
 * specified {@link OnShoppingListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class AdapterFavoritesList extends RecyclerView.Adapter<AdapterFavoritesList.ViewHolder> {
    private final static String TAG = AdapterFavoritesList.class.getSimpleName();

    private final List<FavoriteItem> mValues;
    private ImageLoader imageLoader = ImageLoader.getInstance();

    private Context context;
    private FragmentFavoritesList.OnFavoritesListFragmentInteractionListener mListener;

    public AdapterFavoritesList(Context context, List<FavoriteItem> items, FragmentFavoritesList.OnFavoritesListFragmentInteractionListener listener) {
        this.context = context;
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_favoriteslist_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mItem = mValues.get(position);
        Log.e("A", "onBindViewHolder: position = " + position + ", item = "+ holder.mItem);
        holder.mRecipeNameView.setText(mValues.get(position).getRecipe());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onFavoritesListFragmentInteraction(holder.mItem);
                }
            }
        });

        holder.mRemoveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, "onBindViewHolder ONCLICK: position = " + position + ", item = "+ holder.mItem);
                if (FavoritesHelper.getInstance(context).removeRecipe(holder.mItem.getRecipe())) {
                    FavoritesHelper.saveFavorites(context);
                    notifyItemRemoved(position);

                    notifyItemRangeChanged(position, getItemCount());

                    Snackbar.make(view, R.string.fragment_favoriteslist_snackbar_removed, Snackbar.LENGTH_LONG)
                            .setAction(R.string.fragment_favoriteslist_snackbar_undo, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Log.e(TAG, "onClick: ADD again " + holder.mItem);
                                    FavoritesHelper.getInstance(context).favoritesList.add(position, holder.mItem);
                                    FavoritesHelper.saveFavorites(context);
                                    notifyItemInserted(position);
                                    notifyItemRangeChanged(position, getItemCount());
                                }
                            }).show();

                }

            }
        });
        String imageUri = ActivityMain.PATH_RECIPE + holder.mItem.getImage();
        holder.image.setImageBitmap(null);

        if (imageUri != null && !holder.mItem.getImage().equals("")) {
            final File image = DiskCacheUtils.findInCache(imageUri, imageLoader.getDiskCache());
            if (image!= null && image.exists()) {
                Picasso.with(context).load(image).fit().centerCrop().into(holder.image);
            } else {
                imageLoader.loadImage(imageUri, new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String s, View view) {
                        holder.image.setImageBitmap(null);
                    }

                    @Override
                    public void onLoadingFailed(String s, View view, FailReason failReason) {

                    }

                    @Override
                    public void onLoadingComplete(String s, View view, final Bitmap bitmap) {
                        Picasso.with(context).load(s).fit().centerCrop().into(holder.image);

                    }

                    @Override
                    public void onLoadingCancelled(String s, View view) {

                    }
                });
            }
        }else {
            holder.image.setImageBitmap(null);
        }
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final GridImageView image;
        public final TextView mRecipeNameView;
        public final ImageButton mRemoveButton;
        public FavoriteItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            image = (GridImageView) view.findViewById(R.id.fragment_favoriteslist_image);
            mRecipeNameView = (TextView) view.findViewById(R.id.fragment_favoriteslist_recipe);
            mRemoveButton = (ImageButton) view.findViewById(R.id.fragment_favoriteslist_remove);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mRecipeNameView.getText() + "'";
        }
    }
}
