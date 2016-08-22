package com.appetite;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.appetite.FragmentShoppingList.OnShoppingListFragmentInteractionListener;
import com.appetite.model.ShoppingItem;
import com.appetite.model.ShoppingListHelper;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link ShoppingItem} and makes a call to the
 * specified {@link OnShoppingListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class AdapterShoppingList extends RecyclerView.Adapter<AdapterShoppingList.ViewHolder> {

    private final List<ShoppingItem> mValues;
    private final OnShoppingListFragmentInteractionListener mListener;

    private Context context;

    public AdapterShoppingList(Context context, List<ShoppingItem> items, OnShoppingListFragmentInteractionListener listener) {
        this.context = context;
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_shoppinglist_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mRecipeNameView.setText(mValues.get(position).getRecipe());
        holder.mIngredientsListView.setLayoutManager(new LinearLayoutManager(context));
        holder.mIngredientsListView.setAdapter(new AdapterShoppingListIngredient(context, holder.mItem));

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onShoppingListFragmentInteraction(holder.mItem);
                }
            }
        });

        holder.mRemoveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ShoppingListHelper.getInstance(context).removeRecipe(holder.mItem)) {
                    ShoppingListHelper.saveShoppingList(context);
                    notifyDataSetChanged();
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
        public final RecyclerView mIngredientsListView;
        public final Button mRemoveButton;
        public ShoppingItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIngredientsListView = (RecyclerView) view.findViewById(R.id.fragment_shoppinglist_ingredientslist);
            mRecipeNameView = (TextView) view.findViewById(R.id.fragment_shoppinglist_recipe);
            mRemoveButton = (Button) view.findViewById(R.id.fragment_shoppinglist_remove);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mRecipeNameView.getText() + "'";
        }
    }
}
