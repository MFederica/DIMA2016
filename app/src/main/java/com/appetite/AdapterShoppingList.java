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
public class AdapterShoppingList extends RecyclerView.Adapter<AdapterShoppingList.ViewHolder> {
    private final static String TAG = AdapterShoppingList.class.getSimpleName();

    private final List<ShoppingItem> mValues;
    private final OnShoppingListFragmentInteractionListener mListener;
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private FragmentShoppingList fsl;

    private Context context;

    public AdapterShoppingList(Context context, List<ShoppingItem> items, OnShoppingListFragmentInteractionListener listener, FragmentShoppingList fsl) {
        this.context = context;
        mValues = items;
        mListener = listener;
        this.fsl = fsl;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_shoppinglist_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mItem = mValues.get(position);
        Log.e("A", "onBindViewHolder: position = " + position + ", item = "+ holder.mItem.getRecipe());
        holder.mRecipeNameView.setText(mValues.get(position).getRecipe());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onShoppingListFragmentInteraction(holder.mItem, position);
                }
            }
        });

        holder.mRemoveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, "onBindViewHolder ONCLICK: position = " + position + ", item = "+ holder.mItem.getRecipe());
                if (ShoppingListHelper.getInstance(context).removeRecipe(holder.mItem)) {
                    ShoppingListHelper.saveShoppingList(context);
                    notifyItemRemoved(position);

                    notifyItemRangeChanged(position, getItemCount());

                    Snackbar.make(view, R.string.fragment_shoppinglist_snackbar_removed, Snackbar.LENGTH_LONG)
                            .setAction(R.string.fragment_shoppinglist_snackbar_undo, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Log.e(TAG, "onClick: ADD again " + holder.mItem.getRecipe());
                                    ShoppingListHelper.getInstance(context).shoppingList.add(position, holder.mItem);
                                    ShoppingListHelper.saveShoppingList(context);
                                    notifyItemInserted(position);
                                    notifyItemRangeChanged(position, getItemCount());
                                    fsl.checkEmptyList(null);
                                }
                            }).show();

                }
            fsl.checkEmptyList(null);
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
        public ShoppingItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            image = (GridImageView) view.findViewById(R.id.fragment_shoppinglist_image);
            mRecipeNameView = (TextView) view.findViewById(R.id.fragment_shoppinglist_recipe);
            mRemoveButton = (ImageButton) view.findViewById(R.id.fragment_shoppinglist_remove);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mRecipeNameView.getText() + "'";
        }
    }
}
