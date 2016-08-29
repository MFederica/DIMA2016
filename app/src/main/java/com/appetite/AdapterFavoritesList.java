package com.appetite;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.appetite.FragmentShoppingList.OnShoppingListFragmentInteractionListener;
import com.appetite.model.FavoritesHelper;
import com.appetite.model.Recipe;
import com.appetite.model.ShoppingItem;
import com.appetite.model.ShoppingListHelper;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link ShoppingItem} and makes a call to the
 * specified {@link OnShoppingListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class AdapterFavoritesList extends RecyclerView.Adapter<AdapterFavoritesList.ViewHolder> {
    private final static String TAG = AdapterFavoritesList.class.getSimpleName();

    private final List<String> mValues;

    private Context context;
    private FragmentFavoritesList.OnFavoritesListFragmentInteractionListener mListener;

    public AdapterFavoritesList(Context context, List<String> items, FragmentFavoritesList.OnFavoritesListFragmentInteractionListener listener) {
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
        holder.mRecipeNameView.setText(mValues.get(position));

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
                if (FavoritesHelper.getInstance(context).removeRecipe(holder.mItem)) {
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
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mRecipeNameView;
        public final Button mRemoveButton;
        public String mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mRecipeNameView = (TextView) view.findViewById(R.id.fragment_favoriteslist_recipe);
            mRemoveButton = (Button) view.findViewById(R.id.fragment_favoriteslist_remove);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mRecipeNameView.getText() + "'";
        }
    }
}
