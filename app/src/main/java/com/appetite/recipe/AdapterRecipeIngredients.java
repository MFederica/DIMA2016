package com.appetite.recipe;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

//TODO INTERFACCIA: decommenta 1/3
//import com.appetite.recipe.FragmentRecipeIngredients.OnListFragmentInteractionListener;
import com.appetite.Application;
import com.appetite.R;
import com.appetite.model.Recipe;
import com.appetite.model.RecipeIngredient;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link RecipeIngredient} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class AdapterRecipeIngredients extends RecyclerView.Adapter<AdapterRecipeIngredients.ViewHolder> {

    private final static String TAG = RecyclerView.Adapter.class.getSimpleName();

    private final List<RecipeIngredient> mValues;
    private int amount;
    private Context context;
    private Recipe recipe;

    //TODO INTERFACCIA: decommenta 2/3 e cancella il costruttore usato ora
    /*private final OnListFragmentInteractionListener mListener;

    public AdapterRecipeIngredients(List<DummyItem> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    } */

    public AdapterRecipeIngredients(Context context, Recipe recipe, List<RecipeIngredient> items, int amount) {
        this.context = context;
        this.recipe = recipe;
        mValues = items;
        this.amount = amount;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_recipe_ingredients_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        String ingredientName = mValues.get(position).getName();
        holder.mItem = mValues.get(position);

        holder.mNameView.setText(ingredientName);
        Float tempQuantity = Float.valueOf(mValues.get(position).getQuantity());
        if(tempQuantity == 0) {
            holder.mQuantityView.setText(context.getResources().getString(R.string.quantum_satis));
        } else {
            tempQuantity = tempQuantity * amount;
            holder.mQuantityView.setText(Application.toFraction(tempQuantity, 10));
        }
        String tempUnit = mValues.get(position).getUnit();
        if(tempUnit.compareTo("null") == 0) {
            holder.mUnitView.setText("");
        } else {
            holder.mUnitView.setText(tempUnit);
        }
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
