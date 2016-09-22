package com.appetite.shopping;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

//TODO INTERFACCIA: decommenta 1/3
//import com.appetite.recipe.FragmentRecipeIngredients.OnListFragmentInteractionListener;
import com.appetite.Application;
import com.appetite.R;
import com.appetite.model.RecipeIngredient;
import com.appetite.model.ShoppingItem;
import com.appetite.model.ShoppingListHelper;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link RecipeIngredient} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class AdapterShoppingListIngredient extends RecyclerView.Adapter<AdapterShoppingListIngredient.ViewHolder> {

    private final static String TAG = AdapterShoppingListIngredient.class.getSimpleName();

    private final List<RecipeIngredient> mValues;
    private final ShoppingItem mShoppingItem;
    private Context context;

    //TODO INTERFACCIA: decommenta 2/3 e cancella il costruttore usato ora
    /*private final OnListFragmentInteractionListener mListener;

    public AdapterRecipeIngredients(List<DummyItem> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    } */

    public AdapterShoppingListIngredient(Context context, ShoppingItem shoppingItem) {
        this.context = context;
        this.mShoppingItem = shoppingItem;
        mValues = mShoppingItem.getIngredientsList();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_shoppinglist_item_ingredient, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);

        holder.mNameView.setText(mValues.get(position).getName());
        Float tempQuantity = Float.valueOf(mValues.get(position).getQuantity());
        if(tempQuantity == 0) {
            holder.mQuantityView.setText(context.getResources().getString(R.string.quantum_satis));
        } else {
            tempQuantity = tempQuantity * mShoppingItem.getServings();
            holder.mQuantityView.setText(Application.toFraction(tempQuantity, 10));
        }
        String tempUnit = mValues.get(position).getUnit();
        if(tempUnit.compareTo("null") == 0) {
            holder.mUnitView.setText("");
        } else {
            holder.mUnitView.setText(tempUnit);
        }

        holder.mCheckBox.setChecked(mShoppingItem.getIngredientsMapping().get(holder.mItem));
        holder.mCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Is the view now checked?
                boolean checked = ((CheckBox) view).isChecked();
                // Check which checkbox was clicked
                switch(view.getId()) {
                    case R.id.fragment_shoppinglist_ingredient_checkbox:
                        Log.d(TAG, "onClick: CHECKBOX");
                        ShoppingListHelper.getInstance(context).ingredientChecked(mShoppingItem, holder.mItem, checked);
                        ShoppingListHelper.saveShoppingList(context);
                        break;
                }
            }
        });

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Check or uncheck the checkbox
                holder.mCheckBox.setChecked(!(holder.mCheckBox.isChecked()));
                boolean checked = holder.mCheckBox.isChecked();
                        Log.d(TAG, "onClick: ITEM");
                        ShoppingListHelper.getInstance(context).ingredientChecked(mShoppingItem, holder.mItem, checked);
                        ShoppingListHelper.saveShoppingList(context);
                }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final CheckBox mCheckBox;
        public final TextView mNameView;
        public final TextView mQuantityView;
        public final TextView mUnitView;
        public RecipeIngredient mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mCheckBox = (CheckBox) view.findViewById(R.id.fragment_shoppinglist_ingredient_checkbox);
            mNameView = (TextView) view.findViewById(R.id.fragment_shoppinglist_ingredient_name);
            mQuantityView = (TextView) view.findViewById(R.id.fragment_shoppinglist_ingredient_quantity);
            mUnitView = (TextView) view.findViewById(R.id.fragment_shoppinglist_ingredient_unit);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mNameView.getText() + "'";
        }
    }
}
