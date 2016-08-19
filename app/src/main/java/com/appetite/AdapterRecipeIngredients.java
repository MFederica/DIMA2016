package com.appetite;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

//TODO INTERFACCIA: decommenta 1/3
//import com.appetite.FragmentRecipeIngredients.OnListFragmentInteractionListener;
import com.appetite.model.Recipe;
import com.appetite.model.RecipeIngredient;
import com.appetite.model.ShoppingItem;
import com.appetite.model.ShoppingListHelper;

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
                .inflate(R.layout.fragment_recipeingredients_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        String ingredientName = mValues.get(position).getName();
        holder.mItem = mValues.get(position);

        holder.mNameView.setText(ingredientName);
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
        ShoppingItem si = ShoppingListHelper.getInstance(context).getShoppingItem(recipe);
        if(si != null) {
            if(si.getIngredientsList().contains(ingredientName)) {
                holder.mCheckBox.setChecked(true);
            } else {
                holder.mCheckBox.setChecked(false);
            }
        } else {
            holder.mCheckBox.setChecked(false);
        }
        holder.mCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO INTERFACCIA: decommenta 3/3
                /*if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                    } */

                // Is the view now checked?
                boolean checked = ((CheckBox) view).isChecked();
                // Check which checkbox was clicked
                switch(view.getId()) {
                    case R.id.fragment_recipeingredients_checkbox:
                        if (checked) {
                            Log.i(TAG, "onCheckboxClicked: " + ((TextView) ((View) view.getParent()).findViewById(R.id.fragment_recipeingredients_name)).getText());
                            Snackbar snackbar = Snackbar.make(view, R.string.fragment_recipeingredients_snackbar_add_message, Snackbar.LENGTH_LONG);
                            snackbar.setAction(R.string.fragment_recipeingredients_snackbar_action, new View.OnClickListener(){
                                @Override
                                public void onClick(View view) {
                                    //Redirects the user to the FragmentShoppingList
                                    Intent intent = new Intent(view.getContext(), ActivityMain.class);
                                    intent.putExtra(ActivityMain.FRAGMENT, FragmentShoppingList.class.getSimpleName());
                                    view.getContext().startActivity(intent);
                                }
                            });
                            snackbar.show();
                            if(ShoppingListHelper.getInstance(context).addIngredient(recipe, holder.mItem))
                                ShoppingListHelper.saveShoppingList(context);
                        }
                        else {
                            Snackbar snackbar = Snackbar.make(view, R.string.fragment_recipeingredients_snackbar_remove_message, Snackbar.LENGTH_LONG);
                            snackbar.setAction(R.string.fragment_recipeingredients_snackbar_action, new View.OnClickListener(){
                                @Override
                                public void onClick(View view) {
                                    //Redirects the user to the FragmentShoppingList
                                    Intent intent = new Intent(view.getContext(), ActivityMain.class);
                                    intent.putExtra(ActivityMain.FRAGMENT, FragmentShoppingList.class.getSimpleName());
                                    view.getContext().startActivity(intent);
                                }
                            });
                            snackbar.show();
                            if(ShoppingListHelper.getInstance(context).removeIngredient(recipe, holder.mItem))
                                ShoppingListHelper.saveShoppingList(context);
                        }
                        break;
                }
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
        public final CheckBox mCheckBox;
        public final TextView mNameView;
        public final TextView mQuantityView;
        public final TextView mUnitView;
        public RecipeIngredient mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mCheckBox = (CheckBox) view.findViewById(R.id.fragment_recipeingredients_checkbox);
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
