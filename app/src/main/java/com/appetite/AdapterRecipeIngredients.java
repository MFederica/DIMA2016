package com.appetite;

import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

//TODO INTERFACCIA: decommenta 1/3
//import com.appetite.FragmentRecipeIngredients.OnListFragmentInteractionListener;
import com.appetite.model.RecipeIngredient;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link RecipeIngredient} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class AdapterRecipeIngredients extends RecyclerView.Adapter<AdapterRecipeIngredients.ViewHolder> {

    private final List<RecipeIngredient> mValues;
    private int amount;

    //TODO INTERFACCIA: decommenta 2/3 e cancella il costruttore usato ora
    /*private final OnListFragmentInteractionListener mListener;

    public AdapterRecipeIngredients(List<DummyItem> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    } */

    public AdapterRecipeIngredients(List<RecipeIngredient> items, int amount) {
        mValues = items;
        this.amount = amount;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_recipeingredients_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mNameView.setText(mValues.get(position).getName());
        Float tempQuantity = Float.valueOf(mValues.get(position).getQuantity());
        if(tempQuantity == 0) {
            holder.mQuantityView.setText("");
        } else {
            tempQuantity = tempQuantity * amount;
            holder.mQuantityView.setText(tempQuantity.toString());
        }
        String tempUnit = mValues.get(position).getUnit();
        if(tempUnit.compareTo("null") == 0) {
            holder.mUnitView.setText("");
        } else {
            holder.mUnitView.setText(tempUnit);
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO INTERFACCIA: decommenta 3/3
                /*if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                    } */
                Snackbar snackbar = Snackbar.make(v, R.string.fragment_recipeingredients_snackbar_message, Snackbar.LENGTH_LONG);
                snackbar.setAction(R.string.fragment_recipeingredients_snackbar_action, new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        //TODO implementare switch to shopping list
                    }
                });
                snackbar.show();

            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public void setAmount(int servings) {
        amount = servings;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mNameView;
        public final TextView mQuantityView;
        public final TextView mUnitView;
        public RecipeIngredient mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mNameView = (TextView) view.findViewById(R.id.fragment_recipeingredients_name);
            mQuantityView = (TextView) view.findViewById(R.id.fragment_recipeingredients_quantity);
            mUnitView = (TextView) view.findViewById(R.id.fragment_recipeingredients_unit);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mNameView.getText() + "'";
        }
    }
}
